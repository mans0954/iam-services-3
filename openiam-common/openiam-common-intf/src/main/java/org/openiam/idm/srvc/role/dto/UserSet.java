package org.openiam.idm.srvc.role.dto;

import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userSet", propOrder = {
        "userObj"
})
public class UserSet {
    protected List<UserSet.UserObj> userObj;
    public List<UserSet.UserObj> getUserObj() {
        if (userObj == null) {
            userObj = new ArrayList<UserSet.UserObj>();
        }
        return this.userObj;
    }
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "user"
    })
    public static class UserObj {

        protected User user;

        /**
         * Gets the value of the role property.
         *
         * @return possible object is
         *         {@link User }
         */
        public User getUser() {
            return user;
        }

        /**
         * Sets the value of the role property.
         *
         * @param value allowed object is
         *              {@link User }
         */
        public void setUser(User value) {
            this.user = value;
        }

    }
}
