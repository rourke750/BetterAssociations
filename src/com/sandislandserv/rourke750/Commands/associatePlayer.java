package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.database.BaseValues;

public class associatePlayer {

	private BaseValues db;
	public associatePlayer(BaseValues db){
		this.db = db;
	}
	
	public boolean associateplayer(CommandSender sender, String[] args){
		if (args.length < 1){
			sender.sendMessage("You must specify the main player.");
			return true;
		}
		if (args.length == 1){
			sender.sendMessage("You must specify alts to be added");
			return true;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
		if (!player.hasPlayedBefore()){
			sender.sendMessage("The main account has never played before.\nCancelling request.");
			return true;
		}
		for (int x = 1; x < args.length; x++){
			player = Bukkit.getOfflinePlayer(args[x]);
			if (!player.hasPlayedBefore()){
				sender.sendMessage("Player: " + args[x] + " has never before therefore cannot be added.");
				continue;
			}
			//db.associatePlayer();
		}
		sender.sendMessage("All players have been added.");
		return true;
	}
}
