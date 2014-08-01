package com.sandislandserv.rourke750.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Information.SendInformation;

public class ServerCommunicationsCommand {

	private SendInformation si;
	
	public ServerCommunicationsCommand(SendInformation si){
		this.si = si;
	}
	
	public void authenticateServer(CommandSender sender){
		si.authenticateServer();
		sender.sendMessage(ChatColor.RED + "Sent authentication request again.  Check console for authentication message.");
	}
}
