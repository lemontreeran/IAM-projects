/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.ws.rs.uma;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.uma.CreateRptService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.RPTResponse;
import org.xdi.oxauth.model.uma.UmaConfiguration;
import org.xdi.oxauth.model.uma.UmaTestUtil;
import org.xdi.oxauth.model.uma.wrapper.Token;

import javax.ws.rs.core.Response;

import static org.testng.Assert.*;

/**
 * Test cases for the obtaining UMA requester permission token flow (HTTP)
 *
 * @author Yuriy Movchan Date: 10/03/2012
 */
public class ObtainRptTokenFlowHttpTest extends BaseTest {

    protected UmaConfiguration metadataConfiguration;

    protected Token m_aat;
    protected String rptToken;

    public ObtainRptTokenFlowHttpTest() {
    }

    public ObtainRptTokenFlowHttpTest(UmaConfiguration metadataConfiguration) {
        this.metadataConfiguration = metadataConfiguration;
    }

    @BeforeClass
    @Parameters({"umaMetaDataUrl", "umaAatClientId", "umaAatClientSecret"})
    public void init(final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientSecret) throws Exception {
        if (this.metadataConfiguration == null) {
            this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl).getMetadataConfiguration();
            UmaTestUtil.assert_(this.metadataConfiguration);
        }

        m_aat = UmaClient.requestAat(tokenEndpoint, umaAatClientId, umaAatClientSecret);
        UmaTestUtil.assert_(m_aat);
    }

    /**
     * Test for the obtaining UMA RPT token
     */
    @Test
    @Parameters({"umaAmHost"})
    public void testObtainRptTokenFlow(final String umaAmHost) throws Exception {
        showTitle("testObtainRptTokenFlow");

        CreateRptService requesterPermissionTokenService = UmaClientFactory.instance().createRequesterPermissionTokenService(this.metadataConfiguration);

        // Get requester permission token
        RPTResponse requesterPermissionTokenResponse = null;
        try {
            requesterPermissionTokenResponse = requesterPermissionTokenService.createRPT("Bearer " + m_aat.getAccessToken(), umaAmHost);
        } catch (ClientResponseFailure ex) {
            System.err.println(ex.getResponse().getEntity(String.class));
            throw ex;
        }

        UmaTestUtil.assert_(requesterPermissionTokenResponse);

        this.rptToken = requesterPermissionTokenResponse.getRpt();
    }

    /**
     * Test for the obtaining UMA RPT token
     */
    @Test
    @Parameters({"umaAmHost"})
    public void testObtainRptTokenFlowWithInvalidAat(final String umaAmHost) throws Exception {
        showTitle("testObtainRptTokenFlowWithInvalidAat");

        CreateRptService requesterPermissionTokenService = UmaClientFactory.instance().createRequesterPermissionTokenService(this.metadataConfiguration);

        // Get requester permission token
        RPTResponse requesterPermissionTokenResponse = null;
        try {
            requesterPermissionTokenResponse = requesterPermissionTokenService.createRPT("Bearer " + m_aat.getAccessToken() + "_invalid", umaAmHost);
        } catch (ClientResponseFailure ex) {
            System.err.println(ex.getResponse().getEntity(String.class));
            assertEquals(ex.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode(), "Unexpected response status");
        }

        assertNull(requesterPermissionTokenResponse, "Requester permission token response is not null");
    }
}