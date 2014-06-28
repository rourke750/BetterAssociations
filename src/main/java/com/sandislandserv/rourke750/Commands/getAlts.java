package com.sandislandserv.rourke750.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class getAlts {

	private AssociationsManager am;
	public getAlts(BaseValues db){
		am = db.getAssociationsManager();
	}
	public boolean getAltAccounts(CommandSender sender, String[] args){
		if (args.length > 2){ // only want to return one player's set of alts
			sender.sendMessage("Please only specify one account");
			return true;
		}
		if (args.length == 1){
			sender.sendMessage("Please specify a player's name, can't leave blank!");
			return true;
		}
		List<String> alts = am.getAltsList(args[0]); // get the alts for the player
		if (alts.size() == 0){
			sender.sendMessage("Player has no alts.");
			return true;
		}
		StringBuilder returnmessage = new StringBuilder();
		returnmessage.append("Alts are as follows for " + args[0] +":");
		for (int x = 0; x < alts.size(); x++){
			returnmessage.append(" ");
			returnmessage.append(alts.get(x));
		}
		sender.sendMessage(returnmessage.toString());
		return true;
	}
}
