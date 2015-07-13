package org.openiam.xacml.srvc.rest.service;

import org.openiam.base.ws.ResponseStatus;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.dozer.converter.XACMLPolicyDozerConverter;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.rest.constant.MethodPath;
import org.openiam.xacml.srvc.rest.constant.OIAMRestStatusCode;
import org.openiam.xacml.srvc.rest.constant.ServicePath;
import org.openiam.xacml.srvc.rest.constant.XACMLServicePath;
import org.openiam.xacml.srvc.rest.request.DTOXACMLPolicyRequest;
import org.openiam.xacml.srvc.rest.request.SearchXACMLPolicyRequest;
import org.openiam.xacml.srvc.rest.response.XACMLPolicyRestResponse;
import org.openiam.xacml.srvc.service.XACMLPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zaporozhec on 7/14/15.
 */
@RestController
@RequestMapping(value = ServicePath.XACML + XACMLServicePath.POLICY, produces = "application/json")
public class XACMLPolicyRestServiceImpl implements XACMLPolicyRestService {

    @Autowired
    @Qualifier("xacmlPolicyService")
    private XACMLPolicyService xacmlPolicyService;

    @Autowired
    private XACMLPolicyDozerConverter xacmlPolicyDozerConverter;

    @Override
    @RequestMapping(value = MethodPath.SEARCH, method = RequestMethod.POST)
    public XACMLPolicyRestResponse findBeans(@RequestBody SearchXACMLPolicyRequest searchXACMLPolicyRequest) throws Exception {
        return null;
    }

    @Override
    @RequestMapping(value = MethodPath.ADD, method = RequestMethod.POST)
    public XACMLPolicyRestResponse add(@RequestBody DTOXACMLPolicyRequest policyRequest) throws Exception {
        XACMLPolicyRestResponse response = new XACMLPolicyRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (policyRequest == null || policyRequest.getObject() == null) {
                response.setResposeCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLPolicyEntity entity = xacmlPolicyDozerConverter.convertToEntity(policyRequest.getObject(), true);
            entity = xacmlPolicyService.add(entity);
            response.setObject(xacmlPolicyDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResposeCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_ADD);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_ADD);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.UPDATE, method = RequestMethod.POST)
    public XACMLPolicyRestResponse update(@RequestBody DTOXACMLPolicyRequest policyRequest) throws Exception {
        XACMLPolicyRestResponse response = new XACMLPolicyRestResponse();
        response.setStatus(ResponseStatus.FAILURE);
        try {
            if (policyRequest == null || policyRequest.getObject() == null) {
                response.setResposeCode(OIAMRestStatusCode.EMPTY_REQUEST);
                return response;
            }
            XACMLPolicyEntity entity = xacmlPolicyDozerConverter.convertToEntity(policyRequest.getObject(), true);
            entity = xacmlPolicyService.update(entity);
            response.setObject(xacmlPolicyDozerConverter.convertToDTO(entity, true));
            response.setStatus(ResponseStatus.SUCCESS);
            response.setResposeCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_MODIFY);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_MODIFY);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    @RequestMapping(value = MethodPath.GET, method = RequestMethod.GET)
    public XACMLPolicyRestResponse findById(@PathVariable String id) throws Exception {
        XACMLPolicyRestResponse response = new XACMLPolicyRestResponse();
        try {
            XACMLPolicyEntity policyEntity = xacmlPolicyService.findById(id);
            if (policyEntity == null) {
                response.setStatus(ResponseStatus.FAILURE);
                response.setResposeCode(OIAMRestStatusCode.NO_POLICY_WITH_SUCH_ID);
                response.setErrorText("No Policy with such Id");
                return response;
            }
            XACMLPolicyDTO xacmlPolicyDTO = xacmlPolicyDozerConverter.convertToDTO(policyEntity, true);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setObject(xacmlPolicyDTO);
            response.setResposeCode(OIAMRestStatusCode.OK);
        } catch (XACMLException xacmlException) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_GET);
            response.setErrorText(xacmlException.getReport());
        } catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setResposeCode(OIAMRestStatusCode.ERROR_DURING_POLICY_GET);
            response.setErrorText(e.getMessage());
        }

        return response;
    }

    @Override
    @RequestMapping(value = MethodPath.GET, method = RequestMethod.DELETE)
    public XACMLPolicyRestResponse delete(@PathVariable String id) throws Exception {
        return null;
    }
}
