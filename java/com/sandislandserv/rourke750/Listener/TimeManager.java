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

public class TimeManager implements Listener {

	private BetterAssociations plugin;
	private static Map<String, Long> time = new HashMap<String, Long>();
	private BaseValues db;

	public TimeManager(BetterAssociations plugin, BaseValues db) {
		this.db = db;
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void loginEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				String uuid = player.getUniqueId().toString();
				long loginTime = System.currentTimeMillis() / 1000;
				time.put(uuid, loginTime);
				db.insertIntialPlayerTime(player);
			}
		});
	}

	public long getTime(Player player) {
		return time.get(player.getUniqueId().toString());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void logoutEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		save(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerKickEvent(PlayerKickEvent event) {
		Player player = event.getPlayer();
		save(player);
	}

	public void save(final Player player) {
				String uuid = player.getUniqueId().toString();
				if (!time.containsKey(uuid))
					return;
				long logoutTime = System.currentTimeMillis() * 100;
				long playerPlayedTime = logoutTime - time.get(uuid);
				db.addTimetoPlayer(player, playerPlayedTime);
				time.remove(uuid);
	}

	public void load() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					String uuid = player.getUniqueId().toString();
					long loginTime = System.currentTimeMillis() / 1000;
					time.put(uuid, loginTime);
					db.insertIntialPlayerTime(player);
				}
			});
		}
	}
}
