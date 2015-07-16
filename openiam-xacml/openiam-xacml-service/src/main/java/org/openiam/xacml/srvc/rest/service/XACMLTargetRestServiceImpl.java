package org.openiam.xacml.srvc.rest.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.rest.constant.MethodPath;
import org.openiam.rest.constant.OIAMRestStatusCode;
import org.openiam.rest.constant.ServicePath;
import org.openiam.rest.constant.XACMLServicePath;
import org.openiam.rest.request.DTOXACMLTargetRequest;
import org.openiam.rest.request.SearchXACMLTargetRequest;
import org.openiam.rest.response.XACMLTargetRestResponse;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;
import org.openiam.xacml.srvc.dozer.converter.XACMLTargetDozerConverter;
import org.openiam.xacml.srvc.dto.XACMLTargetDTO;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.service.XACMLTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by zaporozhec on 7/14/15.
 */
@RestController
@RequestMapping(value = ServicePath.XACML + XACMLServicePath.TARGET, produces = "application/json")
public class XACMLTargetRestServiceImpl extends XACMLTargetRestService {

    @Autowired
    @Qualifier("xacmlTargetService")
    private XACMLTargetService xacmlTargetService;

    @Autowired
    private XACMLTargetDozerConverter xacmlTargetDozerConverter;

    @Override
    @RequestMapping(value = MethodPath.SEARCH, method = RequestMethod.POST)
    public XACMLTargetRestResponse findBeans(@RequestBody SearchXACMLTargetRequest searchXACMLTargetRequest) throws Exception {
        XACMLTargetRestResponse response = new XACMLTargetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);

        if (searchXACMLTargetRequest == null || searchXACMLTargetRequest.getSearchBean() == null) {
            response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
            response.setErrorText("Search Request is empty");
            return response;
        }

        java.util.List<XACMLTargetDTO> targetDTOList = null;
        java.util.List<XACMLTargetEntity> targetEntityList = xacmlTargetService.findBeans(searchXACMLTargetRequest.getSearchBean(), searchXACMLTargetRequest.getFrom(), searchXACMLTargetRequest.getSize());
        if (CollectionUtils.isNotEmpty(targetEntityList)) {
            targetDTOList = xacmlTargetDozerConverter.convertToDTOList(targetEntityList, searchXACMLTargetRequest.getSearchBean().isDeepCopy());
        } else {
            targetDTOList = new ArrayList<XACMLTargetDTO>();
        }
        response.setStatus(ResponseStatus.SUCCESS);
        response.setObjectList(targetDTOList);
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.ADD, method = RequestMethod.POST)
    public XACMLTargetRestResponse add(@RequestBody DTOXACMLTargetRequest targetRequest) throws Exception {
        XACMLTargetRestResponse response = new XACMLTargetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (targetRequest == null || targetRequest.getObject() == null) {
                response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLTargetEntity entity = xacmlTargetDozerConverter.convertToEntity(targetRequest.getObject(), true);
            entity = xacmlTargetService.add(entity);
            response.setObject(xacmlTargetDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_ADD);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_ADD);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.UPDATE, method = RequestMethod.POST)
    public XACMLTargetRestResponse update(@RequestBody DTOXACMLTargetRequest targetRequest) throws Exception {
        XACMLTargetRestResponse response = new XACMLTargetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (targetRequest == null || targetRequest.getObject() == null) {
                response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLTargetEntity entity = xacmlTargetDozerConverter.convertToEntity(targetRequest.getObject(), true);
            entity = xacmlTargetService.update(entity);
            response.setObject(xacmlTargetDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_MODIFY);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_MODIFY);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    @RequestMapping(value = MethodPath.GET, method = RequestMethod.GET)
    public XACMLTargetRestResponse findById(@PathVariable String id, final @RequestParam(required = false, value = "deepCopy") boolean deepCopy) throws Exception {
        XACMLTargetRestResponse response = new XACMLTargetRestResponse();
        try {
            XACMLTargetEntity targetEntity = xacmlTargetService.findById(id);
            if (targetEntity == null) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setResponseCode(OIAMRestStatusCode.NO_TARGET_WITH_SUCH_ID);
                response.setErrorText("No Target with such Id");
                return response;
            }
            XACMLTargetDTO xacmlTargetDTO = xacmlTargetDozerConverter.convertToDTO(targetEntity, deepCopy);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setObject(xacmlTargetDTO);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_GET);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_TARGET_GET);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.DELETE, method = RequestMethod.GET)
    public XACMLTargetRestResponse delete(@PathVariable String id) throws Exception {
        XACMLTargetRestResponse response = new XACMLTargetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            xacmlTargetService.delete(id);
        } catch (XACMLException xe) {
            response.setErrorText(xe.getReport());
            response.setResponseCode(OIAMRestStatusCode.CAN_NOT_DELETE_ENTITY_NOT_EXISTS);
            return response;
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
            response.setResponseCode(OIAMRestStatusCode.CAN_NOT_DELETE_INTERNAL_ERROR);
            return response;
        }
        response.setStatus(ResponseStatus.SUCCESS);
        return response;
    }
}
