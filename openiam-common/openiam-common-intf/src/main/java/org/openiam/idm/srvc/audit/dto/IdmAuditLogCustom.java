package org.openiam.idm.srvc.audit.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;

/**
 * @author zaporozhec
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdmAuditLogCustom", propOrder = { "customIdmAuditLogId",
        "logId", "customName", "customValue", "dispayOrder", "type" })
@DozerDTOCorrespondence(IdmAuditLogCustomEntity.class)
public class IdmAuditLogCustom implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String customIdmAuditLogId;
    private String logId;
    private String customName;
    private String customValue;
    private int dispayOrder;
    private CustomIdmAuditLogType type;

    /**
     * @return the customIdmAuditLogId
     */
    public String getCustomIdmAuditLogId() {
        return customIdmAuditLogId;
    }

    /**
     * @param customIdmAuditLogId the customIdmAuditLogId to set
     */
    public void setCustomIdmAuditLogId(String customIdmAuditLogId) {
        this.customIdmAuditLogId = customIdmAuditLogId;
    }

    /**
     * @return the logId
     */
    public String getLogId() {
        return logId;
    }

    /**
     * @param logId the logId to set
     */
    public void setLogId(String logId) {
        this.logId = logId;
    }

    /**
     * @return the customName
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * @param customName the customName to set
     */
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    /**
     * @return the customValue
     */
    public String getCustomValue() {
        return customValue;
    }

    /**
     * @param customValue the customValue to set
     */
    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }

    /**
     * @return the dispayOrder
     */
    public int getDispayOrder() {
        return dispayOrder;
    }

    /**
     * @param dispayOrder the dispayOrder to set
     */
    public void setDispayOrder(int dispayOrder) {
        this.dispayOrder = dispayOrder;
    }

    /**
     * @return the type
     */
    public CustomIdmAuditLogType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(CustomIdmAuditLogType type) {
        this.type = type;
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
        result = prime
                * result
                + ((customIdmAuditLogId == null) ? 0 : customIdmAuditLogId
                        .hashCode());
        result = prime * result
                + ((customName == null) ? 0 : customName.hashCode());
        result = prime * result
                + ((customValue == null) ? 0 : customValue.hashCode());
        result = prime * result + dispayOrder;
        result = prime * result + ((logId == null) ? 0 : logId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        IdmAuditLogCustom other = (IdmAuditLogCustom) obj;
        if (customIdmAuditLogId == null) {
            if (other.customIdmAuditLogId != null)
                return false;
        } else if (!customIdmAuditLogId.equals(other.customIdmAuditLogId))
            return false;
        if (customName == null) {
            if (other.customName != null)
                return false;
        } else if (!customName.equals(other.customName))
            return false;
        if (customValue == null) {
            if (other.customValue != null)
                return false;
        } else if (!customValue.equals(other.customValue))
            return false;
        if (dispayOrder != other.dispayOrder)
            return false;
        if (logId == null) {
            if (other.logId != null)
                return false;
        } else if (!logId.equals(other.logId))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
