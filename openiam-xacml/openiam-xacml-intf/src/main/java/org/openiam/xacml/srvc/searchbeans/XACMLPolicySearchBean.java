package org.openiam.xacml.srvc.searchbeans;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by zaporozhec on 7/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XACMLPolicySearchBean extends AbstractSearchBean<XACMLPolicyDTO, String> {
    String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
