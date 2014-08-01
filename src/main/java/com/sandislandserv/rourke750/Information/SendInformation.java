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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.BaseValues;

public class SendInformation {

	private BaseValues bv;
	private BetterAssociations plugin;
	private FileConfiguration config_;
	private String serialInfo = "";
	private boolean authenticated = false;
	private static Map<Player, String> keys = new HashMap<Player, String>();

	public SendInformation(BetterAssociations plugin, BaseValues bv, FileConfiguration config_) {
		this.plugin = plugin;
		this.bv = bv;
		this.config_ = config_;
		new HandleWebCommincations(); // starts the web handler
		serialInfo = config_.getString("secret.username") + " " + config_.getString("secret.secretkey");
		if (!config_.getString("secret.username").equals(""))
			authenticateServer();
	}

	public void sendPlayer(Player player) {
		if (!authenticated) // server was not authenticated
			return;
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(new FileInputStream(plugin.getDataFolder().toString() + File.separator+"myTrustStore"), null);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
			
			SSLSocketFactory socketFactory = ctx.getSocketFactory();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket("share.betterassociations.com", 25500);
			socket.setEnabledProtocols(new String[] { "TLSv1" });
			PrintStream out = new PrintStream(socket.getOutputStream(), true,
					"UTF-8");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			out.println("SEND");
			out.println("PLAYER_LOG");
			out.println("UUID");
			out.println(player.getUniqueId().toString()); // sends the player uuid
			out.println("NAME");
			out.println(player.getName().toString()); // sends the player name
			out.println("IP");
			out.println(player.getAddress().getAddress()
					.getHostAddress());
			out.println("NULL");
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
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(new FileInputStream(plugin.getDataFolder().toString() + File.separator+"myTrustStore"), null);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
			
			SSLSocketFactory socketFactory = ctx.getSocketFactory();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket("share.betterassociations.com", 25500);
			socket.setEnabledProtocols(new String[] { "TLSv1" });
			PrintStream out = new PrintStream(socket.getOutputStream(), true,
					"UTF-8");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			out.println("CHECK");
			// First thing we need to do is authenticate this server
			out.println(serialInfo);
			if (input.readLine().equals("AUTHENTICATED")){
				authenticated = true;
				Bukkit.getLogger().log(Level.INFO, "Authenticated!");
			}
			else
				Bukkit.getLogger().log(Level.WARNING, "Did not authenticate to server.");
			socket.close();
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.INFO, "Could not connect to share.betterassociations.com");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
