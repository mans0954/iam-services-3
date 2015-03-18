package org.openiam.idm.searchbeans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.policy.dto.Policy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicySearchBean", propOrder = {
        "policyDefId",
        "attributes"
})
public class PolicySearchBean extends AbstractKeyNameSearchBean<Policy, String> {

	private String policyDefId;
	
	private List<Tuple<String,String>> attributes;

    public String getPolicyDefId() {
		return policyDefId;
	}

	public void setPolicyDefId(String policyDefId) {
		this.policyDefId = policyDefId;
	}

    public void addAttribute(final String key, final String value) {
        if(StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
            if(this.attributes == null) {
                this.attributes = new LinkedList<Tuple<String,String>>();
            }
            final Tuple<String, String> tuple = new Tuple<String, String>(key, value);
            this.attributes.add(tuple);
        }
    }

    public List<Tuple<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Tuple<String, String>> attributes) {
        this.attributes = attributes;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((policyDefId == null) ? 0 : policyDefId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicySearchBean other = (PolicySearchBean) obj;
		if (policyDefId == null) {
			if (other.policyDefId != null)
				return false;
		} else if (!policyDefId.equals(other.policyDefId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"PolicySearchBean [policyDefId=%s, toString()=%s]",
				policyDefId, super.toString());
	}
	
}
