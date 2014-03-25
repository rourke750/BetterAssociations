package com.sandislandserv.rourke750.Listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.sandislandserv.rourke750.database.BaseValues;

public class TimeManager implements Listener{

	private static Map<String, Long> time = new HashMap<String, Long>();
	private BaseValues db;
	public TimeManager(BaseValues db){
		this.db = db;
	}
	
	@EventHandler()
	public void loginEvent(PlayerJoinEvent event){
		
	}
	
	public long getTime(Player player){
		
	}
}
