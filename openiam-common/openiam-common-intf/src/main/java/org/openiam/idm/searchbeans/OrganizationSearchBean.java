package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "keySet",
        "name",
		"organizationTypeIdSet",
		"userId",
		"parentId",
		"childId",
		"validParentTypeId",
        "internalOrgId"
})
public class OrganizationSearchBean extends AbstractSearchBean<Organization, String> implements SearchBean<Organization, String>,
        Serializable {
    private static final long serialVersionUID = 1L;
    private Set<String> keySet;
    private String name;
    private Set<String> organizationTypeIdSet;
    private String userId;
    private String parentId;
    private String childId;
    private String validParentTypeId;
    private String internalOrgId;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganizationTypeId() {
        return (CollectionUtils.isNotEmpty(organizationTypeIdSet)) ? organizationTypeIdSet.iterator().next() : null;
//		return organizationTypeId;
	}

	public void setOrganizationTypeId(String organizationTypeId) {
//		this.organizationTypeId = organizationTypeId;
        this.addOrganizationTypeId(organizationTypeId);
	}

    public void addOrganizationTypeId(final String organizationTypeId) {
        if(organizationTypeIdSet == null) {
            organizationTypeIdSet = new HashSet<String>();
        }
        organizationTypeIdSet.add(organizationTypeId);
    }

    public Set<String> getOrganizationTypeIdSet() {
        return organizationTypeIdSet;
    }

    public void setOrganizationTypeIdSet(final List<String> organizationTypeIdSet) {
        if(organizationTypeIdSet != null) {
            setKeys(new HashSet<String>(organizationTypeIdSet));
        }
    }

    public void setOrganizationTypeIdSet(final Set<String> organizationTypeIdSet) {
        this.organizationTypeIdSet = organizationTypeIdSet;
    }

	@Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
//        if(keySet == null) {
//            keySet = new HashSet<String>();
//        }
//        keySet.add(key);
        this.addKey(key);
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
    
    public void setKeys(final List<String> keySet) {
    	if(keySet != null) {
    		setKeys(new HashSet<String>(keySet));
    	}
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	public String getValidParentTypeId() {
		return validParentTypeId;
	}

	public void setValidParentTypeId(String validParentTypeId) {
		this.validParentTypeId = validParentTypeId;
	}

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }
}
