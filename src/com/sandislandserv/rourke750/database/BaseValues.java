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
    private PreparedStatement getUUIDfromIP;
    private PreparedStatement getIpfromUUID;
    private PreparedStatement isIpThere;
    private PreparedStatement addPlayer;
    private PreparedStatement getUUIDfromPlayer;
    private PreparedStatement getPlayerfromUUID;
    private PreparedStatement getAllPlayers;
    private PreparedStatement updateAssociate;
    
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
				+ "(main_account_uuid, uuid) "
				+ "VALUES(?, ?)", "associations"));
		addIp = db.prepareStatement(String.format("INSERT INTO %s "
				+ "(uuid, ip) "
				+ "VALUES(?, ?)", "ips"));
		getAlts = db.prepareStatement(String.format("SELECT uuid, valid FROM %s "
				+ "WHERE main_account_uuid=?", "associations"));
		getLastId = db.prepareStatement(String.format(
    			"SELECT LAST_INSERT_ID() AS id ", "player"));
		getUUIDfromIP = db.prepareStatement(String.format("SELECT uuid FROM %s "
				+ "WHERE ip=?", "ips"));
		getIpfromUUID = db.prepareStatement(String.format("SELECT ip from %s "
				+ "WHERE uuid=?", "ips"));
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
		updateAssociate = db.prepareStatement(String.format("UPDATE %s SET valid=? WHERE"
				+ "main_account_uuid=? AND uuid=?", "associations"));
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
	
	public void reconnectAndSetPreparedStatements() {
        db.connect();
        initializeStatements();
	}
	
	public void addPlayerUUID(Player player){
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		String name = player.getName();
		String uuid = player.getUniqueId().toString();
		try{
			getUUIDfromPlayer.setString(1, name);
			ResultSet data = getUUIDfromPlayer.executeQuery();
			if (data == null || !data.next()){
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
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		List<String> altassociations = new ArrayList<String>();
		String ip = player.getAddress().getAddress().getHostAddress();
		String uuid = player.getUniqueId().toString();
		try { // this code block runs through all the same ips and tries to associate that way
			getUUIDfromIP.setString(1, ip); // gets similar ips for the player
			ResultSet ipset = getUUIDfromIP.executeQuery();
			getAlts.setString(1, uuid);
			ResultSet altset = getAlts.executeQuery();
			while (altset.next()){
				String alt = altset.getString("uuid");
				altassociations.add(alt);
				subCatagoryAssociation(uuid, alt);
			}
			while (ipset.next()){
				String alt = ipset.getString("uuid");
				if (!altassociations.contains(alt)){
					addAlt.setString(1, uuid);
					addAlt.setString(2, alt);
					addAlt.execute();
					subCatagoryAssociation(uuid, alt);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void associatePlayer(String main, String alt){ // associates 2 players together
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		try {
			getUUIDfromPlayer.setString(1, main);
			ResultSet mainUuidset = getUUIDfromPlayer.executeQuery();
			getUUIDfromPlayer.setString(1, alt);
			ResultSet altUuidset = getUUIDfromPlayer.executeQuery();
			String mainUuid = null, altUuid = null;
			while(mainUuidset.next()) mainUuid = mainUuidset.getString("uuid");
			while(altUuidset.next()) altUuid = altUuidset.getString("uuid");
			addAlt.setString(1, mainUuid);
			addAlt.setString(2, altUuid);
			addAlt.execute();
			updateAssociate.setBoolean(1, true);
			updateAssociate.setString(2, mainUuid);
			updateAssociate.setString(3, altUuid);
			updateAssociate.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void subCatagoryAssociation(String uuid1, String uuid2){
		List<String> addedAlts = new ArrayList<String>();
		try{
			getAlts.setString(1, uuid1);
			ResultSet altset = getAlts.executeQuery();
			while(altset.next()){
				addedAlts.add(altset.getString("uuid"));
			}
			getAlts.setString(1, uuid2);
			ResultSet newaltset = getAlts.executeQuery();
			while(newaltset.next()){
				String newAlt = newaltset.getString("uuid");
				if (!addedAlts.contains(newAlt)){
					addAlt.setString(1, uuid1);
					addAlt.setString(2, newAlt);
					addAlt.execute();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getLastId(){
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
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
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
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
				boolean removed = altuuids.getBoolean("valid");
				if (!removed) continue;
				uuidaltlist.add(alt);
			}
			List<String> altlist = new ArrayList<String>();
			for (String uuidalt: uuidaltlist){
				getPlayerfromUUID.setString(1, uuidalt);
				ResultSet playername = getPlayerfromUUID.executeQuery();
			    while (playername.next()){
			    	String add = playername.getString("player");
			    	altlist.add(add);
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
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
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
	
	public void disAssociateAltfromPlayer(String player, String alt){
		try {
			getUUIDfromPlayer.setString(1, player);
			ResultSet uuidSet = getUUIDfromPlayer.executeQuery();
			getUUIDfromPlayer.setString(1, alt);
			ResultSet altuuidSet = getUUIDfromPlayer.executeQuery();
			uuidSet.next();
			altuuidSet.next();
			String uuidMain = uuidSet.getString("uuid");
			String uuidAlt = altuuidSet.getString("uuid");
			updateAssociate.setBoolean(1, false);
			updateAssociate.setString(2, uuidMain);
			updateAssociate.setString(3, uuidAlt);
			updateAssociate.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
