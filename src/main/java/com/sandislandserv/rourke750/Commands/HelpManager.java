package com.sandislandserv.rourke750.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpManager {

	public void getCommandInfo(CommandSender sender){
		ChatColor color = ChatColor.YELLOW;
		String info = color + "BetterAssociations Help Page.\n"
				+ "/ba alts <player>: Returns the alts of a player.\n"
				+ "/ba dassociate <main player> <args>: Disassociates players to \"main player\" where args is  multiple player names.\n"
				+ "/ba associate <main player> <args>: Associates players to \"main player\" where args is multiple player names.\n"
				+ "/ba ban <arg> <player>: Bans a player, arg should be repalced with either \"player\" or \"all\" to ban"
				+ " either a player or all its alts.\n"
				+ "/ba unban <arg> <player>: Unbans a player, arg should be replaced with either \"player\" or \"all\" to unban "
				+ "either a player or all its alts.\n"
				+ "/ba reauthenticate: Sends a reauthentication to the server, also reloads the config from server.\n";
		sender.sendMessage(info);
	}
}
