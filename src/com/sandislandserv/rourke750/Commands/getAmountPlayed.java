package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.database.BaseValues;

public class getAmountPlayed {

	private BaseValues db;
	
	public getAmountPlayed(BaseValues db){
		this.db = db;
	}
	
	public boolean getTimePlayed(CommandSender sender, String[] args){
		if (args.length > 1){
			sender.sendMessage(ChatColor.RED + "Too many args.");
			return true;
		}
		if (args.length == 0){
			sender.sendMessage(ChatColor.RED + "Please provide a name!");
			return true;
		}
		String name =  args[0];
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (!player.hasPlayedBefore()){
			sender.sendMessage(ChatColor.RED + "Player does not exist!");
			return true;
		}
		float amount = (float) db.getPlayerTimeinSeconds(player.getName());
		float hours = (long) amount/3600;
		float minutes = ((amount/3600) - (long)hours) * 60;
		sender.sendMessage("Player: " + player.getName() + " has played for " + hours + " hours and " + minutes + " minutes.");
		return true;
	}
}
