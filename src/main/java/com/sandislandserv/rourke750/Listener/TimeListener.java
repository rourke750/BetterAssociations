package com.sandislandserv.rourke750.Listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.TimeManager;

public class TimeListener implements Listener {

	private BetterAssociations plugin;
	private TimeManager tm;

	public TimeListener(BetterAssociations plugin, BaseValues db) {
		tm = db.getTimeManager();
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void loginEvent(PlayerJoinEvent event) {
		
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void logoutEvent(PlayerQuitEvent event) {
		
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerKickEvent(PlayerKickEvent event) {
		;
	}
}
