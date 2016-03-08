package org.openiam.am.srvc.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_METHOD_PARAMS")
@DozerDTOCorrespondence(URIPatternMethodParameter.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_METHOD_PARAM_ID"))
})
public class URIPatternMethodParameterEntity extends AbstractParameterEntity {

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_URI_METHOD_ID", referencedColumnName = "URI_PATTERN_URI_METHOD_ID", insertable=true, updatable=false, nullable=false)
	private URIPatternMethodEntity patternMethod;
	
    @ElementCollection
    @CollectionTable(name="URI_PATTERN_METHOD_PARAM_VAL", joinColumns=@JoinColumn(name="URI_PATTERN_METHOD_PARAM_ID", referencedColumnName="URI_PATTERN_METHOD_PARAM_ID"))
    @Column(name="PARAM_VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

	public URIPatternMethodEntity getPatternMethod() {
		return patternMethod;
	}

	public void setPatternMethod(URIPatternMethodEntity patternMethod) {
		this.patternMethod = patternMethod;
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
		result = prime * result
				+ ((patternMethod == null) ? 0 : patternMethod.hashCode());
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
		URIPatternMethodParameterEntity other = (URIPatternMethodParameterEntity) obj;
		if (patternMethod == null) {
			if (other.patternMethod != null)
				return false;
		} else if (!patternMethod.equals(other.patternMethod))
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
		return "URIPatternMethodParameterEntity [patternMethod="
				+ patternMethod + ", values=" + values + ", name=" + name + ", id=" + id + "]";
	}

	
}
