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
package org.openiam.idm.srvc.synch.ws;

import java.util.LinkedList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.SynchConfigDozerConverter;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.msg.ws.SysMessageResponse;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.searchbeans.converter.SynchConfigSearchBeanConverter;
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.idm.srvc.synch.service.SynchReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.synch.ws.IdentitySynchWebService", 
		targetNamespace = "http://www.openiam.org/service/synch", 
		portName = "IdentitySynchWebServicePort", 
		serviceName = "IdentitySynchWebService")
@Component("synchServiceWS")
public class IdentitySynchWebServiceImpl implements IdentitySynchWebService {

	protected static final Log log = LogFactory.getLog(IdentitySynchWebServiceImpl.class);
    @Autowired
    protected IdentitySynchService synchService;
    @Autowired
    private SynchConfigDozerConverter synchConfigDozerConverter;
    @Autowired
    protected SynchReviewService synchReviewService;
    @Autowired
    private SynchConfigSearchBeanConverter synchConfigSearchBeanConverter;
    @Autowired
    private AttributeMapDozerConverter attributeMapDozerConverter;

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.sync.ws.IdentitySynchWebService#getAllConfig()
	 */
	public SynchConfigListResponse getAllConfig() {
		SynchConfigListResponse resp = new SynchConfigListResponse(ResponseStatus.SUCCESS);
		List<SynchConfigEntity> configList = synchService.getAllConfig();
		if (configList == null || configList.isEmpty()) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setConfigList(synchConfigDozerConverter.convertToDTOList(configList, false));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.synch.ws.IdentitySynchWebService#addConfig(org.openiam.idm.srvc.synch.dto.SynchConfig)
	 */
	public SynchConfigResponse addConfig(SynchConfig synchConfig) {
		SynchConfigResponse resp = new SynchConfigResponse(ResponseStatus.SUCCESS);
		SynchConfigEntity config = synchService.addConfig(synchConfigDozerConverter.convertToEntity(synchConfig, false));
		if (config == null || config.getSynchConfigId()==null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setConfig(synchConfigDozerConverter.convertToDTO(config, false));
		}
		return resp;
	}

    public Response testConnection(@WebParam(name = "synchConfig", targetNamespace = "") SynchConfig config) {
        return synchService.testConnection(synchConfigDozerConverter.convertToEntity(config, false));
    }

    @Override
    public Response bulkUserMigration(BulkMigrationConfig config) {
        return synchService.bulkUserMigration(config);
    }

    @Override
    public Response resynchRole(final String roleId) {
       return synchService.resynchRole(roleId);
    }

    /* (non-Javadoc)
    * @see org.openiam.idm.srvc.synch.ws.IdentitySynchWebService#findById(java.lang.String)
    */
	public SynchConfigResponse findById(String id) {
		SynchConfigResponse resp = new SynchConfigResponse(ResponseStatus.SUCCESS);
		SynchConfigEntity config = synchService.findById(id);
		if (config == null || config.getSynchConfigId()==null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setConfig(synchConfigDozerConverter.convertToDTO(config, false));
		}
		return resp;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.synch.ws.IdentitySynchWebService#removeConfig(java.lang.String)
	 */
	public Response removeConfig(String configId) {
		SysMessageResponse resp = new SysMessageResponse(ResponseStatus.SUCCESS);
		synchService.removeConfig(configId);
		return resp;
	}

	public SynchConfigResponse mergeConfig(SynchConfig synchConfig) {
		SynchConfigResponse resp = new SynchConfigResponse(ResponseStatus.SUCCESS);
		SynchConfigEntity config = synchService.mergeConfig(synchConfigDozerConverter.convertToEntity(synchConfig, false));
		if (config == null || config.getSynchConfigId()==null) {
			resp.setStatus(ResponseStatus.FAILURE);
		}else {
			resp.setConfig(synchConfigDozerConverter.convertToDTO(config, false));
		}
		return resp;
	}

    @Override
	public SyncResponse startSynchronization(SynchConfig config) {
		return synchService.startSynchronization(synchConfigDozerConverter.convertToEntity(config, false));
	}

    @Override
    public SynchReviewResponse executeSynchReview(SynchReviewRequest synchReviewRequest) {
        return synchReviewService.executeSynchReview(synchReviewRequest);
    }

    @Override
    public Integer getSynchConfigCount(@WebParam(name = "searchBean", targetNamespace = "")SynchConfigSearchBean searchBean) {
        SynchConfigEntity entity = synchConfigSearchBeanConverter.convert(searchBean);
        return synchService.getSynchConfigCountByExample(entity);
    }

    @Override
    public List<SynchConfig> getSynchConfigs(@WebParam(name = "searchBean", targetNamespace = "") SynchConfigSearchBean searchBean, @WebParam(name = "size", targetNamespace = "") Integer size, @WebParam(name = "from", targetNamespace = "") Integer from) {
        List<SynchConfig> synchConfigDtos = new LinkedList<SynchConfig>();
        SynchConfigEntity entity = synchConfigSearchBeanConverter.convert(searchBean);
        List<SynchConfigEntity> entities = synchService.getSynchConfigsByExample(entity, size, from);
        if(entities != null) {
            synchConfigDtos = synchConfigDozerConverter.convertToDTOList(entities, false);
        }
        return synchConfigDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMap> getSynchConfigAttributeMaps(String synchConfigId) {
        if (synchConfigId == null) {
            throw new IllegalArgumentException("synchConfigId is null");
        }
        List<AttributeMapEntity> ameList = synchService.getSynchConfigAttributeMaps(synchConfigId);
        return (ameList == null) ? null : attributeMapDozerConverter.convertToDTOList(ameList, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMap> findSynchConfigAttributeMaps(AttributeMapSearchBean searchBean) {
        if (searchBean == null) {
            throw new IllegalArgumentException("searchBean is null");
        }
        List<AttributeMapEntity> ameList = synchService.getSynchConfigAttributeMaps(searchBean);
        return (ameList == null) ? null : attributeMapDozerConverter.convertToDTOList(ameList, true);
    }
}
