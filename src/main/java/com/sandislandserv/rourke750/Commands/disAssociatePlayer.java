package com.sandislandserv.rourke750.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class disAssociatePlayer {
	
	private AssociationsManager am;
	private PlayerManager pm;
	public disAssociatePlayer(BaseValues db){
		am = db.getAssociationsManager();
		pm = db.getPlayerManager();
	}

	public void disAssociate(CommandSender sender, String[] args){
		if (args.length == 1){
			sender.sendMessage(ChatColor.RED + "You must specify a player.");
			return;
		}
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "You must add an account to remove.");
			return;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
		if (!player.hasPlayedBefore()){
			sender.sendMessage(ChatColor.RED + "Player has never played before, cannot add alts.");
			return;
		}
		List<UUID> alts = new ArrayList<UUID>();
		for (int x = 2; x < args.length; x++){
			UUID alt = pm.getUUIDfromPlayerName(args[x]);
			if (alt == null){
				sender.sendMessage("Player: " + args[x] + " has never before therefore cannot be added.");
				continue;
			}
			sender.sendMessage(ChatColor.RED + args[x] + " has been removed from " + args[1] + "'s list of alts.");
			alts.add(alt);
		}
		am.disAssociateAltfromPlayer(player.getUniqueId(), alts);
		return;
	}
}
