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
package org.openiam.idm.srvc.synch.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.SynchConfigDozerConverter;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.service.AttributeMapDAO;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.srcadapter.AdapterFactory;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserToResourceMembershipXref;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.AsynchUserProvisionService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 *
 */
@Service("synchService")
public class IdentitySynchServiceImpl implements IdentitySynchService {

    @Autowired
    private SynchConfigDAO synchConfigDao;
    @Autowired
    protected AttributeMapDAO attributeMapDAO;
    @Autowired
    protected SynchReviewDAO synchReviewDAO;
    @Autowired
    protected SynchReviewDozerConverter synchReviewDozerConverter;
    @Autowired
    private AdapterFactory adapterFactory;

    @Autowired
    private UserDataService userManager;
    @Autowired
    @Qualifier("defaultProvision")
    private ProvisionService provisionService;

    @Autowired
    @Qualifier("asynchProvisonWS")
    private AsynchUserProvisionService asyncProvisionService;

    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private SynchConfigDozerConverter synchConfigDozerConverter;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    @Autowired
    private AuditLogService auditLogService;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

	private static final Log log = LogFactory.getLog(IdentitySynchServiceImpl.class);

    /*
    * The flags for the running tasks are handled by this Thread-Safe Set.
    * It stores the taskIds of the currently executing tasks.
    * This is faster and as reliable as storing the flags in the database,
    * if the tasks are only launched from ONE host in a clustered environment.
    * It is unique for each class-loader, which means unique per war-deployment.
    */
    private static Set<String> runningTask = Collections.newSetFromMap(new ConcurrentHashMap());
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.synch.service.IdentitySynchService#getAllConfig()
	 */
    @Transactional(readOnly = true)
	public List<SynchConfigEntity> getAllConfig() {
		List<SynchConfigEntity> configList = synchConfigDao.findAllConfig();
		if ( configList != null && !configList.isEmpty()) {
			return configList;
		}
		return null;
	}

    @Transactional(readOnly = true)
	public SynchConfigEntity findById(java.lang.String id)  {
		if (id == null) {
			throw new IllegalArgumentException("id parameter is null");
		}
		
		return synchConfigDao.findById(id);
	}

    @Transactional
	public SynchConfigEntity addConfig(SynchConfigEntity synchConfig) {
		if (synchConfig == null) {
			throw new IllegalArgumentException("synchConfig parameter is null");
		}
		return synchConfigDao.add(synchConfig);
		
	}

    @Transactional
	public SynchConfigEntity mergeConfig(SynchConfigEntity synchConfig) {
		if (synchConfig == null) {
			throw new IllegalArgumentException("synchConfig parameter is null");
		}
        if (synchConfig.getSynchReviews() == null) { // Explicitly add synch reviews to the entity
            synchConfig.setSynchReviews(getAllSynchReviewsBySynchConfigId(synchConfig.getSynchConfigId()));
        }
		return synchConfigDao.merge(synchConfig);
				
	}

    @Transactional
	public void removeConfig(String configId ) {
		if (configId == null) {
			throw new IllegalArgumentException("id parameter is null");
		}
        deleteAttributesMapList(getSynchConfigAttributeMaps(configId));
        SynchConfigEntity config = synchConfigDao.findById(configId);
		synchConfigDao.remove(config);
		
	}

    public SyncResponse startSynchronization(SynchConfigEntity config) {
        return startSynchronization(config, null);
    }

    public SyncResponse startSynchReview(SynchReviewEntity synchReview) {
        return startSynchronization(synchReview.getSynchConfig(), synchReview);
    }

