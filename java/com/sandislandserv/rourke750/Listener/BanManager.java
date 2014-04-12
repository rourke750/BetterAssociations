package com.sandislandserv.rourke750.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class BanManager implements Listener{
	
	private BaseValues db = BetterAssociations.getDataBaseManager();
	private BetterAssociations plugin;
	
	public BanManager(BetterAssociations plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerLoginEvent(PlayerLoginEvent event){
		String uuid = event.getPlayer().getUniqueId().toString();
		String reason = db.isBanned(uuid);
		if (reason == null) return;
		Player p = event.getPlayer();
		p.kickPlayer(reason);
	}
}
