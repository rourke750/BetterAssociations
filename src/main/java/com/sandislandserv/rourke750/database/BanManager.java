package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BanManager {
	
	private PreparedStatement getPlayerBanReason;
	private PreparedStatement banPlayer;
	
	private BaseValues db;
	public BanManager(BaseValues bv){
		db = bv;
	}
	
	public void initializeStatements(){
		getPlayerBanReason = db.prepareStatement("SELECT reason FROM ban "
				+ "WHERE id=("
				+ "SELECT p.id FROM player p "
				+ "WHERE p.uuid=?)");
		banPlayer = db.prepareStatement("insert into into ban(id, reason) "
				+ "select id, ? from player where uuid = ?");
	}
	
	public String isBanned(String uuid){
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			getPlayerBanReason.setString(1, uuid);
			ResultSet set = getPlayerBanReason.executeQuery();
			return set.next() ? set.getString("reason") : "";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void banPlayer(String reason, String uuid){
		try {
			banPlayer.setString(1, reason);
			banPlayer.setString(2, uuid);
			banPlayer.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
