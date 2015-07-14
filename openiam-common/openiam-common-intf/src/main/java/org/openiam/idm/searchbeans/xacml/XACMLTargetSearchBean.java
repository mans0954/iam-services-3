package org.openiam.idm.searchbeans.xacml;

import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;
import org.openiam.xacml.srvc.dto.XACMLTargetDTO;

/**
 * Created by zaporozhec on 7/10/15.
 */
public class XACMLTargetSearchBean extends AbstractSearchBean<XACMLTargetDTO, String> {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
