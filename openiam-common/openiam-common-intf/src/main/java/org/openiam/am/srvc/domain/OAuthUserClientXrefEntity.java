package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by alexander on 01/07/15.
 */
@Entity
@Table(name = "OAUTH_USER_CLIENT_AUTHORIZATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "OAUTH_AUTHORIZATION_ID"))
public class OAuthUserClientXrefEntity extends KeyEntity {


    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "PROVIDER_ID", insertable = true, updatable = false, nullable=false)
    private AuthProviderEntity client;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = true, updatable = false, nullable=false)
    private UserEntity user;

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
    @JoinTable(name = "OAUTH_AUTHORIZED_SCOPE",
            joinColumns = {@JoinColumn(name = "OAUTH_AUTHORIZATION_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> scopes;

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

    public Set<RoleEntity> getScopes() {
        return scopes;
    }

    public void setScopes(Set<RoleEntity> scopes) {
        this.scopes = scopes;
    }
}
