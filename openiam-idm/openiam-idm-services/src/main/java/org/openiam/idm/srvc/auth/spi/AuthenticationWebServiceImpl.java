/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.auth.spi;

import java.util.List;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.dto.*;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.idm.srvc.auth.service.AuthenticationWebService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

// import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author suneet
 */

@Service("authenticate")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.service.AuthenticationWebService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
@ManagedResource(objectName = "openiam:name=authenticationService", description = "Authentication Service")
public class AuthenticationWebServiceImpl extends AbstractBaseService implements AuthenticationWebService {

    @Autowired
    private AuthenticationServiceService authenticationService;

    @Override
    @ManagedAttribute
    public void globalLogout(String userId) throws Throwable {

        authenticationService.globalLogout(userId);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        return authenticationService.login(request);
    }

    @Override
    public Response renewToken(String principal, String token, String tokenType) {
       return authenticationService.renewToken(principal, token, tokenType);
    }

    @Override
    public List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean,
                                           int from, int size) {
        return authenticationService.findBeans(searchBean, from, size);
    }

    @Override
    public Response save(final AuthStateEntity entity) {
       return authenticationService.save(entity);
    }

}
