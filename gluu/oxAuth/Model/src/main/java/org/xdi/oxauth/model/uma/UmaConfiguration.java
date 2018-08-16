/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.model.uma;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * UMA metadata configuration
 *
 * @author Yuriy Movchan
 * @author Yuriy Zabrovarnyy
 *         Date: 10/25/2012
 */
@IgnoreMediaTypes("application/*+json")
// try to ignore jettison as it's recommended here: http://docs.jboss.org/resteasy/docs/2.3.4.Final/userguide/html/json.html
@JsonPropertyOrder({"version",
        "issuer",
        "pat_profiles_supported",
        "aat_profiles_supported",
        "rptProfilesSupported",
        "pat_grant_types_supported",
        "aat_grant_types_supported",
        "claim_token_profiles_supported",
        "dynamic_client_endpoint",
        "token_endpoint",
        "authorization_endpoint",
        "requesting_party_claims_endpoint",
        "introspection_endpoint",
        "resource_set_registration_endpoint",
        "permission_registration_endpoint",
        "rpt_endpoint",
        "scope_endpoint",
        "user_endpoint"
})
@XmlRootElement
@ApiModel(value = "UMA Configuration")
public class UmaConfiguration {

    @ApiModelProperty(value = "The version of the UMA core protocol to which this authorization server conforms. The value MUST be the string \"1.0\"."
            , required = true)
    private String version;

    @ApiModelProperty(value = "A URI indicating the party operating the authorization server.",
            required = true)
    private String issuer;

    @ApiModelProperty(value = "OAuth access token profiles supported by this authorization server for PAT issuance. The property value is an array of string values, where each string value is either a reserved keyword defined in this specification or a URI identifying an access token profile defined elsewhere. The reserved keyword \"bearer\" as a value for this property stands for the OAuth bearer token profile [OAuth-bearer]. The authorization server is REQUIRED to support this profile, and to supply this string value explicitly. The authorization server MAY declare its support for additional access token profiles for PATs.",
            required = true)
    private String[] patProfilesSupported;

    @ApiModelProperty(value = "OAuth access token profiles supported by this authorization server for AAT issuance. The property value is an array of string values, where each string value is either a reserved keyword defined in this specification or a URI identifying an access token profile defined elsewhere. The reserved keyword \"bearer\" as a value for this property stands for the OAuth bearer token profile [OAuth-bearer]. The authorization server is REQUIRED to support this profile, and to supply this string value explicitly. The authorization server MAY declare its support for additional access token profiles for AATs.",
            required = true)
    private String[] aatProfilesSupported;

    @ApiModelProperty(value = "UMA RPT profiles supported by this authorization server for RPT issuance. The property value is an array of string values, where each string value is either a reserved keyword defined in this specification or a URI identifying an RPT profile defined elsewhere. The reserved keyword \"bearer\" as a value for this property stands for the UMA bearer RPT profile defined in Section 3.3.2. The authorization server is REQUIRED to support this profile, and to supply this string value explicitly. The authorization server MAY declare its support for additional RPT profiles.",
            required = true)
    private String[] rptProfilesSupported;

    @ApiModelProperty(value = "OAuth grant types supported by this authorization server in issuing PATs. The property value is an array of string values. Each string value MUST be one of the grant_type values defined in [OAuth2], or alternatively a URI identifying a grant type defined elsewhere.",
            required = true)
    private String[] patGrantTypesSupported;

    @ApiModelProperty(value = "OAuth grant types supported by this authorization server in issuing AATs. The property value is an array of string values. Each string value MUST be one of the grant_type values defined in [OAuth2], or alternatively a URI identifying a grant type defined elsewhere.",
            required = true)
    private String[] aatGrantTypesSupported;

    @ApiModelProperty(value = "Claim formats and associated sub-protocols for gathering claims from requesting parties, as supported by this authorization server. The property value is an array of string values, which each string value is either a reserved keyword defined in this specification or a URI identifying a claim profile defined elsewhere.",
            required = true)
    private String[] claimTokenProfilesSupported;

