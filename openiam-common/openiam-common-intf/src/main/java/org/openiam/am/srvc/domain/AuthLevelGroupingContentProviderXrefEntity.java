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
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "CP_AUTH_LEVEL_GRP_XREF")
@DozerDTOCorrespondence(AuthLevelGroupingContentProviderXref.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthLevelGroupingContentProviderXrefEntity implements Serializable {

	@EmbeddedId
	AuthLevelGroupingContentProviderXrefIdEntity id;
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    @JoinColumn(name = "CONTENT_PROVIDER_ID", insertable = false, updatable = false)
	private ContentProviderEntity contentProvider;
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    @JoinColumn(name = "AUTH_LEVEL_GROUPING_ID", insertable = false, updatable = false)
	private AuthLevelGroupingEntity grouping;
	
	@Column(name="EXEC_ORDER")
	private int order;
	
	public AuthLevelGroupingContentProviderXrefEntity() {
		
	}

	public AuthLevelGroupingContentProviderXrefIdEntity getId() {
		return id;
	}

	public void setId(AuthLevelGroupingContentProviderXrefIdEntity id) {
		this.id = id;
	}

	public ContentProviderEntity getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ContentProviderEntity contentProvider) {
		this.contentProvider = contentProvider;
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
//		result = prime * result
//				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
//		result = prime * result
//				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + order;
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
		AuthLevelGroupingContentProviderXrefEntity other = (AuthLevelGroupingContentProviderXrefEntity) obj;
//		if (contentProvider == null) {
//			if (other.contentProvider != null)
//				return false;
//		} else if (!contentProvider.equals(other.contentProvider))
//			return false;
//		if (grouping == null) {
//			if (other.grouping != null)
//				return false;
//		} else if (!grouping.equals(other.grouping))
//			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return order == other.order;
	}

	@Override
	public String toString() {
		return String
				.format("AuthLevelGroupingContentProviderXrefEntity [id=%s, contentProvider=%s, grouping=%s, order=%s]",
						id, contentProvider, grouping, order);
	}
	
	
}
