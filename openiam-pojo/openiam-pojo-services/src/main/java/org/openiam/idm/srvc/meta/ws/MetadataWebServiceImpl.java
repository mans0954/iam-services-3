/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
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
package org.openiam.idm.srvc.meta.ws;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for the MetadataWebServiceImpl
 *
 * @author suneet
 * @version 2.1
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.meta.ws.MetadataWebService", targetNamespace = "urn:idm.openiam.org/srvc/meta/service", portName = "MetadataWebServicePort", serviceName = "MetadataWebService")
@Service("metadataWS")
public class MetadataWebServiceImpl implements MetadataWebService {
    @Autowired
    private MetadataService metadataService;

    @Autowired
    private MetaDataTypeDozerConverter metaDataTypeDozerConverter;

    @Autowired
    private MetaDataElementDozerConverter metaDataElementDozerConverter;

    private static Logger LOG = Logger.getLogger(MetadataWebServiceImpl.class);

    @Override
    public List<MetadataElement> findElementBeans(final MetadataElementSearchBean searchBean, final int from, final int size) {
        final List<MetadataElementEntity> entityList = metadataService.findBeans(searchBean, from, size, null);
        return (entityList != null) ? metaDataElementDozerConverter.convertToDTOList(entityList,
                searchBean.isDeepCopy()) : null;
    }

    @Override
    @LocalizedServiceGet
    public List<MetadataType> findTypeBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language) {
        final List<MetadataTypeEntity> entityList = metadataService.findBeans(searchBean, from, size);
        return (entityList != null) ? metaDataTypeDozerConverter.convertToDTOList(entityList, true) : null;
    }

    @Override
    public Response saveMetadataType(final MetadataType dto) {
        final Response response = new Response();
        try {
            if (dto == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            if (StringUtils.isBlank(dto.getDescription())) {
                throw new BasicDataServiceException(ResponseCode.NO_NAME);
            }
            
            if(MapUtils.isEmpty(dto.getDisplayNameMap())) {
            	throw new BasicDataServiceException(ResponseCode.DISPLAY_NAME_REQUIRED);
            }
            
            final MetadataTypeEntity entity = metaDataTypeDozerConverter.convertToEntity(dto, true);
            metadataService.save(entity);
            response.setResponseValue(entity.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setResponseValue(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            LOG.error("Unknown Error", e);
            response.setResponseValue(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public Response saveMetadataEntity(final MetadataElement dto) {
        final Response response = new Response();
        try {
            if (dto == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            final MetadataElementEntity entity = metaDataElementDozerConverter.convertToEntity(dto, true);
            if (StringUtils.isBlank(entity.getAttributeName())) {
                throw new BasicDataServiceException(ResponseCode.ATTRIBUTE_NAME_MISSING);
            }

            if (entity.getMetadataType() == null) {
                throw new BasicDataServiceException(ResponseCode.METADATA_TYPE_MISSING);
            }

            metadataService.save(entity);
            response.setResponseValue(entity.getId());
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setResponseValue(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            LOG.error("Unknown Error", e);
            response.setResponseValue(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public Response deleteMetadataType(final String id) {
        final Response response = new Response();
        try {
            if (StringUtils.isBlank(id)) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            MetadataElementSearchBean searchBean = new MetadataElementSearchBean();
            Set<String> ids = new HashSet<String>();
            ids.add(id);
            searchBean.setTypeIdSet(ids);
            List<MetadataElementEntity> list = metadataService.findBeans(searchBean, -1, -1, null);
            if (!CollectionUtils.isEmpty(list))
                throw new BasicDataServiceException(ResponseCode.METATYPE_LINKED_WITH_METAELEMENT);
            metadataService.deleteMetdataType(id);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setResponseValue(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            LOG.error("Unknown Error", e);
            response.setResponseValue(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public Response deleteMetadataElement(final String id) {
        final Response response = new Response();
        try {
            if (StringUtils.isBlank(id)) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            metadataService.deleteMetdataElement(id);
            response.setStatus(ResponseStatus.SUCCESS);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setResponseValue(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            LOG.error("Unknown Error", e);
            response.setResponseValue(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    public int countElementBeans(final MetadataElementSearchBean searchBean) {
        return metadataService.count(searchBean);
    }

    @Override
    public int countTypeBeans(final MetadataTypeSearchBean searchBean) {
        return metadataService.count(searchBean);
    }
}
