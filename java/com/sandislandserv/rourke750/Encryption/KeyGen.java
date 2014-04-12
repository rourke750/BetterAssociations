package com.sandislandserv.rourke750.Encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.logging.Logger;

public class KeyGen {
	private static final Logger LOG = Logger.getLogger("Better Associations");
	
	public static KeyPair generate(int bits) throws Exception {
		LOG.info("Better Associations is generating an RSA key pair.");
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits,
				RSAKeyGenParameterSpec.F4);
		keygen.initialize(spec);
		return keygen.generateKeyPair();
	}

}
