package com.sandislandserv.rourke750.Misc;

import org.bukkit.Server;

/*
 * Thank you orbfuscator for showing me how to do this.
 */
public class ClassHandler {

	private static ClassHandler ch;
	private String version;
	
	public static boolean Initialize(Server server){
		ch = new ClassHandler();
		String packageName = server.getClass().getPackage().getName();
		ch.version = packageName.substring(packageName.lastIndexOf('.') + 1);
		try {
			Class.forName("com.sandislandserv.rourke750.Misc." + ch.version + ".ProfileModifier");
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public static ProfileInterface getProfileClass(){
		return (ProfileInterface) getObject(ProfileInterface.class, "ProfileModifier");
	}
	
	private static Object getObject(Class<? extends Object> Class, String name){
		try {
			Class<?> internalClass = Class.forName("com.sandislandserv.rourke750.Misc." + ch.version + name);
			if (internalClass.isAssignableFrom(internalClass)) 
				return internalClass.getConstructor().newInstance();
		} catch (Exception e) {
            System.out.println(e.toString());
		}
		return null;
	}
}
