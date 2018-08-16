package org.xdi.oxauth.ws.rs.uma;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.uma.RptAuthorizationRequestService;
import org.xdi.oxauth.client.uma.RptStatusService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.client.uma.wrapper.UmaClient;
import org.xdi.oxauth.model.uma.RptAuthorizationRequest;
import org.xdi.oxauth.model.uma.RptAuthorizationResponse;
import org.xdi.oxauth.model.uma.RptIntrospectionResponse;
import org.xdi.oxauth.model.uma.UmaConfiguration;
import org.xdi.oxauth.model.uma.UmaTestUtil;
import org.xdi.oxauth.model.uma.wrapper.Token;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/04/2015
 */

public class TrustElevationHttpTest extends BaseTest {

    protected UmaConfiguration metadataConfiguration;

    protected ObtainRptTokenFlowHttpTest umaObtainRptTokenFlowHttpTest;

    protected RegisterResourceSetFlowHttpTest umaRegisterResourceSetFlowHttpTest;
    protected RegisterResourceSetPermissionFlowHttpTest umaRegisterResourceSetPermissionFlowHttpTest;

    protected RptStatusService rptStatusService;
    protected RptAuthorizationRequestService rptPermissionAuthorizationService;

    protected Token m_aat;
    protected Token m_pat;

    @Test
    @Parameters({"umaMetaDataUrl", "umaAmHost",
            "umaPatClientId", "umaPatClientSecret",
            "umaAatClientId", "umaAatClientSecret"
    })
    public void trustElevation(final String umaMetaDataUrl, final String umaAmHost,
                     final String umaPatClientId, final String umaPatClientSecret,
                     final String umaAatClientId, final String umaAatClientSecret
    ) throws Exception {
        this.metadataConfiguration = UmaClientFactory.instance().createMetaDataConfigurationService(umaMetaDataUrl).getMetadataConfiguration();
        UmaTestUtil.assert_(this.metadataConfiguration);

        this.umaObtainRptTokenFlowHttpTest = new ObtainRptTokenFlowHttpTest(this.metadataConfiguration);
        this.umaRegisterResourceSetFlowHttpTest = new RegisterResourceSetFlowHttpTest(this.metadataConfiguration);
        this.umaRegisterResourceSetPermissionFlowHttpTest = new RegisterResourceSetPermissionFlowHttpTest(this.metadataConfiguration);

        this.rptStatusService = UmaClientFactory.instance().createRptStatusService(metadataConfiguration);
        this.rptPermissionAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(metadataConfiguration);

        m_pat = UmaClient.requestPat(tokenEndpoint, umaPatClientId, umaPatClientSecret);
        m_aat = UmaClient.requestAat(tokenEndpoint, umaAatClientId, umaAatClientSecret);

        UmaTestUtil.assert_(m_pat);
        UmaTestUtil.assert_(m_aat);

        final List<String> rsScopes = Arrays.asList("http://gluu.example.com/dev/scopes/view", "http://gluu.example.com/dev/scopes/all");

        this.umaRegisterResourceSetFlowHttpTest.m_pat = m_pat;
        final String resourceId = this.umaRegisterResourceSetFlowHttpTest.registerResourceSet(
                rsScopes);

        this.umaObtainRptTokenFlowHttpTest.m_aat = this.m_aat;
        this.umaObtainRptTokenFlowHttpTest.testObtainRptTokenFlow(umaAmHost);

        this.umaRegisterResourceSetPermissionFlowHttpTest.umaRegisterResourceSetFlowHttpTest = umaRegisterResourceSetFlowHttpTest;
        this.umaRegisterResourceSetPermissionFlowHttpTest.registerResourceSetPermission(umaAmHost, resourceId, rsScopes);

        RptIntrospectionResponse rptStatus = this.rptStatusService.requestRptStatus("Bearer " + m_pat.getAccessToken(),
                this.umaObtainRptTokenFlowHttpTest.rptToken, "");

        RptAuthorizationRequest rptAuthorizationRequest = new RptAuthorizationRequest(this.umaObtainRptTokenFlowHttpTest.rptToken, umaRegisterResourceSetPermissionFlowHttpTest.ticketForFullAccess);

        try {
            RptAuthorizationResponse authorizationResponse = this.rptPermissionAuthorizationService.requestRptPermissionAuthorization(
                    "Bearer " + m_aat.getAccessToken(), umaAmHost, rptAuthorizationRequest);
        } catch (ClientResponseFailure ex) {
            System.err.println(ex.getResponse().getEntity(String.class));
            throw ex;
        }

        rptStatus = this.rptStatusService.requestRptStatus("Bearer " + m_pat.getAccessToken(),
                       this.umaObtainRptTokenFlowHttpTest.rptToken, "");

    }
}
