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

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
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

    private static Logger LOG = Logger.getLogger(MetadataWebServiceImpl.class);

    @Override
    public MetadataElement getElementByAttrNameAndTypeId(String attrName, String typeId, final Language language) {
        return metadataService.findElementByAttrNameAndTypeId(attrName, typeId, language);
    }

    @Override
    public String getElementIdByAttrNameAndTypeId(String attrName, String typeId) {
        MetadataElement element = metadataService.findElementByAttrNameAndTypeId(attrName, typeId, null);
        return (element!=null)?element.getId():null;
    }

    @Override
    public MetadataType getByNameGrouping(String name, MetadataTypeGrouping grouping, final Language language) {
        return metadataService.findMetadataTypeByNameAndGrouping(name, grouping, language);
    }

    @Override
    public List<MetadataElement> findElementBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final Language language) {
        return metadataService.findBeans(searchBean, from, size, language);
    }

    @Override
    public MetadataElement getMetadataElementById(String id, Language language) {
        return metadataService.findElementById(id, language);
    }

    @Override
    public MetadataType getMetadataTypeById(String id) {
        return metadataService.findById(id);
    }

    @Override
    public List<MetadataType> findTypeBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language) {
        return metadataService.findBeans(searchBean, from, size, language);
    }

    @Override
    public Response saveMetadataType(final MetadataType dto) {
        final Response response = new Response();
        try {
            if (dto == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            if (StringUtils.isBlank(dto.getName())) {
                throw new BasicDataServiceException(ResponseCode.NO_NAME);
            }
            
            if(MapUtils.isEmpty(dto.getDisplayNameMap())) {
            	throw new BasicDataServiceException(ResponseCode.DISPLAY_NAME_REQUIRED);
            }
            
            metadataService.save(dto);
            response.setResponseValue(dto.getId());
            response.succeed();
        } catch (BasicDataServiceException e) {
        	response.setResponseValue(e.getResponseValue());
        	response.setErrorTokenList(e.getErrorTokenList());
            response.setErrorCode(e.getCode());
            response.fail();
        } catch (Throwable e) {
            LOG.error("Unknown Error", e);
            response.fail();
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

            if (StringUtils.isBlank(dto.getAttributeName())) {
                throw new BasicDataServiceException(ResponseCode.ATTRIBUTE_NAME_MISSING);
            }

            if (dto.getMetadataTypeId() == null) {
                throw new BasicDataServiceException(ResponseCode.METADATA_TYPE_MISSING);
            }

            metadataService.save(dto);
            response.setResponseValue(dto.getId());
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
            List<MetadataElement> list = metadataService.findBeans(searchBean, -1, -1, null);
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
