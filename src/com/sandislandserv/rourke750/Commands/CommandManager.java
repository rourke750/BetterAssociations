package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.database.BaseValues;

public class CommandManager implements CommandExecutor{

	private BaseValues db;
	private getAlts getalts;
	private associatePlayer associateplayer;
	public CommandManager(BaseValues db){
		this.db = db;
		getalts = new getAlts(db);
		associateplayer = new associatePlayer(db);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("getalts"))
			return getalts.getAltAccounts(sender, args);
		if (command.getName().equalsIgnoreCase("associateplayer"))
			return associateplayer.associateplayer(sender, args);
		return false;
	}
}
