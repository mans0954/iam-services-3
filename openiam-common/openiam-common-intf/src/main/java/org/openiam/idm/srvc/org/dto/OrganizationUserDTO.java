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
        "primaryKey",
        "mdType", "operation"
})
@DozerDTOCorrespondence(OrganizationUserEntity.class)
public class OrganizationUserDTO implements Serializable {
    private OrganizationUserIdDto primaryKey = new OrganizationUserIdDto();
    private MetadataType mdType;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;

    public OrganizationUserDTO(String userId, String organizationId, AttributeOperationEnum operation) {
        this.primaryKey = new OrganizationUserIdDto();
        this.primaryKey.setUser(new User(userId));
        this.primaryKey.setOrganization(new Organization());
        this.primaryKey.getOrganization().setId(organizationId);
        this.operation = operation;

    }

    public OrganizationUserDTO() {
    }

    public OrganizationUserIdDto getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(OrganizationUserIdDto primaryKey) {
        this.primaryKey = primaryKey;
    }

    public User getUser() {
        return primaryKey.getUser();
    }

    public void setUser(User user) {
        this.primaryKey.setUser(user);
    }

    public Organization getOrganization() {
        return primaryKey.getOrganization();
    }

    public void setOrganization(Organization organization) {
        this.primaryKey.setOrganization(organization);
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
