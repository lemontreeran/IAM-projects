/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.ws.rs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.xdi.oxauth.model.register.RegisterRequestParam.APPLICATION_TYPE;
import static org.xdi.oxauth.model.register.RegisterRequestParam.CLIENT_NAME;
import static org.xdi.oxauth.model.register.RegisterRequestParam.ID_TOKEN_SIGNED_RESPONSE_ALG;
import static org.xdi.oxauth.model.register.RegisterRequestParam.REDIRECT_URIS;
import static org.xdi.oxauth.model.register.RegisterRequestParam.RESPONSE_TYPES;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.RegisterClient;
import org.xdi.oxauth.client.RegisterRequest;
import org.xdi.oxauth.client.RegisterResponse;
import org.xdi.oxauth.model.register.ApplicationType;
import org.xdi.oxauth.model.util.StringUtils;

public class ApplicationTypeRestrictionHttpTest extends BaseTest {

    /**
     * Register a client without specify an Application Type.
     * Read client to check whether it is using the default Application Type <code>web</code>.
     */
    @Parameters({"redirectUris"})
    @Test
    public void omittedApplicationType(final String redirectUris) throws Exception {
        showTitle("omittedApplicationType");

        // 1. Register client
        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(null, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 200, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getClientId());
        assertNotNull(registerResponse.getClientSecret());
        assertNotNull(registerResponse.getRegistrationAccessToken());
        assertNotNull(registerResponse.getClientIdIssuedAt());
        assertNotNull(registerResponse.getClientSecretExpiresAt());

        String registrationAccessToken = registerResponse.getRegistrationAccessToken();
        String registrationClientUri = registerResponse.getRegistrationClientUri();

        // 2. Client read
        RegisterRequest readClientRequest = new RegisterRequest(registrationAccessToken);

        RegisterClient readClient = new RegisterClient(registrationClientUri);
        readClient.setRequest(readClientRequest);
        RegisterResponse readClientResponse = readClient.exec();

        showClient(readClient);
        assertEquals(readClientResponse.getStatus(), 200, "Unexpected response code: " + readClientResponse.getEntity());
        assertNotNull(readClientResponse.getClientId());
        assertNotNull(readClientResponse.getClientSecret());
        assertNotNull(readClientResponse.getClientIdIssuedAt());
        assertNotNull(readClientResponse.getClientSecretExpiresAt());

        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertEquals(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()), ApplicationType.WEB.toString());
        assertNotNull(readClientResponse.getClaims().get(RESPONSE_TYPES.toString()));
        assertNotNull(readClientResponse.getClaims().get(REDIRECT_URIS.toString()));
        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertNotNull(readClientResponse.getClaims().get(CLIENT_NAME.toString()));
        assertNotNull(readClientResponse.getClaims().get(ID_TOKEN_SIGNED_RESPONSE_ALG.toString()));
        assertNotNull(readClientResponse.getClaims().get("scopes"));
    }

    /**
     * Register a client with Application Type <code>web</code>.
     * Read client to check whether it is using the Application Type <code>web</code>.
     */
    @Parameters({"redirectUris"})
    @Test
    public void applicationTypeWeb(final String redirectUris) throws Exception {
        showTitle("applicationTypeWeb");

        // 1. Register client
        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.WEB, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 200, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getClientId());
        assertNotNull(registerResponse.getClientSecret());
        assertNotNull(registerResponse.getRegistrationAccessToken());
        assertNotNull(registerResponse.getClientIdIssuedAt());
        assertNotNull(registerResponse.getClientSecretExpiresAt());

        String registrationAccessToken = registerResponse.getRegistrationAccessToken();
        String registrationClientUri = registerResponse.getRegistrationClientUri();

        // 2. Client read
        RegisterRequest readClientRequest = new RegisterRequest(registrationAccessToken);

        RegisterClient readClient = new RegisterClient(registrationClientUri);
        readClient.setRequest(readClientRequest);
        RegisterResponse readClientResponse = readClient.exec();

        showClient(readClient);
        assertEquals(readClientResponse.getStatus(), 200, "Unexpected response code: " + readClientResponse.getEntity());
        assertNotNull(readClientResponse.getClientId());
        assertNotNull(readClientResponse.getClientSecret());
        assertNotNull(readClientResponse.getClientIdIssuedAt());
        assertNotNull(readClientResponse.getClientSecretExpiresAt());

        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertEquals(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()), ApplicationType.WEB.toString());
        assertNotNull(readClientResponse.getClaims().get(RESPONSE_TYPES.toString()));
        assertNotNull(readClientResponse.getClaims().get(REDIRECT_URIS.toString()));
        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertNotNull(readClientResponse.getClaims().get(CLIENT_NAME.toString()));
        assertNotNull(readClientResponse.getClaims().get(ID_TOKEN_SIGNED_RESPONSE_ALG.toString()));
        assertNotNull(readClientResponse.getClaims().get("scopes"));
    }

    /**
     * Fail: Register a client with Application Type <code>web</code> and Redirect URI with the schema HTTP.
     */
    @Test
    public void applicationTypeWebFail1() throws Exception {
        showTitle("applicationTypeWebFail1");

        final String redirectUris = "http://client.example.com/cb";

        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.WEB, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 400, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getEntity(), "The entity is null");
        assertNotNull(registerResponse.getErrorType(), "The error type is null");
        assertNotNull(registerResponse.getErrorDescription(), "The error description is null");
    }

    /**
     * Fail: Register a client with Application Type <code>web</code> and Redirect URI with the host localhost.
     */
    @Test
    public void applicationTypeWebFail2() throws Exception {
        showTitle("applicationTypeWebFail2");

        final String redirectUris = "https://localhost/cb";

        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.WEB, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 400, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getEntity(), "The entity is null");
        assertNotNull(registerResponse.getErrorType(), "The error type is null");
        assertNotNull(registerResponse.getErrorDescription(), "The error description is null");
    }

    /**
     * Register a client with Application Type <code>native</code>.
     * Read client to check whether it is using the Application Type <code>native</code>.
     */
    @Test
    public void applicationTypeNative() throws Exception {
        showTitle("applicationTypeNative");

        final String redirectUris = "http://localhost/cb";

        // 1. Register client
        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.NATIVE, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 200, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getClientId());
        assertNotNull(registerResponse.getClientSecret());
        assertNotNull(registerResponse.getRegistrationAccessToken());
        assertNotNull(registerResponse.getClientIdIssuedAt());
        assertNotNull(registerResponse.getClientSecretExpiresAt());

        String registrationAccessToken = registerResponse.getRegistrationAccessToken();
        String registrationClientUri = registerResponse.getRegistrationClientUri();

        // 2. Client read
        RegisterRequest readClientRequest = new RegisterRequest(registrationAccessToken);

        RegisterClient readClient = new RegisterClient(registrationClientUri);
        readClient.setRequest(readClientRequest);
        RegisterResponse readClientResponse = readClient.exec();

        showClient(readClient);
        assertEquals(readClientResponse.getStatus(), 200, "Unexpected response code: " + readClientResponse.getEntity());
        assertNotNull(readClientResponse.getClientId());
        assertNotNull(readClientResponse.getClientSecret());
        assertNotNull(readClientResponse.getClientIdIssuedAt());
        assertNotNull(readClientResponse.getClientSecretExpiresAt());

        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertEquals(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()), ApplicationType.NATIVE.toString());
        assertNotNull(readClientResponse.getClaims().get(RESPONSE_TYPES.toString()));
        assertNotNull(readClientResponse.getClaims().get(REDIRECT_URIS.toString()));
        assertNotNull(readClientResponse.getClaims().get(APPLICATION_TYPE.toString()));
        assertNotNull(readClientResponse.getClaims().get(CLIENT_NAME.toString()));
        assertNotNull(readClientResponse.getClaims().get(ID_TOKEN_SIGNED_RESPONSE_ALG.toString()));
        assertNotNull(readClientResponse.getClaims().get("scopes"));
    }

    /**
     * Fail: Register a client with Application Type <code>native</code> and Redirect URI with the schema HTTPS.
     */
    @Test
    public void applicationTypeNativeFail1() throws Exception {
        showTitle("applicationTypeNativeFail1");

        final String redirectUris = "https://client.example.com/cb";

        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.NATIVE, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 400, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getEntity(), "The entity is null");
        assertNotNull(registerResponse.getErrorType(), "The error type is null");
        assertNotNull(registerResponse.getErrorDescription(), "The error description is null");
    }

    /**
     * Fail: Register a client with Application Type <code>web</code> and Redirect URI with the host different than localhost.
     */
    @Parameters({"redirectUris"})
    @Test
    public void applicationTypeNativeFail2(final String redirectUris) throws Exception {
        showTitle("applicationTypeNativeFail2");

        RegisterClient registerClient = new RegisterClient(registrationEndpoint);
        RegisterResponse registerResponse = registerClient.execRegister(ApplicationType.NATIVE, "oxAuth test app",
                StringUtils.spaceSeparatedToList(redirectUris));

        showClient(registerClient);
        assertEquals(registerResponse.getStatus(), 400, "Unexpected response code: " + registerResponse.getEntity());
        assertNotNull(registerResponse.getEntity(), "The entity is null");
        assertNotNull(registerResponse.getErrorType(), "The error type is null");
        assertNotNull(registerResponse.getErrorDescription(), "The error description is null");
    }
}