	private SyncResponse startSynchronization(final SynchConfigEntity config, SynchReviewEntity review) {

        SyncResponse syncResponse = new SyncResponse(ResponseStatus.SUCCESS);

        log.debug("-startSynchronization CALLED.^^^^^^^^");

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setRequestorPrincipal("sysadmin");
        idmAuditLog.setAction(AuditAction.SYNCHRONIZATION.value());
        idmAuditLog.setSource(config.getSynchConfigId());

        if ("INACTIVE".equalsIgnoreCase(config.getStatus())) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Synchronization config is in 'INACTIVE' status");
            idmAuditLog = auditLogService.save(idmAuditLog);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_PROCESS_INACTIVE);
            return resp;
        }

        SyncResponse processCheckResponse = addTask(config.getSynchConfigId());
        if (processCheckResponse.getStatus() == ResponseStatus.FAILURE &&
                processCheckResponse.getErrorCode() == ResponseCode.FAIL_PROCESS_ALREADY_RUNNING) {
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "WARNING: Previous synchronization run is not finished yet");
            idmAuditLog = auditLogService.save(idmAuditLog);
            return processCheckResponse;

        }

        Date startDate = new Date();
        SynchReviewEntity resultReview = new SynchReviewEntity(config, startDate);

        try {

            SynchConfig configDTO = synchConfigDozerConverter.convertToDTO(config, false);
            SynchReview reviewDTO = synchReviewDozerConverter.convertToDTO(review, false);

            String preScriptUrl = config.getPreSyncScript();
            if (StringUtils.isNotBlank(preScriptUrl)) {
                log.debug("-PRE synchronization script CALLED.^^^^^^^^");
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("config", configDTO);
                if (reviewDTO != null) {
                    bindingMap.put("review", reviewDTO);
                }

                try {
                    int ret = (Integer)scriptRunner.execute(bindingMap, preScriptUrl);
                    if (ret == SyncConstants.FAIL) {
                        syncResponse.setStatus(ResponseStatus.FAILURE);
                        syncResponse.setErrorCode(ResponseCode.SYNCHRONIZATION_PRE_SRIPT_FAILURE);
                        return syncResponse;
                    }
                    log.debug("-PRE synchronization script COMPLETE.^^^^^^^^");
                    if (ret == SyncConstants.SKIP) {
                        return syncResponse;

                    } else if (ret == SyncConstants.SKIP_TO_REVIEW) {
                        resultReview.setSourceRejected(true);
                        return syncResponse;
                    }

                } catch(Exception e) {
                    log.error(e);
                }
            }

            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "Synchronization started..." + startDate);

			long newLastExecTime = System.currentTimeMillis();
            idmAuditLog = auditLogService.save(idmAuditLog);
            configDTO.setParentAuditLogId(idmAuditLog.getId());
            SourceAdapter adapt = adapterFactory.create(configDTO);
            syncResponse = adapt.startSynch(configDTO, review, resultReview);
			
 			log.debug("SyncReponse updateTime value=" + newLastExecTime);
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "SyncReponse updateTime value=" + newLastExecTime);

            if (syncResponse.getLastRecordTime() == null) {
				synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( newLastExecTime ));
			} else {
				synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( syncResponse.getLastRecordTime().getTime() ));
			}

            if (syncResponse.getLastRecProcessed() != null) {

				synchConfigDao.updateLastRecProcessed(config.getSynchConfigId(),syncResponse.getLastRecProcessed() );
			}

		    log.debug("-startSynchronization COMPLETE.^^^^^^^^");
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "-startSynchronization COMPLETE.^^^^^^^^");

            String postScriptUrl = config.getPostSyncScript();
            if (StringUtils.isNotBlank(postScriptUrl)) {
                log.debug("-POST synchronization script CALLED.^^^^^^^^");
                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("config", synchConfigDozerConverter.convertToDTO(config, false));
                try {
                    int ret = (Integer)scriptRunner.execute(bindingMap, postScriptUrl);
                    if (ret == SyncConstants.FAIL) {
                        syncResponse.setStatus(ResponseStatus.FAILURE);
                        syncResponse.setErrorCode(ResponseCode.SYNCHRONIZATION_POST_SRIPT_FAILURE);
                        return syncResponse;
                    }
                    log.debug("-POST synchronization script COMPLETE.^^^^^^^^");
                } catch(Exception e) {
                    log.error(e);
                }
            }

            System.out.println("IdentitySyncServiceImpl finished in => "+(System.currentTimeMillis()-newLastExecTime) +" milliseconds.");

		} catch( ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
			log.error(cnfe);
            syncResponse = new SyncResponse(ResponseStatus.FAILURE);
            syncResponse.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            syncResponse.setErrorText(cnfe.getMessage());
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ERROR: "+cnfe.getMessage());
        } catch(Exception e) {
			log.error(e);
            e.printStackTrace();
            syncResponse = new SyncResponse(ResponseStatus.FAILURE);
            syncResponse.setErrorText(e.getMessage());
            idmAuditLog.addAttribute(AuditAttributeName.DESCRIPTION, "ERROR: "+e.getMessage());
        } finally {
            endTask(config.getSynchConfigId());
            if (resultReview.isSourceRejected() || CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) {
                synchReviewDAO.save(resultReview);
            }
            idmAuditLog = auditLogService.save(idmAuditLog);
        }

        return syncResponse;
	}

    // manage if the task is running

    /**
     * Updates the RunningTask list to show that a process is running
     * @param configId
     * @return
     */
    private SyncResponse addTask(String configId) {

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
        synchronized (runningTask) {
            if(runningTask.contains(configId)) {

                resp = new SyncResponse(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_PROCESS_ALREADY_RUNNING);
                return resp;
            }
            runningTask.add(configId);
            return resp;
        }

    }

    private void endTask(String configID) {
        runningTask.remove(configID);
    }

    public Response testConnection(SynchConfigEntity config) {
        try {
            SynchConfig configDTO = synchConfigDozerConverter.convertToDTO(config, false);
            SourceAdapter adapt = adapterFactory.create(configDTO);

            return adapt.testConnection(configDTO);

        } catch (ClassNotFoundException e) {
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(e.getMessage());

            return resp;

        } catch (IOException e) {
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            resp.setErrorText(e.getMessage());

            return resp;
        }
    }

    @Transactional
    public Response bulkUserMigration(final BulkMigrationConfig config) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            // select the user that we need to move
            final UserSearchBean search = buildSearch(config);
            /*
            if (search.isEmpty()) {
                resp.setStatus(ResponseStatus.FAILURE);
                return resp;
            }
            */

            List<User> searchResult = null;

            searchResult = userDozerConverter.convertToDTOList(userManager.findBeans(search), true);


            // all the provisioning service
            for (final  User user :  searchResult) {

                log.debug("Migrating user: " + user.getId() + " " + user.getLastName());

                ProvisionUser pUser = new ProvisionUser(user);

                if (config.getTargetRole() != null && !config.getTargetRole().isEmpty() ) {

                    final Role r = parseRole(config.getTargetRole());
                    if ("ADD".equalsIgnoreCase(config.getOperation())) {
                        // add to role
                    	pUser.addRole(r, config.getRightIds());
                    } else {
                    	pUser.removeRole(r.getId());
                    }

                } else if (config.getTargetResource() != null && !config.getTargetResource().isEmpty()) {

                    final Set<UserToResourceMembershipXref> xrefSet = new HashSet<UserToResourceMembershipXref>();

                    final UserToResourceMembershipXref xref = new UserToResourceMembershipXref();
                    xref.setEntityId(config.getTargetResource());

                    if ("ADD".equalsIgnoreCase(config.getOperation())) {
                        // add to resourceList
                    	xref.setOperation(AttributeOperationEnum.ADD);
                        xrefSet.add(xref);
                        pUser.setResources(xrefSet);

                    } else {
                        // remove from resource List

                    	xref.setOperation(AttributeOperationEnum.DELETE);
                    	xrefSet.add(xref);
                        pUser.setResources(xrefSet);

                    }
                }
                // send message to provisioning service asynchronously
                asyncProvisionService.modifyUser(pUser);
            }
        } catch (BasicDataServiceException e) {
            log.error(e.getLocalizedMessage(),e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(e.getCode());
        }
        return null;
    }

    private UserSearchBean buildSearch(BulkMigrationConfig config){
    	UserSearchBean search = new UserSearchBean();
        if (config.getOrganizationId() != null && !config.getOrganizationId().isEmpty()) {
             search.addOrganizationId(config.getOrganizationId());
        }

        if (config.getLastName() != null && !config.getLastName().isEmpty()) {
            search.setLastNameMatchToken(new SearchParam(config.getLastName(), MatchType.EXACT));
        }

        if (config.getDeptId() != null && !config.getDeptId().isEmpty()) {
        	search.addOrganizationId(config.getDeptId());
        }

        if (config.getDivision() != null && !config.getDivision().isEmpty()) {
        	search.addOrganizationId(config.getDivision());
        }

        if (config.getAttributeName() != null && !config.getAttributeName().isEmpty()) {
        	search.addAttribute(config.getAttributeName(), config.getAttributeValue());
        }

        if (config.getUserStatus() != null ) {
        	search.setUserStatus(config.getUserStatus().toString());
        }

        return search;
    }

    private Role parseRole(String roleStr) {
        String roleId = null;

        StringTokenizer st = new StringTokenizer(roleStr, "*");
        if (st.hasMoreElements()) {
            roleId = st.nextToken();
        }
        Role r = new Role();
        r.setId(roleId);

        return r;
    }

    @Override
    @Transactional
    public Response resynchRole(final String roleId) {

        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            log.debug("Resynch Role: " + roleId );

            final UserSearchBean searchBean = new UserSearchBean();
            searchBean.addRoleId(roleId);
            List<User> searchResult = null;

            searchResult = userDozerConverter.convertToDTOList(userManager.findBeans(searchBean), true);

            if (searchResult == null) {
                resp.setStatus(ResponseStatus.FAILURE);
                return resp;
            }

            // create role object to show role membership
            Role rl = new Role();
            rl.setId(roleId);

            // all the provisioning service
            for ( User user :  searchResult) {

                log.debug("Updating the user since this role's configuration has changed.: " + user.getId() + " " + user.getLastName());

                ProvisionUser pUser = new ProvisionUser(user);

                pUser.addRole(rl, null);
                provisionService.modifyUser(pUser);

            }
        } catch (BasicDataServiceException e) {
            log.error(e.getLocalizedMessage(),e);
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorCode(e.getCode());
        }
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSynchConfigCountByExample(SynchConfigEntity example) {
        return synchConfigDao.count(example);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SynchConfigEntity> getSynchConfigsByExample(SynchConfigEntity example, Integer from, Integer size) {
        return synchConfigDao.getByExample(example, from, size);
    }

    @Override
    @Transactional
    public void deleteAttributesMapList(List<AttributeMapEntity> attrMap) {
        attributeMapDAO.deleteAttributesMapList(attrMap);
    }

    @Override
    @Transactional
    public void deleteSynchReviewList(List<SynchReviewEntity> reviewList) {
        if (CollectionUtils.isNotEmpty(reviewList)) {
            for (SynchReviewEntity e : reviewList) {
                synchReviewDAO.delete(e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SynchReviewEntity> getAllSynchReviewsBySynchConfigId(String synchConfigId) {
        return synchReviewDAO.findAllBySynchConfigId(synchConfigId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getSynchConfigAttributeMaps(String synchConfigId) {
        return attributeMapDAO.findBySynchConfigId(synchConfigId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getSynchConfigAttributeMaps(AttributeMapSearchBean searchBean) {
        return attributeMapDAO.getByExample(searchBean);
    }

}
