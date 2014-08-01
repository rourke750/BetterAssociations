package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BanManager {

	private PreparedStatement getPlayerBanReason;
	private PreparedStatement banPlayer;
	private PreparedStatement unbanPlayer;

	private BaseValues db;

	public BanManager(BaseValues bv) {
		db = bv;
	}

	public void initializeStatements() {
		getPlayerBanReason = db.prepareStatement("SELECT reason FROM ban "
				+ "WHERE id=(" + "SELECT p.id FROM player p "
				+ "WHERE p.uuid=?)");
		banPlayer = db.prepareStatement("insert ignore into ban(id, reason) "
				+ "select id, ? from player where uuid = ?");
		unbanPlayer = db
				.prepareStatement("delete from ban where id = (select id from player where uuid=?)");
	}

	public String isBanned(UUID uuid) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			getPlayerBanReason.setString(1, uuid.toString());
			ResultSet set = getPlayerBanReason.executeQuery();
			return set.next() ? set.getString("reason") : "";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void banPlayer(String reason, UUID uuid) {
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			banPlayer.setString(1, reason);
			banPlayer.setString(2, uuid.toString());
			banPlayer.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void unbanPlayer(UUID uuid) {
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			unbanPlayer.setString(1, uuid.toString());
			unbanPlayer.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void banPlayers(List<UUID> uuids, String reason) {
		if (!db.isConnected()) db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			for (UUID uuid : uuids) {
				banPlayer.setString(1, uuid.toString());
				banPlayer.setString(2, reason);
				banPlayer.addBatch();
			}
			banPlayer.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
