package org.openiam.idm.searchbeans;

import org.openiam.base.ws.SortParam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSearchBean", propOrder = {
        "key",
        "deepCopy",
        "sortBy"
})
public abstract class AbstractSearchBean<T, KeyType> {

	private boolean deepCopy = true;
	private KeyType key;

    private List<SortParam> sortBy;
	
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

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deepCopy ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

    /**
     * This method must be used only for as a key for secondary level cache
     * @return
     */
    public abstract String getCacheUniqueBeanKey();

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
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AbstractSearchBean [deepCopy=%s, key=%s]",
				deepCopy, key);
	}
	
	
}
