package org.openiam.idm.srvc.user.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.dto.ProfilePicture;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;

@Entity
@Table(name = "PROFILE_PIC")
@AttributeOverrides(value={
    @AttributeOverride(name = "id", column = @Column(name = "PROFILE_PIC_ID")),
    @AttributeOverride(name = "name", column = @Column(name="NAME", length=256))
})
@DozerDTOCorrespondence(ProfilePicture.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProfilePictureEntity extends AbstractKeyNameEntity {

    @Lob
    @Column(name = "PICTURE", nullable = false)
    private byte[] picture;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "USER_ID", unique = true, updatable = false, nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity user;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfilePictureEntity)) return false;
        if (!super.equals(o)) return false;

        ProfilePictureEntity entity = (ProfilePictureEntity) o;

        if (!Arrays.equals(picture, entity.picture)) return false;
        if (user != null ? !user.equals(entity.user) : entity.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (picture != null ? Arrays.hashCode(picture) : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}