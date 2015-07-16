package org.openiam.idm.searchbeans.xacml;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;
import org.openiam.xacml.srvc.dto.XACMLPolicySetDTO;

/**
 * Created by zaporozhec on 7/10/15.
 */
public class XACMLPolicySetSearchBean extends AbstractSearchBean<XACMLPolicySetDTO, String> {
    String identifier;
    private String version;
    private String issuer;
    private String policySetDefaults;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getPolicySetDefaults() {
        return policySetDefaults;
    }

    public void setPolicySetDefaults(String policySetDefaults) {
        this.policySetDefaults = policySetDefaults;
    }
}
