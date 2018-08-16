/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.model.crypto;

import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.bouncycastle.jce.provider.JCERSAPublicKey;
import org.bouncycastle.openssl.PEMWriter;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.xdi.oxauth.model.crypto.signature.ECDSAPublicKey;
import org.xdi.oxauth.model.crypto.signature.RSAPublicKey;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.util.StringUtils;

/**
 * @author Javier Rojas Blum Date: 10.22.2012
 */
public class Certificate {

    private SignatureAlgorithm signatureAlgorithm;
    private X509Certificate x509Certificate;

    public Certificate(SignatureAlgorithm signatureAlgorithm, X509Certificate x509Certificate) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.x509Certificate = x509Certificate;
    }

    public PublicKey getPublicKey() {
        PublicKey publicKey = null;

        if (x509Certificate != null && x509Certificate.getPublicKey() instanceof JCERSAPublicKey) {
            JCERSAPublicKey jcersaPublicKey = (JCERSAPublicKey) x509Certificate.getPublicKey();

            publicKey = new RSAPublicKey(jcersaPublicKey.getModulus(), jcersaPublicKey.getPublicExponent());
        } else if (x509Certificate != null && x509Certificate.getPublicKey() instanceof JCEECPublicKey) {
            JCEECPublicKey jceecPublicKey = (JCEECPublicKey) x509Certificate.getPublicKey();

            publicKey = new ECDSAPublicKey(signatureAlgorithm, jceecPublicKey.getQ().getX().toBigInteger(),
                    jceecPublicKey.getQ().getY().toBigInteger());
        }

        return publicKey;
    }

    public RSAPublicKey getRsaPublicKey() {
        RSAPublicKey rsaPublicKey = null;

        if (x509Certificate != null && x509Certificate.getPublicKey() instanceof JCERSAPublicKey) {
            JCERSAPublicKey publicKey = (JCERSAPublicKey) x509Certificate.getPublicKey();

            rsaPublicKey = new RSAPublicKey(publicKey.getModulus(), publicKey.getPublicExponent());
        }

        return rsaPublicKey;
    }

    public ECDSAPublicKey getEcdsaPublicKey() {
        ECDSAPublicKey ecdsaPublicKey = null;

        if (x509Certificate != null && x509Certificate.getPublicKey() instanceof JCEECPublicKey) {
            JCEECPublicKey publicKey = (JCEECPublicKey) x509Certificate.getPublicKey();

            ecdsaPublicKey = new ECDSAPublicKey(signatureAlgorithm, publicKey.getQ().getX().toBigInteger(),
                    publicKey.getQ().getY().toBigInteger());
        }

        return ecdsaPublicKey;
    }

    public JSONArray toJSONArray() throws JSONException {
        String cert = toString();

        cert = cert.replace("\n", "");
        cert = cert.replace("-----BEGIN CERTIFICATE-----", "");
        cert = cert.replace("-----END CERTIFICATE-----", "");

        return new JSONArray(Arrays.asList(cert));
    }

    @Override
    public String toString() {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(x509Certificate);
            pemWriter.flush();
            return stringWriter.toString();
        } catch (IOException e) {
            return StringUtils.EMPTY_STRING;
        } catch (Exception e) {
            return StringUtils.EMPTY_STRING;
        }
    }
}