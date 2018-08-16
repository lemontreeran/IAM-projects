/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.client;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.xdi.oxauth.model.token.ValidateTokenErrorResponseType;

/**
 * Encapsulates functionality to make validate token request calls to an authorization server via REST Services.
 *
 * @author Javier Rojas Blum Date: 10.27.2011
 */
public class ValidateTokenClient extends BaseClient<ValidateTokenRequest, ValidateTokenResponse> {

    private static final String mediaType = MediaType.APPLICATION_JSON;

    /**
     * Constructs a validate token client by providing an URL where the REST service is located.
     *
     * @param url The REST Service location.
     */
    public ValidateTokenClient(String url) {
        super(url);
    }

    @Override
    public ValidateTokenRequest getRequest() {
        if (request instanceof ValidateTokenRequest) {
            return (ValidateTokenRequest) request;
        } else {
            return null;
        }
    }

    @Override
    public void setRequest(ValidateTokenRequest request) {
        super.request = request;
    }

    @Override
    public ValidateTokenResponse getResponse() {
        if (response instanceof ValidateTokenResponse) {
            return (ValidateTokenResponse) response;
        } else {
            return null;
        }
    }

    @Override
    public void setResponse(ValidateTokenResponse response) {
        super.response = response;
    }

    @Override
    public String getHttpMethod() {
        return HttpMethod.GET;
    }

    /**
     * Executes the call to the REST Service requesting to validate a token and processes the response.
     *
     * @param accessToken The access token to be validated.
     * @return The service response.
     */
    public ValidateTokenResponse execValidateToken(String accessToken) {
        setRequest(new ValidateTokenRequest());
        getRequest().setAccessToken(accessToken);

        return exec();
    }

    /**
     * Executes the call to the REST Service and processes the response.
     *
     * @return The service response.
     */
    public ValidateTokenResponse exec() {
        // Prepare request parameters
        initClientRequest();
        clientRequest.accept(mediaType);
        clientRequest.setHttpMethod(getHttpMethod());

        if (StringUtils.isNotBlank(getRequest().getAccessToken())) {
            clientRequest.queryParameter("access_token", getRequest().getAccessToken());
        }

        // Call REST Service and handle response
        try {
            clientResponse = clientRequest.get(String.class);
            int status = clientResponse.getStatus();

            setResponse(new ValidateTokenResponse(status));
            getResponse().setHeaders(clientResponse.getHeaders());

            String entity = clientResponse.getEntity(String.class);
            getResponse().setEntity(entity);
            if (entity != null && !entity.isEmpty()) {
                JSONObject jsonObj = new JSONObject(entity);
                if (jsonObj.has("valid")) {
                    getResponse().setValid(jsonObj.getBoolean("valid"));
                }
                if (jsonObj.has("expires_in")) {
                    getResponse().setExpiresIn(jsonObj.getInt("expires_in"));
                }
                if (jsonObj.has("error")) {
                    getResponse().setErrorType(ValidateTokenErrorResponseType.fromString(jsonObj.getString("error")));
                }
                if (jsonObj.has("error_description")) {
                    getResponse().setErrorDescription(jsonObj.getString("error_description"));
                }
                if (jsonObj.has("error_uri")) {
                    getResponse().setErrorUri(jsonObj.getString("error_uri"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return getResponse();
    }
}