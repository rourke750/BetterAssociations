package com.sandislandserv.rourke750.Commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class getAlts {

	private AssociationsManager am;
	private PlayerManager pm;
	private BetterAssociations plugin;

	public getAlts(BetterAssociations pl, BaseValues db) {
		am = db.getAssociationsManager();
		pm = db.getPlayerManager();
		plugin = pl;
	}

	public void getAltAccounts(final CommandSender sender,
			final String[] args) {
		if (args.length > 2) { // only want to return one player's set of alts
			sender.sendMessage("Please only specify one account");
			return;
		}
		if (args.length == 1) {
			sender.sendMessage("Please specify a player's name, can't leave blank!");
			return;
		}
		UUID uuid = pm.getUUIDfromPlayerName(args[1]);
		if (uuid == null){
			sender.sendMessage(ChatColor.RED + "Player has not played before.");
			return;
		}
		final List<String> alts = am.getAltsList(uuid); // get the alts for
															// the player
		if (alts.size() == 0) {
			sender.sendMessage("Player has no alts.");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				StringBuilder returnmessage = new StringBuilder();
				returnmessage
						.append("Alts are as follows for " + args[1] + ":");
				for (int x = 0; x < alts.size(); x++) {
					returnmessage.append(" ");
					returnmessage.append(alts.get(x));
				}
				sender.sendMessage(returnmessage.toString());
			}

		});
	}
}
