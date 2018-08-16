/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxauth.client;

import java.util.List;

import org.xdi.oxauth.model.federation.FederationErrorResponseType;
import org.xdi.oxauth.model.federation.FederationMetadata;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/09/2012
 */

public class FederationMetadataResponse extends BaseResponseWithErrors<FederationErrorResponseType> {

    private List<String> m_existingMetadataIdList;
    private FederationMetadata m_metadata;

    public FederationMetadata getMetadata() {
        return m_metadata;
    }

    public void setMetadata(FederationMetadata p_metadata) {
        m_metadata = p_metadata;
    }

    public List<String> getExistingMetadataIdList() {
        return m_existingMetadataIdList;
    }

    public void setExistingMetadataIdList(List<String> p_existingMetadataIdList) {
        m_existingMetadataIdList = p_existingMetadataIdList;
    }

    @Override
    public FederationErrorResponseType fromString(String p_str) {
        return FederationErrorResponseType.fromString(p_str);
    }
}
