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
package org.openiam.idm.srvc.synch.dto;

import org.openiam.idm.srvc.mngsys.dto.AttributeMap;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Response object from a synchronization request.
 *
 * @author suneet
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImportSyncResponse", propOrder = {
        "attributeMap",
})
public class ImportSyncResponse extends SyncResponse {
    private List<AttributeMap> attributeMap;

    public List<AttributeMap> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(List<AttributeMap> attributeMap) {
        this.attributeMap = attributeMap;
    }
}
