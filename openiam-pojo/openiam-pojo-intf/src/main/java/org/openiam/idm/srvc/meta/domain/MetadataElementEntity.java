package org.openiam.idm.srvc.meta.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataElement;

@Entity
@Table(name = "METADATA_ELEMENT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElement.class)
public class MetadataElementEntity implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "METADATA_ID", length = 32)
    private String metadataElementId;

    @Column(name = "TYPE_ID", length = 32)
    private Long metadataTypeId;

    @Column(name = "ATTRIBUTE_NAME", length = 50)
    private String attributeName;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;

    @Column(name = "MIN_LEN")
    private Integer minLen;

    @Column(name = "MAX_LEN")
    private Integer maxLen;

    @Column(name = "TEXT_CASE", length = 20)
    private String textCase;

    @Column(name = "DATA_TYPE", length = 20)
    private String dataType;

    @Column(name = "MIN_VALUE")
    private Long minValue = 0L;

    @Column(name = "MAX_VALUE")
    private Long maxValue;

    @Column(name = "DEFAULT_VALUE", length = 100)
    private String defaultValue;

    @Column(name = "VALUE_LIST", length = 1000)
    private String valueList;

    @Column(name = "LABEL", length = 100)
    private String label;

    @Column(name = "MULTI_VALUE")
    private Integer multiValue;

    @Column(name = "AUDITABLE")
    private Integer auditable = 1;

    @Column(name = "REQUIRED")
    private Integer required = 0;

    @Column(name = "SELF_EDITABLE")
    private Integer selfEditable = 0;

    @Column(name = "SELF_VIEWABLE")
    private Integer selfViewable = 0;

    @Column(name = "UI_TYPE", length = 20)
    private String uiType;
    @Column(name = "UI_OBJECT_SIZE", length = 40)
    private String uiSize;
    @Column(name = "VALUE_SRC", length = 1000)
    private String valueSrc;

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinLen() {
        return minLen;
    }

    public void setMinLen(Integer minLen) {
        this.minLen = minLen;
    }

    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    public String getTextCase() {
        return textCase;
    }

    public void setTextCase(String textCase) {
        this.textCase = textCase;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueList() {
        return valueList;
    }

    public void setValueList(String valueList) {
        this.valueList = valueList;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getMultiValue() {
        return multiValue;
    }

    public void setMultiValue(Integer multiValue) {
        this.multiValue = multiValue;
    }

    public Integer getAuditable() {
        return auditable;
    }

    public void setAuditable(Integer auditable) {
        this.auditable = auditable;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getSelfEditable() {
        return selfEditable;
    }

    public void setSelfEditable(Integer selfEditable) {
        this.selfEditable = selfEditable;
    }

    public Integer getSelfViewable() {
        return selfViewable;
    }

    public void setSelfViewable(Integer selfViewable) {
        this.selfViewable = selfViewable;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public String getUiSize() {
        return uiSize;
    }

    public void setUiSize(String uiSize) {
        this.uiSize = uiSize;
    }

    public String getValueSrc() {
        return valueSrc;
    }

    public void setValueSrc(String valueSrc) {
        this.valueSrc = valueSrc;
    }

    public Long getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(Long metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }
}
