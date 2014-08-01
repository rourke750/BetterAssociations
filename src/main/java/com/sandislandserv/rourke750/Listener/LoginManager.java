package com.sandislandserv.rourke750.Listener;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.Misc.ClassHandler;
import com.sandislandserv.rourke750.Misc.ProfileInterface;
import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class LoginManager implements Listener {

	private PlayerManager pm;
	private AssociationsManager am;
	private BetterAssociations plugin;
	private SendInformation si;
	private ProfileInterface profile;

	public LoginManager(BaseValues db, BetterAssociations plugin,
			SendInformation si) {
		pm = db.getPlayerManager();
		am = db.getAssociationsManager();
		this.plugin = plugin;
		this.si = si;
		profile = ClassHandler.getProfileClass();
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
		UUID uuid = event.getUniqueId();
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
				si.sendPlayer(player);
			}
		});
		// set the player display name. Plugins by now should be running based
		// by uuid
		// This just helps show every person as who they were when they first
		// logged on
		String name = pm.getPlayer(
				player.getUniqueId());
		profile.modifyGameProfile(player, name);
		player.setDisplayName(name);
		player.setPlayerListName(name);
		player.setCustomName(name);
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
