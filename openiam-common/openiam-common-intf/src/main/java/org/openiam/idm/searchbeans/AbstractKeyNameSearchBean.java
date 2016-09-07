package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractKeyNameSearchBean", propOrder = {
        "name",
        "nameToken"
})
public abstract class AbstractKeyNameSearchBean<T extends KeyNameDTO, KeyType extends Serializable> extends AbstractSearchBean<T,KeyType> {
	
	@Deprecated
	protected String name;
	
	/**
     * First name token to search by
     */
    private SearchParam nameToken = null;
	
	@Deprecated
	public String getName() {
		return name;
	}
	
	@Deprecated
	public void setName(String name) {
		if(StringUtils.isNotBlank(name)) {
			this.setNameToken(new SearchParam(name, MatchType.CONTAINS));
		}
	}

	public SearchParam getNameToken() {
		return nameToken;
	}

	public void setNameToken(SearchParam nameToken) {
		this.nameToken = nameToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nameToken == null) ? 0 : nameToken.hashCode());
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
		AbstractKeyNameSearchBean other = (AbstractKeyNameSearchBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameToken == null) {
			if (other.nameToken != null)
				return false;
		} else if (!nameToken.equals(other.nameToken))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractKeyNameSearchBean [name=" + name + ", nameToken="
				+ nameToken + "]";
	}

	
	
}
