package org.openiam.idm.srvc.auth.spi.social;

import java.io.Serializable;

/**
 * Created by alexander on 01.04.15.
 */
public abstract  class AbstractSocialProfile implements Serializable {
    private String id;
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public abstract String getEmail();
    public abstract String getFirstName();
    public abstract String getLastName();
}
