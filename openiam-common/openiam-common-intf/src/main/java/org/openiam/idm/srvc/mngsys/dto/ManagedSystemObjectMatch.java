package org.openiam.idm.srvc.mngsys.dto;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

// Generated Dec 20, 2008 7:54:58 PM by Hibernate Tools 3.2.2.GA

/**
 * Domain object which defines how specific objects with in a managed system are to be located.
 * For example, in a directory we can use either a BaseDN or a search filter. Note that the
 * search filters can contain parameters to increase their flexibility.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSystemObjectMatch", propOrder = {
    "objectSearchId",
    "managedSys",
    "objectType",
    "matchMethod",
    "searchFilter",
    "baseDn",
    "searchBaseDn",
    "keyField"
})
@DozerDTOCorrespondence(ManagedSystemObjectMatchEntity.class)
public class ManagedSystemObjectMatch implements java.io.Serializable {
	private String objectSearchId;
	private String managedSys;
	private String objectType;
	private String matchMethod;
	private String searchFilter;
	private String baseDn;
	private String searchBaseDn;
	private String keyField;

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public ManagedSystemObjectMatch() {
	}

	public String getObjectSearchId() {
		return this.objectSearchId;
	}

	public void setObjectSearchId(String objectSearchId) {
		this.objectSearchId = objectSearchId;
	}

	public String getManagedSys() {
		return this.managedSys;
	}

	public void setManagedSys(String managedSys) {
		this.managedSys = managedSys;
	}

	public String getObjectType() {
		return this.objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getMatchMethod() {
		return this.matchMethod;
	}

	public void setMatchMethod(String matchMethod) {
		this.matchMethod = matchMethod;
	}

	public String getSearchFilter() {
		return this.searchFilter;
	}

    public String getSearchFilterUnescapeXml() {
        return StringEscapeUtils.unescapeXml(searchFilter);
    }

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getSearchBaseDn() {
		return searchBaseDn;
	}

	public void setSearchBaseDn(String searchBaseDn) {
		this.searchBaseDn = searchBaseDn;
	}

    @Override
    public String toString() {
        return "ManagedSystemObjectMatch{" +
                "objectSearchId='" + objectSearchId + '\'' +
                ", managedSys='" + managedSys + '\'' +
                ", objectType='" + objectType + '\'' +
                ", matchMethod='" + matchMethod + '\'' +
                ", searchFilter='" + searchFilter + '\'' +
                ", baseDn='" + baseDn + '\'' +
                ", searchBaseDn='" + searchBaseDn + '\'' +
                ", keyField='" + keyField + '\'' +
                '}';
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseDn == null) ? 0 : baseDn.hashCode());
		result = prime * result
				+ ((keyField == null) ? 0 : keyField.hashCode());
		result = prime * result
				+ ((managedSys == null) ? 0 : managedSys.hashCode());
		result = prime * result
				+ ((matchMethod == null) ? 0 : matchMethod.hashCode());
		result = prime * result
				+ ((objectSearchId == null) ? 0 : objectSearchId.hashCode());
		result = prime * result
				+ ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result
				+ ((searchBaseDn == null) ? 0 : searchBaseDn.hashCode());
		result = prime * result
				+ ((searchFilter == null) ? 0 : searchFilter.hashCode());
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
		ManagedSystemObjectMatch other = (ManagedSystemObjectMatch) obj;
		if (baseDn == null) {
			if (other.baseDn != null)
				return false;
		} else if (!baseDn.equals(other.baseDn))
			return false;
		if (keyField == null) {
			if (other.keyField != null)
				return false;
		} else if (!keyField.equals(other.keyField))
			return false;
		if (managedSys == null) {
			if (other.managedSys != null)
				return false;
		} else if (!managedSys.equals(other.managedSys))
			return false;
		if (matchMethod == null) {
			if (other.matchMethod != null)
				return false;
		} else if (!matchMethod.equals(other.matchMethod))
			return false;
		if (objectSearchId == null) {
			if (other.objectSearchId != null)
				return false;
		} else if (!objectSearchId.equals(other.objectSearchId))
			return false;
		if (objectType == null) {
			if (other.objectType != null)
				return false;
		} else if (!objectType.equals(other.objectType))
			return false;
		if (searchBaseDn == null) {
			if (other.searchBaseDn != null)
				return false;
		} else if (!searchBaseDn.equals(other.searchBaseDn))
			return false;
		if (searchFilter == null) {
			if (other.searchFilter != null)
				return false;
		} else if (!searchFilter.equals(other.searchFilter))
			return false;
		return true;
	}
    
    
}
