package com.sandislandserv.rourke750.Encryption;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import net.minecraft.util.io.netty.handler.codec.DecoderException;
import net.minecraft.util.io.netty.handler.codec.base64.Base64;

import com.sandislandserv.rourke750.BetterAssociations;

/*
 * Thank you Florian Westreicher
 * http://coding.westreicher.org/?p=23
 * His code ends by the //==================
 */
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
	
	public static PublicKey convertToPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException{
		byte[] bit = key.getBytes();
		byte[] decode = DatatypeConverter.parseBase64Binary(new String(
				bit));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				decode);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}
}