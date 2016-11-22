package org.openiam.idm.searchbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SortParam;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSearchBean", propOrder = {
        "keySet",
        "deepCopy",
        "sortBy",
        "findInCache",
        "languageId",
        "useElasticSearch"
})
public abstract class AbstractSearchBean<T, KeyType> implements SearchBean<T, KeyType>, Serializable {

	private boolean deepCopy = true;
	private Set<KeyType> keySet;
	//private KeyType key;
	private boolean findInCache;
	
	/*
	 * if set to true, the implementer *should* use ElasticSearch to search for the ojbect
	 */
	private boolean useElasticSearch;
	
	/* if set, the provider SHOULD return a localized object */
	private String languageId;

    private List<SortParam> sortBy;
    
    /* used by Spring @Cacheable methods to determine the key */
    public String getCacheKey() {
    	return String.valueOf(this.hashCode());
    }
	
    @Deprecated
	public void setKey(final KeyType key) {
		addKey(key);
	}

	public Set<KeyType> getKeySet() {
		return keySet;
	}

	public void setKeySet(Collection<KeyType> keySet) {
		if(CollectionUtils.isNotEmpty(keySet)) {
			this.keySet = new HashSet<KeyType>(keySet);
		}
	}

	public void addKey(final KeyType key) {
		if(key != null) {
			if(this.keySet == null) {
				this.keySet = new HashSet<KeyType>();
			}
			this.keySet.add(key);
		}
	}

	public boolean isDeepCopy() {
		return deepCopy;
	}

	public void setDeepCopy(boolean deepCopy) {
		this.deepCopy = deepCopy;
	}

    public List<SortParam> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<SortParam> sortBy) {
        this.sortBy = sortBy;
    }

	public void addSortParam(SortParam sortParam){
		if(sortParam!=null && StringUtils.isNotBlank(sortParam.getSortBy())) {
			if (this.sortBy == null) {
				this.sortBy = new ArrayList<>();
			}
			this.sortBy.add(sortParam);
		}
	}

	public boolean isFindInCache() {
		return findInCache;
	}

	public void setFindInCache(boolean findInCache) {
		this.findInCache = findInCache;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguage(final Language language) {
		if(language != null) {
			this.languageId = language.getId();
		}
	}
	
	public void setLanguage(final LanguageEntity language) {
		if(language != null) {
			this.languageId = language.getId();
		}
	}
	
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public boolean isUseElasticSearch() {
		return useElasticSearch;
	}

	public void setUseElasticSearch(boolean useElasticSearch) {
		this.useElasticSearch = useElasticSearch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deepCopy ? 1231 : 1237);
		result = prime * result + (findInCache ? 1231 : 1237);
		result = prime * result + ((keySet == null) ? 0 : keySet.hashCode());
		result = prime * result
				+ ((languageId == null) ? 0 : languageId.hashCode());
		result = prime * result + ((sortBy == null) ? 0 : sortBy.hashCode());
		result = prime * result + (useElasticSearch ? 1231 : 1237);
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
		AbstractSearchBean other = (AbstractSearchBean) obj;
		if (deepCopy != other.deepCopy)
			return false;
		if (findInCache != other.findInCache)
			return false;
		if (keySet == null) {
			if (other.keySet != null)
				return false;
		} else if (!keySet.equals(other.keySet))
			return false;
		if (languageId == null) {
			if (other.languageId != null)
				return false;
		} else if (!languageId.equals(other.languageId))
			return false;
		if (sortBy == null) {
			if (other.sortBy != null)
				return false;
		} else if (!sortBy.equals(other.sortBy))
			return false;
		if (useElasticSearch != other.useElasticSearch)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractSearchBean [deepCopy=" + deepCopy + ", keySet=" + keySet
				+ ", findInCache=" + findInCache + ", useElasticSearch="
				+ useElasticSearch + ", languageId=" + languageId + ", sortBy="
				+ sortBy + "]";
	}

	
}
