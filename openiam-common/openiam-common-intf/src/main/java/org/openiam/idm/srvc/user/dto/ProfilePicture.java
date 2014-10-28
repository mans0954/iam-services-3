package org.openiam.idm.srvc.user.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "picture", propOrder = {
        "picture",
        "user"
})
@DozerDTOCorrespondence(ProfilePictureEntity.class)

public class ProfilePicture extends KeyNameDTO {

    private byte[] picture;

    private User user;

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfilePicture)) return false;
        if (!super.equals(o)) return false;

        ProfilePicture that = (ProfilePicture) o;

        if (!Arrays.equals(picture, that.picture)) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

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
