package org.openiam.connector.salesforce.model;

import com.sforce.soap.partner.sobject.SObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseModel extends SObject {
    protected static final String DEFAULT_LOCALE = "en_US";

    public String getProfileId() {
        final Object value = getField("ProfileId");
        if(value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    public void setProfileId(final String profileId) {
        setField("ProfileId", StringUtils.trimToNull(profileId));
    }

    public abstract String getNameField();
}
