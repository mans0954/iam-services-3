package org.openiam.idm.srvc.mngsys.domain;

import java.util.Date;

import javax.persistence.*;

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

    @Column(name = "MANAGED_SYS_ID", length = 32)
    private String managedSysId;

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

    public AttributeMapEntity(String attributeMapId, String managedSysId) {
        this.attributeMapId = attributeMapId;
        this.managedSysId = managedSysId;
    }

    public AttributeMapEntity(String attributeMapId, String managedSysId,
            String resourceId, String synchConfigId, String mapForObjectType, String attributeName,
            String targetAttributeName, Integer authoritativeSrc, String rule,
            String status, Date startDate, Date endDate, Integer storeInIamdb) {
        this.attributeMapId = attributeMapId;
        this.managedSysId = managedSysId;
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

    public String getManagedSysId() {
        return this.managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
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

    @Override
    public String toString() {
        return "AttributeMap{" + "attributeMapId='" + attributeMapId + '\''
                + ", managedSysId='" + managedSysId + '\'' + ", resourceId='"
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
}
