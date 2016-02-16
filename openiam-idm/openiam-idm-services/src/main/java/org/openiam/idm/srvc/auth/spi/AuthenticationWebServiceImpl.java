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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.*;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

// import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author suneet
 */

@Service("authenticate")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.service.AuthenticationService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
@ManagedResource(objectName = "openiam:name=authenticationService", description = "Authentication Service")
public class AuthenticationWebServiceImpl extends AbstractBaseService implements AuthenticationService {

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Value("${org.openiam.idm.sendSmsOtp}")
    private String smsGrooryScript;

    @Autowired
    private AuthenticationServiceService authenticationService;

    @Autowired
    protected LoginDataService loginManager;

    @Value("${org.openiam.idm.smsTokenExpirationInMin}")
    private Integer smsTokenExpirationInMin;

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

    @Override
    public Response sendSMSOtp(OTPServiceRequest request) {
        System.out.println("-------- in sendSMSOtp ---------------");
        final IdmAuditLog event = new IdmAuditLog();
        event.setAction(AuditAction.SEND_SMS_OTP_TOKEN.value());
        event.setUserId(request.getUserId());
        final Response response = new Response();
        final Phone phone = request.getPhone();
        final String userId = request.getUserId();
        try {
            List<LoginEntity> loginEntities =  loginManager.getLoginByUser(userId);
            if(loginEntities != null && !loginEntities.isEmpty()) {
                LoginEntity login =  loginEntities.get(0);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, smsTokenExpirationInMin);
                login.setSmsResetTokenExp(c.getTime());
                AbstractSMSOTPModule module = (AbstractSMSOTPModule) scriptRunner.instantiateClass(null, smsGrooryScript);
                String token = module.generateSMSToken(phone, login);
                login.setSmsResetToken(token);
                loginManager.updateLogin(login);
                response.succeed();
                event.succeed();
            } else {
                event.addWarning("Login not found!");
                event.fail();
            }
        } catch (Exception e) {
            event.fail();
            response.fail();
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            event.fail();
            response.fail();
            response.setErrorText(e.getMessage());
        } finally {
            auditLogService.save(event);
        }
        return response;
    }

    @Override
    public Response validateSMSOtp(OTPServiceRequest request) {
        final IdmAuditLog event = new IdmAuditLog();
        event.setAction(AuditAction.VALIDATE_SMS_OTP_TOKEN.value());
        event.setUserId(request.getUserId());
        event.setAuditDescription("Token used to validate is "+ request.getOtpCode());
        event.succeed();
        List<LoginEntity> loginEntities = loginManager.getLoginByUser(request.getUserId());
        final Response response = new Response();
        try {
            if (loginEntities != null && !loginEntities.isEmpty()) {
                LoginEntity login = loginEntities.get(0);
                if (login.getSmsResetToken() != null && login.getSmsResetToken().equals(request.getOtpCode()) && new Date().before(login.getSmsResetTokenExp())) {
                    response.succeed();
                } else {
                    response.fail();
                }
            } else {
                response.fail();
            }
        } catch (Exception e) {
            event.fail();
        } finally {
            auditLogService.save(event);
        }
        return response;
    }

}
