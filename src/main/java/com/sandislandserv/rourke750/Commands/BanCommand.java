package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BanManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class BanCommand {

	private BanManager bm;
	public BanCommand(BaseValues db){
		bm = db.getBanManager();
	}
	
	public boolean banPlayer(CommandSender sender, String[] args){
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "Must give player name to ban!");
			return true;
		}
		String ban = args[2];
		OfflinePlayer pl = Bukkit.getOfflinePlayer(args[1]);
		if (!pl.hasPlayedBefore()){
			sender.sendMessage(ChatColor.RED + "Player has not played before.");
			return true;
		}
		String reason = "";
		for (int x = 3; x < args.length; x++){
			reason += args[x];
		}
		if (reason == ""){
			reason = BetterAssociations.getConfigFile().getString("banmanager.set.defaultbanreason");
			bm.banPlayer(reason, pl.getUniqueId().toString());
			sender.sendMessage(ChatColor.RED + "Player banned with default reason: " + reason);
		}
		else{
			bm.banPlayer(reason, pl.getUniqueId().toString());
			sender.sendMessage(ChatColor.RED + "Player banned with reason: " + reason);
		}
		return true;
	}
}
