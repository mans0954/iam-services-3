package org.openiam.idm.srvc.org.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.domain.OrganizationUserIdEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by zaporozhec on 7/17/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationUserId", propOrder = {
        "user",
        "organization"
})
@DozerDTOCorrespondence(OrganizationUserIdEntity.class)
public class OrganizationUserIdDto implements Serializable {
    private User user;
    private Organization organization;

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
}
