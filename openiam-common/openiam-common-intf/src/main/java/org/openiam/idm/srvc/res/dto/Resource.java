package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.entitlements.AbstractEntitlementsDTO;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

/**
 * Resources are items that need to be managed or protected. These can be both logic and physical in nature.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource", propOrder = {
        "resourceType",
        "description",
        "displayOrder",
        "URL",
        "roles",
        "resourceProps",
        "groups",
        "childResources",
        "parentResources",
        "minAuthLevel",
        "isPublic",
        "operation",
        "risk",
        "displayNameMap",
        "displayName",
        "coorelatedName",
        "referenceId",
        "groovyScript"
})
@XmlSeeAlso({
        Role.class,
        User.class
})
@DozerDTOCorrespondence(ResourceEntity.class)
@Internationalized
public class Resource extends AbstractEntitlementsDTO {

	@Internationalized
    private ResourceType resourceType;
    private String description;
    private Integer displayOrder;
    private String URL;

    @Deprecated
    private Set<Resource> parentResources = new HashSet<Resource>(0);
    
    @Deprecated
    private Set<Resource> childResources = new HashSet<Resource>(0);

    @Deprecated
    private Set<Role> roles = new HashSet<Role>(0);

    private Set<ResourceProp> resourceProps = new HashSet<ResourceProp>(0); // defined as a Set in Hibernate map

    @Deprecated
    private Set<Group> groups = new HashSet<Group>(0);
    private String minAuthLevel;
    private boolean isPublic = false;
    private ResourceRisk risk;
    private String groovyScript;
    
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> displayNameMap;
	    
    private String displayName;
    private String coorelatedName;
    private String referenceId;

    //private boolean isSSL = false;

    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    public Resource() {
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<ResourceProp> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourceProp> resourceProps) {
        this.resourceProps = resourceProps;
    }

    public ResourceProp getResourceProperty(String propName) {
        if (resourceProps == null) {
            return null;
        }
        for (ResourceProp prop : resourceProps) {
            if (prop.getName().equalsIgnoreCase(propName)) {
                return prop;
            }
        }
        return null;
    }
    
    @Deprecated
    public void addParentResource(final Resource resource) {
    	if(this.parentResources == null) {
    		this.parentResources = new LinkedHashSet<Resource>();
    	}
    	this.parentResources.add(resource);
    }
    
	public String getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(String minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public AttributeOperationEnum getOperation() {
        return operation;
	}

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
	}

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    @Deprecated
    public Set<Role> getRoles() {
        return roles;
    }

    @Deprecated
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Deprecated
    public Set<Group> getGroups() {
        return groups;
    }

    @Deprecated
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Deprecated
	public Set<Resource> getParentResources() {
		return parentResources;
	}

    @Deprecated
	public void setParentResources(Set<Resource> parentResources) {
		this.parentResources = parentResources;
	}

	@Deprecated
	public Set<Resource> getChildResources() {
		return childResources;
	}

	@Deprecated
	public void setChildResources(Set<Resource> childResources) {
		this.childResources = childResources;
	}

    public ResourceRisk getRisk() {
        return risk;
    }

    public void setRisk(ResourceRisk risk) {
        this.risk = risk;
    }

    public Map<String, LanguageMapping> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMapping> displayNameMap) {
		this.displayNameMap = displayNameMap;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCoorelatedName() {
		return coorelatedName;
	}

	public void setCoorelatedName(String coorelatedName) {
		this.coorelatedName = coorelatedName;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	public String getGroovyScript() {
		return groovyScript;
	}

	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result
				+ ((coorelatedName == null) ? 0 : coorelatedName.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((displayOrder == null) ? 0 : displayOrder.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result
				+ ((minAuthLevel == null) ? 0 : minAuthLevel.hashCode());
		result = prime * result
				+ ((resourceType == null) ? 0 : resourceType.hashCode());
		result = prime * result + ((risk == null) ? 0 : risk.hashCode());
		result = prime * result + ((referenceId == null) ? 0 : referenceId.hashCode());
		result = prime * result + ((groovyScript == null) ? 0 : groovyScript.hashCode());
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
		Resource other = (Resource) obj;
		if (URL == null) {
			if (other.URL != null)
				return false;
		} else if (!URL.equals(other.URL))
			return false;
		if (coorelatedName == null) {
			if (other.coorelatedName != null)
				return false;
		} else if (!coorelatedName.equals(other.coorelatedName))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (displayOrder == null) {
			if (other.displayOrder != null)
				return false;
		} else if (!displayOrder.equals(other.displayOrder))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (minAuthLevel == null) {
			if (other.minAuthLevel != null)
				return false;
		} else if (!minAuthLevel.equals(other.minAuthLevel))
			return false;
		if (resourceType == null) {
			if (other.resourceType != null)
				return false;
		} else if (!resourceType.equals(other.resourceType))
			return false;
		if (risk != other.risk)
			return false;
		
		if (referenceId == null) {
			if (other.referenceId != null)
				return false;
		} else if (!referenceId.equals(other.referenceId))
			return false;
		
		if (groovyScript == null) {
			if (other.groovyScript != null)
				return false;
		} else if (!groovyScript.equals(other.groovyScript))
			return false;
		return true;
	}

	
}
