package org.openiam.idm.srvc.org.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by zaporozhec on 7/17/15.
 */
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrganizationUserIdEntity implements Serializable {
    @ManyToOne
    private UserEntity user;
    @ManyToOne
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
