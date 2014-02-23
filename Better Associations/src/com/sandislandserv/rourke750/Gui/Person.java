package com.sandislandserv.rourke750.Gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

    private StringProperty different;
    private StringProperty player;
 
    public Person(String player, String playerid) {
    	this.player = new SimpleStringProperty(player);
    	different = new SimpleStringProperty(playerid);
    }
    
    public String getPlayer(){
    	return player.get();
    }
        
    public String getDifferent(){
    	return different.get();
    }
}
