package com.sandislandserv.rourke750;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import net.minecraft.server.v1_7_R3.MinecraftEncryption;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PacketLoginInEncryptionBegin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.sandislandserv.rourke750.Commands.CommandManager;
import com.sandislandserv.rourke750.Information.SendInformation;
import com.sandislandserv.rourke750.Listener.LoginManager;
import com.sandislandserv.rourke750.Listener.PrisonPearlListener;
import com.sandislandserv.rourke750.Listener.TimeManager;
import com.sandislandserv.rourke750.database.BaseValues;

public class BetterAssociations extends JavaPlugin implements Listener{

	private TimeManager time;
	private LoginManager log;
	private static BaseValues database;
	private static FileConfiguration config;
	private static KeyPair pair;
	private static PublicKey serverkey;

	public void onEnable() {
		registerProticalPackets();
		try {
			ExractFile(); // extract needed file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // extract needed files
		
		ConfigManager con = new ConfigManager();
		config = this.getConfig();
		con.initconfig(config);
		saveConfig();
		database = new BaseValues(this.getConfig(), this);
		SendInformation si = new SendInformation(database, config);
		log = new LoginManager(database, this, null); // replace null with si
		time = new TimeManager(this, database);
		

		// Currently no plan to implement GUI functionality
		//final BetterAssociations plugin = this;
		//Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
	//		@Override
	//		public void run() {
	//			new AssociationInformation(database, config, plugin);
	//		}
	//	});
		
		// ====================================
		
		enableListener();
		Command();
		time.load(); // if someone reloads the server reinitiats all player times.
	}

	public void onDisable() {
		saveAllPlayerTimes();
	}

	public void saveAllPlayerTimes() {
		for (Player player : Bukkit.getOnlinePlayers())
			time.save(player);
	}

	private void enableListener() {
		getServer().getPluginManager().registerEvents(log, this);
		if (Bukkit.getPluginManager().getPlugin("PrisonPearl") != null)
			getServer().getPluginManager().registerEvents(
					new PrisonPearlListener(database), this);
		getServer().getPluginManager().registerEvents(time, this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	public static void addPlayerIp(Player player) { // used to add a player's ip
		database.addPlayerIp(player);
	}

	public static void associatePlayers(Player player) { // used to associate
															// players
		database.associatePlayer(player);
	}

	public void Command() {
		CommandManager com = new CommandManager(database, this);
		for (String command : getDescription().getCommands().keySet()) {
			getCommand(command).setExecutor(com);
		}
	}

	public static BaseValues getDataBaseManager() {
		return database;
	}
	
	public void registerProticalPackets(){
		System.out.println("ProticalRegistered");
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Login.Server.ENCRYPTION_BEGIN){
	    	@Override
	    	public void onPacketSending(PacketEvent e){
	    		try{
	    			System.out.println("Protical method ran");
	    			PacketContainer con = e.getPacket();
	    			PacketLoginInEncryptionBegin packet = (PacketLoginInEncryptionBegin) con.getHandle();
	    			MinecraftServer server = MinecraftServer.getServer();
	    			Field a;
	    			try {
	    				a = PacketLoginInEncryptionBegin.class.getDeclaredField("a");
	    				a.setAccessible(true);
	    				
	    				KeyPair pair = server.K();
	    				SecretKey secret = packet.a(pair.getPrivate());
	    				
	    				String aa = (String) a.get(packet);
	    				String s = new BigInteger(MinecraftEncryption.a(aa, pair.getPublic(), secret)).toString(16);
	    				System.out.println(s);
	    				Player player = e.getPlayer();
	    			} catch (SecurityException ee) {
	    				// TODO Auto-generated catch block
	    				ee.printStackTrace();
	    			} catch (NoSuchFieldException ee) {
	    				// TODO Auto-generated catch block
	    				ee.printStackTrace();
	    			} catch (IllegalArgumentException ee) {
	    				// TODO Auto-generated catch block
	    				ee.printStackTrace();
	    			} catch (IllegalAccessException ee) {
	    				// TODO Auto-generated catch block
	    				ee.printStackTrace();
	    			}
	    		}catch (FieldAccessException exception){ //Should catch if the packet is the wrong type
	    			exception.printStackTrace();
	    		}
	    	}
		});
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
		System.setProperty("javax.net.ssl.trustStore", this.getDataFolder().toString() + File.separator+"myTrustStore"); // set cert
	}
}
