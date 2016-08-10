package org.openiam.base.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URISubstitutionToken", propOrder = {
	"query",
	"replaceWith",
	"exactMatch",
	"fastSearch"
})
public class URISubstitutionToken implements Serializable {

	private String query;
	private String replaceWith;
	private boolean exactMatch;
	private boolean fastSearch;
	
	private URISubstitutionToken() {}
	
	public URISubstitutionToken(final String query, final String replaceWith, final boolean exactMatch, final boolean fastSearch) {
		this.query = query;
		this.replaceWith = replaceWith;
		this.exactMatch = exactMatch;
		this.fastSearch = fastSearch;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exactMatch ? 1231 : 1237);
		result = prime * result + (fastSearch ? 1231 : 1237);
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((replaceWith == null) ? 0 : replaceWith.hashCode());
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
		URISubstitutionToken other = (URISubstitutionToken) obj;
		if (exactMatch != other.exactMatch)
			return false;
		if (fastSearch != other.fastSearch)
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
		return "URISubstitutionToken [query=" + query + ", replaceWith="
				+ replaceWith + ", exactMatch=" + exactMatch + ", fastSearch="
				+ fastSearch + "]";
	}
	
	
}
