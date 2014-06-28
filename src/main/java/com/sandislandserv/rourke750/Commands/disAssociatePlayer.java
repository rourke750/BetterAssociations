package com.sandislandserv.rourke750.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class disAssociatePlayer {
	
	private AssociationsManager am;
	public disAssociatePlayer(BaseValues db){
		am = db.getAssociationsManager();
	}

	public boolean disAssociate(CommandSender sender, String[] args){
		if (args.length == 1){
			sender.sendMessage(ChatColor.RED + "You must specify a player.");
			return true;
		}
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "You must add an account to remove.");
			return true;
		}
		for (int x = 2; x < args.length; x++){
			am.disAssociateAltfromPlayer(args[1], args[x]);
			sender.sendMessage(ChatColor.RED + args[x] + " has been removed from " + args[0] + "'s list of alts.");
		}
		return true;
	}
}
