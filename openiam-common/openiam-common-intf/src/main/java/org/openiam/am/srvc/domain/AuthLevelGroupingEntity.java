package org.openiam.am.srvc.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "AUTH_LEVEL_GROUPING")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(AuthLevelGrouping.class)
public class AuthLevelGroupingEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "AUTH_LEVEL_GROUPING_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name = "NAME", length = 100, nullable = false)
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AUTH_LEVEL_ID", referencedColumnName = "AUTH_LEVEL_ID", insertable=true, updatable=true, nullable=false)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private AuthLevelEntity authLevel;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "grouping")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelAttributeEntity> attributes;
	
	@OneToMany(orphanRemoval = true, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "grouping", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelGroupingURIPatternXrefEntity> patternXrefs;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "grouping", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<AuthLevelGroupingContentProviderXrefEntity> contentProviderXrefs;
	
	public AuthLevelGroupingEntity() {
		
	}

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
	public AuthLevelEntity getAuthLevel() {
		return authLevel;
	}

	public void setAuthLevel(AuthLevelEntity authLevel) {
		this.authLevel = authLevel;
	}

	public Set<AuthLevelAttributeEntity> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AuthLevelAttributeEntity> attributes) {
		this.attributes = attributes;
	}

	public Set<AuthLevelGroupingURIPatternXrefEntity> getPatternXrefs() {
		return patternXrefs;
	}

	public void setPatternXrefs(
			Set<AuthLevelGroupingURIPatternXrefEntity> patternXrefs) {
		this.patternXrefs = patternXrefs;
	}

	public Set<AuthLevelGroupingContentProviderXrefEntity> getContentProviderXrefs() {
		return contentProviderXrefs;
	}

	public void setContentProviderXrefs(
			Set<AuthLevelGroupingContentProviderXrefEntity> contentProviderXrefs) {
		this.contentProviderXrefs = contentProviderXrefs;
	}
	
	public void addContentProviderXref(final AuthLevelGroupingContentProviderXrefEntity xref) {
		if(xref != null) {
			if(this.contentProviderXrefs == null) {
				this.contentProviderXrefs = new HashSet<AuthLevelGroupingContentProviderXrefEntity>();
			}
			this.contentProviderXrefs.add(xref);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authLevel == null) ? 0 : authLevel.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AuthLevelGroupingEntity other = (AuthLevelGroupingEntity) obj;
		if (authLevel == null) {
			if (other.authLevel != null)
				return false;
		} else if (!authLevel.equals(other.authLevel))
			return false;
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
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuthLevelGroupingEntity [id=%s, authLevel=%s, name=%s]",
				id, authLevel, name);
	}
	
	
}
