/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.client.model.authorize;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.xdi.oxauth.client.AuthorizationRequest;
import org.xdi.oxauth.model.common.Display;
import org.xdi.oxauth.model.common.Prompt;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.crypto.encryption.BlockEncryptionAlgorithm;
import org.xdi.oxauth.model.crypto.encryption.KeyEncryptionAlgorithm;
import org.xdi.oxauth.model.crypto.signature.ECDSAPrivateKey;
import org.xdi.oxauth.model.crypto.signature.RSAPrivateKey;
import org.xdi.oxauth.model.crypto.signature.RSAPublicKey;
import org.xdi.oxauth.model.crypto.signature.SignatureAlgorithm;
import org.xdi.oxauth.model.exception.InvalidJweException;
import org.xdi.oxauth.model.exception.InvalidJwtException;
import org.xdi.oxauth.model.jwe.JweEncrypterImpl;
import org.xdi.oxauth.model.jwt.JwtHeader;
import org.xdi.oxauth.model.jwt.JwtType;
import org.xdi.oxauth.model.util.JwtUtil;
import org.xdi.oxauth.model.util.Pair;
import org.xdi.oxauth.model.util.Util;

/**
 * @author Javier Rojas Blum Date: 03.07.2012
 */
public class JwtAuthorizationRequest {

    // Header
    private JwtType type;
    private SignatureAlgorithm signatureAlgorithm;
    private KeyEncryptionAlgorithm keyEncryptionAlgorithm;
    private BlockEncryptionAlgorithm blockEncryptionAlgorithm;
    private String keyId;

    // Payload
    private List<ResponseType> responseTypes;
    private String clientId;
    private List<String> scopes;
    private String redirectUri;
    private String state;
    private String nonce;
    private Display display;
    private List<Prompt> prompts;
    private Integer maxAge;
    private List<String> uiLocales;
    private List<String> claimsLocales;
    private String idTokenHint;
    private String loginHint;
    private List<String> acrValues;
    private String registration;
    private boolean requestUniqueId;

    private UserInfoMember userInfoMember;
    private IdTokenMember idTokenMember;

    // Signature Keys
    private String sharedKey;
    private RSAPrivateKey rsaPrivateKey;
    private ECDSAPrivateKey ecPrivateKey;

    // Encryption Keys
    private RSAPublicKey rsaPublicKey;
    private byte[] sharedSymmetricKey;

    public JwtAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        this.type = JwtType.JWT;
        this.signatureAlgorithm = SignatureAlgorithm.NONE;

        this.userInfoMember = new UserInfoMember();
        this.idTokenMember = new IdTokenMember();

