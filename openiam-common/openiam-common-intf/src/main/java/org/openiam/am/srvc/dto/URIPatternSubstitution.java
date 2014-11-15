package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternSubstitutionEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternSubstitution", propOrder = {
	"patternId",
	"query",
	"replaceWith",
	"exactMatch",
	"fastSearch",
	"order"
})
@DozerDTOCorrespondence(URIPatternSubstitutionEntity.class)
public class URIPatternSubstitution extends KeyDTO {

	private String patternId;
	private String query;
	private String replaceWith;
	private String exactMatch;
	private String fastSearch;
	private Integer order;
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
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
	public String getExactMatch() {
		return exactMatch;
	}
	public void setExactMatch(String exactMatch) {
		this.exactMatch = exactMatch;
	}
	public String getFastSearch() {
		return fastSearch;
	}
	public void setFastSearch(String fastSearch) {
		this.fastSearch = fastSearch;
	}
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((exactMatch == null) ? 0 : exactMatch.hashCode());
		result = prime * result
				+ ((order == null) ? 0 : order.hashCode());
		result = prime * result
				+ ((fastSearch == null) ? 0 : fastSearch.hashCode());
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
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
		URIPatternSubstitution other = (URIPatternSubstitution) obj;
		if (exactMatch == null) {
			if (other.exactMatch != null)
				return false;
		} else if (!exactMatch.equals(other.exactMatch))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (fastSearch == null) {
			if (other.fastSearch != null)
				return false;
		} else if (!fastSearch.equals(other.fastSearch))
			return false;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
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
	@Override
	public String toString() {
		return "URIPatternSubstitution [patternId=" + patternId + ", query="
				+ query + ", replaceWith=" + replaceWith + ", exactMatch="
				+ exactMatch + ", fastSearch=" + fastSearch
				+ ", order=" + order + ", id=" + id
				+ ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}
	
	
}
