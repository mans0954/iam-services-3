package org.openiam.idm.srvc.org.domain;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.OrganizationUserIdDto;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zaporozhec on 7/17/15.
 */
@Embeddable
@DozerDTOCorrespondence(OrganizationUserIdDto.class)
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
