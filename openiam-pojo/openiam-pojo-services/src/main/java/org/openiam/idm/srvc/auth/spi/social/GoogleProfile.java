package org.openiam.idm.srvc.auth.spi.social;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexander on 23.03.15.
 */
public class GoogleProfile  extends AbstractSocialProfile {
    private String displayName;

    private List<GoogleEmailBean> emails;

    private GoogleNameBean name;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    @Override
    public String getEmail() {
        if(CollectionUtils.isNotEmpty(emails))
            return emails.get(0).getValue();
        return null;
    }

    @Override
    public String getFirstName() {
        return (name!=null)?name.getGivenName():null;
    }

    @Override
    public String getLastName() {
        return (name!=null)?name.getFamilyName():null;
    }
}
