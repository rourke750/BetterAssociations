package com.sandislandserv.rourke750.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.BaseValues;

public class disAssociatePlayer {
	
	private BaseValues db;
	public disAssociatePlayer(BaseValues db){
		this.db = db;
	}

	public boolean disAssociate(CommandSender sender, String[] args){
		if (args.length == 0){
			sender.sendMessage(ChatColor.RED + "You must specify a player.");
			return true;
		}
		if (args.length == 1){
			sender.sendMessage(ChatColor.RED + "You must add an account to remove.");
			return true;
		}
		for (int x = 1; x < args.length; x++){
			db.disAssociateAltfromPlayer(args[0], args[x]);
			sender.sendMessage(ChatColor.RED + args[x] + " has been removed from " + args[0] + "'s list of alts.");
		}
		return true;
	}
}
