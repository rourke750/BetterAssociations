package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerManager {

    private PreparedStatement addIp;
    private PreparedStatement getUUIDfromIP;
    private PreparedStatement getIpfromUUID;
    private PreparedStatement addPlayer;
    private PreparedStatement getUUIDfromPlayer;
    private PreparedStatement getPlayerfromUUID;
    private PreparedStatement getAllPlayers;
    private PreparedStatement getPlayerfromId;
    
	private BaseValues db;
	public PlayerManager(BaseValues db){
		this.db = db;
		initializeStatements();
	}
	
	public void initializeStatements(){
		addIp = db.prepareStatement(String.format("INSERT IGNORE INTO %s "
				+ "(id, ip) SELECT "
				+ "p.id, ? FROM player p WHERE p.uuid=?", "ips"));
		
		getUUIDfromIP = db.prepareStatement(String.format("SELECT id FROM %s "
				+ "WHERE ip=?", "ips"));
		getIpfromUUID = db.prepareStatement(String.format("SELECT ip from %s "
				+ "WHERE id=?", "ips"));
		addPlayer = db.prepareStatement("call addplayertotable(?, ?)");
		getUUIDfromPlayer = db.prepareStatement(String.format("SELECT uuid, id FROM %s " +
				"WHERE player=?", "player"));
		getPlayerfromUUID = db.prepareStatement(String.format("SELECT player FROM %s " +
				"WHERE uuid=?", "player"));
		getPlayerfromId = db.prepareStatement(String.format("SELECT player FROM %s "
				+ "WHERE id=?", "player"));
		getAllPlayers = db.prepareStatement(String.format("SELECT player, uuid from %s ", "player"));
	}
	
	public int getId(String player){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
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
	
	public UUID getUUIDfromPlayerName(String name){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			getUUIDfromPlayer.setString(1, name);
			ResultSet set = getUUIDfromPlayer.executeQuery();
			if(!set.next()) return null;
			return UUID.fromString(set.getString("uuid"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; // failed
	}
	
	public String getPlayer(int id) throws SQLException{
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		getPlayerfromId.setInt(1, id);
		ResultSet set = getPlayerfromId.executeQuery();
		set.next();
		return set.getString("player");
	}
	
	public void addPlayerIp(UUID uuid, String ip){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
				addIp.setString(1, ip);
				addIp.setString(2, uuid.toString());
				addIp.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addPlayerUUID(String name, UUID uuid){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try{
			addPlayer.setString(1, name);
			addPlayer.setString(2, uuid.toString());
			addPlayer.execute();
			return true;
		}
		catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String getAllPlayers(){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
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
	
	public List<Integer> getNamesfromIp(String ip) throws SQLException{
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		List<Integer> list = new ArrayList<Integer>();
		getUUIDfromIP.setString(1, ip);
		ResultSet set = getUUIDfromIP.executeQuery();
		while(set.next()){
			int id = set.getInt("id");
			list.add(id);
		}
		return list;
	}
	
	public String getPlayer(UUID uuid){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			getPlayerfromUUID.setString(1, uuid.toString());
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
}
