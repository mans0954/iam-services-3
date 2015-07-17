package org.openiam.idm.srvc.org.domain;

import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zaporozhec on 7/17/15.
 */
@Embeddable
public class OrganizationUserIdEntity implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;
    @ManyToOne(cascade = CascadeType.ALL)
    private OrganizationEntity organization;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }
}
