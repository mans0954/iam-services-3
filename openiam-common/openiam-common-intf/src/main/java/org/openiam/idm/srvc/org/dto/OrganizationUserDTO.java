package org.openiam.idm.srvc.org.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by zaporozhec on 7/17/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationUser", propOrder = {
        "organization", "user",
        "mdTypeId", "operation"
})
@DozerDTOCorrespondence(OrganizationUserEntity.class)
public class OrganizationUserDTO implements Serializable {
    private Organization organization;
    private User user;
    private String mdTypeId;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    public OrganizationUserDTO(String userId, String organizationId, String mdTypeId, AttributeOperationEnum operation) {
        user = new User(userId);
        organization = new Organization();
        organization.setId(organizationId);
        this.operation = operation;
        this.mdTypeId = mdTypeId;

    }

    public OrganizationUserDTO() {
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getMdTypeId() {
        return mdTypeId;
    }

    public void setMdTypeId(String mdTypeId) {
        this.mdTypeId = mdTypeId;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
