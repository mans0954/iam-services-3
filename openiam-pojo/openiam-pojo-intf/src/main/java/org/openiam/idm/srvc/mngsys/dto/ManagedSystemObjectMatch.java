package org.openiam.idm.srvc.mngsys.dto;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
@Entity
@Table(name = "MNG_SYS_OBJECT_MATCH")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ManagedSystemObjectMatch implements java.io.Serializable {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="OBJECT_SEARCH_ID", length=32, nullable = false)
	private String objectSearchId;
    @Column(name="MANAGED_SYS_ID", length=32)
	private String managedSys;
    @Column(name="OBJECT_TYPE", length=20)
	private String objectType;
    @Column(name="MATCH_METHOD", length=20)
	private String matchMethod;
    @Column(name="SEARCH_FILTER", length=1000)
	private String searchFilter;
    @Column(name="BASE_DN", length=200)
	private String baseDn;
    @Column(name="SEARCH_BASE_DN", length=200)
	private String searchBaseDn;
    @Column(name="KEY_FIELD", length=40)
	private String keyField;

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public ManagedSystemObjectMatch() {
	}

	public ManagedSystemObjectMatch(String objectSearchId, String managedSys) {
		this.objectSearchId = objectSearchId;
		this.managedSys = managedSys;
	}





	public ManagedSystemObjectMatch(String baseDn, String keyField, String managedSys, String matchMethod, String objectSearchId, String objectType, String searchFilter) {
		super();
		this.baseDn = baseDn;
		this.keyField = keyField;
		this.managedSys = managedSys;
		this.matchMethod = matchMethod;
		this.objectSearchId = objectSearchId;
		this.objectType = objectType;
		this.searchFilter = searchFilter;
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
}
