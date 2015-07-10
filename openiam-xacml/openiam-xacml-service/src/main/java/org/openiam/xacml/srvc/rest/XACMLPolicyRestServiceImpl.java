package org.openiam.xacml.srvc.rest;

import org.openiam.xacml.srvc.constants.XACMLServicePath;
import org.openiam.xacml.srvc.dozer.converter.XACMLPolicyDozerConverter;
import org.openiam.xacml.srvc.dto.XACMLPolicyDTO;
import org.openiam.xacml.srvc.searchbeans.XACMLPolicySearchBean;
import org.openiam.xacml.srvc.service.XACMLPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Path(XACMLServicePath.POLICY)
@Produces("application/json")
public class XACMLPolicyRestServiceImpl implements XACMLPolicyRestService {

    @Autowired
    XACMLPolicyService xACMLPolicyService;
    @Autowired
    XACMLPolicyDozerConverter xacmlPolicyDozerConverter;

    @Override
    public List<XACMLPolicyDTO> findBeans(XACMLPolicySearchBean policySearchBean, int from, int size) {
        return xacmlPolicyDozerConverter.convertToDTOList(xACMLPolicyService.findBeans(policySearchBean, from, size), true);
    }

    @Override
    public List<XACMLPolicyDTO> findBeans(XACMLPolicySearchBean policySearchBean) {
        return this.findBeans(policySearchBean, -1, -1);
    }

    @Override
    public void save(XACMLPolicyDTO policyDTO) {
        xACMLPolicyService.save(xacmlPolicyDozerConverter.convertToEntity(policyDTO, false));
    }

    @Override
    public List<XACMLPolicyDTO> findAll() {
        return xacmlPolicyDozerConverter.convertToDTOList(xACMLPolicyService.findAll(), true);
    }

    @Override
    @Transactional(readOnly = true)
    @GET
    @Path(XACMLServicePath.POLICY_SUFFIX + "{id}/")
    public XACMLPolicyDTO findById(@PathParam("id") String id) {
        return xacmlPolicyDozerConverter.convertToDTO(xACMLPolicyService.findById(id), true);
    }

    @Override
    public void delete(String id) {
        xACMLPolicyService.delete(id);
    }
}
