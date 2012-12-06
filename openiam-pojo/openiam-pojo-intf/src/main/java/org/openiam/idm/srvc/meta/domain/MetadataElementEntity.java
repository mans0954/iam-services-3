package org.openiam.idm.srvc.meta.domain;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private String metadataTypeId;

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable = false, updatable = false)
    MetadataTypeEntity metadataType;

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

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attributeName == null) ? 0 : attributeName.hashCode());
        result = prime * result
                + ((auditable == null) ? 0 : auditable.hashCode());
        result = prime * result
                + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result
                + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((maxLen == null) ? 0 : maxLen.hashCode());
        result = prime * result
                + ((maxValue == null) ? 0 : maxValue.hashCode());
        result = prime
                * result
                + ((metadataElementId == null) ? 0 : metadataElementId
                        .hashCode());
        result = prime * result
                + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
        result = prime * result + ((minLen == null) ? 0 : minLen.hashCode());
        result = prime * result
                + ((minValue == null) ? 0 : minValue.hashCode());
        result = prime * result
                + ((multiValue == null) ? 0 : multiValue.hashCode());
        result = prime * result
                + ((required == null) ? 0 : required.hashCode());
        result = prime * result
                + ((selfEditable == null) ? 0 : selfEditable.hashCode());
        result = prime * result
                + ((selfViewable == null) ? 0 : selfViewable.hashCode());
        result = prime * result
                + ((textCase == null) ? 0 : textCase.hashCode());
        result = prime * result + ((uiSize == null) ? 0 : uiSize.hashCode());
        result = prime * result + ((uiType == null) ? 0 : uiType.hashCode());
        result = prime * result
                + ((valueList == null) ? 0 : valueList.hashCode());
        result = prime * result
                + ((valueSrc == null) ? 0 : valueSrc.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetadataElementEntity other = (MetadataElementEntity) obj;
        if (attributeName == null) {
            if (other.attributeName != null)
                return false;
        } else if (!attributeName.equals(other.attributeName))
            return false;
        if (auditable == null) {
            if (other.auditable != null)
                return false;
        } else if (!auditable.equals(other.auditable))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (maxLen == null) {
            if (other.maxLen != null)
                return false;
        } else if (!maxLen.equals(other.maxLen))
            return false;
        if (maxValue == null) {
            if (other.maxValue != null)
                return false;
        } else if (!maxValue.equals(other.maxValue))
            return false;
        if (metadataElementId == null) {
            if (other.metadataElementId != null)
                return false;
        } else if (!metadataElementId.equals(other.metadataElementId))
            return false;
        if (metadataTypeId == null) {
            if (other.metadataTypeId != null)
                return false;
        } else if (!metadataTypeId.equals(other.metadataTypeId))
            return false;
        if (minLen == null) {
            if (other.minLen != null)
                return false;
        } else if (!minLen.equals(other.minLen))
            return false;
        if (minValue == null) {
            if (other.minValue != null)
                return false;
        } else if (!minValue.equals(other.minValue))
            return false;
        if (multiValue == null) {
            if (other.multiValue != null)
                return false;
        } else if (!multiValue.equals(other.multiValue))
            return false;
        if (required == null) {
            if (other.required != null)
                return false;
        } else if (!required.equals(other.required))
            return false;
        if (selfEditable == null) {
            if (other.selfEditable != null)
                return false;
        } else if (!selfEditable.equals(other.selfEditable))
            return false;
        if (selfViewable == null) {
            if (other.selfViewable != null)
                return false;
        } else if (!selfViewable.equals(other.selfViewable))
            return false;
        if (textCase == null) {
            if (other.textCase != null)
                return false;
        } else if (!textCase.equals(other.textCase))
            return false;
        if (uiSize == null) {
            if (other.uiSize != null)
                return false;
        } else if (!uiSize.equals(other.uiSize))
            return false;
        if (uiType == null) {
            if (other.uiType != null)
                return false;
        } else if (!uiType.equals(other.uiType))
            return false;
        if (valueList == null) {
            if (other.valueList != null)
                return false;
        } else if (!valueList.equals(other.valueList))
            return false;
        if (valueSrc == null) {
            if (other.valueSrc != null)
                return false;
        } else if (!valueSrc.equals(other.valueSrc))
            return false;
        return true;
    }
}
