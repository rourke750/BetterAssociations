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
		
		if (!config.contains("banmanager.set.altlimitreason"))  config.set("banmanager.set.altlimitreason", "Too many associated accounts"
				+ " logged on.\nPlease contact this server's administration.");
		
		if (!config.contains("banmanager.set.altlimit")) config.set("banmanager.set.altlimit", 0);
		
		if (!config.contains("banmanager.set.defaultbanreason")) config.set("banmanager.set.defaultbanreason", "Please contact the admins"
				+ " because you have been banned.");
	}
}
