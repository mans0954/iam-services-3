/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */
/**
 *
 */
package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.synch.dto.*;

import org.springframework.stereotype.Component;

/**
 * Scan Ldap for any new records, changed users, or delete operations and then synchronizes them back into OpenIAM.
 *
 * @author suneet
 */
@Component
public class LdapAdapter extends GenericLdapAdapter { // implements SourceAdapter

    protected LastRecordTime getRowTime(LineObject rowObj) {
        org.openiam.idm.srvc.synch.dto.Attribute atr = rowObj.get("modifyTimestamp");
        if (StringUtils.isNotBlank(atr.getValue())) {
            return getTime(atr);
        }
        atr = rowObj.get("createTimestamp");
        if (StringUtils.isNotBlank(atr.getValue())) {
            return getTime(atr);
        }
        return new LastRecordTime();
    }

    protected String[] getDirAttrIds() {
        return new String[] {"*", "modifyTimestamp", "createTimestamp"};
    }

}
