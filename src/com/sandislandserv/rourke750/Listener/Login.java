package com.sandislandserv.rourke750.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.database.BaseValues;

public class Login implements Listener{
	
	private BaseValues db;
	private BetterAssociations plugin;
	private SendInformation si;
	public Login(BaseValues db, SendInformation si, BetterAssociations plugin){
		this.db = db;
		this.si = si;
		this.plugin = plugin;
	}
	
	@EventHandler()
	public void loginEvent(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			@Override
			public void run(){
				db.addPlayerIp(player);
				db.associatePlayer(player);
				db.addPlayerUUID(player);
				si.run(player);
				
			}
		});
	}

}
