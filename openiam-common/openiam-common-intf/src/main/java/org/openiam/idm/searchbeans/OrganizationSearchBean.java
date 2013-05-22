package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "keySet",
        "organizationName",
		"typeId",
		"classification"
})
public class OrganizationSearchBean extends AbstractSearchBean<Organization, String> implements SearchBean<Organization, String>,
        Serializable {
    private static final long serialVersionUID = 1L;
    private Set<String> keySet;
    private String organizationName;
    private String typeId;
    private String classification;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

    @Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
        if(keySet == null) {
            keySet = new HashSet<String>();
        }
        keySet.add(key);
    }

    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if(this.keySet == null) {
            this.keySet = new HashSet<String>();
        }
        this.keySet.add(key);
    }

    public boolean hasMultipleKeys() {
        return (keySet != null && keySet.size() > 1);
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }
}
