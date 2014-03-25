package com.sandislandserv.rourke750.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class LoginManager implements Listener{
	
	private BaseValues db;
	private BetterAssociations plugin;
	
	public LoginManager(BaseValues db, BetterAssociations plugin){
		this.db = db;
		this.plugin = plugin;
	}
	
	@EventHandler()
	public void loginEvent(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			@Override
			public void run(){
				db.addPlayerUUID(player);
				db.addPlayerIp(player);
				db.associatePlayer(player);
			}
		});
		// set the player display name.  Plugins by now should be running based by uuid
		// This just helps show every person as who they were when they first logged on
		String name = db.getPlayer(player.getUniqueId().toString());
		if (name == null) return;
		player.setDisplayName(name);
		player.setPlayerListName(name);
		player.setCustomName(name);
	}

}
