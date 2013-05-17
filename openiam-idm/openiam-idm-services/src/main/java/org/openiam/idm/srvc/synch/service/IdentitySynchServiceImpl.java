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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.module.client.MuleClient;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.SynchConfigDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.srcadapter.AdapterFactory;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SynchConfigDataMappingDAO synchConfigDataMappingDAO;
    @Autowired
    private AdapterFactory adaptorFactory;

    private MuleContext muleContext;
    @Autowired
    private UserDataService userManager;
    @Autowired
    private ProvisionService provisionService;
    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private SynchConfigDozerConverter synchConfigDozerConverter;

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

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
		return synchConfigDao.merge(synchConfig);
				
	}
    @Transactional
	public void removeConfig(String configId ) {
		if (configId == null) {
			throw new IllegalArgumentException("id parameter is null");
		}
        SynchConfigEntity config = synchConfigDao.findById(configId);
		synchConfigDao.remove(config);
		
	}
    @Transactional
	public SyncResponse startSynchronization(SynchConfigEntity config) {

        SyncResponse syncResponse = new SyncResponse(ResponseStatus.SUCCESS);

        log.debug("-startSynchronization CALLED.^^^^^^^^");

        SyncResponse processCheckResponse = addTask(config.getSynchConfigId());
        if ( processCheckResponse.getStatus() == ResponseStatus.FAILURE ) {
            return processCheckResponse;

        }
        try {

            SynchConfig configDTO = synchConfigDozerConverter.convertToDTO(config, false);

			SourceAdapter adapt = adaptorFactory.create(configDTO);
            adapt.setMuleContext(muleContext);

			long newLastExecTime = System.currentTimeMillis();

            syncResponse = adapt.startSynch(configDTO);
			
			log.debug("SyncReponse updateTime value=" + syncResponse.getLastRecordTime());
			
			if (syncResponse.getLastRecordTime() == null) {
			
				synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( newLastExecTime ));
			}else {
				synchConfigDao.updateExecTime(config.getSynchConfigId(), new Timestamp( syncResponse.getLastRecordTime().getTime() ));
			}

            if (syncResponse.getLastRecProcessed() != null) {

				synchConfigDao.updateLastRecProcessed(config.getSynchConfigId(),syncResponse.getLastRecProcessed() );
			}

		    log.debug("-startSynchronization COMPLETE.^^^^^^^^");

		}catch( ClassNotFoundException cnfe) {

            cnfe.printStackTrace();

			log.error(cnfe);
            syncResponse = new SyncResponse(ResponseStatus.FAILURE);
            syncResponse.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            syncResponse.setErrorText(cnfe.getMessage());

		}catch(Exception e) {


			log.error(e);
            syncResponse = new SyncResponse(ResponseStatus.FAILURE);
            syncResponse.setErrorText(e.getMessage());

		}finally {
            endTask(config.getSynchConfigId());

            return syncResponse;

        }

	}

    // manage if the task is running

    /**
     * Updates the RunningTask list to show that a process is running
     * @param configId
     * @return
     */
    @Transactional
    public SyncResponse addTask(String configId) {

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
    @Transactional
    public void endTask(String configID) {
        runningTask.remove(configID);
    }

    public Response testConnection(SynchConfigEntity config) {
        try {
            SynchConfig configDTO = synchConfigDozerConverter.convertToDTO(config, false);
            SourceAdapter adapt = adaptorFactory.create(configDTO);
            adapt.setMuleContext(muleContext);

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
    public Response bulkUserMigration(BulkMigrationConfig config) {

        Response resp = new Response(ResponseStatus.SUCCESS);

        // select the user that we need to move
        UserSearch search = buildSearch(config);
        if (search.isEmpty()) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }

        List<User> searchResult =  userDozerConverter.convertToDTOList(userManager.search(search), true);

        // all the provisioning service
        for ( User user :  searchResult) {

            log.debug("Migrating user: " + user.getUserId() + " " + user.getLastName());

            ProvisionUser pUser = new ProvisionUser(user);

            if (config.getTargetRole() != null && !config.getTargetRole().isEmpty() ) {

                Role r = parseRole(config.getTargetRole());
                if ( pUser.getMemberOfRoles() == null ) {
                    List<Role> roleList = new ArrayList<Role>();
                    pUser.setMemberOfRoles(roleList);
                }

                if ("ADD".equalsIgnoreCase(config.getOperation())) {
                    // add to role
                    r.setOperation(AttributeOperationEnum.ADD);


                    pUser.getMemberOfRoles().add(r);

                } else {
                    // remove from role
                    r.setOperation(AttributeOperationEnum.DELETE);
                    pUser.getMemberOfRoles().add(r);
                }

            }else if (config.getTargetResource() != null && !config.getTargetResource().isEmpty()) {

                List<UserResourceAssociation> uraList = new ArrayList<UserResourceAssociation>();

                UserResourceAssociation ura = new UserResourceAssociation();
                ura.setResourceId(config.getTargetResource());

                if ("ADD".equalsIgnoreCase(config.getOperation())) {
                    // add to resourceList
                    ura.setOperation(AttributeOperationEnum.ADD);
                    uraList.add(ura);
                    pUser.setUserResourceList(uraList);

                } else {
                    // remove from resource List

                    ura.setOperation(AttributeOperationEnum.DELETE);
                    uraList.add(ura);
                    pUser.setUserResourceList(uraList);

                }

            }
            // send message to provisioning service asynchronously
            //invokeOperation(pUser);
            provisionService.modifyUser(pUser);

        }

        return null;
    }

    private void invokeOperation(ProvisionUser pUser) {
        try {

            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);

            //Create the client with the context
            MuleClient client = new MuleClient(muleContext);
            client.sendAsync("vm://provisionServiceModifyMessage", pUser, msgPropMap);

        } catch (Exception e) {
            log.debug("EXCEPTION:bulkUserMigration");
            log.error(e);
        }
    }

    private UserSearch buildSearch(BulkMigrationConfig config){
        UserSearch search = new UserSearch();
        if (config.getOrganizationId() != null && !config.getOrganizationId().isEmpty()) {
             search.setOrgId(config.getOrganizationId());
        }

        if (config.getLastName() != null && !config.getLastName().isEmpty()) {
            search.setLastName(config.getLastName() + "%");
        }

        if (config.getDeptId() != null && !config.getDeptId().isEmpty()) {
            search.setDeptCd(config.getDeptId());
        }

        if (config.getDivision() != null && !config.getDivision().isEmpty()) {
            search.setDivision(config.getDivision());
        }

        if (config.getAttributeName() != null && !config.getAttributeName().isEmpty()) {
            search.setAttributeName(config.getAttributeName());
            search.setAttributeValue(config.getAttributeValue());
        }

        if (config.getUserStatus() != null ) {
            search.setStatus(config.getUserStatus().toString());
        }

        return search;

    }

    private UserSearch buildSearchByRole(final String roleId) {
        UserSearch search = new UserSearch();

        List<String> roleList = new ArrayList<String>();
        roleList.add(roleId);
        search.setRoleIdList(roleList);

        return search;
    }


    private Role parseRole(String roleStr) {
        String roleId = null;

        StringTokenizer st = new StringTokenizer(roleStr, "*");
        if (st.hasMoreElements()) {
            roleId = st.nextToken();
        }
        Role r = new Role();
        r.setRoleId(roleId);

        return r;
    }


    @Override
    @Transactional
    public Response resynchRole(final String roleId) {

        Response resp = new Response(ResponseStatus.SUCCESS);

        log.debug("Resynch Role: " + roleId );

        // select the user that we need to move
        UserSearch search = buildSearchByRole(roleId);
        if (search.isEmpty()) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }

        List<User> searchResult =  userDozerConverter.convertToDTOList(userManager.search(search), true);

        if (searchResult == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }

        // create role object to show role membership
        Role rl = new Role();
        rl.setRoleId(roleId);

        // all the provisioning service
        for ( User user :  searchResult) {

            log.debug("Updating the user since this role's configuration has changed.: " + user.getUserId() + " " + user.getLastName());

            ProvisionUser pUser = new ProvisionUser(user);

            if (pUser.getMemberOfRoles() == null ) {
                List<Role> rList = new ArrayList<Role>();
                rList.add(rl);
                pUser.setMemberOfRoles(rList);

            }  else {

                pUser.getMemberOfRoles().add(rl);

            }

            provisionService.modifyUser(pUser);

        }

        return resp;
    }

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
     }

	public SynchConfigDAO getSynchConfigDao() {
	    return synchConfigDao;
	}

	public void setSynchConfigDao(SynchConfigDAO synchConfigDao) {
		this.synchConfigDao = synchConfigDao;
	}

	public SynchConfigDataMappingDAO getSynchConfigDataMappingDAO() {
		return synchConfigDataMappingDAO;
	}

	public void setSynchConfigDataMappingDAO(
			SynchConfigDataMappingDAO synchConfigDataMappingDAO) {
		this.synchConfigDataMappingDAO = synchConfigDataMappingDAO;
	}

	public AdapterFactory getAdaptorFactory() {
		return adaptorFactory;
	}

	public void setAdaptorFactory(AdapterFactory adaptorFactory) {
		this.adaptorFactory = adaptorFactory;
	}

    public UserDataService getUserManager() {
        return userManager;
    }

    public void setUserManager(UserDataService userManager) {
        this.userManager = userManager;
    }

    public ProvisionService getProvisionService() {
        return provisionService;
    }

    public void setProvisionService(ProvisionService provisionService) {
        this.provisionService = provisionService;
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
}
