package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.comparator.AuthLevelGroupingXrefComparator;
import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
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
        "id",
        "contentProviderId",
        "contentProviderName",
        "pattern",
        "isPublic",
        "resourceId",
        "resourceName",
        "metaEntitySet",
        "pageTemplates",
        "themeId",
        "groupingXrefs"
})
@DozerDTOCorrespondence(URIPatternEntity.class)
public class URIPattern implements Serializable {

	private String id;
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
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentProviderId == null) ? 0 : contentProviderId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPattern other = (URIPattern) obj;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
				.format("URIPattern [id=%s, contentProviderId=%s, pattern=%s, isPublic=%s, resourceId=%s]",
						id, contentProviderId, pattern, isPublic, resourceId);
	}
	
	
}
