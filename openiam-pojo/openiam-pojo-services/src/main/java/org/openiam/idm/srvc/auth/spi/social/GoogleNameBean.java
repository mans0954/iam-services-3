package org.openiam.idm.srvc.auth.spi.social;

import java.io.Serializable;

/**
 * Created by alexander on 24.03.15.
 */
public class GoogleNameBean implements Serializable{
    private String familyName;
    private String givenName;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
}
