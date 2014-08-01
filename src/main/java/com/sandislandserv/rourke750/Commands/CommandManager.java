package com.sandislandserv.rourke750.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.database.BaseValues;

public class CommandManager implements CommandExecutor{

	private getAlts getalts;
	private associatePlayer associateplayer;
	private disAssociatePlayer disassociatePlayer;
	private Utility utl;
	private HelpManager helpManager;
	private BanCommand banCommand;
	private ServerCommunicationsCommand scc;
	
	public CommandManager(BaseValues db, BetterAssociations plugin, SendInformation si){
		getalts = new getAlts(plugin, db);
		associateplayer = new associatePlayer(db);
		disassociatePlayer = new disAssociatePlayer(db);
		utl = new Utility(plugin);
		helpManager = new HelpManager();
		banCommand = new BanCommand(db);
		scc = new ServerCommunicationsCommand(si);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("ba")) return false;
		
		if (args.length == 0)
			helpManager.getCommandInfo(sender);
		
		// Handles alt accounts
		else if (args[0].equalsIgnoreCase("alts"))
			getalts.getAltAccounts(sender, args);
		
		// Handles associating players
		else if (args[0].equalsIgnoreCase("associate"))
			associateplayer.associateplayer(sender, args);
		
		// Handles disassociating players
		else if (args[0].equalsIgnoreCase("dassociate"))
			disassociatePlayer.disAssociate(sender, args);
		
		// Handles arguements dealing with banning players
		else if (args[0].equalsIgnoreCase("ban")){
			if (args.length <= 1)
				sender.sendMessage(ChatColor.RED + "Please provide an argument such as \"player\" or \"all\".");
			else if (!(args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("all")))
				sender.sendMessage(ChatColor.RED + "That arg is not correct, please define either \"player\" or \"all\".");
			else if (args[1].equalsIgnoreCase("player"))
				banCommand.banPlayer(sender, args);
			else if (args[1].equalsIgnoreCase("all"))
				banCommand.banAllAlts(sender, args);
		}
		
		// Handles unbanning a player
		else if(args[0].equalsIgnoreCase("unban")){
			if (args.length <= 1)
				sender.sendMessage(ChatColor.RED + "Please provide an argument such as \"player\" or \"all\".");
			else if (!(args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("all")))
				sender.sendMessage(ChatColor.RED + "That arg is not correct, please define either \"player\" or \"all\".");
			
		}
		
		// Handles authenticating the client with the server again	
		else if(args[0].equalsIgnoreCase("reauthenticate"))
			scc.authenticateServer(sender);
		return true;
	}
}
