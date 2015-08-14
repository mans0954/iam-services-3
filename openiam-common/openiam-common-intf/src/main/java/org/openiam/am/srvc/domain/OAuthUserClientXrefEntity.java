package org.openiam.am.srvc.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.am.srvc.dto.OAuthUserClientXref;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by alexander on 01/07/15.
 */
@Entity
@Table(name = "OAUTH_USER_CLIENT_AUTHORIZATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(OAuthUserClientXref.class)
@AttributeOverride(name = "id", column = @Column(name = "OAUTH_AUTHORIZATION_ID"))
public class OAuthUserClientXrefEntity extends KeyEntity {

    @Column(name="IS_ALLOWED")
    @Type(type = "yes_no")
    private boolean isAllowed;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = false, nullable=false)
    private AuthProviderEntity client;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = true, updatable = false, nullable=false)
    private UserEntity user;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = true, nullable=false)
    private ResourceEntity scope;

    public AuthProviderEntity getClient() {
        return client;
    }

    public void setClient(AuthProviderEntity client) {
        this.client = client;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ResourceEntity getScope() {
        return scope;
    }

    public void setScope(ResourceEntity scope) {
        this.scope = scope;
    }

    public boolean getIsAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OAuthUserClientXrefEntity that = (OAuthUserClientXrefEntity) o;

        return isAllowed == that.isAllowed;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isAllowed ? 1 : 0);
        return result;
    }
}
