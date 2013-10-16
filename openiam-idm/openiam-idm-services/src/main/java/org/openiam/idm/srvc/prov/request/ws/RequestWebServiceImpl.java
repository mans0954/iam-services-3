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
package org.openiam.idm.srvc.prov.request.ws;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.ProvisionRequestDozerConverter;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation object for the request web service.
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.prov.request.ws.RequestWebService", 
		targetNamespace = "urn:idm.openiam.org/srvc/prov/request/service", 
		serviceName = "RequestWebService")
@Service("provRequestWS")
public class RequestWebServiceImpl implements RequestWebService {

	/*
	@Autowired
	private RequestDataService provRequestService;
	
	@Autowired
	private ProvisionRequestDozerConverter converter;

	@Override
	public Response addRequest(ProvisionRequest request) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		try {
			if(request == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ProvisionRequestEntity entity = converter.convertToEntity(request, true);
			provRequestService.addRequest(entity);
		} catch(BasicDataServiceException e) {
			resp.setErrorCode(e.getCode());
			resp.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		return resp;
	}

	@Override
	public ProvisionRequest getRequest(String requestId) {
		final ProvisionRequestEntity entity = provRequestService.getRequest(requestId);
		return (entity != null) ? converter.convertToDTO(entity, true) : null;
	}

	@Override
	public List<ProvisionRequest> requestByApprover(String approverId,
			String status) {
		final List<ProvisionRequestEntity> entityList = provRequestService.requestByApprover(approverId, status);
		return  (entityList != null) ? converter.convertToDTOList(entityList, false) : null;
	}

	@Override
	public List<ProvisionRequest> search(SearchRequest search) {
		final List<ProvisionRequestEntity> entityList = provRequestService.search(search);
		return (entityList != null) ? converter.convertToDTOList(entityList, false) : null;
	}

	@Override
	public Response updateRequest(ProvisionRequest request) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
		try {
			if(request == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final ProvisionRequestEntity entity = converter.convertToEntity(request, true);
			provRequestService.updateRequest(entity);
		} catch(BasicDataServiceException e) {
			resp.setErrorCode(e.getCode());
			resp.setStatus(ResponseStatus.FAILURE);
		} catch(Throwable e) {
			resp.setStatus(ResponseStatus.FAILURE);
		}
		return resp;
	}
	*/
}
