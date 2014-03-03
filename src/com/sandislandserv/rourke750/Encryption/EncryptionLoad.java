package com.sandislandserv.rourke750.Encryption;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class EncryptionLoad {

	public static void save(File directory, KeyPair keyPair) throws Exception {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream out = new FileOutputStream(directory + "/public.key");
		out.write(DatatypeConverter.printBase64Binary(publicSpec.getEncoded())
				.getBytes());
		out.close();
		
		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		out = new FileOutputStream(directory + "/private.key");
		out.write(DatatypeConverter.printBase64Binary(privateSpec.getEncoded())
				.getBytes());
		out.close();
		
		FileOutputStream outt = new FileOutputStream(directory + "/serverkey.key");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outt));
		writer.append("MIGfMA0GCSqGSIb3"
				+ "DQEBAQUAA4GNADCBiQKBgQCwhh6aLcmiPQjlmu//qOe6yrpZSVsKX4"
				+ "/617KMXPt20kQNww2lbbuv1Lb2ULH1Qp/9HlJYymak2xXa6IL0Ax8y4"
				+ "VGXiRJ7kE1vb0lEeQcIEtLgFqUwJStkNGZ0HlcUKYA5Rv6lqmYPyg+F6cfh"
				+ "4Ne0FcXlcevVv+c7mjIxHU82/QIDAQAB");
		writer.flush();
		writer.close();
		out.close();
		outt.close();
	}
	
	public static KeyPair load(File directory) throws Exception {
		File publicKeyFile = new File(directory + "/public.key");
		FileInputStream in = new FileInputStream(directory + "/public.key");
		byte[] encodedPublicKey = new byte[(int) publicKeyFile.length()];
		in.read(encodedPublicKey);
		encodedPublicKey = DatatypeConverter.parseBase64Binary(new String(
				encodedPublicKey));
		in.close();

		File privateKeyFile = new File(directory + "/private.key");
		in = new FileInputStream(directory + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
		in.read(encodedPrivateKey);
		encodedPrivateKey = DatatypeConverter.parseBase64Binary(new String(
				encodedPrivateKey));
		in.close();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		return new KeyPair(publicKey, privateKey);
	}
	
	public static PublicKey getServerKey(File directory) throws Exception {
		File publicKeyFile = new File(directory + "/serverkey.key");
		FileInputStream in = new FileInputStream(directory + "/serverkey.key");
		byte[] encodedPublicKey = new byte[(int) publicKeyFile.length()];
		in.read(encodedPublicKey);
		encodedPublicKey = DatatypeConverter.parseBase64Binary(new String(
				encodedPublicKey));
		in.close();
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
	}
}
