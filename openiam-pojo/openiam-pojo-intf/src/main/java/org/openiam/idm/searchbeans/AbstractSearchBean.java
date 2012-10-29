package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSearchBean", propOrder = {
        "key",
        "deepCopy"
})
public class AbstractSearchBean<T, KeyType> {

	private boolean deepCopy = true;
	private KeyType key;
	
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
	
	
}
