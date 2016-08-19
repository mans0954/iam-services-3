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
package org.openiam.provision.service;

import org.openiam.idm.srvc.prov.request.dto.BulkOperationRequest;
import org.openiam.provision.dto.ProvisionUser;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


/**
 * <code>AsynchUserProvisionService</code> Interface for the Asynchronous Provisioning service which is used for provisioning users.
 * @author suneet
 *
 */
public interface AsynchUserProvisionDataService {

	/**
	 * The addUser operation enables a requestor to create a new user on the target systems
	 */
    void addUser(ProvisionUser user);
	
	/**
	 * The modifyUser operation enables the requestor to modify an existing user in appropriate target systems
	 */
    void modifyUser(ProvisionUser user);

    void startBulkOperation(BulkOperationRequest bulkRequest);
}
