package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.comparator.AuthLevelGroupingXrefComparator;
import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPattern", propOrder = {
        "contentProviderId",
        "contentProviderName",
        "pattern",
        "isPublic",
        "resourceId",
        "resourceName",
        "metaEntitySet",
        "pageTemplates",
        "themeId",
        "groupingXrefs",
        "resourceCoorelatedName"
})
@DozerDTOCorrespondence(URIPatternEntity.class)
public class URIPattern extends KeyDTO {

	private String contentProviderId;
	private String contentProviderName;
	private String pattern;
	private boolean isPublic;
	private String resourceId;
    private String resourceName;
	private Set<URIPatternMeta> metaEntitySet;
	private Set<MetadataElementPageTemplate> pageTemplates;
	private String themeId;
	private Set<AuthLevelGroupingURIPatternXref> groupingXrefs;
	private String resourceCoorelatedName;
	
	public String getContentProviderId() {
		return contentProviderId;
	}
	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Set<URIPatternMeta> getMetaEntitySet() {
		return metaEntitySet;
	}
	public void setMetaEntitySet(Set<URIPatternMeta> metaEntitySet) {
		this.metaEntitySet = metaEntitySet;
	}

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
	public Set<MetadataElementPageTemplate> getPageTemplates() {
		return pageTemplates;
	}
	public void setPageTemplates(Set<MetadataElementPageTemplate> pageTemplates) {
		this.pageTemplates = pageTemplates;
	}
	public String getContentProviderName() {
		return contentProviderName;
	}
	public void setContentProviderName(String contentProviderName) {
		this.contentProviderName = contentProviderName;
	}
	
	public String getThemeId() {
		return themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
	
	public List<AuthLevelGroupingURIPatternXref> getOrderedGroupingXrefs() {
		List<AuthLevelGroupingURIPatternXref> sorted = null;
		if(groupingXrefs != null) {
			sorted = new ArrayList<AuthLevelGroupingURIPatternXref>(groupingXrefs);
			Collections.sort(sorted, new AuthLevelGroupingXrefComparator());
		}
		return sorted;
	}
	
	public Set<AuthLevelGroupingURIPatternXref> getGroupingXrefs() {
		return groupingXrefs;
	}
	public void setGroupingXrefs(Set<AuthLevelGroupingURIPatternXref> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}
	
	public String getResourceCoorelatedName() {
		return resourceCoorelatedName;
	}
	public void setResourceCoorelatedName(String resourceCoorelatedName) {
		this.resourceCoorelatedName = resourceCoorelatedName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
						.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
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
		URIPattern other = (URIPattern) obj;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (themeId == null) {
			if (other.themeId != null)
				return false;
		} else if (!themeId.equals(other.themeId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("URIPattern [contentProviderId=%s, pattern=%s, isPublic=%s, resourceId=%s, themeId=%s, toString()=%s]",
						contentProviderId, pattern, isPublic, resourceId,
						themeId, super.toString());
	}
	
	
	
}
