package org.openiam.idm.srvc.mngsys.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MNG_SYS_OBJECT_MATCH")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(ManagedSystemObjectMatch.class)
public class ManagedSystemObjectMatchEntity implements Serializable {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="OBJECT_SEARCH_ID", length=32, nullable = false)
    private String objectSearchId;

    @ManyToOne
    @JoinColumn(name = "MANAGED_SYS_ID")
    private ManagedSysEntity managedSys;

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

    public String getObjectSearchId() {
        return objectSearchId;
    }

    public void setObjectSearchId(String objectSearchId) {
        this.objectSearchId = objectSearchId;
    }

    public ManagedSysEntity getManagedSys() {
        return managedSys;
    }

    public void setManagedSys(ManagedSysEntity managedSys) {
        this.managedSys = managedSys;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getMatchMethod() {
        return matchMethod;
    }

    public void setMatchMethod(String matchMethod) {
        this.matchMethod = matchMethod;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getSearchBaseDn() {
        return searchBaseDn;
    }

    public void setSearchBaseDn(String searchBaseDn) {
        this.searchBaseDn = searchBaseDn;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }
}
