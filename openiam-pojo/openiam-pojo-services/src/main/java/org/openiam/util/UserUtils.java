package org.openiam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.MatchType;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.dto.SearchAttribute;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {

    private static final Log log = LogFactory.getLog(UserUtils.class);
    final static private Pattern delegationFilterAttributePattern = Pattern.compile("\"(.*)\";\"(.*)\";\"(.*)\"");

    public static LoginEntity getUserManagedSysIdentityEntity(final String managedSysId, final List<LoginEntity> principalList) {
        if (principalList == null ||
                principalList.size() == 0) {
            return null;
        }
        for (LoginEntity l : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {

                if (log.isDebugEnabled()) {
                    log.debug("getUserManagedSysIdentityEntity() return ->" + l);
                }

                return l;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("getUserManagedSysIdentityEntity() not found. returning null");
        }
        return null;
    }

    public static SearchAttribute parseDelegationFilterAttribute(String param) {
        SearchAttribute retVal = new SearchAttribute();
        try {
            Matcher matcher = delegationFilterAttributePattern.matcher(param.toLowerCase());
            if (matcher.matches()) {
                retVal.setAttributeName(matcher.group(1));
                retVal.setAttributeValue(matcher.group(2));
                retVal.setMatchType(MatchType.valueOf(matcher.group(3).toUpperCase()));
            }
        } catch (Exception e) {
            log.warn("Can't parse Attribute delegation filer=" + param);
            log.warn(e);
        }
        return retVal;
    }

    public static Login getUserManagedSysIdentity(final String managedSysId, final List<Login> principalList) {
        if (principalList == null ||
                principalList.size() == 0) {
            return null;
        }
        for (Login l : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
                if (log.isDebugEnabled()) {
                    log.debug("getUserManagedSysIdentityEntity() return ->" + l);
                }
                return l;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("getUserManagedSysIdentityEntity() not found. returning null");
        }
        return null;
    }
}
