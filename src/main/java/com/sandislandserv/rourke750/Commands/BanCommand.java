package com.sandislandserv.rourke750.Commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.sandislandserv.rourke750.BetterAssociations;
import com.sandislandserv.rourke750.database.AssociationsManager;
import com.sandislandserv.rourke750.database.BanManager;
import com.sandislandserv.rourke750.database.BaseValues;
import com.sandislandserv.rourke750.database.PlayerManager;

public class BanCommand {

	private BanManager bm;
	private AssociationsManager am;
	private PlayerManager pm;
	public BanCommand(BaseValues db){
		bm = db.getBanManager();
		am = db.getAssociationsManager();
		pm = db.getPlayerManager();
	}
	
	public void banPlayer(CommandSender sender, String[] args){
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "Must give player name to ban!");
			return;
		}
		String ban = args[2];
		OfflinePlayer pl = Bukkit.getOfflinePlayer(ban);
		if (!pl.hasPlayedBefore()){
			sender.sendMessage(ChatColor.RED + "Player has not played before.");
			return;
		}
		String reason = "";
		for (int x = 3; x < args.length; x++){
			reason += args[x];
		}
		if (reason.equals("")){
			reason = BetterAssociations.getConfigFile().getString("banmanager.set.defaultbanreason");
			bm.banPlayer(reason, pl.getUniqueId());
			sender.sendMessage(ChatColor.RED + "Player banned with default reason: " + reason);
		}
		else{
			bm.banPlayer(reason, pl.getUniqueId());
			sender.sendMessage(ChatColor.RED + "Player banned with reason: " + reason);
		}
		return;
	}
	
	public void unBanPlayer(CommandSender sender, String[] args){
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "Must give a player name to unban!");
			return;
		}
		String unban = args[2];
		OfflinePlayer pl = Bukkit.getOfflinePlayer(unban);
		bm.unbanPlayer(pl.getUniqueId());
		sender.sendMessage(ChatColor.RED + "Player has been unbanned.");
		return;
	}
	
	public void banAllAlts(CommandSender sender, String[] args){
		if (args.length == 2){
			sender.sendMessage(ChatColor.RED + "Must give a player name to ban all his alts!");
			return;
		}
		String playerName = args[2];
		UUID uuid = pm.getUUIDfromPlayerName(playerName);
		List<UUID> alts = am.getAltsListUUID(uuid);
		String reason = "";
		for (int x = 3; x < args.length; x++){
			reason += args[x];
		}
		if (reason.equals("")){
			reason = BetterAssociations.getConfigFile().getString("banmanager.set.defaultbanreason");
			bm.banPlayers(alts, reason);
			sender.sendMessage(ChatColor.RED + "Player banned with default reason: " + reason);
		}
		else{
			bm.banPlayers(alts, reason);
			sender.sendMessage(ChatColor.RED + "Player banned with reason: " + reason);
		}
	}
}
