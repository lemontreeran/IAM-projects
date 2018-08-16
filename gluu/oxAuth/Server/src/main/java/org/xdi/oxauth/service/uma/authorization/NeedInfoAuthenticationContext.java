package org.xdi.oxauth.service.uma.authorization;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/04/2015
 */

public class NeedInfoAuthenticationContext implements Serializable {

    @JsonProperty(value = "required_acr")
    private List<String> requiredAcrs;

    public NeedInfoAuthenticationContext() {
    }

    public List<String> getRequiredAcrs() {
        return requiredAcrs;
    }

    public void setRequiredAcrs(List<String> requiredAcrs) {
        this.requiredAcrs = requiredAcrs;
    }
}
