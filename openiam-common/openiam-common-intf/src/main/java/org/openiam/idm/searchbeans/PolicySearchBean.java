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
        "name",
        "policyDefId",
        "attributes"
})
public class PolicySearchBean extends AbstractSearchBean<Policy, String> implements SearchBean<Policy, String>, Serializable {

    private String name;

	private String policyDefId;

    private List<Tuple<String,String>> attributes;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PolicySearchBean that = (PolicySearchBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (policyDefId != null ? !policyDefId.equals(that.policyDefId) : that.policyDefId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (policyDefId != null ? policyDefId.hashCode() : 0);
        return result;
    }
}
