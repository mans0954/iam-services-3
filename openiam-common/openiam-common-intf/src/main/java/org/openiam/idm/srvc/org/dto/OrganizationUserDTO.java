package org.openiam.idm.srvc.org.dto;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/17/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationUser", propOrder = {
        "user",
        "organization",
        "mdType", "operation"
})
@DozerDTOCorrespondence(OrganizationUserEntity.class)
public class OrganizationUserDTO {
    private User user;
    private Organization organization;
    private MetadataType mdType;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    public OrganizationUserDTO(String userId, String organizationId, AttributeOperationEnum operation) {
        this.user = new User(userId);
        this.organization = new Organization();
        this.organization.setId(organizationId);
        this.operation = operation;

    }

    public OrganizationUserDTO() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public MetadataType getMdType() {
        return mdType;
    }

    public void setMdType(MetadataType mdType) {
        this.mdType = mdType;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }

    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }
}
