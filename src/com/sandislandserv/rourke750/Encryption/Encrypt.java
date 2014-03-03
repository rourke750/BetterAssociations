package com.sandislandserv.rourke750.Encryption;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class Encrypt {
		
	public static byte[] encrypt(byte[] data, PublicKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}
	
	public static byte[] decrypt(byte[] data, PrivateKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
}