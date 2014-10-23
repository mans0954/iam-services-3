package org.openiam.am.srvc.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.dto.URIPatternServer;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_SERVER")
@DozerDTOCorrespondence(URIPatternServer.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_SERVER_ID"))
})
public class URIPatternServerEntity extends AbstractServerEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID")
	private URIPatternEntity pattern;

	
	
	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((pattern == null) ? 0 : pattern.hashCode());
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
		URIPatternServerEntity other = (URIPatternServerEntity) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternServerEntity [pattern=" + pattern
				+ ", getServerURL()=" + getServerURL() + ", getId()=" + getId()
				+ "]";
	}
	
	
}
