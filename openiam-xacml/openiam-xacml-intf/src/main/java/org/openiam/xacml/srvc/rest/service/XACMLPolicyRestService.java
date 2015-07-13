package org.openiam.xacml.srvc.rest.service;

import org.openiam.xacml.srvc.rest.request.DTOXACMLPolicyRequest;
import org.openiam.xacml.srvc.rest.request.SearchXACMLPolicyRequest;
import org.openiam.xacml.srvc.rest.response.XACMLPolicyRestResponse;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface XACMLPolicyRestService extends AbstractBeansRestService<SearchXACMLPolicyRequest, DTOXACMLPolicyRequest, XACMLPolicyRestResponse> {


}
