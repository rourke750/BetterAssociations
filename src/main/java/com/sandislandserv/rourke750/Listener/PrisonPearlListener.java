package com.sandislandserv.rourke750.Listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sandislandserv.rourke750.database.BaseValues;
import com.untamedears.PrisonPearl.AltsListEvent;
import com.untamedears.PrisonPearl.RequestAltsListEvent;

public class PrisonPearlListener implements Listener{

	private BaseValues bv;
	
	public PrisonPearlListener(BaseValues bv){
		this.bv = bv;
	}
	
	@EventHandler
	public void updatePrisonPearlAltList(RequestAltsListEvent event){
		List<UUID> uuids = event.getPlayersToCheck();
		for (UUID uuid: uuids){ // loop through all the players
			AltsListEvent altsList = new AltsListEvent(bv.getAssociationsManager().getAltsListUUID(uuid));
			Bukkit.getServer().getPluginManager().callEvent(altsList);
		}
	}
	
}
