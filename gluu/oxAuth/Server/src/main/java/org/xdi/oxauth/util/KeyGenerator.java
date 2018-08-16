/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.util;

import java.util.UUID;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.xdi.oxauth.model.crypto.Key;
import org.xdi.oxauth.model.crypto.KeyFactory;
import org.xdi.oxauth.model.crypto.signature.ECDSAKeyFactory;
import org.xdi.oxauth.model.crypto.signature.ECDSAPrivateKey;
import org.xdi.oxauth.model.crypto.signature.ECDSAPublicKey;
import org.xdi.oxauth.model.crypto.signature.RSAKeyFactory;
import org.xdi.oxauth.model.crypto.signature.RSAPrivateKey;
import org.xdi.oxauth.model.crypto.signature.RSAPublicKey;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.util.SecurityProviderUtility;

/**
 * @author Javier Rojas Blum
 * @version 0.9 January 14, 2015
 */
public class KeyGenerator {

	private static final Logger log;

	static {
		// Add console appender
		LogManager.getRootLogger().removeAllAppenders();

		ConsoleAppender consoleAppender = new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
		LogManager.getRootLogger().addAppender(consoleAppender);

		log = Logger.getLogger(KeyGenerator.class);
	}

    public static void main(String[] args) throws Exception {
    	SecurityProviderUtility.installBCProvider(true);

    	JSONArray keys = new JSONArray();

        keys.put(generateRS256Keys(null));
        keys.put(generateRS384Keys(null));
        keys.put(generateRS512Keys(null));

        keys.put(generateES256Keys(null));
        keys.put(generateES384Keys(null));
        keys.put(generateES512Keys(null));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keys", keys);

        System.out.println(jsonObject.toString(4).replace("\\/", "/"));
    }

    public static JSONObject generateRS256Keys(Long expirationTime) throws Exception {
        KeyFactory<RSAPrivateKey, RSAPublicKey> keyFactory = new RSAKeyFactory(
                SignatureAlgorithm.RS256,
                "CN=Test CA Certificate");

        Key<RSAPrivateKey, RSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("RSA");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.RS256.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve(JSONObject.NULL);

        return key.toJSONObject();
    }

    public static JSONObject generateRS384Keys(Long expirationTime) throws Exception {
        KeyFactory<RSAPrivateKey, RSAPublicKey> keyFactory = new RSAKeyFactory(
                SignatureAlgorithm.RS384,
                "CN=Test CA Certificate");

        Key<RSAPrivateKey, RSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("RSA");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.RS384.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve(JSONObject.NULL);

        return key.toJSONObject();
    }

    public static JSONObject generateRS512Keys(Long expirationTime) throws Exception {
        KeyFactory<RSAPrivateKey, RSAPublicKey> keyFactory = new RSAKeyFactory(
                SignatureAlgorithm.RS512,
                "CN=Test CA Certificate");

        Key<RSAPrivateKey, RSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("RSA");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.RS512.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve(JSONObject.NULL);

        return key.toJSONObject();
    }

    public static JSONObject generateES256Keys(Long expirationTime) throws Exception {
        KeyFactory<ECDSAPrivateKey, ECDSAPublicKey> keyFactory = new ECDSAKeyFactory(
                SignatureAlgorithm.ES256,
                "CN=Test CA Certificate");

        Key<ECDSAPrivateKey, ECDSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("EC");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.ES256.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve("P-256");

        return key.toJSONObject();
    }

    public static JSONObject generateES384Keys(Long expirationTime) throws Exception {
        KeyFactory<ECDSAPrivateKey, ECDSAPublicKey> keyFactory = new ECDSAKeyFactory(
                SignatureAlgorithm.ES384,
                "CN=Test CA Certificate");

        Key<ECDSAPrivateKey, ECDSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("EC");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.ES384.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve("P-384");

        return key.toJSONObject();
    }

    public static JSONObject generateES512Keys(Long expirationTime) throws Exception{
        KeyFactory<ECDSAPrivateKey, ECDSAPublicKey> keyFactory = new ECDSAKeyFactory(
                SignatureAlgorithm.ES512,
                "CN=Test CA Certificate");

        Key<ECDSAPrivateKey, ECDSAPublicKey> key = keyFactory.getKey();

        key.setKeyType("EC");
        key.setUse("SIGNATURE");
        key.setAlgorithm(SignatureAlgorithm.ES512.getName());
        key.setKeyId(UUID.randomUUID().toString());
        key.setExpirationTime(expirationTime);
        key.setCurve("P-521");

        return key.toJSONObject();
    }
}
