package org.openiam.idm.srvc.mngsys.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapDataTypeOptions;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "ATTRIBUTE_MAP")
@DozerDTOCorrespondence(AttributeMap.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides(value= {
        @AttributeOverride(name = "id", column = @Column(name = "ATTRIBUTE_MAP_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "ATTRIBUTE_NAME", length=50, nullable = false)),
})
public class AttributeMapEntity extends AbstractKeyNameEntity {
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MNG_SYS_POLICY_ID", referencedColumnName = "MNG_SYS_POLICY_ID", insertable=true, updatable=true, nullable=true)
    private MngSysPolicyEntity mngSysPolicy;

    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;

    @Column(name = "SYNCH_CONFIG_ID", length = 32)
    private String synchConfigId;

    @Column(name = "MAP_FOR_OBJECT_TYPE", length = 20)
    private String mapForObjectType;

    @Column(name = "TARGET_ATTRIBUTE_NAME", length = 50)
    private String targetname;

    @Column(name = "AUTHORITATIVE_SRC", length = 11)
    private Integer authoritativeSrc;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "ATTRIBUTE_POLICY_ID", nullable = false)
    private ReconciliationResourceAttributeMapEntity reconResAttribute;

    @Column(name = "RULE_TEXT")
    private String rule;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "START_DATE", length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "END_DATE", length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "STORE_IN_IAMDB", length = 11)
    private Integer storeInIamdb;

    @Transient
    private Boolean selected = new Boolean(false);

    /* Data type of the attribute */
    @Column(name = "DATA_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private PolicyMapDataTypeOptions dataType;

    @Column(name = "DEFAULT_VALUE", length = 32)
    private String defaultValue;

    public AttributeMapEntity() {
    }

    public AttributeMapEntity(String id, MngSysPolicyEntity mngSysPolicy) {
        this.id = id;
        this.mngSysPolicy = mngSysPolicy;
    }

    public AttributeMapEntity(String id, MngSysPolicyEntity mngSysPolicy,
            String resourceId, String synchConfigId, String mapForObjectType, String name,
            String targetname, Integer authoritativeSrc, String rule,
            String status, Date startDate, Date endDate, Integer storeInIamdb) {
        this.id = id;
        this.mngSysPolicy = mngSysPolicy;
        this.resourceId = resourceId;
        this.synchConfigId = synchConfigId;
        this.mapForObjectType = mapForObjectType;
        this.name = name;
        this.targetname = targetname;
        this.authoritativeSrc = authoritativeSrc;
        this.rule = rule;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storeInIamdb = storeInIamdb;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSynchConfigId() {
        return synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

    public String getMapForObjectType() {
        return this.mapForObjectType;
    }

    public void setMapForObjectType(String mapForObjectType) {
        this.mapForObjectType = mapForObjectType;
    }

    public String getTargetname() {
        return this.targetname;
    }

    public void setTargetname(String targetname) {
        this.targetname = targetname;
    }

    public Integer getAuthoritativeSrc() {
        return this.authoritativeSrc;
    }

    public void setAuthoritativeSrc(Integer authoritativeSrc) {
        this.authoritativeSrc = authoritativeSrc;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStoreInIamdb() {
        return this.storeInIamdb;
    }

    public void setStoreInIamdb(Integer storeInIamdb) {
        this.storeInIamdb = storeInIamdb;
    }

    public ReconciliationResourceAttributeMapEntity getReconResAttribute() {
        return reconResAttribute;
    }

    public void setReconResAttribute(
            ReconciliationResourceAttributeMapEntity reconResAttribute) {
        this.reconResAttribute = reconResAttribute;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public PolicyMapDataTypeOptions getDataType() {
        return dataType;
    }

    public void setDataType(PolicyMapDataTypeOptions dataType) {
        this.dataType = dataType;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }


    public MngSysPolicyEntity getMngSysPolicy() {
        return mngSysPolicy;
    }

    public void setMngSysPolicy(MngSysPolicyEntity mngSysPolicy) {
        this.mngSysPolicy = mngSysPolicy;
    }

    @Override
    public String toString() {
        return "AttributeMap{" + "id='" + id + '\''
                + ", mngSysPolicy='" + mngSysPolicy + '\'' + ", resourceId='"
                + resourceId + '\''  + ", synchConfigId='"
                + synchConfigId + '\'' + ", mapForObjectType='" + mapForObjectType
                + '\'' + ", name='" + name + '\''
                + ", targetname='" + targetname + '\''
                + ", authoritativeSrc=" + authoritativeSrc
                + ", reconResAttribute=" + reconResAttribute + ", rule='"
                + rule + '\'' + ", status='" + status + '\'' + ", startDate="
                + startDate + ", endDate=" + endDate + ", storeInIamdb="
                + storeInIamdb + ", selected=" + selected + ", dataType='"
                + dataType + '\'' + ", defaultValue='" + defaultValue + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeMapEntity entity = (AttributeMapEntity) o;
        if (id != null ? !id.equals(entity.id) : entity.id != null)
            return false;
        if (name != null ? !name.equals(entity.name) : entity.name != null)
            return false;
        if (mngSysPolicy != null ? !mngSysPolicy.equals(entity.mngSysPolicy) : entity.mngSysPolicy != null)
            return false;
        if (mapForObjectType != null ? !mapForObjectType.equals(entity.mapForObjectType) : entity.mapForObjectType != null)
            return false;
        if (resourceId != null ? !resourceId.equals(entity.resourceId) : entity.resourceId != null) return false;
        if (targetname != null ? !targetname.equals(entity.targetname) : entity.targetname != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (mngSysPolicy != null ? mngSysPolicy.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (mapForObjectType != null ? mapForObjectType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (targetname != null ? targetname.hashCode() : 0);
        return result;
    }
}
