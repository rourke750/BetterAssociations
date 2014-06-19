package com.sandislandserv.rourke750;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	
	public void initconfig(FileConfiguration config){
		config.options().header("Author Rourke750" +
				"Sql information is self explanitory.");
		if (!config.contains("sql.hostname")) config.set("sql.hostname", "localhost");
		
		if (!config.contains("sql.port")) config.set("sql.port", 3306);
		
		if (!config.contains("sql.dbname")) config.set("sql.dbname", "BetterAssociation");
		
		if (!config.contains("sql.username")) config.set("sql.username", "");
		
		if (!config.contains("sql.password")) config.set("sql.password", "");
		
		if (!config.contains("secret")) config.set("secret", "");
		
		//if (!config.contains("send_data")) config.set("send_data", false);
		
		//if (!config.contains("gui_options.username")) config.set("gui_options.username", "\n\r");
		
		//if (!config.contains("gui_options.password")) config.set("gui_options.password", "\n\r");
		
		//if (!config.contains("gui_options.port")) config.set("gui_options.port", 0+"\n\r");
		
		//if (!config.contains("banmanager.set.altlimitreason"))  config.set("banmanager.set.altlimitreason", "\n\r");
		
		//if (!config.contains("banmanager.set.altlimit")) config.set("banmanager.set.altlimit", 0+"\n\r");
	}
}
