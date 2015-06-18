package org.openiam.idm.srvc.mngsys.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.policy.dto.Policy;

/**
 * AttributeMap represents the mapping between an attribute in the target system
 * and an attribute policy.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeMap", propOrder = {"managedSysId","name",
        "resourceId", "synchConfigId", "mapForObjectType",
        "targetAttributeName", "authoritativeSrc", "reconResAttribute", "rule",
        "status", "startDate", "endDate", "storeInIamdb", "selected",
        "dataType", "defaultValue"})
@DozerDTOCorrespondence(AttributeMapEntity.class)
public class AttributeMap extends KeyDTO {

    /**
     *
     */
    private static final long serialVersionUID = -4584242607384442243L;
    private String name;
    private String managedSysId;
    private String resourceId;
    private String synchConfigId;
    private String mapForObjectType;
    private String targetAttributeName;
    private Integer authoritativeSrc;
    private ReconciliationResourceAttributeMap reconResAttribute;
    private String rule;
    private String status;
    @XmlSchemaType(name = "dateTime")
    private Date startDate;
    @XmlSchemaType(name = "dateTime")
    private Date endDate;
    private Integer storeInIamdb;
    private Boolean selected = new Boolean(false);
    /* Data type of the attribute */
    private PolicyMapDataTypeOptions dataType;
    private String defaultValue;

    public AttributeMap() {
    }

    public AttributeMap(String attributeMapId, String managedSysId) {
        this.id = attributeMapId;
        this.managedSysId = managedSysId;
    }

    public AttributeMap(String attributeMapId, String managedSysId,
                        String resourceId, String synchConfigId, String mapForObjectType, String attributeName,
                        String targetAttributeName, Integer authoritativeSrc, String rule,
                        String status, Date startDate, Date endDate, Integer storeInIamdb) {
        this.id = attributeMapId;
        this.managedSysId = managedSysId;
        this.resourceId = resourceId;
        this.synchConfigId = synchConfigId;
        this.mapForObjectType = mapForObjectType;
        this.name = attributeName;
        this.targetAttributeName = targetAttributeName;
        this.authoritativeSrc = authoritativeSrc;
        this.rule = rule;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storeInIamdb = storeInIamdb;
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

    public ReconciliationResourceAttributeMap getReconResAttribute() {
        return reconResAttribute;
    }

    public void setReconResAttribute(
            ReconciliationResourceAttributeMap reconResAttribute) {
        this.reconResAttribute = reconResAttribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AttributeMap{" + "attributeMapId='" + id + '\''
                + ", managedSysId='" + managedSysId + '\'' + ", resourceId='"
                + resourceId + ", synchConfigId='" + synchConfigId + '\'' +
                ", mapForObjectType='" + mapForObjectType
                + '\'' + ", attributeName='" + name + '\''
                + ", targetAttributeName='" + targetAttributeName + '\''
                + ", authoritativeSrc=" + authoritativeSrc
                + ", attributePolicy=" + reconResAttribute + ", rule='" + rule
                + '\'' + ", status='" + status + '\'' + ", startDate="
                + startDate + ", endDate=" + endDate + ", storeInIamdb="
                + storeInIamdb + ", selected=" + selected + ", dataType='"
                + dataType + '\'' + ", defaultValue='" + defaultValue + '\''
                + '}';
    }
}
