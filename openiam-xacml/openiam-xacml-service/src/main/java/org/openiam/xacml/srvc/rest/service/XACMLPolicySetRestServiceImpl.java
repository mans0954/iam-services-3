package org.openiam.xacml.srvc.rest.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.rest.constant.MethodPath;
import org.openiam.rest.constant.OIAMRestStatusCode;
import org.openiam.rest.constant.ServicePath;
import org.openiam.rest.constant.XACMLServicePath;
import org.openiam.rest.request.DTOXACMLPolicySetRequest;
import org.openiam.rest.request.SearchXACMLPolicySetRequest;
import org.openiam.rest.response.XACMLPolicySetRestResponse;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;
import org.openiam.xacml.srvc.dozer.converter.XACMLPolicySetDozerConverter;
import org.openiam.xacml.srvc.dto.XACMLPolicySetDTO;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.service.XACMLPolicySetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by zaporozhec on 7/14/15.
 */
@RestController
@RequestMapping(value = ServicePath.XACML + XACMLServicePath.POLICY_SET, produces = "application/json")
public class XACMLPolicySetRestServiceImpl extends XACMLPolicySetRestService {

    @Autowired
    @Qualifier("xacmlPolicySetService")
    private XACMLPolicySetService xacmlPolicySetService;

    @Autowired
    private XACMLPolicySetDozerConverter xacmlPolicySetDozerConverter;

    @Override
    @RequestMapping(value = MethodPath.SEARCH, method = RequestMethod.POST)
    public XACMLPolicySetRestResponse findBeans(@RequestBody SearchXACMLPolicySetRequest searchXACMLPolicyRequest) throws Exception {
        XACMLPolicySetRestResponse response = new XACMLPolicySetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);

        if (searchXACMLPolicyRequest == null || searchXACMLPolicyRequest.getSearchBean() == null) {
            response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
            response.setErrorText("Search Request is empty");
            return response;
        }

        java.util.List<XACMLPolicySetDTO> policyDTOList = null;
        java.util.List<XACMLPolicySetEntity> policyEntityList = xacmlPolicySetService.findBeans(searchXACMLPolicyRequest.getSearchBean(), searchXACMLPolicyRequest.getFrom(), searchXACMLPolicyRequest.getSize());
        if (CollectionUtils.isNotEmpty(policyEntityList)) {
            policyDTOList = xacmlPolicySetDozerConverter.convertToDTOList(policyEntityList, searchXACMLPolicyRequest.getSearchBean().isDeepCopy());
        } else {
            policyDTOList = new ArrayList<XACMLPolicySetDTO>();
        }
        response.setStatus(ResponseStatus.SUCCESS);
        response.setObjectList(policyDTOList);
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.ADD, method = RequestMethod.POST)
    public XACMLPolicySetRestResponse add(@RequestBody DTOXACMLPolicySetRequest policyRequest) throws Exception {
        XACMLPolicySetRestResponse response = new XACMLPolicySetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (policyRequest == null || policyRequest.getObject() == null) {
                response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLPolicySetEntity entity = xacmlPolicySetDozerConverter.convertToEntity(policyRequest.getObject(), true);
            entity = xacmlPolicySetService.add(entity);
            response.setObject(xacmlPolicySetDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_ADD);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_ADD);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.UPDATE, method = RequestMethod.POST)
    public XACMLPolicySetRestResponse update(@RequestBody DTOXACMLPolicySetRequest policyRequest) throws Exception {
        XACMLPolicySetRestResponse response = new XACMLPolicySetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (policyRequest == null || policyRequest.getObject() == null) {
                response.setResponseCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLPolicySetEntity entity = xacmlPolicySetDozerConverter.convertToEntity(policyRequest.getObject(), true);
            entity = xacmlPolicySetService.update(entity);
            response.setObject(xacmlPolicySetDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_MODIFY);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_MODIFY);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    @RequestMapping(value = MethodPath.GET, method = RequestMethod.GET)
    public XACMLPolicySetRestResponse findById(@PathVariable String id, final @RequestParam(required = false, value = "deepCopy") boolean deepCopy) throws Exception {
        XACMLPolicySetRestResponse response = new XACMLPolicySetRestResponse();
        try {
            XACMLPolicySetEntity policyEntity = xacmlPolicySetService.findById(id);
            if (policyEntity == null) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setResponseCode(OIAMRestStatusCode.NO_POLICY_SET_WITH_SUCH_ID);
                response.setErrorText("No Policy with such Id");
                return response;
            }
            XACMLPolicySetDTO xacmlPolicyDTO = xacmlPolicySetDozerConverter.convertToDTO(policyEntity, deepCopy);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setObject(xacmlPolicyDTO);
            response.setResponseCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_GET);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResponseCode(OIAMRestStatusCode.ERROR_DURING_POLICY_SET_GET);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.DELETE, method = RequestMethod.GET)
    public XACMLPolicySetRestResponse delete(@PathVariable String id) throws Exception {
        XACMLPolicySetRestResponse response = new XACMLPolicySetRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            xacmlPolicySetService.delete(id);
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