    @ApiModelProperty(value = "The endpoint to use for performing dynamic client registration. Usage of this endpoint is defined by [DynClientReg]. The presence of this property indicates authorization server support for the dynamic client registration feature and its absence indicates a lack of support.",
            required = true)
    private String dynamicClientEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the resource server or client asks the authorization server for a PAT or AAT, respectively. A requested scope of \"uma_protection\" results in a PAT. A requested scope of \"uma_authorization\" results in an AAT. Usage of this endpoint is defined by [OAuth2].",
            required = true)
    private String tokenEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the resource server gathers the consent of the end-user resource owner or the client gathers the consent of the end-user requesting party, if the \"authorization_code\" grant type is used. Usage of this endpoint is defined by [OAuth2].",
            required = true)
    private String userEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the resource server introspects an RPT presented to it by a client. Usage of this endpoint is defined by [OAuth-introspection] and Section 3.3.1. A valid PAT MUST accompany requests to this protected endpoint.",
            required = true)
    private String resourceSetRegistrationEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the resource server introspects an RPT presented to it by a client. Usage of this endpoint is defined by [OAuth-introspection] and Section 3.3.1. A valid PAT MUST accompany requests to this protected endpoint.",
            required = true)
    private String introspectionEndpoint;

    @ApiModelProperty(value = " The endpoint URI at which the resource server registers a client-requested permission with the authorization server. Usage of this endpoint is defined by Section 3.2. A valid PAT MUST accompany requests to this protected endpoint.",
            required = true)
    private String permissionRegistrationEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the client asks the authorization server for an RPT. Usage of this endpoint is defined by Section 3.4.1. A valid AAT MUST accompany requests to this protected endpoint.",
            required = true)
    private String rptEndpoint;

    @ApiModelProperty( value = "The endpoint URI at which the client asks to have authorization data associated with its RPT. Usage of this endpoint is defined in Section 3.4.2. A valid AAT MUST accompany requests to this protected endpoint.",
            required = true)
    private String authorizationEndpoint;

    @ApiModelProperty(value = "Scope endpoint.",
            required = true)
    private String scopeEndpoint;

    @ApiModelProperty(value = "The endpoint URI at which the authorization server interacts with the end-user requesting party to gather claims. If this property is absent, the authorization server does not interact with the end-user requesting party for claims gathering.",
            required = false)
    private String requestingPartyClaimsEndpoint;

    @JsonProperty(value = "requesting_party_claims_endpoint")
    @XmlElement(name = "requesting_party_claims_endpoint")
    public String getRequestingPartyClaimsEndpoint() {
        return requestingPartyClaimsEndpoint;
    }

    @ApiModelProperty(value = "The endpoint URI at which the authorization server interacts with the end-user requesting party to gather claims. If this property is absent, the authorization server does not interact with the end-user requesting party for claims gathering.",
            required = false)
    public void setRequestingPartyClaimsEndpoint(String requestingPartyClaimsEndpoint) {
        this.requestingPartyClaimsEndpoint = requestingPartyClaimsEndpoint;
    }

    @JsonProperty(value = "scope_endpoint")
    @XmlElement(name = "scope_endpoint")
    public String getScopeEndpoint() {
        return scopeEndpoint;
    }

    public void setScopeEndpoint(String p_scopeEndpoint) {
        scopeEndpoint = p_scopeEndpoint;
    }

    @JsonProperty(value = "pat_profiles_supported")
    @XmlElement(name = "pat_profiles_supported")
    public String[] getPatProfilesSupported() {
        return patProfilesSupported;
    }

    public void setPatProfilesSupported(String[] p_patProfilesSupported) {
        patProfilesSupported = p_patProfilesSupported;
    }

    @JsonProperty(value = "version")
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty(value = "issuer")
    @XmlElement(name = "issuer")
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @JsonProperty(value = "aat_profiles_supported")
    @XmlElement(name = "aat_profiles_supported")
    public String[] getAatProfilesSupported() {
        return aatProfilesSupported;
    }

    public void setAatProfilesSupported(String[] oauthTokenProfilesSupported) {
        this.aatProfilesSupported = oauthTokenProfilesSupported;
    }

    @JsonProperty(value = "rpt_profiles_supported")
    @XmlElement(name = "rpt_profiles_supported")
    public String[] getRptProfilesSupported() {
        return rptProfilesSupported;
    }

    public void setRptProfilesSupported(String[] umaTokenProfilesSupported) {
        this.rptProfilesSupported = umaTokenProfilesSupported;
    }

    @JsonProperty(value = "aat_grant_types_supported")
    @XmlElement(name = "aat_grant_types_supported")
    public String[] getAatGrantTypesSupported() {
        return aatGrantTypesSupported;
    }

    public void setAatGrantTypesSupported(String[] p_aatGrantTypesSupported) {
        this.aatGrantTypesSupported = p_aatGrantTypesSupported;
    }


    @JsonProperty(value = "pat_grant_types_supported")
    @XmlElement(name = "pat_grant_types_supported")
    public String[] getPatGrantTypesSupported() {
        return patGrantTypesSupported;
    }

    public void setPatGrantTypesSupported(String[] p_patGrantTypesSupported) {
        this.patGrantTypesSupported = p_patGrantTypesSupported;
    }

    @JsonProperty(value = "claim_token_profiles_supported")
    @XmlElement(name = "claim_token_profiles_supported")
    public String[] getClaimTokenProfilesSupported() {
        return claimTokenProfilesSupported;
    }

    public void setClaimTokenProfilesSupported(String[] claimTokenProfilesSupported) {
        this.claimTokenProfilesSupported = claimTokenProfilesSupported;
    }

    @JsonProperty(value = "dynamic_client_endpoint")
    @XmlElement(name = "dynamic_client_endpoint")
    public String getDynamicClientEndpoint() {
        return dynamicClientEndpoint;
    }

    public void setDynamicClientEndpoint(String dynamicClientEndpoint) {
        this.dynamicClientEndpoint = dynamicClientEndpoint;
    }

    @JsonProperty(value = "token_endpoint")
    @XmlElement(name = "token_endpoint")
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    @JsonProperty(value = "user_endpoint")
    @XmlElement(name = "user_endpoint")
    public String getUserEndpoint() {
        return userEndpoint;
    }

    public void setUserEndpoint(String userEndpoint) {
        this.userEndpoint = userEndpoint;
    }

    @JsonProperty(value = "resource_set_registration_endpoint")
    @XmlElement(name = "resource_set_registration_endpoint")
    public String getResourceSetRegistrationEndpoint() {
        return resourceSetRegistrationEndpoint;
    }

    public void setResourceSetRegistrationEndpoint(String resourceSetRegistrationEndpoint) {
        this.resourceSetRegistrationEndpoint = resourceSetRegistrationEndpoint;
    }

    @JsonProperty(value = "introspection_endpoint")
    @XmlElement(name = "introspection_endpoint")
    public String getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }

    public void setIntrospectionEndpoint(String rptStatusEndpoint) {
        this.introspectionEndpoint = rptStatusEndpoint;
    }

    @JsonProperty(value = "permission_registration_endpoint")
    @XmlElement(name = "permission_registration_endpoint")
    public String getPermissionRegistrationEndpoint() {
        return permissionRegistrationEndpoint;
    }

    public void setPermissionRegistrationEndpoint(String permissionRegistrationEndpoint) {
        this.permissionRegistrationEndpoint = permissionRegistrationEndpoint;
    }

    @JsonProperty(value = "rpt_endpoint")
    @XmlElement(name = "rpt_endpoint")
    public String getRptEndpoint() {
        return rptEndpoint;
    }

    public void setRptEndpoint(String rptEndpoint) {
        this.rptEndpoint = rptEndpoint;
    }

    @JsonProperty(value = "authorization_endpoint")
    @XmlElement(name = "authorization_endpoint")
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String permissionRequestEndpoint) {
        this.authorizationEndpoint = permissionRequestEndpoint;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MetadataConfiguration [version=");
        builder.append(version);
        builder.append(", issuer=");
        builder.append(issuer);
        builder.append(", patProfilesSupported=");
        builder.append(Arrays.toString(patProfilesSupported));
        builder.append(", aatProfilesSupported=");
        builder.append(Arrays.toString(aatProfilesSupported));
        builder.append(", rptProfilesSupported=");
        builder.append(Arrays.toString(rptProfilesSupported));
        builder.append(", patGrantTypesSupported=");
        builder.append(Arrays.toString(patGrantTypesSupported));
        builder.append(", aatGrantTypesSupported=");
        builder.append(Arrays.toString(aatGrantTypesSupported));
        builder.append(", claimProfilesSupported=");
        builder.append(Arrays.toString(claimTokenProfilesSupported));
        builder.append(", tokenEndpoint=");
        builder.append(tokenEndpoint);
        builder.append(", userEndpoint=");
        builder.append(userEndpoint);
        builder.append(", introspection_endpoint=");
        builder.append(introspectionEndpoint);
        builder.append(", resourceSetRegistrationEndpoint=");
        builder.append(resourceSetRegistrationEndpoint);
        builder.append(", permissionRegistrationEndpoint=");
        builder.append(permissionRegistrationEndpoint);
        builder.append(", rptEndpoint=");
        builder.append(rptEndpoint);
        builder.append(", authorization_request_endpoint=");
        builder.append(authorizationEndpoint);
        builder.append("]");
        return builder.toString();
    }

}
