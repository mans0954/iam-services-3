package org.openiam.idm.srvc.oauth.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.oauth.dto.OauthScope;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "OAUTH_SCOPE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(OauthScope.class)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OAUTH_SCOPE_ID")),  //TODO
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
    @JoinTable(name = "RES",
            joinColumns = {@JoinColumn(name = "RESOURCE_ID")},  //?
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_TYPE_ID")})  //?
    @Fetch(FetchMode.SUBSELECT)
    private Set<Resource> resources;


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

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }


}
