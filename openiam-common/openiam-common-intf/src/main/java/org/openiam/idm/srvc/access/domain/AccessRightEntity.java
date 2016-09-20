package org.openiam.idm.srvc.access.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "ACCESS_RIGHTS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(AccessRight.class)
@AttributeOverrides({@AttributeOverride(name = "id", column = @Column(name = "ACCESS_RIGHT_ID")), @AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable = true))})
@Internationalized
public class AccessRightEntity extends AbstractKeyNameEntity {

    @ManyToMany(cascade = {}, fetch = FetchType.LAZY)
    @JoinTable(name = "RES_RES_MEMBERSHIP_RIGHTS",
            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceToResourceMembershipXrefEntity> resource2ResourceMappings;

//    @ManyToMany(cascade={},fetch=FetchType.LAZY)
//    @JoinTable(name = "GRP_GRP_MEMBERSHIP_RIGHTS",
//            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
//    @Fetch(FetchMode.SUBSELECT)
//	private Set<GroupToGroupMembershipXrefEntity> group2GroupMappings;

//    @ManyToMany(cascade={},fetch=FetchType.LAZY)
//    @JoinTable(name = "ROLE_ROLE_MEMBERSHIP_RIGHTS",
//            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
//    @Fetch(FetchMode.SUBSELECT)
//	private Set<RoleToRoleMembershipXrefEntity> role2RoleMappings;

//    @ManyToMany(cascade={},fetch=FetchType.LAZY)
//    @JoinTable(name = "ORG_TO_ORG_MEMBERSHIP_RIGHTS",
//            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
//            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
//    @Fetch(FetchMode.SUBSELECT)
//	private Set<OrgToOrgMembershipXrefEntity> org2OrgMappings;


    @Transient
    private String displayName;

    @Transient
    @InternationalizedCollection(targetField = "displayName")
    private Map<String, LanguageMappingEntity> languageMap;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, LanguageMappingEntity> getLanguageMap() {
        return languageMap;
    }

    public void setLanguageMap(Map<String, LanguageMappingEntity> languageMap) {
        this.languageMap = languageMap;
    }

    public Set<ResourceToResourceMembershipXrefEntity> getResource2ResourceMappings() {
        return resource2ResourceMappings;
    }

    public void setResource2ResourceMappings(Set<ResourceToResourceMembershipXrefEntity> resource2ResourceMappings) {
        this.resource2ResourceMappings = resource2ResourceMappings;
    }
}
