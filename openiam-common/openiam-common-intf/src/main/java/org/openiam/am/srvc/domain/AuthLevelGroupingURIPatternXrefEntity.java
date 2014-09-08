package org.openiam.am.srvc.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingURIPatternXrefIdEntity;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_AUTH_LEVEL_GRP_XREF")
@DozerDTOCorrespondence(AuthLevelGroupingURIPatternXref.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthLevelGroupingURIPatternXrefEntity implements Serializable {
	
	@EmbeddedId
	private AuthLevelGroupingURIPatternXrefIdEntity id;

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    @JoinColumn(name = "URI_PATTERN_ID", insertable = false, updatable = false)
	private URIPatternEntity pattern;
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    @JoinColumn(name = "AUTH_LEVEL_GROUPING_ID", insertable = false, updatable = false)
	private AuthLevelGroupingEntity grouping;
	
	@Column(name="EXEC_ORDER")
	private int order;
	
	public AuthLevelGroupingURIPatternXrefEntity() {
		
	}
	
	public AuthLevelGroupingURIPatternXrefEntity(final URIPatternEntity pattern, final AuthLevelGroupingEntity grouping) {
		this.pattern = pattern;
		this.grouping = grouping;
		this.id = new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), pattern.getId());
	}

	public AuthLevelGroupingURIPatternXrefIdEntity getId() {
		return id;
	}

	public void setId(AuthLevelGroupingURIPatternXrefIdEntity id) {
		this.id = id;
	}

	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	public AuthLevelGroupingEntity getGrouping() {
		return grouping;
	}

	public void setGrouping(AuthLevelGroupingEntity grouping) {
		this.grouping = grouping;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + order;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		AuthLevelGroupingURIPatternXrefEntity other = (AuthLevelGroupingURIPatternXrefEntity) obj;
		if (grouping == null) {
			if (other.grouping != null)
				return false;
		} else if (!grouping.equals(other.grouping))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (order != other.order)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthLevelGroupingURIPatternXrefEntity [id=%s, pattern=%s, grouping=%s, order=%s]",
						id, pattern, grouping, order);
	}
	
	
}
