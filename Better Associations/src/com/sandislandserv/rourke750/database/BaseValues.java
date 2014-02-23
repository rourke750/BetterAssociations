package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.BetterAssociations;


public class BaseValues {
	private final BetterAssociations plugin;
	private DataBase db;
    private final String host;
    private final String dbname;
    private final String username;
    private final int port;
    private final String password;
	
    private int lastId;
    
    private PreparedStatement addAlt;
    private PreparedStatement getAlts;
    private PreparedStatement getLastId;
    private PreparedStatement addIp;
    private PreparedStatement getIp;
    private PreparedStatement isIpThere;
    private PreparedStatement addPlayer;
    private PreparedStatement getUUIDfromPlayer;
    private PreparedStatement getPlayerfromUUID;
    private PreparedStatement getAllPlayers;
    
	public BaseValues(FileConfiguration config_, BetterAssociations plugin){
	this.plugin = plugin;
	host = config_.getString("sql.hostname");
	port = config_.getInt("sql.port");
	dbname = config_.getString("sql.dbname");
	username = config_.getString("sql.username");
	password = config_.getString("sql.password");
	db = new DataBase(host, port, dbname, username, password, plugin.getLogger());
	boolean connected = db.connect();
		if (connected){
			genTables();
			initializeStatements();
		}
	}
	
	public void genTables(){
		db.execute("CREATE TABLE IF NOT EXISTS `associations` ("
				+ "`id` int(10) unsigned NOT NULL AUTO_INCREMENT,"
				+ "`main_account_uuid` varchar(40) NOT NULL,"
				+ "`uuid` varchar(40) NOT NULL,"
				+ "`ip` varchar(40) NOT NULL,"
				+ "`valid` BOOL DEFAULT 1,"
				+ "PRIMARY KEY(`id`));");
		db.execute("CREATE TABLE IF NOT EXISTS `ips` (" +
				"`id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`uuid` varchar(40) NOT NULL," +
				"`ip` varchar(40) NOT NULL," +
				"PRIMARY KEY(`id`));");
		db.execute("CREATE TABLE IF NOT EXISTS `player` (" +
				"`id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`uuid` varchar(40) NOT NULL," +
				"`player` varchar(40) NOT NULL," +
				"PRIMARY KEY(`id`));");
	}
	
