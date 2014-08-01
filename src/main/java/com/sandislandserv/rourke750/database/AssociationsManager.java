package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class AssociationsManager {

	private PreparedStatement addAlt;
	private PreparedStatement getAlts;
	private PreparedStatement manualAddAlt;
	private PreparedStatement getAmountOfAlts;
	private PreparedStatement updateAssociate;

	private BaseValues db;
	private PlayerManager pm;

	public AssociationsManager(BaseValues bv, PlayerManager pm) {
		db = bv;
		this.pm = pm;
	}

	public void initializeStatements() {
		addAlt = db
				.prepareStatement(String
						.format("INSERT IGNORE INTO %s "
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
		manualAddAlt = db
				.prepareStatement(String.format(
						"INSERT IGNORE INTO %s (main_account_id, alt_id) "
								+ "SELECT main.id, alt.id "
								+ "FROM player main, player alt "
								+ "WHERE main.player=? AND alt.player=? "
								+ "UNION " + "SELECT alt.id, main.id "
								+ "FROM player main, player alt "
								+ "WHERE main.player=? AND alt.uuid=?",
						"associations"));
		getAlts = db.prepareStatement(String.format("SELECT p.player, p.uuid "
				+ "FROM %s ass " + "inner join player p "
				+ "on p.id = ass.alt_id " + "WHERE " + "ass.main_account_id=("
				+ "SELECT pp.id FROM player pp WHERE pp.player=?" + ")"
				+ " AND ass.valid=1 " + "order by p.player asc ",
				"associations"));
		getAmountOfAlts = db.prepareStatement(String.format(
				"SELECT COUNT(*) AS count FROM %s " + "WHERE main_account_id="
						+ "(SELECT p.id FROM player p "
						+ "WHERE p.uuid=?) and valid=1", "associations"));
		updateAssociate = db
				.prepareStatement("UPDATE associations SET valid=? WHERE "
						+ "main_account_id=(select id from player where player=?) "
						+ "AND alt_id=(select id from player where player=?)");
	}

	public void associatePlayer(Player player) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		try { // this code block runs through all the same ips and tries to
				// associate that way
			int playerid = pm.getId(player.getName());
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Associates players
	 */
	public void associatePlayer(UUID main, List<UUID> alt) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			for (int x = 0; x < alt.size(); x++) {
				manualAddAlt.setString(1, main.toString());
				manualAddAlt.setString(2, alt.get(x).toString());
				manualAddAlt.setString(3, main.toString());
				manualAddAlt.setString(4, alt.get(x).toString());
				manualAddAlt.addBatch();

				// should change how this works, isnt as fast as it can be
				updateAssociate.setInt(1, 1);
				updateAssociate.setString(2, main.toString());
				updateAssociate.setString(3, alt.get(x).toString());
				updateAssociate.addBatch();

				updateAssociate.setInt(1, 1);
				updateAssociate.setString(2, alt.get(x).toString());
				updateAssociate.setString(3, main.toString());
				updateAssociate.addBatch();
			}
			updateAssociate.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getAltsList(UUID player) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		List<String> list = new ArrayList<String>();
		try {
			getAlts.setString(1, player.toString());
			ResultSet set = getAlts.executeQuery();
			while (set.next()) {
				String alt = set.getString("player");
				list.add(alt);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public List<UUID> getAltsListUUID(UUID player) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		List<UUID> list = new ArrayList<UUID>();
		try {
			getAlts.setString(1, player.toString());
			ResultSet set = getAlts.executeQuery();
			while (set.next()) {
				UUID alt = UUID.fromString(set.getString("uuid"));
				list.add(alt);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public void disAssociateAltfromPlayer(UUID main, List<UUID> alt) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
		try {
			for(int x = 0; x < alt.size(); x++){
				updateAssociate.setInt(1, 0);
				updateAssociate.setString(2, main.toString());
				updateAssociate.setString(3, alt.get(x).toString());
				updateAssociate.addBatch();

				updateAssociate.setInt(1, 0);
				updateAssociate.setString(2, alt.get(x).toString());
				updateAssociate.setString(3, main.toString());
				updateAssociate.addBatch();
			}
			updateAssociate.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getAmountOfAlts(Player player) {
		if (!db.isConnected())
			db.reconnectAndSetPreparedStatements(); // reconnects database
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
}
