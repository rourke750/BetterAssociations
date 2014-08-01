package com.sandislandserv.rourke750;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sandislandserv.rourke750.Commands.CommandManager;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.Listener.BanListener;
import com.sandislandserv.rourke750.Listener.LoginManager;
import com.sandislandserv.rourke750.Listener.PrisonPearlListener;
import com.sandislandserv.rourke750.Listener.TimeListener;
import com.sandislandserv.rourke750.Misc.ClassHandler;
import com.sandislandserv.rourke750.database.BaseValues;

public class BetterAssociations extends JavaPlugin{

	private TimeListener time;
	private LoginManager log;
	private static BaseValues database;
	private static FileConfiguration config;
	private SendInformation si;
	
	public void onEnable() {
		try {
			ExractFile(); // extract needed file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		ConfigManager con = new ConfigManager();
		config = this.getConfig();
		con.initconfig(config);
		saveConfig();
		database = new BaseValues(this.getConfig(), this);
		si = new SendInformation(this, database, config);
		log = new LoginManager(database, this, si);
		time = new TimeListener(this, database);
		if (!ClassHandler.Initialize(getServer())){
			Bukkit.getLogger().log(Level.SEVERE, "BetterAssociations has disabled itself, you are not running a "
					+ "viable version of bukkit.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		enableListener();
		Command(); // Initiates the command class.
	}

	public void onDisable() {
	}

	private void enableListener() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(log, this); // registers the LoginListener Class
		if (Bukkit.getPluginManager().getPlugin("PrisonPearl") != null) // PrisonPearl Detected
			pm.registerEvents(
					new PrisonPearlListener(database), this); // Register the PrisonPearlListener Class
		pm.registerEvents(time, this); // Register the TimeManager Class
		pm.registerEvents(new BanListener(this), this); // Register the BanListener Class
	}

	public void Command() {
		CommandManager com = new CommandManager(database, this, si);
		for (String command : getDescription().getCommands().keySet()) {
			getCommand(command).setExecutor(com);
		}
	}

	public static BaseValues getManagerHandler() {
		return database;
	}
	
	public void ExractFile() throws IOException{
		File file = new File(this.getDataFolder() + File.separator + "myTrustStore");
		if (!file.exists()){
			InputStream in = BetterAssociations.class.getResourceAsStream("myTrustStore");
			OutputStream out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		}
	}
	
	public static FileConfiguration getConfigFile(){
		return config;
	}
}
