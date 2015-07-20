package org.openiam.idm.srvc.org.domain;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zaporozhec on 7/17/15.
 */
@Entity
@Table(name = "USER_AFFILIATION")
@AssociationOverrides({
        @AssociationOverride(name = "primaryKey.user",
                joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")),
        @AssociationOverride(name = "primaryKey.organization",
                joinColumns = @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID"))})
@DozerDTOCorrespondence(OrganizationUserDTO.class)
public class OrganizationUserEntity implements Serializable {

    public OrganizationUserEntity(String userId, String organizationId) {
        this.primaryKey = new OrganizationUserIdEntity();
        primaryKey.setUser(new UserEntity());
        primaryKey.setOrganization(new OrganizationEntity());
        primaryKey.getOrganization().setId(organizationId);
        primaryKey.getUser().setId(userId);
    }

    public OrganizationUserEntity() {
    }

    @EmbeddedId
    private OrganizationUserIdEntity primaryKey = new OrganizationUserIdEntity();


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "METADATA_TYPE_ID", referencedColumnName = "TYPE_ID", insertable = true, updatable = true)
    private MetadataTypeEntity metadataTypeEntity;


    public OrganizationUserIdEntity getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(OrganizationUserIdEntity primaryKey) {
        this.primaryKey = primaryKey;
    }

    public MetadataTypeEntity getMetadataTypeEntity() {
        return metadataTypeEntity;
    }

    public void setMetadataTypeEntity(MetadataTypeEntity metadataTypeEntity) {
        this.metadataTypeEntity = metadataTypeEntity;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.getPrimaryKey().setOrganization(organization);
    }

    public void setUser(UserEntity user) {
        this.getPrimaryKey().setUser(user);
    }

    @Transient
    public OrganizationEntity getOrganization() {
        return this.getPrimaryKey().getOrganization();
    }

    @Transient
    public UserEntity getUser() {
        return this.getPrimaryKey().getUser();
    }
}
