package com.sandislandserv.rourke750.Information;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class SendInformation {

	private BaseValues bv;
	private FileConfiguration config_;
	private String serialInfo = "";
	private boolean authenticated = false;
	private static Map<Player, String> keys = new HashMap<Player, String>();

	public SendInformation(BaseValues bv, FileConfiguration config_) {
		this.bv = bv;
		this.config_ = config_;
		new HandleWebCommincations(); // starts the web handler
		serialInfo = config_.getString("secret");
		authenticateServer();
	}

	public void run(Player player, GameProfile profile) {
		if (!authenticated) // server was not authenticated
			return;
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket("share.betterassociations.com", 25500);
			socket.setEnabledProtocols(new String[] { "TLSv1" });
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out.println("SEND");
			out.println("PLAYER_INFO");
			out.println("UUID");
			out.println(player.getUniqueId().toString()); // sends the player uuid
			out.println("NAME");
			out.println(player.getName().toString()); // sends the player name
			out.println("IP");
			out.println(player.getAddress().getAddress()
					.getHostAddress());
			socket.close();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void authenticateServer() {
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket("share.betterassociations.com", 25500);
			socket.setEnabledProtocols(new String[] { "TLSv1" });
			PrintStream out = new PrintStream(socket.getOutputStream(), true,
					"UTF-8");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			out.println("CHECK");
			// First thing we need to do is authenticate this server
			out.println("it worked");
			if (input.readLine().equals("AUTHENTICATED"))
				authenticated = true;
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
