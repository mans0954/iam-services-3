package org.openiam.idm.srvc.mngsys.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
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
public class AttributeMapEntity implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ATTRIBUTE_MAP_ID", length = 32, nullable = false)
    private String attributeMapId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "MANAGED_SYS_ID", nullable = false)
    private ManagedSysEntity managedSystem;

    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;

    @Column(name = "SYNCH_CONFIG_ID", length = 32)
    private String synchConfigId;

    @Column(name = "MAP_FOR_OBJECT_TYPE", length = 20)
    private String mapForObjectType;

    @Column(name = "ATTRIBUTE_NAME", length = 50)
    private String attributeName;

    @Column(name = "TARGET_ATTRIBUTE_NAME", length = 50)
    private String targetAttributeName;

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

    public AttributeMapEntity(String attributeMapId, ManagedSysEntity managedSystem) {
        this.attributeMapId = attributeMapId;
        this.managedSystem = managedSystem;
    }

    public AttributeMapEntity(String attributeMapId, ManagedSysEntity managedSystem,
            String resourceId, String synchConfigId, String mapForObjectType, String attributeName,
            String targetAttributeName, Integer authoritativeSrc, String rule,
            String status, Date startDate, Date endDate, Integer storeInIamdb) {
        this.attributeMapId = attributeMapId;
        this.managedSystem = managedSystem;
        this.resourceId = resourceId;
        this.synchConfigId = synchConfigId;
        this.mapForObjectType = mapForObjectType;
        this.attributeName = attributeName;
        this.targetAttributeName = targetAttributeName;
        this.authoritativeSrc = authoritativeSrc;
        this.rule = rule;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storeInIamdb = storeInIamdb;
    }

    public String getAttributeMapId() {
        return this.attributeMapId;
    }

    public void setAttributeMapId(String attributeMapId) {
        this.attributeMapId = attributeMapId;
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

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getTargetAttributeName() {
        return this.targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
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

    public ManagedSysEntity getManagedSystem() {
        return managedSystem;
    }

    public void setManagedSystem(ManagedSysEntity managedSystem) {
        this.managedSystem = managedSystem;
    }

    @Override
    public String toString() {
        return "AttributeMap{" + "attributeMapId='" + attributeMapId + '\''
                + ", managedSys='" + managedSystem + '\'' + ", resourceId='"
                + resourceId + '\''  + ", synchConfigId='"
                + synchConfigId + '\'' + ", mapForObjectType='" + mapForObjectType
                + '\'' + ", attributeName='" + attributeName + '\''
                + ", targetAttributeName='" + targetAttributeName + '\''
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
        if (attributeMapId != null ? !attributeMapId.equals(entity.attributeMapId) : entity.attributeMapId != null)
            return false;
        if (attributeName != null ? !attributeName.equals(entity.attributeName) : entity.attributeName != null)
            return false;
        if (managedSystem != null ? !managedSystem.equals(entity.managedSystem) : entity.managedSystem != null)
            return false;
        if (mapForObjectType != null ? !mapForObjectType.equals(entity.mapForObjectType) : entity.mapForObjectType != null)
            return false;
        if (resourceId != null ? !resourceId.equals(entity.resourceId) : entity.resourceId != null) return false;
        return !(targetAttributeName != null ? !targetAttributeName.equals(entity.targetAttributeName) : entity.targetAttributeName != null);

    }

    @Override
    public int hashCode() {
        int result = attributeMapId != null ? attributeMapId.hashCode() : 0;
        result = 31 * result + (managedSystem != null ? managedSystem.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (mapForObjectType != null ? mapForObjectType.hashCode() : 0);
        result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
        result = 31 * result + (targetAttributeName != null ? targetAttributeName.hashCode() : 0);
        return result;
    }
}
