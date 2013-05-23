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
package org.openiam.idm.srvc.recon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.RemoteReconciliationConfig;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ReconciliationConfigDozerConverter;
import org.openiam.dozer.converter.ReconciliationSituationDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.mngsys.ws.ProvisionConnectorWebService;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.service.RemoteConnectorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suneet
 * 
 */
@Service
public class ReconciliationServiceImpl implements ReconciliationService,
        MuleContextAware {
    @Autowired
    protected ReconciliationSituationDAO reconSituationDAO;
    protected ReconciliationResultDAO reconResultDao;

    @Autowired
    protected ReconciliationConfigDAO reconConfig;
    protected ReconciliationResultDAO reconResultDetailDao;
    protected MuleContext muleContext;
    protected LoginDataService loginManager;
    protected ProvisionService provisionService;
    protected ResourceDataService resourceDataService;
    @Autowired
    protected UserDataService userMgr;
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private ProvisionConnectorWebService connectorService;

    protected ConnectorAdapter connectorAdapter;
    protected RemoteConnectorAdapter remoteConnectorAdapter;
    protected RoleDataService roleDataService;

    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private ReconciliationConfigDozerConverter reconConfigDozerMapper;
    @Autowired
    private ReconciliationSituationDozerConverter reconSituationDozerMapper;
    private static final Log log = LogFactory
            .getLog(ReconciliationServiceImpl.class);

    public ReconciliationResultDAO getReconResultDao() {
        return reconResultDao;
    }

    public void setReconResultDao(ReconciliationResultDAO reconResultDao) {
        this.reconResultDao = reconResultDao;
    }

    public ReconciliationResultDAO getReconResultDetailDao() {
        return reconResultDetailDao;
    }

    public void setReconResultDetailDao(
            ReconciliationResultDAO reconResultDetailDao) {
        this.reconResultDetailDao = reconResultDetailDao;
    }

    public MuleContext getMuleContext() {
        return muleContext;
    }

    public ReconciliationConfig addConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        return reconConfigDozerMapper.convertToDTO(reconConfig
                .add(reconConfigDozerMapper.convertToEntity(config, true)),
                true);

    }

    public void updateConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        reconConfig
                .update(reconConfigDozerMapper.convertToEntity(config, true));
    }

    public void removeConfigByResourceId(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        reconConfig.removeByResourceId(resourceId);
    }

    public void removeConfig(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        reconConfig.delete(reconConfig.findById(configId));
    }

    public ReconciliationConfig getConfigByResource(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfig
                .findByResourceId(resourceId);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);

    }

    public ReconciliationConfig getConfigById(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfig.findById(configId);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);
    }

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
    }

    public ReconciliationResponse startReconciliation(
            ReconciliationConfig config) {
        try {
            log.debug("Reconciliation started for configId="
                    + config.getReconConfigId() + " - resource="
                    + config.getResourceId());

            Resource res = resourceDataService.getResource(config
                    .getResourceId());
            String managedSysId = res.getManagedSysId();
            ManagedSysEntity mSys = managedSysService
                    .getManagedSysById(managedSysId);

            log.debug("ManagedSysId = " + managedSysId);
            log.debug("Getting identities for managedSys");

            Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
            for (ReconciliationSituation situation : config.getSituationSet()) {
                situations.put(situation.getSituation().trim(),
                        ReconciliationCommandFactory.createCommand(
                                situation.getSituationResp(), situation,
                                managedSysId));
                log.debug("Created Command for: " + situation.getSituation());
            }

            List<UserEntity> users = new ArrayList<UserEntity>();
            for (ResourceRole rRole : res.getResourceRoles()) {
                final List<UserEntity> usersInrole = roleDataService
                        .getUsersInRole(rRole.getId().getRoleId(), 0,
                                Integer.MAX_VALUE);
                if (CollectionUtils.isNotEmpty(usersInrole)) {
                    users.addAll(usersInrole);
                }
            }

            final List<LoginEntity> principalList = loginManager
                    .getAllLoginByManagedSys(managedSysId);
            if (principalList == null || principalList.isEmpty()) {
                log.debug("No identities found for managedSysId in IDM repository");
                ReconciliationResponse resp = new ReconciliationResponse(
                        ResponseStatus.SUCCESS);
                return resp;
            }
            for (final UserEntity u : users) {
                Login l = null;
                User user = userDozerConverter.convertToDTO(
                        userMgr.getUser(u.getUserId()), true);
                List<Login> logins = user.getPrincipalList();
                if (logins != null) {
                    for (Login login : logins) {
                        if (login.getDomainId().equalsIgnoreCase(
                                mSys.getDomainId())
                                && login.getManagedSysId().equalsIgnoreCase(
                                        managedSysId)) {
                            l = login;
                            break;
                        }
                    }
                }
                if (l == null) {
                    if (user.getStatus().equals(UserStatusEnum.DELETED)) {
                        // User is deleted and has no Identity for this managed
                        // system -> goto next user
                        continue;
                    }
                    // There was never a resource account for this user.
                    // Possibility: User was created before the managed Sys was
                    // associated.
                    // Situation: Login Not Found
                    ReconciliationCommand command = situations
                            .get("Login Not Found");
                    if (command != null) {
                        log.debug("Call command for IDM Login Not Found");
                        command.execute(l, user, null);
                    }
                    ReconciliationResponse resp = new ReconciliationResponse(
                            ResponseStatus.SUCCESS);
                    return resp;
                }

                String principal = l.getLogin();
                log.debug("looking up identity in resource: " + principal);

                LookupUserResponse lookupResp = provisionService
                        .getTargetSystemUser(principal, managedSysId);

                log.debug("Lookup status for " + principal + " ="
                        + lookupResp.getStatus());

                // User user =
                // userMgr.getUserByPrincipal(l.getId().getDomainId(),
                // l.getId().getLogin(), l.getId().getManagedSysId(), true);

                if (lookupResp.getStatus() == ResponseStatus.FAILURE
                        && !l.getStatus().equalsIgnoreCase("INACTIVE")) {
                    // Situation: Resource Delete
                    ReconciliationCommand command = situations
                            .get("Resource Delete");
                    if (command != null) {
                        log.debug("Call command for Resource Delete");
                        command.execute(l, user, null);
                    }
                } else if (lookupResp.getStatus() == ResponseStatus.SUCCESS) {
                    // found entry in managed sys
                    if (l.getStatus().equalsIgnoreCase("INACTIVE")
                            || user.getStatus().equals(UserStatusEnum.DELETED)) {
                        // Situation: IDM Delete
                        ReconciliationCommand command = situations
                                .get("IDM Delete");
                        if (command != null) {
                            log.debug("Call command for IDM Delete");
                            command.execute(l, user, null);
                        }
                    } else {
                        // Situation: IDM Changed/Resource Changed/Match Found
                        ReconciliationCommand command = situations
                                .get("Match Found");
                        if (command != null) {
                            log.debug("Call command for Match Found");
                            command.execute(l, user, null);
                        }
                    }

                }
            }

            ProvisionConnectorDto connector = connectorService
                    .getProvisionConnector(mSys.getConnectorId());

            if (connector.getConnectorInterface() != null
                    && connector.getConnectorInterface().equalsIgnoreCase(
                            "REMOTE")) {

                log.debug("Calling reconcileResource with Remote connector");
                RemoteReconciliationConfig remoteReconciliationConfig = null;
                if (config != null) {
                    remoteReconciliationConfig = new RemoteReconciliationConfig(
                            config);
                    remoteReconciliationConfig.setScriptHandler(mSys
                            .getReconcileResourceHandler());
                }
                remoteConnectorAdapter.reconcileResource(
                        remoteReconciliationConfig, connector, muleContext);
            } else {

                log.debug("Calling reconcileResource local connector");
                connectorAdapter.reconcileResource(
                        managedSysDozerConverter.convertToDTO(mSys, true),
                        config, muleContext);
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            ReconciliationResponse resp = new ReconciliationResponse(
                    ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
            return resp;
        }
        ReconciliationResponse resp = new ReconciliationResponse(
                ResponseStatus.SUCCESS);
        return resp;

    }

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    public ProvisionService getProvisionService() {
        return provisionService;
    }

    public void setProvisionService(ProvisionService provisionService) {
        this.provisionService = provisionService;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    public UserDataService getUserMgr() {
        return userMgr;
    }

    public void setUserMgr(UserDataService userMgr) {
        this.userMgr = userMgr;
    }

    public ProvisionConnectorWebService getConnectorService() {
        return connectorService;
    }

    public void setConnectorService(
            ProvisionConnectorWebService connectorService) {
        this.connectorService = connectorService;
    }

    public void setConnectorAdapter(ConnectorAdapter connectorAdapter) {
        this.connectorAdapter = connectorAdapter;
    }

    public void setRemoteConnectorAdapter(
            RemoteConnectorAdapter remoteConnectorAdapter) {
        this.remoteConnectorAdapter = remoteConnectorAdapter;
    }

    public void setRoleDataService(RoleDataService roleDataService) {
        this.roleDataService = roleDataService;
    }
}
