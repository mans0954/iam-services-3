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

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.PasswordRequest;
import org.openiam.base.request.StringDataRequest;
import org.openiam.base.response.data.PolicyResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.base.request.PasswordResetTokenRequest;
import org.openiam.base.response.PasswordResetTokenResponse;
import org.openiam.base.response.PasswordValidationResponse;
import org.openiam.base.response.ValidatePasswordResetTokenResponse;
import org.openiam.mq.constants.api.user.PasswordAPI;
import org.openiam.mq.constants.queue.user.PasswordQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Web service implementation for the PasswordWebService
 *
 * @author suneet
 */
@Service("passwordWS")
@WebService(endpointInterface = "org.openiam.srvc.user.PasswordWebService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "PasswordWebServicePort", serviceName = "PasswordWebService")
public class PasswordWebServiceImpl extends AbstractApiService implements PasswordWebService {

    @Autowired
    public PasswordWebServiceImpl(PasswordQueue queue) {
        super(queue);
    }

    public PasswordValidationResponse isPasswordValid(Password pswd) {
        PasswordRequest request = new PasswordRequest();
        request.setPassword(pswd);
        return this.getResponse(PasswordAPI.Validate, request, PasswordValidationResponse.class);
    }

    @Override
    public PasswordResetTokenResponse generatePasswordResetToken(PasswordResetTokenRequest request) {
        return this.getResponse(PasswordAPI.GeneratePasswordResetToken, request, PasswordResetTokenResponse.class);
    }

    @Override
    public ValidatePasswordResetTokenResponse validatePasswordResetToken(String token) {
        StringDataRequest request = new StringDataRequest();
        request.setData(token);
        return this.getResponse(PasswordAPI.ValidateResetToken, request, ValidatePasswordResetTokenResponse.class);
    }

	@Override
	public String getPasswordResetToken(PasswordResetTokenRequest request) {
        return this.getValue(PasswordAPI.GetPasswordResetToken, request, StringResponse.class);
	}

	@Override
	public Policy getPasswordPolicy(final PasswordPolicyAssocSearchBean searchBean) {
        return this.getValue(PasswordAPI.GetPasswordPolicy, new BaseSearchServiceRequest<>(searchBean), PolicyResponse.class);
	}
}
