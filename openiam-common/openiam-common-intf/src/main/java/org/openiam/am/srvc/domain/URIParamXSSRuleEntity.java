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
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.URIParamXSSRule;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PARAM_XSS_RULES")
@DozerDTOCorrespondence(URIParamXSSRule.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PARAM_XSS_RULE_ID"))
})
public class URIParamXSSRuleEntity extends KeyEntity {
	
	public URIParamXSSRuleEntity() {}

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID")
	private URIPatternEntity pattern;
	
	@Column(name="PARAM_NAME", length=100, nullable=false)
	private String paramName;
	
	@Column(name = "IGNORE_XSS", nullable = false)
	@Type(type = "yes_no")
	private boolean ignoreXSS = true;

	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public boolean isIgnoreXSS() {
		return ignoreXSS;
	}

	public void setIgnoreXSS(boolean ignoreXSS) {
		this.ignoreXSS = ignoreXSS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (ignoreXSS ? 1231 : 1237);
		result = prime * result
				+ ((paramName == null) ? 0 : paramName.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		URIParamXSSRuleEntity other = (URIParamXSSRuleEntity) obj;
		if (ignoreXSS != other.ignoreXSS)
			return false;
		if (paramName == null) {
			if (other.paramName != null)
				return false;
		} else if (!paramName.equals(other.paramName))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}
	
	
	
}
