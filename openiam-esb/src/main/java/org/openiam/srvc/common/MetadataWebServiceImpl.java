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
package org.openiam.srvc.common;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.MetadataElementResponse;
import org.openiam.base.response.data.MetadataTypeResponse;
import org.openiam.base.response.list.MetadataElementListResponse;
import org.openiam.base.response.list.MetadataTypeListResponse;
import org.openiam.base.ws.Response;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.mq.constants.api.common.MetadataAPI;
import org.openiam.mq.constants.queue.common.MetadataQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for the MetadataWebServiceImpl
 *
 * @author suneet
 * @version 2.1
 */
@WebService(endpointInterface = "org.openiam.srvc.common.MetadataWebService", targetNamespace = "urn:idm.openiam.org/srvc/meta/service", portName = "MetadataWebServicePort", serviceName = "MetadataWebService")
@Service("metadataWS")
public class MetadataWebServiceImpl extends AbstractApiService implements MetadataWebService {

    private static final Log LOG = LogFactory.getLog(MetadataWebServiceImpl.class);

    @Autowired
    public MetadataWebServiceImpl(MetadataQueue queue) {
        super(queue);
    }

    @Override
    public List<MetadataElement> findElementBeans(final MetadataElementSearchBean searchBean, final int from, final int size) {
        return this.getValueList(MetadataAPI.FindElementBeans, new BaseSearchServiceRequest<>(searchBean, from, size),MetadataElementListResponse.class);
    }

    @Override
    public MetadataElement getMetadataElement(String id) {
        IdServiceRequest request = new IdServiceRequest(id);
        return this.getValue(MetadataAPI.GetMetadataElement, request, MetadataElementResponse.class);
    }
    @Override
    public int countElementBeans(final MetadataElementSearchBean searchBean) {
        return this.getIntValue(MetadataAPI.CountElementBeans, new BaseSearchServiceRequest<>(searchBean));
    }

    @Override
    public MetadataType getMetadataType(String id) {
        IdServiceRequest request = new IdServiceRequest(id);
        return this.getValue(MetadataAPI.GetMetadataType, request, MetadataTypeResponse.class);
    }
    @Override
    public List<MetadataType> findTypeBeans(final MetadataTypeSearchBean searchBean, final int from, final int size) {
        return this.getValueList(MetadataAPI.FindTypeBeans, new BaseSearchServiceRequest<>(searchBean, from, size),MetadataTypeListResponse.class);
    }
    @Override
    public int countTypeBeans(final MetadataTypeSearchBean searchBean) {
        return this.getIntValue(MetadataAPI.CountTypeBeans, new BaseSearchServiceRequest<>(searchBean));
    }

    @Override
    public Response saveMetadataType(final MetadataType dto) {
        return this.manageCrudApiRequest(MetadataAPI.SaveMetadataType, dto);
    }

    @Override
    public Response saveMetadataElement(final MetadataElement dto) {
        return this.manageCrudApiRequest(MetadataAPI.SaveMetadataElement, dto);
    }

    @Override
    public Response deleteMetadataType(final String id) {
        MetadataType dto = new MetadataType();
        dto.setId(id);
        return this.manageCrudApiRequest(MetadataAPI.DeleteMetadataType, dto);
    }

    @Override
    public Response deleteMetadataElement(final String id) {
        MetadataElement dto = new MetadataElement();
        dto.setId(id);
        return this.manageCrudApiRequest(MetadataAPI.DeleteMetadataElement, dto);
    }
}
