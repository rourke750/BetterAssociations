package com.sandislandserv.rourke750.Listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class BanManager implements Listener{
	
	private BaseValues db = BetterAssociations.getDataBaseManager();
	private BetterAssociations plugin;
	
	public BanManager(BetterAssociations plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerLoginEvent(PlayerJoinEvent event){
		String uuid = event.getPlayer().getUniqueId().toString();
		String reason = db.isBanned(uuid);
		if (reason == null) return;
		Player p = event.getPlayer();
		p.kickPlayer(reason);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void tooManyLoggedInAccounts(PlayerJoinEvent event){
		String name = event.getPlayer().getName();
		int count = 0;
		int allowed = plugin.getConfig().getInt("banmanager.set.altlimit");
		if (allowed == 0) return;
		List<String> alts = db.getAltsList(name);
		Player[] oplayer = Bukkit.getOnlinePlayers();
		for (Player p: oplayer){
			if(alts.contains(p.getName())) count++;
		}
		if (count > allowed){
			String reason = plugin.getConfig().getString("banmanager.set.altlimitreason");
			Player p = event.getPlayer();
			p.kickPlayer(reason);
		}
	}
}
