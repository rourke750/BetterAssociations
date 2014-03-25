package com.sandislandserv.rourke750;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sandislandserv.rourke750.Commands.CommandManager;
import com.sandislandserv.rourke750.Encryption.Encrypt;
import com.sandislandserv.rourke750.Encryption.EncryptionLoad;
import com.sandislandserv.rourke750.Encryption.KeyGen;
import com.sandislandserv.rourke750.Information.AssociationInformation;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.Listener.LoginManager;
import com.sandislandserv.rourke750.Listener.PrisonPearlListener;
import com.sandislandserv.rourke750.Listener.TimeManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class BetterAssociations extends JavaPlugin{

	private TimeManager time;
	private LoginManager log;
	private static BaseValues database;
	private static FileConfiguration config;
	private static KeyPair pair;
	private static PublicKey serverkey;
	
	public void onEnable(){
		try {
			encryption();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConfigManager con = new ConfigManager();
		config = this.getConfig();
		con.initconfig(config);
		saveConfig();
		database = new BaseValues(this.getConfig(), this);
		SendInformation si = new SendInformation(database, config);
		time = new TimeManager(database);
		final BetterAssociations plugin = this;
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable(){
			@Override
			public void run(){
		new AssociationInformation(database, config, plugin);
			}
		});
		new LoginManager(database, this);		
		enableListener();
		Command();
		
	}
	
	public void onDisable(){
		
	}
	
	private void enableListener() {
		getServer().getPluginManager().registerEvents(log, this);
		if (Bukkit.getPluginManager().getPlugin("PrisonPearl") != null)
		getServer().getPluginManager().registerEvents(new PrisonPearlListener(database), this);
		getServer().getPluginManager().registerEvents(time, this);
	}
	
	public static void addPlayerIp(Player player){ // used to add a player's ip
		database.addPlayerIp(player);
	}
	
	public static void associatePlayers(Player player){ // used to associate players
		database.associatePlayer(player);
	}
	public void Command(){
		CommandManager com = new CommandManager(database, this);
		for (String command : getDescription().getCommands().keySet()) {
            getCommand(command).setExecutor(com);
        }
	}
	public static BaseValues getDataBaseManager(){
		return database;
	}
	public void encryption() throws Exception{
		String dir = this.getDataFolder() + File.separator + "Encryption" + File.separator;
		File publickey = new File(dir + "public.key");
		File privatekey = new File(dir + "private.key");
		File directory = new File(dir);
		File serverkey = new File(dir +"server.key");
		if (!directory.exists()) directory.mkdir();
		if (!publickey.exists() || !privatekey.exists() || serverkey.exists()){
			KeyPair pair = KeyGen.generate(1024);
			EncryptionLoad.save(directory, pair);
		}
			pair = EncryptionLoad.load(directory);
	}
	public static KeyPair getKeyPair(){
		return pair;
	}
	public static PublicKey getServerKey(){
		return serverkey;
	}
}
