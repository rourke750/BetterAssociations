package com.sandislandserv.rourke750.Information;

import java.io.IOException;
import java.net.ServerSocket;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class AssociationInformation {
	
	public AssociationInformation(final BaseValues db, FileConfiguration config_, BetterAssociations plugin){
		int port = config_.getInt("gui_options.port");
		if (port == 0) return;
		try {
			final ServerSocket socket = new ServerSocket(port);
						try {
							while(true){
								new Info(socket.accept(), db, plugin);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
