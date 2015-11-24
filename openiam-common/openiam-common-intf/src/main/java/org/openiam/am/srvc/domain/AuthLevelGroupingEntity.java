package org.openiam.am.srvc.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "AUTH_LEVEL_GROUPING")
@DozerDTOCorrespondence(AuthLevelGrouping.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "AUTH_LEVEL_GROUPING_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable = false))
})
public class AuthLevelGroupingEntity extends AbstractKeyNameEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AUTH_LEVEL_ID", referencedColumnName = "AUTH_LEVEL_ID", insertable=true, updatable=true, nullable=false)
	private AuthLevelEntity authLevel;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "grouping")
	private Set<AuthLevelAttributeEntity> attributes;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "grouping", fetch = FetchType.LAZY)
	private Set<AuthLevelGroupingURIPatternXrefEntity> patternXrefs;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "grouping", fetch = FetchType.LAZY)
	private Set<AuthLevelGroupingContentProviderXrefEntity> contentProviderXrefs;
	
	public AuthLevelGroupingEntity() {
		
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
		int result = super.hashCode();
		result = prime * result
				+ ((authLevel == null) ? 0 : authLevel.hashCode());
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
		AuthLevelGroupingEntity other = (AuthLevelGroupingEntity) obj;
		if (authLevel == null) {
			if (other.authLevel != null)
				return false;
		} else if (!authLevel.equals(other.authLevel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthLevelGroupingEntity [authLevel=" + authLevel
				+ ", toString()=" + super.toString() + "]";
	}

	
}
