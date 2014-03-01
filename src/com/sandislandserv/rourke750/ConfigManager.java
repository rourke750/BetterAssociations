package com.sandislandserv.rourke750;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	
	public void initconfig(FileConfiguration config){
		config.options().header("Author Rourke750\r\n" +
				"Sql information is self explanitory.\r\n"
				+ "You must sign up on the website: BetterAssociations.com to be able to recieve alt information"
				+ " from players from the main server.\r\n"
				+ "The send_data config option sends data back to BetterAssociation servers to help create a large account network."
				+ "  If you choose to set this to false players that log in will not get alt association from the main network.\r\n");
		if (!config.contains("sql.hostname")) config.set("sql.hostname", "localhost");
		
		if (!config.contains("sql.port")) config.set("sql.port", 3306);
		
		if (!config.contains("sql.dbname")) config.set("sql.dbname", "BetterAssociation");
		
		if (!config.contains("sql.username")) config.set("sql.username", "");
		
		if (!config.contains("sql.password")) config.set("sql.password", "");
		
		if (!config.contains("send_data")) config.set("send_data", true);
		
		if (!config.contains("gui_options.username")) config.set("gui_options.username", "");
		
		if (!config.contains("gui_options.password")) config.set("gui_options.password", "");
		
		if (!config.contains("gui_options.port")) config.set("gui_options.port", "25549");
	}
}
