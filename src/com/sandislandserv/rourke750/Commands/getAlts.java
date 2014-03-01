package com.sandislandserv.rourke750.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.BaseValues;

public class getAlts {

	private BaseValues db;
	
	public getAlts(BaseValues db){
		this.db = db;
	}
	public boolean getAltAccounts(CommandSender sender, String[] args){
		if (args.length > 1){ // only want to return one player's set of alts
			sender.sendMessage("Please only specify one account");
			return true;
		}
		if (args.length == 0){
			sender.sendMessage("Please specify a player's name, can't leave blank!");
			return true;
		}
		List<String> alts = db.getAltsList(args[0]); // get tye alts for the player
		if (alts == null){
			sender.sendMessage("Player does not exist, or has not logged on.");
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
