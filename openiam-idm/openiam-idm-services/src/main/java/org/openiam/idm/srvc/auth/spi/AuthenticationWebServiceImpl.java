package org.openiam.idm.srvc.auth.spi;
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



*
 *
*/


import java.util.List;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.dto.*;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import edu.emory.mathcs.backport.java.util.Arrays;

/*
*
 * @author suneet

*/


@Service("authenticate")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.service.AuthenticationService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
@ManagedResource(objectName = "openiam:name=authenticationService", description = "Authentication Service")
@Transactional
public class AuthenticationWebServiceImpl extends AbstractBaseService implements AuthenticationService, ApplicationContextAware, BeanFactoryAware {

    @Autowired
    private AuthenticationServiceService authenticationService;

    private ApplicationContext ctx;
    private BeanFactory beanFactory;

    @Override
    //@ManagedAttribute
    public void globalLogout(String userId) throws Throwable {

        authenticationService.globalLogout(userId);
    }

    @Override
    public Response globalLogoutRequest(LogoutRequest request) {
        return authenticationService.globalLogoutRequest(request);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        return authenticationService.login(request);
    }

    @Override
    public Response clearOTPActiveStatus(OTPServiceRequest request) {
        return authenticationService.clearOTPActiveStatus(request);
    }

    @Override
    public boolean isOTPActive(OTPServiceRequest request) {
        return authenticationService.isOTPActive(request);
    }

    @Override
    public Response sendOTPToken(OTPServiceRequest request) {
        return authenticationService.sendOTPToken(request);
    }

    @Override
    public Response confirmOTPToken(OTPServiceRequest request) {
        return authenticationService.confirmOTPToken(request);
    }

    @Override
    public Response getOTPSecretKey(OTPServiceRequest request) {
        return authenticationService.getOTPSecretKey(request);
    }

    @Override
    public Response renewToken(String principal, String token, String tokenType, String patternId) {
       return authenticationService.renewToken(principal, token, tokenType, patternId);
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


    @Override
    public void setApplicationContext(ApplicationContext ctx)
            throws BeansException {
        this.ctx = ctx;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
