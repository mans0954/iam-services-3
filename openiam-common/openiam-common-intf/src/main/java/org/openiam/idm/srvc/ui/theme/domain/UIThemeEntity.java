package org.openiam.idm.srvc.ui.theme.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;

@Entity
@Table(name = "UI_THEME")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(UITheme.class)
public class UIThemeEntity implements Serializable {
	
	public UIThemeEntity() {}

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "UI_THEME_ID", length = 32)
	private String id;
	
	@Column(name = "UI_THEME_NAME", length = 100)
    @Size(max = 100, message = "ui.theme.name.too.long")
	private String name;
	
	@Column(name = "URL", length = 300)
    @Size(max = 300, message = "ui.theme.url.too.long")
	private String url;
	
	@OneToMany(orphanRemoval = false, cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "uiTheme", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<ContentProviderEntity> contentProviders;
	
	@OneToMany(orphanRemoval = false, cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "uiTheme", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<URIPatternEntity> uriPatterns;

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

	public Set<ContentProviderEntity> getContentProviders() {
		return contentProviders;
	}

	public void setContentProviders(Set<ContentProviderEntity> contentProviders) {
		this.contentProviders = contentProviders;
	}

	public Set<URIPatternEntity> getUriPatterns() {
		return uriPatterns;
	}

	public void setUriPatterns(Set<URIPatternEntity> uriPatterns) {
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
		UIThemeEntity other = (UIThemeEntity) obj;
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
		return String.format("UIThemeEntity [id=%s, name=%s, url=%s]", id,
				name, url);
	}
	
	
}
