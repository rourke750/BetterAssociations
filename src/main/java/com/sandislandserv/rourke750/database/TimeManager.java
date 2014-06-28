package com.sandislandserv.rourke750.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class TimeManager {
	
	private PreparedStatement LogIn;
    
    private BaseValues db;
    public TimeManager(BaseValues bv){
    	db = bv;
    }
    
    public void initializeStatements(){
    	
    }
    
}
