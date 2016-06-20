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
package org.openiam.idm.srvc.sysprop.ws;

import org.apache.log4j.Logger;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.sysprop.service.SystemPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

/**
 * @author suneet
 */
@Service("systemPropertyWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.sysprop.ws.SystemPropertyWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/sysprop/ws",
        serviceName = "SystemPropertyWebService",
        portName = "SystemPropertyWebServicePort")
public class SystemPropertyWebServiceImpl implements SystemPropertyWebService {

    private static Logger log = Logger.getLogger(SystemPropertyWebServiceImpl.class);

    @Autowired
    @Qualifier("systemPropertyService")
    private SystemPropertyService systemPropertyService;

    @Override
    public List<SystemPropertyDto> getByName(String name) {
        try {
            return systemPropertyService.getByName(name);
        } catch (Exception e) {
            log.equals(e);
            return null;
        }
    }

    @Override
    public List<SystemPropertyDto> getByType(String mdTypeId) {
        try {
            return systemPropertyService.getByType(mdTypeId);
        } catch (Exception e) {
            log.equals(e);
            return null;
        }
    }

    @Override
    public void save(SystemPropertyDto propertyDto) {
        try {
            systemPropertyService.save(propertyDto);
        } catch (Exception e) {
            log.equals(e);
        }
    }

    @Override
    public SystemPropertyDto getById(String id) {
        return systemPropertyService.getById(id);
    }

    @Override
    public List<SystemPropertyDto> getAll() {
        return systemPropertyService.getAll();
    }

}