        setAuthorizationRequestParams(authorizationRequest);
    }

    public JwtAuthorizationRequest(SignatureAlgorithm algorithm, String sharedKey) {
        this(algorithm, sharedKey, null, null);
    }

    public JwtAuthorizationRequest(SignatureAlgorithm algorithm, RSAPrivateKey privateKey) {
        this(algorithm, null, privateKey, null);
    }

    public JwtAuthorizationRequest(SignatureAlgorithm algorithm, ECDSAPrivateKey privateKey) {
        this(algorithm, null, null, privateKey);
    }

    public JwtAuthorizationRequest(AuthorizationRequest authorizationRequest, SignatureAlgorithm algorithm,
                                   String sharedKey) {
        this(authorizationRequest, algorithm, sharedKey, null, null);
    }

    public JwtAuthorizationRequest(AuthorizationRequest authorizationRequest, SignatureAlgorithm algorithm,
                                   RSAPrivateKey privateKey) {
        this(authorizationRequest, algorithm, null, privateKey, null);
    }

    public JwtAuthorizationRequest(AuthorizationRequest authorizationRequest, SignatureAlgorithm algorithm,
                                   ECDSAPrivateKey privateKey) {
        this(authorizationRequest, algorithm, null, null, privateKey);
    }

    public JwtAuthorizationRequest(
            AuthorizationRequest authorizationRequest, KeyEncryptionAlgorithm keyEncryptionAlgorithm,
            BlockEncryptionAlgorithm blockEncryptionAlgorithm, RSAPublicKey rsaPublicKey) {
        this.type = JwtType.JWE;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.blockEncryptionAlgorithm = blockEncryptionAlgorithm;

        this.userInfoMember = new UserInfoMember();
        this.idTokenMember = new IdTokenMember();

        this.rsaPublicKey = rsaPublicKey;

        setAuthorizationRequestParams(authorizationRequest);
    }

    public JwtAuthorizationRequest(
            AuthorizationRequest authorizationRequest, KeyEncryptionAlgorithm keyEncryptionAlgorithm,
            BlockEncryptionAlgorithm blockEncryptionAlgorithm, byte[] sharedSymmetricKey) {
        this.type = JwtType.JWE;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.blockEncryptionAlgorithm = blockEncryptionAlgorithm;

        this.userInfoMember = new UserInfoMember();
        this.idTokenMember = new IdTokenMember();

        this.sharedSymmetricKey = sharedSymmetricKey;

        setAuthorizationRequestParams(authorizationRequest);
    }

    private JwtAuthorizationRequest(SignatureAlgorithm signatureAlgorithm, String sharedKey,
                                    RSAPrivateKey rsaPrivateKey, ECDSAPrivateKey ecPrivateKey) {
        this.type = JwtType.JWS;
        this.sharedKey = sharedKey;
        this.rsaPrivateKey = rsaPrivateKey;
        this.ecPrivateKey = ecPrivateKey;
        this.signatureAlgorithm = signatureAlgorithm;

        this.userInfoMember = new UserInfoMember();
        this.idTokenMember = new IdTokenMember();
    }

    private JwtAuthorizationRequest(AuthorizationRequest authorizationRequest, SignatureAlgorithm algorithm,
                                    String sharedKey, RSAPrivateKey rsaPrivateKey, ECDSAPrivateKey ecPrivateKey) {
        this(algorithm, sharedKey, rsaPrivateKey, ecPrivateKey);
        setAuthorizationRequestParams(authorizationRequest);
    }

    private void setAuthorizationRequestParams(AuthorizationRequest authorizationRequest) {
        if (authorizationRequest != null) {
            this.responseTypes = authorizationRequest.getResponseTypes();
            this.clientId = authorizationRequest.getClientId();
            this.scopes = authorizationRequest.getScopes();
            this.redirectUri = authorizationRequest.getRedirectUri();
            this.state = authorizationRequest.getState();
            this.nonce = authorizationRequest.getNonce();
            this.display = authorizationRequest.getDisplay();
            this.prompts = authorizationRequest.getPrompts();
            this.maxAge = authorizationRequest.getMaxAge();
            this.uiLocales = authorizationRequest.getUiLocales();
            this.claimsLocales = authorizationRequest.getClaimsLocales();
            this.idTokenHint = authorizationRequest.getIdTokenHint();
            this.loginHint = authorizationRequest.getLoginHint();
            this.acrValues = authorizationRequest.getAcrValues();
            this.registration = authorizationRequest.getRegistration();
            this.requestUniqueId = authorizationRequest.isRequestSessionId();
        }
    }

    public JwtType getType() {
        return type;
    }

    public void setType(JwtType type) {
        this.type = type;
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public KeyEncryptionAlgorithm getKeyEncryptionAlgorithm() {
        return keyEncryptionAlgorithm;
    }

    public void setKeyEncryptionAlgorithm(KeyEncryptionAlgorithm keyEncryptionAlgorithm) {
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
    }

    public BlockEncryptionAlgorithm getBlockEncryptionAlgorithm() {
        return blockEncryptionAlgorithm;
    }

    public void setBlockEncryptionAlgorithm(BlockEncryptionAlgorithm blockEncryptionAlgorithm) {
        this.blockEncryptionAlgorithm = blockEncryptionAlgorithm;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public boolean isRequestUniqueId() {
        return requestUniqueId;
    }

    public void setRequestUniqueId(boolean p_requestUniqueId) {
        requestUniqueId = p_requestUniqueId;
    }

    public List<ResponseType> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<ResponseType> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public List<Prompt> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<Prompt> prompts) {
        this.prompts = prompts;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public List<String> getUiLocales() {
        return uiLocales;
    }

    public void setUiLocales(List<String> uiLocales) {
        this.uiLocales = uiLocales;
    }

    public List<String> getClaimsLocales() {
        return claimsLocales;
    }

    public void setClaimsLocales(List<String> claimsLocales) {
        this.claimsLocales = claimsLocales;
    }

    public String getIdTokenHint() {
        return idTokenHint;
    }

    public void setIdTokenHint(String idTokenHint) {
        this.idTokenHint = idTokenHint;
    }

    public String getLoginHint() {
        return loginHint;
    }

    public void setLoginHint(String loginHint) {
        this.loginHint = loginHint;
    }

    public List<String> getAcrValues() {
        return acrValues;
    }

    public void setAcrValues(List<String> acrValues) {
        this.acrValues = acrValues;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public UserInfoMember getUserInfoMember() {
        return userInfoMember;
    }

    public void setUserInfoMember(UserInfoMember userInfoMember) {
        this.userInfoMember = userInfoMember;
    }

    public IdTokenMember getIdTokenMember() {
        return idTokenMember;
    }

    public void setIdTokenMember(IdTokenMember idTokenMember) {
        this.idTokenMember = idTokenMember;
    }

    public void addUserInfoClaim(Claim claim) {
        userInfoMember.getClaims().add(claim);
    }

    public void addIdTokenClaim(Claim claim) {
        idTokenMember.getClaims().add(claim);
    }

    public String getEncodedJwt() {
        String encodedJwt = null;

        try {
            if (signatureAlgorithm == SignatureAlgorithm.NONE) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadToJSONObject(), signatureAlgorithm);
            } else if (signatureAlgorithm == SignatureAlgorithm.HS256 || signatureAlgorithm == SignatureAlgorithm.HS384 || signatureAlgorithm == SignatureAlgorithm.HS512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadToJSONObject(), signatureAlgorithm, sharedKey);
            } else if (signatureAlgorithm == SignatureAlgorithm.RS256 || signatureAlgorithm == SignatureAlgorithm.RS384 || signatureAlgorithm == SignatureAlgorithm.RS512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadToJSONObject(), signatureAlgorithm, rsaPrivateKey);
            } else if (signatureAlgorithm == SignatureAlgorithm.ES256 || signatureAlgorithm == SignatureAlgorithm.ES384 || signatureAlgorithm == SignatureAlgorithm.ES512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadToJSONObject(), signatureAlgorithm, ecPrivateKey);
            } else if (keyEncryptionAlgorithm != null && blockEncryptionAlgorithm != null) {
                JweEncrypterImpl jweEncrypter = null;
                if (rsaPublicKey != null) {
                    jweEncrypter = new JweEncrypterImpl(keyEncryptionAlgorithm, blockEncryptionAlgorithm, rsaPublicKey);
                } else {
                    jweEncrypter = new JweEncrypterImpl(keyEncryptionAlgorithm, blockEncryptionAlgorithm, sharedSymmetricKey);
                }

                String header = headerToJSONObject().toString();
                String encodedHeader = JwtUtil.base64urlencode(header.getBytes(Util.UTF8_STRING_ENCODING));

                String claims = payloadToJSONObject().toString();
                String encodedClaims = JwtUtil.base64urlencode(claims.getBytes(Util.UTF8_STRING_ENCODING));

                byte[] contentMasterKey = new byte[blockEncryptionAlgorithm.getCmkLength() / 8];
                SecureRandom random = new SecureRandom();
                random.nextBytes(contentMasterKey);
                String encodedEncryptedKey = jweEncrypter.generateEncryptedKey(contentMasterKey);

                byte[] initializationVector = new byte[blockEncryptionAlgorithm.getInitVectorLength() / 8];
                random.nextBytes(initializationVector);
                String encodedInitializationVector = JwtUtil.base64urlencode(initializationVector);

                String additionalAuthenticatedData = encodedHeader + "."
                        + encodedEncryptedKey + "."
                        + encodedInitializationVector;

                Pair<String, String> result = jweEncrypter.generateCipherTextAndIntegrityValue(contentMasterKey, initializationVector,
                        additionalAuthenticatedData.getBytes(Util.UTF8_STRING_ENCODING),
                        encodedClaims.getBytes(Util.UTF8_STRING_ENCODING));
                String encodedCipherText = result.getFirst();
                String encodedIntegrityValue = result.getSecond();

                encodedJwt = encodedHeader + "."
                        + encodedEncryptedKey + "."
                        + encodedInitializationVector + "."
                        + encodedCipherText + "."
                        + encodedIntegrityValue;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidJwtException e) {
            e.printStackTrace();
        } catch (InvalidJweException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedJwt;
    }

    public String getEncodedJwt(String payload) {
        String encodedJwt = null;

        try {
            JSONObject payloadJsonObject = new JSONObject(payload);
            if (signatureAlgorithm == SignatureAlgorithm.NONE) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadJsonObject, signatureAlgorithm);
            } else if (signatureAlgorithm == SignatureAlgorithm.HS256 || signatureAlgorithm == SignatureAlgorithm.HS384 || signatureAlgorithm == SignatureAlgorithm.HS512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadJsonObject, signatureAlgorithm, sharedKey);
            } else if (signatureAlgorithm == SignatureAlgorithm.RS256 || signatureAlgorithm == SignatureAlgorithm.RS384 || signatureAlgorithm == SignatureAlgorithm.RS512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadJsonObject, signatureAlgorithm, rsaPrivateKey);
            } else if (signatureAlgorithm == SignatureAlgorithm.ES256 || signatureAlgorithm == SignatureAlgorithm.ES384 || signatureAlgorithm == SignatureAlgorithm.ES512) {
                encodedJwt = JwtUtil.encodeJwt(headerToJSONObject(), payloadJsonObject, signatureAlgorithm, ecPrivateKey);
            } else if (keyEncryptionAlgorithm != null && blockEncryptionAlgorithm != null) {
                JweEncrypterImpl jweEncrypter = null;
                if (rsaPublicKey != null) {
                    jweEncrypter = new JweEncrypterImpl(keyEncryptionAlgorithm, blockEncryptionAlgorithm, rsaPublicKey);
                } else {
                    jweEncrypter = new JweEncrypterImpl(keyEncryptionAlgorithm, blockEncryptionAlgorithm, sharedSymmetricKey);
                }

                String header = headerToJSONObject().toString();
                String encodedHeader = JwtUtil.base64urlencode(header.getBytes(Util.UTF8_STRING_ENCODING));

                String claims = payloadJsonObject.toString();
                String encodedClaims = JwtUtil.base64urlencode(claims.getBytes(Util.UTF8_STRING_ENCODING));

                byte[] contentMasterKey = new byte[blockEncryptionAlgorithm.getCmkLength() / 8];
                SecureRandom random = new SecureRandom();
                random.nextBytes(contentMasterKey);
                String encodedEncryptedKey = jweEncrypter.generateEncryptedKey(contentMasterKey);

                byte[] initializationVector = new byte[blockEncryptionAlgorithm.getInitVectorLength() / 8];
                random.nextBytes(initializationVector);
                String encodedInitializationVector = JwtUtil.base64urlencode(initializationVector);

                String additionalAuthenticatedData = encodedHeader + "."
                        + encodedEncryptedKey + "."
                        + encodedInitializationVector;

                Pair<String, String> result = jweEncrypter.generateCipherTextAndIntegrityValue(contentMasterKey, initializationVector,
                        additionalAuthenticatedData.getBytes(Util.UTF8_STRING_ENCODING),
                        encodedClaims.getBytes(Util.UTF8_STRING_ENCODING));
                String encodedCipherText = result.getFirst();
                String encodedIntegrityValue = result.getSecond();

                encodedJwt = encodedHeader + "."
                        + encodedEncryptedKey + "."
                        + encodedInitializationVector + "."
                        + encodedCipherText + "."
                        + encodedIntegrityValue;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidJwtException e) {
            e.printStackTrace();
        } catch (InvalidJweException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedJwt;
    }

    public String getDecodedJwt() {
        String decodedJwt = null;
        try {
            decodedJwt = payloadToJSONObject().toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return decodedJwt;
    }

    protected JSONObject headerToJSONObject() throws InvalidJwtException {
        JwtHeader jwtHeader = new JwtHeader();

        jwtHeader.setType(type);
        if (type == JwtType.JWE) {
            jwtHeader.setAlgorithm(keyEncryptionAlgorithm);
            jwtHeader.setEncryptionMethod(blockEncryptionAlgorithm);
        } else {
            jwtHeader.setAlgorithm(signatureAlgorithm);
        }
        jwtHeader.setKeyId(keyId);

        return jwtHeader.toJsonObject();
    }

    protected JSONObject payloadToJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();

        try {
            if (responseTypes != null && !responseTypes.isEmpty()) {
                if (responseTypes.size() == 1) {
                    ResponseType responseType = responseTypes.get(0);
                    obj.put("response_type", responseType);
                } else {
                    JSONArray responseTypeJsonArray = new JSONArray();
                    for (ResponseType responseType : responseTypes) {
                        responseTypeJsonArray.put(responseType);
                    }
                    obj.put("response_type", responseTypeJsonArray);
                }
            }
            if (StringUtils.isNotBlank(clientId)) {
                obj.put("client_id", clientId);
            }
            if (scopes != null && !scopes.isEmpty()) {
                if (scopes.size() == 1) {
                    String scope = scopes.get(0);
                    obj.put("scope", scope);
                } else {
                    JSONArray scopeJsonArray = new JSONArray();
                    for (String scope : scopes) {
                        scopeJsonArray.put(scope);
                    }
                    obj.put("scope", scopeJsonArray);
                }
            }
            if (StringUtils.isNotBlank(redirectUri)) {
                obj.put("redirect_uri", URLEncoder.encode(redirectUri, "UTF-8"));
            }
            if (StringUtils.isNotBlank(state)) {
                obj.put("state", state);
            }
            if (StringUtils.isNotBlank(nonce)) {
                obj.put("nonce", nonce);
            }
            if (display != null) {
                obj.put("display", display);
            }
            if (prompts != null && !prompts.isEmpty()) {
                JSONArray promptJsonArray = new JSONArray();
                for (Prompt prompt : prompts) {
                    promptJsonArray.put(prompt);
                }
                obj.put("prompt", promptJsonArray);
            }
            if (maxAge != null) {
                obj.put("max_age", maxAge);
            }
            if (uiLocales != null && !uiLocales.isEmpty()) {
                JSONArray uiLocalesJsonArray = new JSONArray(uiLocales);
                obj.put("ui_locales", uiLocalesJsonArray);
            }
            if (claimsLocales != null && !claimsLocales.isEmpty()) {
                JSONArray claimsLocalesJsonArray = new JSONArray(claimsLocales);
                obj.put("claims_locales", claimsLocalesJsonArray);
            }
            if (StringUtils.isNotBlank(idTokenHint)) {
                obj.put("id_token_hint", idTokenHint);
            }
            if (StringUtils.isNotBlank(loginHint)) {
                obj.put("login_hint", loginHint);
            }
            if (acrValues != null && !acrValues.isEmpty()) {
                JSONArray acrValuesJsonArray = new JSONArray(acrValues);
                obj.put("acr_values", acrValues);
            }
            if (StringUtils.isNotBlank(registration)) {
                obj.put("registration", registration);
            }

            if (userInfoMember != null || idTokenMember != null) {
                JSONObject claimsObj = new JSONObject();

                if (userInfoMember != null) {
                    claimsObj.put("userinfo", userInfoMember.toJSONObject());
                }
                if (idTokenMember != null) {
                    claimsObj.put("id_token", idTokenMember.toJSONObject());
                }

                obj.put("claims", claimsObj);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return obj;
    }
}