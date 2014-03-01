package com.sandislandserv.rourke750;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sandislandserv.rourke750.Commands.CommandManager;
import com.sandislandserv.rourke750.Information.AssociationInformation;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.Listener.Login;
import com.sandislandserv.rourke750.Listener.PrisonPearlListener;
import com.sandislandserv.rourke750.database.BaseValues;

public class BetterAssociations extends JavaPlugin{

	private Login log;
	private static BaseValues database;
	private static FileConfiguration config;
	
	public void onEnable(){
		ConfigManager con = new ConfigManager();
		config = this.getConfig();
		con.initconfig(config);
		saveConfig();
		database = new BaseValues(this.getConfig(), this);
		SendInformation si = new SendInformation(database, config);
		final BetterAssociations plugin = this;
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable(){
			@Override
			public void run(){
		new AssociationInformation(database, config, plugin);
			}
		});
		log = new Login(database, si, this);		
		enableListener();
		Command();
	}
	
	public void onDisable(){
		
	}
	
	private void enableListener() {
		getServer().getPluginManager().registerEvents(log, this);
		getServer().getPluginManager().registerEvents(new PrisonPearlListener(database), this);
	}
	
	public static void addPlayerIp(Player player){ // used to add a player's ip
		database.addPlayerIp(player);
	}
	
	public static void associatePlayers(Player player){ // used to associate players
		database.associatePlayer(player);
	}
	public void Command(){
		CommandManager com = new CommandManager(database);
		for (String command : getDescription().getCommands().keySet()) {
            getCommand(command).setExecutor(com);
        }
	}
	public static BaseValues getDataBaseManager(){
		return database;
	}
}
