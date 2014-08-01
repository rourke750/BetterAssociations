package com.sandislandserv.rourke750.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class associatePlayer {

	private AssociationsManager am;
	private PlayerManager pm;
	public associatePlayer(BaseValues db){
		am = db.getAssociationsManager();
		pm = db.getPlayerManager();
	}
	
	public void associateplayer(CommandSender sender, String[] args){
		if (args.length < 1){
			sender.sendMessage("You must specify the main player.");
			return;
		}
		if (args.length == 1){
			sender.sendMessage("You must specify alts to be added");
			return;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
		if (!player.hasPlayedBefore()){
			sender.sendMessage("The main account has never played before.\nCancelling request.");
			return;
		}
		List<UUID> accounts = new ArrayList<UUID>();
		for (int x = 2; x < args.length; x++){
			UUID alt = pm.getUUIDfromPlayerName(args[x]);
			if (alt == null){
				sender.sendMessage("Player: " + args[x] + " has never before therefore cannot be added.");
				continue;
			}
			accounts.add(alt);
		}
		if (accounts.size() != 0)
			am.associatePlayer(player.getUniqueId(), accounts);
		sender.sendMessage("All players have been added.");
	}
}
