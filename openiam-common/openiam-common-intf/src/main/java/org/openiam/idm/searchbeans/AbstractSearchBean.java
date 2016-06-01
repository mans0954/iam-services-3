package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.SortParam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSearchBean", propOrder = {
        "key",
        "deepCopy",
        "sortBy",
        "findInCache"
})
public abstract class AbstractSearchBean<T, KeyType> implements SearchBean<T, KeyType> {

	private boolean deepCopy = true;
	private KeyType key;
	private boolean findInCache;

    private List<SortParam> sortBy;
    
    /* used by Spring @Cacheable methods to determine the key */
    public String getCacheKey() {
    	return String.valueOf(this.hashCode());
    }
	
	public KeyType getKey() {
		return key;
	}
	
	public void setKey(final KeyType key) {
		this.key = key;
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

	protected String getSortKeyForCache(){
		StringBuilder sb = new StringBuilder();
		if (sortBy != null) {
			for (SortParam sort : sortBy) {
				if (sort.getSortBy() != null)
					sb.append(sort.getSortBy().toString());
				if (sort.getOrderBy() != null)
					sb.append(sort.getOrderBy().toString());
			}
		}
		return StringUtils.isNotBlank(sb.toString()) ? sb.toString() : "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deepCopy ? 1231 : 1237);
		result = prime * result + (findInCache ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((sortBy == null) ? 0 : sortBy.hashCode());
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
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (sortBy == null) {
			if (other.sortBy != null)
				return false;
		} else if (!sortBy.equals(other.sortBy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AbstractSearchBean [deepCopy=%s, key=%s]",
				deepCopy, key);
	}
	
	
}
