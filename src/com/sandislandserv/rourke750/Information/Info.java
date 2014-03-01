package com.sandislandserv.rourke750.Information;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.bukkit.configuration.file.FileConfiguration;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class Info {

	private Socket socket;
	private BaseValues db;
	
	public Info(Socket socket, BaseValues db, BetterAssociations plugin){
		this.socket = socket;
		this.db = db;
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line;
			boolean authenticated = false;
			String username;
			String password;
			FileConfiguration config = plugin.getConfig();
			username = config.getString("gui_options.username");
			password = config.getString("gui_options.password");
			while((line = input.readLine()) != null){
				if (!authenticated){
					String[] x = line.split(" ");
					if (x == null){
						out.println("BAD");
						break;
					}
					if (x.length == 2 && username.equals(x[0]) && password.equals(x[1])){
						authenticated = true;
						out.println("GOOD");
					}
					else{
						out.println("BAD");
						break;
					}
				}
				if (line.equals("Get All Player Info")){
					out.println(getAllPlayers() + "\r\nEND");
				}
				if (line.equals("END")) break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAllPlayers(){
		return db.getAllPlayers();
	}
}
