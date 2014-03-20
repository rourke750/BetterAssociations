package com.sandislandserv.rourke750.Commands;

import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.BetterAssociations;

public class Utility {

	private BetterAssociations plugin;
	public Utility(BetterAssociations plugin){
		this.plugin = plugin;
	}
	
	public boolean run(CommandSender sender, String[] args){
		if (args[0].equals("reload")) plugin.reloadConfig();
		return true;
	}
}
