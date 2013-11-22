package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.policy.dto.Policy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicySearchBean", propOrder = {
        "name",
        "policyDefId"
})
public class PolicySearchBean extends AbstractSearchBean<Policy, String> implements SearchBean<Policy, String>, Serializable {

    private String name;

	private String policyDefId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicyDefId() {
		return policyDefId;
	}

	public void setPolicyDefId(String policyDefId) {
		this.policyDefId = policyDefId;
	}
	
}
