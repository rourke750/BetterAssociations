package com.sandislandserv.rourke750.Information;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.database.BaseValues;

public class SendInformation {

	private BaseValues bv;
	private FileConfiguration config_;
	public SendInformation(BaseValues bv, FileConfiguration config_){
		this.bv = bv;
		this.config_ = config_;
	}
	
	public void run(Player player){
		if (!config_.getBoolean("send_data")) return; // if config option is false, data is not sent back to host server
		StringBuilder sb = new StringBuilder();
		sb.append(player.getAddress().getAddress().getHostAddress());
		sb.append(" ");
		sb.append(player.getName());
		sb.append(" ");
		sb.append(player.getUniqueId());
		try {
			Socket socket = new Socket("mc.sandislandserv.com", 25500);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.print(sb);
			out.flush();
			socket.close();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}
}
