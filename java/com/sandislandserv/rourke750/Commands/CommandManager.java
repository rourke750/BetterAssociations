package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class CommandManager implements CommandExecutor{

	private getAlts getalts;
	private associatePlayer associateplayer;
	private disAssociatePlayer disassociatePlayer;
	private Utility utl;
	private getAmountPlayed getPlayed;
	
	public CommandManager(BaseValues db, BetterAssociations plugin){
		getalts = new getAlts(db);
		associateplayer = new associatePlayer(db);
		disassociatePlayer = new disAssociatePlayer(db);
		utl = new Utility(plugin);
		getPlayed = new getAmountPlayed(db);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("bagetalts"))
			return getalts.getAltAccounts(sender, args);
		if (command.getName().equalsIgnoreCase("baassociateplayer"))
			return associateplayer.associateplayer(sender, args);
		if (command.getName().equals("badisassociateplayer"))
			return disassociatePlayer.disAssociate(sender, args);
		if (command.getName().equals("betterassociations"))
			return utl.run(sender, args);
		if (command.getName().equals("bagetamountplayed"))
			return getPlayed.getTimePlayed(sender, args);
		return false;
	}
}
