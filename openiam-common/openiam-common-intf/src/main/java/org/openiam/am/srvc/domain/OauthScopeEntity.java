package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.am.srvc.dto.OauthScope;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "OAUTH_SCOPE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(OauthScope.class)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OAUTH_SCOPE_ID")),
})
@Internationalized
public class OauthScopeEntity extends AbstractKeyNameEntity {

    @Column(name = "NAME", length = 80)
    @Size(max = 80, message = "oauth.name.too.long")
    private String name;


    @Column(name = "DESCRIPTION")
    @Size(max = 255, message = "oauth.description.too.long")
    private String description;


    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    @JoinTable(name = "OAUTH_TO_RES",
            joinColumns = {@JoinColumn(name = "OAUTH_SCOPE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<String> resourceId;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getResourceId() {
        return resourceId;
    }

    public void setResourceId(Set<String> resourceId) {
        this.resourceId = resourceId;
    }
}
