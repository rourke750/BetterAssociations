package com.sandislandserv.rourke750.Listener;

import java.util.List;

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
		List<String> players = event.getPlayersToCheck();
		for (String name: players){ // loop through all the players
			AltsListEvent altsList = new AltsListEvent(bv.getAltsList(name));
			Bukkit.getServer().getPluginManager().callEvent(altsList);
		}
	}
	
}
