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
    
    private AssociationsManager am;
    private BanManager bm;
    private PlayerManager pm;
    private TimeManager tm;
	
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
			genTables(); // generate the tables
			initializeClasses(); // Initializes the other classes
			initializeStatementsforOtherClasses(); // Initializes the statements in the other class
			initializeProcedure(); // Initializes the specialized procedures used in this plugin
		}
	}
	
	public void initializeClasses(){
		pm = new PlayerManager(this);
		am = new AssociationsManager(this, pm);
		bm = new BanManager(this);
		tm = new TimeManager(this);
	}
	
	public PreparedStatement prepareStatement(String sequence){
		return db.prepareStatement(sequence);
	}
	
	public void reconnectAndSetPreparedStatements() { // reconnects the server if disconnected
        db.connect();
        initializeStatementsforOtherClasses();
	}
	
	public boolean isConnected(){
		return db.isConnected();
	}
	
	public void initializeProcedure(){
		db.execute("drop procedure if exists addplayertotable");
		db.execute("create definer=current_user procedure addplayertotable("
				+ "in pl varchar(40), in uu varchar(40)) sql security invoker begin "
				+ ""
				+ "declare amount int(10);"
				+ "declare account varchar(40);"
				+ "declare nameamount int(10);"
				+ ""
				+ "set amount=0;"
				+ "set amount=(select count(*) from player p where p.uuid=uu);"
				+ ""
				+ "if (amount < 1) then"
				+ "		set account =(select uuid from player p where p.player=pl);"
				+ "		if (account not like uu) then"
				+ "			insert ignore into playercountnames (player, amount) values (pl, 0);"
				+ ""
				+ "			update playercountnames set amount = amount+1 where player=pl;"
				+ ""
				+ "			set nameamount=(select amount from playercountnames where player=pl);"
				+ ""
				+ "			insert into player (player, uuid) values ((select concat (pl,nameamount)), uu);"
				+ "		else"
				+ "			insert ignore into player (player, uuid) values (pl, uu);"
				+ "		end if;"
				+ "end if;"
				+ "end");
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
		db.execute("create table if not exists playercountnames ("
				+ "player varchar(40) not null,"
				+ "amount int(10) not null,"
				+ "primary key (player));");
	}
	
	public void initializeStatementsforOtherClasses(){
		pm.initializeStatements();
		am.initializeStatements();
		bm.initializeStatements();
		tm.initializeStatements();
	}
	
	public AssociationsManager getAssociationsManager(){
		return am;
	}
	
	public BanManager getBanManager(){
		return bm;
	}
	
	public PlayerManager getPlayerManager(){
		return pm;
	}
	
	public TimeManager getTimeManager(){
		return tm;
	}
}