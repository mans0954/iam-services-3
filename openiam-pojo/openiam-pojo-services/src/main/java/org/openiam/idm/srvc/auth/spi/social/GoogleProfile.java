package org.openiam.idm.srvc.auth.spi.social;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexander on 23.03.15.
 */
public class GoogleProfile  implements Serializable {
    private String id;
    private String displayName;

    private String gender;
    private List<GoogleEmailBean> emails;

    private GoogleNameBean name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<GoogleEmailBean> getEmails() {
        return emails;
    }

    public void setEmails(List<GoogleEmailBean> emails) {
        this.emails = emails;
    }

    public GoogleNameBean getName() {
        return name;
    }

    public void setName(GoogleNameBean name) {
        this.name = name;
    }
}
