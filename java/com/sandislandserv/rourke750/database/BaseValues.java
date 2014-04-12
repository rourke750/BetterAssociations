package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private PreparedStatement addPlayer;
    private PreparedStatement getUUIDfromPlayer;
    private PreparedStatement getPlayerfromUUID;
    private PreparedStatement getAllPlayers;
    private PreparedStatement updateAssociate;
    private PreparedStatement getPlayerfromId;
    private PreparedStatement manualAddAlt;
    private PreparedStatement insertTime;
    private PreparedStatement addTime;
    private PreparedStatement getAmountOfAlts;
    private PreparedStatement getAmountofTimePlayed;
    private PreparedStatement getPlayerBanReason;
    
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
				+ "`main_account_id` int(40) NOT NULL,"
				+ "`alt_id` int(40) NOT NULL,"
				+ "`valid` BOOL DEFAULT 1,"
				+ "UNIQUE KEY `associations_main_sub` (`main_account_id`, `alt_id`));");
		db.execute("CREATE TABLE IF NOT EXISTS `ips` (" +
				"`id` int(40) NOT NULL," +
				"`ip` varchar(40) NOT NULL," +
				"UNIQUE KEY `id_ip_comb` (`id`, `ip`));");
		db.execute("CREATE TABLE IF NOT EXISTS `player` (" +
				"`id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
				"`uuid` varchar(40) NOT NULL," +
				"`player` varchar(40) NOT NULL," +
				"PRIMARY KEY(`id`), "
				+ "UNIQUE KEY `uuid_player_combo` (`uuid`, `player`));");
		db.execute("CREATE TABLE IF NOT EXISTS `ban` (" +
				"`id` int(10) NOT NULL," +
				"`reason` varchar(40) NOT NULL);");
		db.execute("CREATE TABLE IF NOT EXISTS `time` ("
				+ "`id` int(10) NOT NULL,"
				+ "`amount` bigint(10) DEFAULT 0,"
				+ "PRIMARY KEY(`id`));");
		db.execute("CREATE TABLE IF NOT EXISTS `play_times` (" +
				"`id` int(10) NOT NULL," +
				"`login` varchar(40) NOT NULL," +
				"`logout` varchar(40) NOT NULL);");
		db.execute("CREATE TABLE IF NOT EXISTS `notes` ("
				+ "`id` int(10) NOT NULL,"
				+ "`info` varchar(40) NOT NULL);");
	}
	
	public void initializeStatements(){
		addAlt = db.prepareStatement(String.format("INSERT IGNORE INTO %s "
				+ "(main_account_id, alt_id) SELECT "
				+ "?, a.id "
				+ "FROM (SELECT LinkedByIpAssociations.id FROM ips as TargetIps inner join ips as LinkedByIpAssociations "
				+ "on LinkedByIpAssociations.ip = TargetIps.ip "
				+ "where TargetIps.ip = ?) a where "
				+ "a.id <> ? union select a.id, ? from ( select LinkedByIpAssociations.id from "
				+ "ips as TargetIps "
				+ "inner join ips as LinkedByIpAssociations "
				+ "on LinkedByIpAssociations.ip = TargetIps.ip "
				+ "where TargetIps.ip = ? ) a "
				+ "where a.id <> ? "
				+ "union " 
				+ "select ?, a.alt_id from ("
				+ "select ass.alt_id from (select LinkedByIpAssociations.id from "
				+ "ips as TargetIps inner join ips as LinkedByIpAssociations "
				+ "on LinkedByIpAssociations.ip = TargetIps.ip "
				+ "where TargetIps.ip = ?) c inner join associations ass "
				+ "on ass.main_account_id = c.id and valid = 1) a where a.alt_id <> ? "
				+ "union "
				+ "select a.alt_id, ? from ("
				+ "select ass.alt_id from (select LinkedByIpAssociations.id from "
				+ "ips as TargetIps inner join ips as LinkedByIpAssociations "
				+ "on LinkedByIpAssociations.ip = TargetIps.ip "
				+ "where TargetIps.ip = ?) c inner join associations ass "
				+ "on ass.main_account_id = c.id and valid = 1) a where a.alt_id <> ? "
				+ ";", "associations"));
		manualAddAlt = db.prepareStatement(String.format("INSERT IGNORE INTO %s (main_account_id, alt_id) "
				+ "SELECT main.id, alt.id "
				+ "FROM player main, player alt "
				+ "WHERE main.player=? AND alt.player=? "
				+ "UNION "
				+ "SELECT alt.id, main.id "
				+ "FROM player main, player alt "
				+ "WHERE main.player=? AND alt.uuid=?", "associations"));
		addIp = db.prepareStatement(String.format("INSERT IGNORE INTO %s "
				+ "(id, ip) SELECT "
				+ "p.id, ? FROM player p WHERE p.uuid=?", "ips"));
		getAlts = db.prepareStatement(String.format("SELECT p.player, p.uuid "
				+ "FROM %s ass "
				+ "inner join player p "
				+ "on p.id = ass.alt_id "
				+ "WHERE "
				+ "ass.main_account_id=("
				+ "SELECT pp.id FROM player pp WHERE pp.player=?"
				+ ")"
				+ " AND valid=1 "
				+ "order by p.player asc ", "associations")); // remember rourke to fix this to get the id in the statement
		getUUIDfromIP = db.prepareStatement(String.format("SELECT id FROM %s "
				+ "WHERE ip=?", "ips"));
		getIpfromUUID = db.prepareStatement(String.format("SELECT ip from %s "
				+ "WHERE id=?", "ips"));
		addPlayer = db.prepareStatement(String.format("INSERT IGNORE INTO %s " +
				"(uuid, player) " +
				"VALUES(?, ?)", "player"));
		getUUIDfromPlayer = db.prepareStatement(String.format("SELECT uuid, id FROM %s " +
				"WHERE player=?", "player"));
		getPlayerfromUUID = db.prepareStatement(String.format("SELECT player FROM %s " +
				"WHERE uuid=?", "player"));
		getPlayerfromId = db.prepareStatement(String.format("SELECT player FROM %s "
				+ "WHERE id=?", "player"));
		getAllPlayers = db.prepareStatement(String.format("SELECT player, uuid from %s ", "player"));
		updateAssociate = db.prepareStatement(String.format("UPDATE %s SET valid=? WHERE "
				+ "main_account_id=? AND alt_id=?", "associations"));
		insertTime = db.prepareStatement(String.format("INSERT IGNORE INTO %s " +
				"(id) SELECT " +
				"id FROM player " +
				"WHERE uuid=?", "time"));
		addTime = db.prepareStatement(String.format("UPDATE %s SET amount = amount + ? WHERE id = (" +
				"SELECT p.id FROM player p WHERE p.uuid=?)", "time"));
		getAmountOfAlts = db.prepareStatement(String.format("SELECT COUNT(*) AS count FROM %s "
				+ "WHERE main_account_id="
				+ "(SELECT p.id FROM player p "
				+ "WHERE p.uuid=?)", "associations"));
		getAmountofTimePlayed = db.prepareStatement("SELECT t.amount FROM time t "
				+ "WHERE t.id="
				+ "(SELECT p.id FROM player p "
				+ "WHERE p.player=?)");
		getPlayerBanReason = db.prepareStatement("SELECT reason FROM ban"
				+ "WHERE id=("
				+ "SELECT p.id FROM player p "
				+ "WHERE uuid=?)");
	}
	
	
	public void addPlayerIp(Player player){
		if (!db.isConnected()) db.connect(); // reconnects database
		String ip = player.getAddress().getAddress().getHostAddress();
		String uuid = player.getUniqueId().toString();
		try {
				addIp.setString(1, ip);
				addIp.setString(2, uuid);
				addIp.execute();
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
			addPlayer.setString(1, uuid);
			addPlayer.setString(2, name);
			addPlayer.execute();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void associatePlayer(Player player){
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		try { // this code block runs through all the same ips and tries to associate that way
			int playerid = getId(player.getName());
			String ip = player.getAddress().getAddress().getHostAddress();
			addAlt.setInt(1, playerid);
			addAlt.setString(2, ip);
			addAlt.setInt(3, playerid);
			addAlt.setInt(4, playerid);
			addAlt.setString(5, ip);
			addAlt.setInt(6, playerid);
			addAlt.setInt(7, playerid);
			addAlt.setString(8, ip);
			addAlt.setInt(9, playerid);
			addAlt.setInt(10, playerid);
			addAlt.setString(11, ip);
			addAlt.setInt(12, playerid);
			addAlt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void associatePlayer(String main, String alt){ // associates 2 players together
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		
		try {
			manualAddAlt.setString(1, main);
			manualAddAlt.setString(2, alt);
			manualAddAlt.setString(3, main);
			manualAddAlt.setString(4, alt);
			manualAddAlt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getAltsList(String player){
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		List<String> list = new ArrayList<String>();
		try {
			getAlts.setString(1, player);
			ResultSet set = getAlts.executeQuery();
			while (set.next()){
				String alt = set.getString("player");
				list.add(alt);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
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
		if (!db.isConnected()) reconnectAndSetPreparedStatements(); // reconnects database
		int id = getId(player);
		int altid = getId(alt);
		try {
			updateAssociate.setInt(1, 0);
			updateAssociate.setInt(2, id);
			updateAssociate.setInt(3, altid);
			updateAssociate.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getId(String player){
		try {
			getUUIDfromPlayer.setString(1, player);
			ResultSet set = getUUIDfromPlayer.executeQuery();
			if(!set.next()) return -1;
			return set.getInt("id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1; // failed
	}
	
	private String getPlayer(int id) throws SQLException{
		getPlayerfromId.setInt(1, id);
		ResultSet set = getPlayerfromId.executeQuery();
		set.next();
		return set.getString("player");
	}
	
	private List<Integer> getNamesfromIp(String ip) throws SQLException{
		List<Integer> list = new ArrayList<Integer>();
		getUUIDfromIP.setString(1, ip);
		ResultSet set = getUUIDfromIP.executeQuery();
		while(set.next()){
			int id = set.getInt("id");
			list.add(id);
		}
		return list;
	}
	
	public String getPlayer(String uuid){
		try {
			getPlayerfromUUID.setString(1, uuid);
			ResultSet set = getPlayerfromUUID.executeQuery();
			if (!set.next() || set == null) return null;
			String player = set.getString("player");
			return player;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertIntialPlayerTime(Player player){
		try {
			insertTime.setString(1, player.getUniqueId().toString());
			insertTime.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTimetoPlayer(Player player, long time){
		try {
			addTime.setLong(1, time);
			addTime.setString(2, player.getUniqueId().toString());
			addTime.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long getPlayerTimeinSeconds(String name){
		try {
			getAmountofTimePlayed.setString(1, name);
			ResultSet set = getAmountofTimePlayed.executeQuery();
			return set.next() ? set.getLong("t.amount") : -1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getAmountOfAlts(Player player){
		String uuid = player.getUniqueId().toString();
		try {
			getAmountOfAlts.setString(1, uuid);
			ResultSet set = getAmountOfAlts.executeQuery();
			return set.next() ? set.getInt("count") : 0;
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public String isBanned(String uuid){
		try {
			getPlayerBanReason.setString(1, uuid);
			ResultSet set = getPlayerBanReason.executeQuery();
			return set.next() ? set.getString("reason") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}