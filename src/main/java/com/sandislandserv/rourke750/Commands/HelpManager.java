package com.sandislandserv.rourke750.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpManager {

	public boolean getCommandInfo(CommandSender sender){
		ChatColor color = ChatColor.YELLOW;
		String info = color + "BetterAssociations Help Page.\n"
				+ "/ba alts <player>: Returns the alts of a player.\n"
				+ "/ba dassociate <main player> <args>: Disassociates players to \"main player\" where args is  multiple player names.\n"
				+ "/ba associate <main player> <args>: Associates players to \"main player\" where args is multiple player names.\n";
		sender.sendMessage(info);
		return true;
	}
}
