package com.sandislandserv.rourke750.Information;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.Encryption.Encrypt;
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
			Socket socket = new Socket("share.betterassociations.com", 25500);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.print("PUBLICKEY");
			String publickey = input.readLine();
			PublicKey key = Encrypt.convertToPublicKey(publickey);
			out.print(Encrypt.encrypt(sb.toString().getBytes(), key));
			out.flush();
			socket.close();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getAlts(Player player){
		
	}
}
