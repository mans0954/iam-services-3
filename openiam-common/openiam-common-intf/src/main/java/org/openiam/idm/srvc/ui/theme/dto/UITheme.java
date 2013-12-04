package org.openiam.idm.srvc.ui.theme.dto;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UITheme", propOrder = {
	"id",
	"name",
	"url",
	"contentProviders",
	"uriPatterns"
})
@DozerDTOCorrespondence(UIThemeEntity.class)
public class UITheme implements Serializable {
	
	public UITheme() {}

	private String id;
	private String name;
	private String url;
	
	private Set<ContentProvider> contentProviders;
	private Set<URIPattern> uriPatterns;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<ContentProvider> getContentProviders() {
		return contentProviders;
	}

	public void setContentProviders(Set<ContentProvider> contentProviders) {
		this.contentProviders = contentProviders;
	}

	public Set<URIPattern> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(Set<URIPattern> uriPatterns) {
		this.uriPatterns = uriPatterns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		UITheme other = (UITheme) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("UITheme [id=%s, name=%s, url=%s]", id,
				name, url);
	}
	
	
}
