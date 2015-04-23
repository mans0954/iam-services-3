package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceRisk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceSearchBean", propOrder = {
        "name",
        "resourceTypeId",
        "rootsOnly",
        "attributes",
        "excludeResourceTypes",
        "risk",
        "URL",
        "metadataType",
        "coorelatedName",
		"adminResourceId",
        "ownerId"
})
public class ResourceSearchBean extends EntitlementsSearchBean<Resource, String> implements SearchBean<Resource, String>, Serializable {

	private static final long serialVersionUID = 1L;
    public static final String TYPE_MANAGED_SYS = "MANAGED_SYS";

	private String name;
	private String resourceTypeId;
	private Boolean rootsOnly;
	private List<Tuple<String, String>> attributes;
	private Set<String> excludeResourceTypes;
    private ResourceRisk risk;
    private String metadataType;
    private String URL;
	private String coorelatedName;
    private String adminResourceId;
    private String ownerId;

    public String getCoorelatedName() {
        return coorelatedName;
    }

    public void setCoorelatedName(String coorelatedName) {
        this.coorelatedName = coorelatedName;
    }

    public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public Boolean getRootsOnly() {
		return rootsOnly;
	}

	public void setRootsOnly(Boolean rootsOnly) {
		this.rootsOnly = rootsOnly;
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

	public Set<String> getExcludeResourceTypes() {
		return excludeResourceTypes;
	}

	public void setExcludeResourceTypes(Set<String> excludeResourceTypes) {
		this.excludeResourceTypes = excludeResourceTypes;
	}
	
	public void addExcludeResourceType(final String excludeResourceType) {
		if(StringUtils.isNotBlank(excludeResourceType)) {
			if(this.excludeResourceTypes == null) {
				this.excludeResourceTypes = new HashSet<String>();
			}
			this.excludeResourceTypes.add(excludeResourceType);
		}
	}

    public ResourceRisk getRisk() {
        return risk;
    }

    public void setRisk(ResourceRisk risk) {
        this.risk = risk;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

	public String getAdminResourceId() {
		return adminResourceId;
	}

	public void setAdminResourceId(String adminResourceId) {
		this.adminResourceId = adminResourceId;
	}

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(resourceTypeId != null ? resourceTypeId : "")
                .append(rootsOnly)
                .append(adminResourceId != null ? adminResourceId : "")
                .append(ownerId != null ? ownerId : "")
                .append(risk != null ? risk.name() : "")
                .append(metadataType != null ? metadataType : "")
                .append(URL != null ? URL : "")
                .append(coorelatedName != null ? coorelatedName : "")
                .append(attributes != null ? attributes.toString().hashCode() : "")
                .append(getKey() != null ? getKey() : "")
                .append(excludeResourceTypes != null ? excludeResourceTypes.toString().hashCode() : "")
                .append(groupIdSet != null ? groupIdSet.toString().hashCode() : "")
                .append(roleIdSet != null ? roleIdSet.toString().hashCode() : "")
                .append(resourceIdSet != null ? resourceIdSet.toString().hashCode() : "")
                .append(organizationIdSet != null ? organizationIdSet.toString().hashCode() : "")
                .append(userIdSet != null ? userIdSet.toString().hashCode() : "")
                .append(getParentIdSet() != null ? getParentIdSet().toString().hashCode() : "")
                .append(getChildIdSet() != null ? getChildIdSet().toString().hashCode() : "")
                .toString();
    }
}
