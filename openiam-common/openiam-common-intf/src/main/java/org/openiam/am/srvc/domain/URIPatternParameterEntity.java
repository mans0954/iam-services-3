package org.openiam.am.srvc.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_PARAMS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(URIPatternParameter.class)
@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_URI_PARAM_ID"))
public class URIPatternParameterEntity extends AbstractParameterEntity {
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID", insertable=true, updatable=false, nullable=false)
	private URIPatternEntity pattern;

    @ElementCollection
    @CollectionTable(name="URI_PATTERN_PARAMS_VALUES", joinColumns=@JoinColumn(name="URI_PATTERN_URI_PARAM_ID", referencedColumnName="URI_PATTERN_URI_PARAM_ID"))
    @Column(name="PARAM_VALUE", length = 255)
    private List<String> values = new ArrayList<String>();
	
	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	@Override
	public List<String> getValues() {
		return this.values;
	}

	@Override
	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		URIPatternParameterEntity other = (URIPatternParameterEntity) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternParameterEntity [pattern=" + pattern + ", values="
				+ values + ", name=" + name
				+ ", id=" + id + "]";
	}
	
	
}