	public void initializeStatements(){
		addAlt = db.prepareStatement(String.format("INSERT INTO %s "
				+ "(main_account_uuid, uuid, ip) "
				+ "VALUES(?, ?, ?)", "associations"));
		addIp = db.prepareStatement(String.format("INSERT INTO %s "
				+ "(uuid, ip) "
				+ "VALUES(?, ?)", "ips"));
		getAlts = db.prepareStatement(String.format("SELECT uuid, ip FROM %s "
				+ "WHERE main_account_uuid=?", "associations"));
		getLastId = db.prepareStatement(String.format(
    			"SELECT LAST_INSERT_ID() AS id ", "player"));
		getIp = db.prepareStatement(String.format("SELECT uuid FROM %s "
				+ "WHERE ip=?", "ips"));
		isIpThere = db.prepareStatement(String.format("SELECT ip, uuid FROM %s" +
				" WHERE ip=? AND uuid=?", "ips"));
		addPlayer = db.prepareStatement(String.format("INSERT INTO %s " +
				"(uuid, player) " +
				"VALUES(?, ?)", "player"));
		getUUIDfromPlayer = db.prepareStatement(String.format("SELECT uuid FROM %s " +
				"WHERE player=?", "player"));
		getPlayerfromUUID = db.prepareStatement(String.format("SELECT player FROM %s " +
				"WHERE uuid=?", "player"));
		getAllPlayers = db.prepareStatement(String.format("SELECT player, uuid from %s ", "player"));
	}
	
	
	public void addPlayerIp(Player player){
		if (!db.isConnected()) db.connect(); // reconnects database
		String ip = player.getAddress().getAddress().getHostAddress();
		String uuid = player.getUniqueId().toString();
		try {
			isIpThere.setString(1, ip);
			isIpThere.setString(2, uuid);
			ResultSet ips = isIpThere.executeQuery();
			if (!ips.next() || ips == null){
				addIp.setString(1, uuid);
				addIp.setString(2, ip);
				addIp.execute();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addPlayerUUID(Player player){
		if (!db.isConnected()) db.connect(); // reconnects database
		String name = player.getName();
		String uuid = player.getUniqueId().toString();
		try{
			getUUIDfromPlayer.setString(1, name);
			ResultSet data = getUUIDfromPlayer.executeQuery();
			if (data == null){
				addPlayer.setString(1, uuid);
				addPlayer.setString(2, name);
				addPlayer.execute();
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void associatePlayer(Player player){
		if (!db.isConnected()) db.connect(); // reconnects database
		List<String> players = new ArrayList<String>();
		List<String> addedAlts = new ArrayList<String>();
		String ip = player.getAddress().getAddress().getHostAddress();
		String uuid = player.getUniqueId().toString();
		try { // this code block runs through all the same ips and tries to associate that way
			getIp.setString(1, ip);
			ResultSet ips = getIp.executeQuery();
			while (ips.next()){
				String ipp = ips.getString("uuid");
				players.add(ipp); // adds all similar ips to a list
			}
			getAlts.setString(1, uuid);
			ResultSet alts = getAlts.executeQuery();
			while(alts.next()){
				String account = alts.getString("uuid");
				addedAlts.add(account);
			}
			for(int x = 0; x < players.size(); x++){
				String y = players.get(x);
				if(!addedAlts.contains(y)){ // if an account with a similar ip is not on their alt list
					addAlt.setString(1, uuid);
					addAlt.setString(2, y);
					addAlt.setString(3, ip);
					addAlt.execute();
					addedAlts.add(y);
				}
				subCatagoryAssociation(uuid, y, players); // adds this players alts to this account's alts
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
/*	
	public void associatePlayer(String main, String alt){
		if (!db.isConnected()) db.connect(); // reconnects database
		getUUIDfromPlayer.setString(1, main);
	}
	*/
	
	public void subCatagoryAssociation(String uuid1, String uuid2, List<String> players){
		if (!db.isConnected()) db.connect(); // reconnects database
		List<String> addedAlts = new ArrayList<String>();
		List<String> addedIps = new ArrayList<String>();
		try {
			getAlts.setString(1, uuid2);
			ResultSet alts = getAlts.executeQuery();
			while(alts.next()){ // adds the alt's alts to their alts.
				String account = alts.getString("uuid");
				String ip = alts.getString("ip");
				addedAlts.add(account);
				addedIps.add(ip);
			}
			for(int x = 0; x < addedAlts.size(); x++){
				String y = addedAlts.get(x);
				String z = addedIps.get(x);
				if(!players.contains(y)){
					addAlt.setString(1, uuid1);
					addAlt.setString(2, y);
					addAlt.setString(3, z);
					addAlt.execute();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getLastId(){
		if (!db.isConnected()) db.connect(); // reconnects database
		try {
			if (getLastId.execute()) {
				  ResultSet rsKey = getLastId.getResultSet();
				  if (rsKey.next()) {
					  lastId = rsKey.getInt("id");
					  rsKey.close();
					  return lastId;
				  }
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	  return lastId;
	}
	
	/*
	 * this entire thing is stupid,
	 * hopefully better support in the future
	 * to easily allow conversion from player to uuid
	 * and uuid to player
	 */
	public List<String> getAltsList(String player){
		if (!db.isConnected()) db.connect(); // reconnects database
		try {
			getUUIDfromPlayer.setString(1, player); // get the uuid from the database. Can't use bukkit because possible offline
			ResultSet name = getUUIDfromPlayer.executeQuery();
			if (!name.next() || name == null) return null; // if name isnt isn't in player-uuid database. Should be fine for all new databases
			String uuid = name.getString("uuid"); // gets the uuid
			getAlts.setString(1, uuid); // gets the players alts from the uuid
			ResultSet altuuids = getAlts.executeQuery();
			if (altuuids == null) return null; // if no alts return null
			List<String> uuidaltlist = new ArrayList<String>(); 
			// creates list of the uuids found in the database
			while (altuuids.next()){
				String alt = altuuids.getString("uuid");
				uuidaltlist.add(alt);
			}
			List<String> altlist = new ArrayList<String>();
			for (String uuidalt: uuidaltlist){
				getPlayerfromUUID.setString(1, uuidalt);
				ResultSet playername = getPlayerfromUUID.executeQuery();
			    while (playername.next()){
			    	altlist.add(playername.getString("player"));
			    }
			}
			return altlist; // return the alts list
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAllPlayers(){
		 StringBuilder string = new StringBuilder();
		 try {
			ResultSet data = getAllPlayers.executeQuery();
			while(data.next()){
				String player = data.getString("player");
				String uuid = data.getString("uuid");
				string.append(player + " " + uuid + " ");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return string.toString();
	}
	
}
