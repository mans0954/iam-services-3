package org.openiam.idm.srvc.membership.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyNameDTO;
import org.openiam.idm.srvc.access.dto.AccessRight;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMembershipXref", propOrder = {"entityId", "memberEntityId", "rights", "startDate", "endDate", "operation"})
public class AbstractMembershipXref extends KeyNameDTO {

    private Date startDate;
    private Date endDate;
    private String entityId;
    private String memberEntityId;
    private Set<AccessRight> rights;
    private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;


    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getMemberEntityId() {
        return memberEntityId;
    }

    public void setMemberEntityId(String memberEntityId) {
        this.memberEntityId = memberEntityId;
    }

    public Set<AccessRight> getRights() {
        return rights;
    }

    public void setRights(final Set<AccessRight> rights) {
        this.rights = rights;
    }

    public Set<String> getAccessRightIds() {
        if (this.rights != null) {
            Set<String> retVal = new HashSet<>();
            for (AccessRight e : rights) {
                retVal.add(e.getId());
            }
            return retVal;
        } else {
            return null;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
        result = prime * result + ((memberEntityId == null) ? 0 : memberEntityId.hashCode());
        result = prime * result + ((rights == null) ? 0 : rights.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractMembershipXref other = (AbstractMembershipXref) obj;
        if (endDate == null) {
            if (other.endDate != null) return false;
        } else if (!endDate.equals(other.endDate)) return false;
        if (entityId == null) {
            if (other.entityId != null) return false;
        } else if (!entityId.equals(other.entityId)) return false;
        if (memberEntityId == null) {
            if (other.memberEntityId != null) return false;
        } else if (!memberEntityId.equals(other.memberEntityId)) return false;
        if (rights == null) {
            if (other.rights != null) return false;
        } else if (!rights.equals(other.rights)) return false;
        if (startDate == null) {
            if (other.startDate != null) return false;
        } else if (!startDate.equals(other.startDate)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "AbstractMembershipXref [startDate=" + startDate + ", endDate=" + endDate + ", entityId=" + entityId + ", memberEntityId=" + memberEntityId + ", rights=" + rights + ", operation=" + operation + ", id=" + id + ", objectState=" + objectState + ", requestorSessionID=" + requestorSessionID + ", requestorUserId=" + requestorUserId + ", requestorLogin=" + requestorLogin + ", requestClientIP=" + requestClientIP + "]";
    }


}
