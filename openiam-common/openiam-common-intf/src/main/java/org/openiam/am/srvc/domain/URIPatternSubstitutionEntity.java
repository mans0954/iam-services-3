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
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_HTML_SUBSTITUTION")
@DozerDTOCorrespondence(URIPatternSubstitution.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_HTML_SUBSTITUTION_ID"))
})
public class URIPatternSubstitutionEntity extends KeyEntity {
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID")
	private URIPatternEntity pattern;

	@Column(name="QUERY", length=100)
	private String query;
	
	@Column(name="REPLACE_WITH", length=300)
	private String replaceWith;
	
	@Column(name = "IS_EXACT_MATCH", nullable = false)
	@Type(type = "yes_no")
	private boolean exactMatch;

	@Column(name = "IS_FAST_SEARCH", nullable = false)
	@Type(type = "yes_no")
	private boolean fastSearch;
	
	@Column(name = "EXECUTION_ORDER", nullable=false)
	private Integer order;

	public URIPatternEntity getPattern() {
		return pattern;
	}

	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}

	public boolean isExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public boolean isFastSearch() {
		return fastSearch;
	}

	public void setFastSearch(boolean fastSearch) {
		this.fastSearch = fastSearch;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "URIPatternSubstitutionEntity [pattern=" + pattern + ", query="
				+ query + ", replaceWith=" + replaceWith + ", exactMatch="
				+ exactMatch + ", fastSearch=" + fastSearch
				+ ", order=" + order + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (exactMatch ? 1231 : 1237);
		result = prime * result
				+ ((order == null) ? 0 : order.hashCode());
		result = prime * result + (fastSearch ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((replaceWith == null) ? 0 : replaceWith.hashCode());
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
		URIPatternSubstitutionEntity other = (URIPatternSubstitutionEntity) obj;
		if (exactMatch != other.exactMatch)
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (fastSearch != other.fastSearch)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (replaceWith == null) {
			if (other.replaceWith != null)
				return false;
		} else if (!replaceWith.equals(other.replaceWith))
			return false;
		return true;
	}
	
	
	
}
