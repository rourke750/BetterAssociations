package com.sandislandserv.rourke750.Listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BanManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class BanListener implements Listener{
	
	private BanManager bm;
	private AssociationsManager am;
	private BetterAssociations plugin;
	
	public BanListener(BetterAssociations plugin){
		this.plugin = plugin;
		bm = BetterAssociations.getManagerHandler().getBanManager();
		am = BetterAssociations.getManagerHandler().getAssociationsManager();
	}
	
	/*
	 * Checks if the player is banned
	 */
	@EventHandler(priority = EventPriority.HIGHEST) // set to highest because this is most important
	public void playerBanned(PlayerLoginEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		String reason = bm.isBanned(uuid);
		if (reason.equals("")) return;
		Player p = event.getPlayer();
		event.setKickMessage(reason);
		event.setResult(Result.KICK_BANNED);
	}
	
	/*
	 * sets the amount of alts a player can have logged on at once
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void tooManyLoggedInAccounts(PlayerLoginEvent event){ // Set to join because if kicked on login ip info isnt tracked.
		UUID name = event.getPlayer().getUniqueId();
		int count = 0;
		int allowed = plugin.getConfig().getInt("banmanager.set.altlimit");
		if (allowed == 0) return;
		List<UUID> alts = am.getAltsListUUID(name);
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player p: players)
			if(alts.contains(p.getUniqueId())) count++;
		
		if (count > allowed){
			String reason = plugin.getConfig().getString("banmanager.set.altlimitreason");
			event.setKickMessage(reason);
			event.setResult(Result.KICK_BANNED);
		}
	}
}
