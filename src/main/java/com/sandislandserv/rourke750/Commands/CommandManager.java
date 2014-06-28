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
	private HelpManager helpManager;
	private BanCommand banCommand;
	
	public CommandManager(BaseValues db, BetterAssociations plugin){
		getalts = new getAlts(db);
		associateplayer = new associatePlayer(db);
		disassociatePlayer = new disAssociatePlayer(db);
		utl = new Utility(plugin);
		helpManager = new HelpManager();
		banCommand = new BanCommand(db);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("ba")) return false;
		
		if (args.length == 0)
			return helpManager.getCommandInfo(sender);
		if (args[0].equalsIgnoreCase("alts"))
			return getalts.getAltAccounts(sender, args);
		if (args[0].equalsIgnoreCase("associate"))
			return associateplayer.associateplayer(sender, args);
		if (args[0].equalsIgnoreCase("dassociate"))
			return disassociatePlayer.disAssociate(sender, args);
		if (args[0].equalsIgnoreCase("ban") && args[1].equalsIgnoreCase("player"))
			return banCommand.banPlayer(sender, args);
		return false;
	}
}
