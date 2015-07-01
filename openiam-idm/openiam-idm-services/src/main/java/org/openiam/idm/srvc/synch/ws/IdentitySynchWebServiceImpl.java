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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.SynchConfigDozerConverter;
import org.openiam.dozer.converter.SynchReviewDozerConverter;
import org.openiam.idm.parser.csv.CSVHelper;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapDataTypeOptions;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.searchbeans.converter.SynchConfigSearchBeanConverter;
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.idm.srvc.synch.service.SynchReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
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

    @Value("${iam.files.location}")
    private String absolutePath;

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.sync.ws.IdentitySynchWebService#getAllConfig()
     */
    public SynchConfigListResponse getAllConfig() {
        SynchConfigListResponse resp = new SynchConfigListResponse(ResponseStatus.SUCCESS);
        List<SynchConfigEntity> configList = synchService.getAllConfig();
        if (configList == null || configList.isEmpty()) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
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
        if (config == null || config.getId() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
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
        if (config == null || config.getId() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setConfig(synchConfigDozerConverter.convertToDTO(config, false));
        }
        return resp;
    }

    /* (non-Javadoc)
     * @see org.openiam.idm.srvc.synch.ws.IdentitySynchWebService#removeConfig(java.lang.String)
     */
    public Response removeConfig(String configId) {
        Response resp = new Response(ResponseStatus.SUCCESS);
        synchService.removeConfig(configId);
        return resp;
    }

    public SynchConfigResponse mergeConfig(SynchConfig synchConfig) {
        SynchConfigResponse resp = new SynchConfigResponse(ResponseStatus.SUCCESS);
        SynchConfigEntity config = synchService.mergeConfig(synchConfigDozerConverter.convertToEntity(synchConfig, false));
        if (config == null || config.getId() == null) {
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
            resp.setConfig(synchConfigDozerConverter.convertToDTO(config, false));
        }
        return resp;
    }

    @Override
    public ImportSyncResponse importAttrMapFromCSV(String syncId) {
        final StringBuilder SYNC_DIR = new StringBuilder(absolutePath);
        SYNC_DIR.append(absolutePath.endsWith(File.separator) ? "" : File.separator);
        SYNC_DIR.append("sync");

        ImportSyncResponse response = new ImportSyncResponse();
        response.setStatus(ResponseStatus.FAILURE);
        if (StringUtils.isBlank(syncId)) {
            response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_SYNC_ID_EMPTY);
            return response;
        }

        SynchConfigResponse synchConfigResponse = this.findById(syncId);
        if (synchConfigResponse == null || ResponseStatus.FAILURE.equals(synchConfigResponse.getStatus())) {
            response.setErrorCode(synchConfigResponse.getErrorCode());
            return response;
        }

        SynchConfig config = synchConfigResponse.getConfig();
        if (config == null) {
            response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_SYNC_ID_WRONG);
            return response;
        }

        if (StringUtils.isBlank(config.getFileName())) {
            response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_FILE_PATH_EMPTY);
            return response;
        }

        SYNC_DIR.append(config.getFileName().startsWith(File.separator) ? "" : File.separator);
        SYNC_DIR.append(config.getFileName());
        CSVHelper parser = null;
        String[][] allValues = null;
        try {
            parser = new CSVHelper(new FileInputStream(SYNC_DIR.toString()), "UTF-8", CSVStrategy.EXCEL_STRATEGY);
            allValues = parser.getAllValues();
            if (allValues == null && allValues[0].length > 0) {
                response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_FILE_EMPTY);
                return response;
            }
        } catch (Exception e) {
            response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_PARSE_EXCEPTION);
            response.setErrorText(e.getMessage());
            return response;
        }
        String[] header = allValues[0];
        List<AttributeMap> attributeMapListFromDB = getSynchConfigAttributeMaps(syncId);
        List<AttributeMap> attributeMapList = new ArrayList<AttributeMap>();
        boolean exist;
        for (String head : header) {
            exist = false;
            if (StringUtils.isNotBlank(head.trim())) {
                for (AttributeMap attributeMap : attributeMapListFromDB) {
                    if (attributeMap.getName().equalsIgnoreCase(head.trim())) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    AttributeMap attributeMap = new AttributeMap();
                    attributeMap.setName(head.trim());
                    attributeMap.setDataType(PolicyMapDataTypeOptions.STRING);
                    attributeMap.setMapForObjectType(config.getProcessRule());
                    attributeMap.setSynchConfigId(syncId);
                    attributeMapList.add(attributeMap);
                }
            }
        }
        if (CollectionUtils.isEmpty(attributeMapList)){
            response.setErrorCode(ResponseCode.IMPORT_ATTR_MAP_NOTHING_IMPORT);
            return response;
        }
        response.setAttributeMap(attributeMapList);
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
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
    public Integer getSynchConfigCount(@WebParam(name = "searchBean", targetNamespace = "") SynchConfigSearchBean searchBean) {
        SynchConfigEntity entity = synchConfigSearchBeanConverter.convert(searchBean);
        return synchService.getSynchConfigCountByExample(entity);
    }

    @Override
    public List<SynchConfig> getSynchConfigs(@WebParam(name = "searchBean", targetNamespace = "") SynchConfigSearchBean searchBean, @WebParam(name = "size", targetNamespace = "") Integer size, @WebParam(name = "from", targetNamespace = "") Integer from) {
        List<SynchConfig> synchConfigDtos = new LinkedList<SynchConfig>();
        SynchConfigEntity entity = synchConfigSearchBeanConverter.convert(searchBean);
        List<SynchConfigEntity> entities = synchService.getSynchConfigsByExample(entity, size, from);
        if (entities != null) {
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
