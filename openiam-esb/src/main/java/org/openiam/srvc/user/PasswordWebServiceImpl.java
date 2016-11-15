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
package org.openiam.srvc.user;

import javax.jws.WebService;

import org.openiam.exception.ObjectNotFoundException;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.base.request.PasswordResetTokenRequest;
import org.openiam.base.response.PasswordResetTokenResponse;
import org.openiam.base.response.PasswordValidationResponse;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.base.response.ValidatePasswordResetTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web service implementation for the PasswordWebService
 *
 * @author suneet
 */
@Service("passwordWS")
@WebService(endpointInterface = "org.openiam.srvc.user.PasswordWebService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "PasswordWebServicePort", serviceName = "PasswordWebService")
public class PasswordWebServiceImpl implements PasswordWebService {

    @Autowired
    private PasswordService passwordDS;

    public PasswordValidationResponse isPasswordValid(Password pswd) {
        return passwordDS.isPasswordValid(pswd);
    }

    @Override
    public PasswordResetTokenResponse generatePasswordResetToken(PasswordResetTokenRequest request) {
        return passwordDS.generatePasswordResetToken(request);
    }

    @Override
    public ValidatePasswordResetTokenResponse validatePasswordResetToken(String token) {
        return passwordDS.validatePasswordResetToken(token);
    }

	@Override
	public String getPasswordResetToken(PasswordResetTokenRequest request) {
		return passwordDS.getPasswordResetToken(request);
	}

	@Override
	public Policy getPasswordPolicy(final PasswordPolicyAssocSearchBean searchBean) {
		return passwordDS.getPasswordPolicy(searchBean);
	}
}
