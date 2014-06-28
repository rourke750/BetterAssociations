package com.sandislandserv.rourke750.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class LoginManager implements Listener {

	private PlayerManager pm;
	private AssociationsManager am;
	private BetterAssociations plugin;

	public LoginManager(BaseValues db, BetterAssociations plugin,
			SendInformation si) {
		pm = db.getPlayerManager();
		am = db.getAssociationsManager();
		this.plugin = plugin;
	}

	/*
	 * Adds player name to database. If name already exists adds an incremental
	 * number to their name to differentiate between the players. Don't be
	 * stupid, don't change your name unless you have never played on the
	 * server.
	 * 
	 * Also adds the ip
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void preLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		String uuid = event.getUniqueId().toString();
		pm.addPlayerUUID(name, uuid);
		pm.addPlayerIp(uuid,
				event.getAddress().getHostAddress());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void loginEvent(PlayerLoginEvent event) {
		// associates the player
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				am.associatePlayer(player);
			}
		});
		// set the player display name. Plugins by now should be running based
		// by uuid
		// This just helps show every person as who they were when they first
		// logged on
		String name = pm.getPlayer(
				player.getUniqueId().toString());
		try {
			// start of getting the GameProfile
			CraftHumanEntity craftHuman = (CraftHumanEntity) player;
			EntityHuman human = craftHuman.getHandle();
			Field fieldName = EntityHuman.class.getDeclaredField("i");
			fieldName.setAccessible(true);
			GameProfile prof = (GameProfile) fieldName.get(human);
			// End

			// Start of adding a new name
			Field nameUpdate = prof.getClass().getDeclaredField("name");

			setFinalStatic(nameUpdate, name, prof);
			// end
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.setDisplayName(name);
		player.setPlayerListName(name);
		player.setCustomName(name);
	}

	static void setFinalStatic(Field field, Object newValue, GameProfile prof) {
		try {
			field.setAccessible(true);

			// remove final modifier from field
			Field modifiersField;
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField
					.setInt(field, field.getModifiers() & ~Modifier.FINAL);

			field.set(prof, newValue);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Handles alerts to Admins
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void sendAdminMessages(PlayerJoinEvent event) {
		Player playerLogger = event.getPlayer();
		int amount = am.getAmountOfAlts(playerLogger);
		if (amount == 0)
			return;
		Set<OfflinePlayer> admins = Bukkit.getOperators();
		for (OfflinePlayer admin : admins) {
			if (!admin.isOnline())
				continue;
			Player player = admin.getPlayer();
			player.sendMessage(ChatColor.RED + playerLogger.getDisplayName()
					+ " logged in and has " + amount + " alts.");
		}
	}

}