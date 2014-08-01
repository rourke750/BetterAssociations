package com.sandislandserv.rourke750.Misc;

import java.lang.reflect.Field;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.entity.Player;

public interface ProfileInterface {

	public void modifyGameProfile(Player player, String name);
	void setFinalStatic(Field field, Object newValue, GameProfile prof);
}
