package org.openiam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;

import java.util.List;

public class UserUtils {

    private static final Log log = LogFactory.getLog(UserUtils.class);

    public static LoginEntity getUserManagedSysIdentityEntity(final String managedSysId, final List<LoginEntity> principalList) {
        if (principalList == null ||
                principalList.size() == 0) {
            return null;
        }
        for (LoginEntity l  : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {

            	if(log.isDebugEnabled()) {
            		log.debug("getUserManagedSysIdentityEntity() return ->" + l);
            	}

                return l;
            }
        }
        if(log.isDebugEnabled()) {
        	log.debug("getUserManagedSysIdentityEntity() not found. returning null" );
        }
        return null;
    }

    public static Login getUserManagedSysIdentity(final String managedSysId, final List<Login> principalList) {
        if (principalList == null ||
                principalList.size() == 0) {
            return null;
        }
        for (Login l  : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
            	if(log.isDebugEnabled()) {
            		log.debug("getUserManagedSysIdentityEntity() return ->" + l);
            	}
                return l;
            }
        }
        if(log.isDebugEnabled()) {
        	log.debug("getUserManagedSysIdentityEntity() not found. returning null" );
        }
        return null;
    }
}
