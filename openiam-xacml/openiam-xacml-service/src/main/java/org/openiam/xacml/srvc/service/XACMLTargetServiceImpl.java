package org.openiam.xacml.srvc.service;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySearchBean;
import org.openiam.idm.searchbeans.xacml.XACMLTargetSearchBean;
import org.openiam.xacml.srvc.constants.XACMLError;
import org.openiam.xacml.srvc.dao.XACMLPolicyDAO;
import org.openiam.xacml.srvc.dao.XACMLTargetDAO;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;
import org.openiam.xacml.srvc.dozer.converter.XACMLTargetDozerConverter;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLPolicySearchBeanConverter;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLTargetSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Service("xacmlTargetService")
public class XACMLTargetServiceImpl implements XACMLTargetService {

    @Autowired
    private XACMLTargetDAO xacmlTargetDAO;
    @Autowired
    private XACMLTargetSearchBeanConverter xacmlTargetSearchBeanConverter;

    @Override
    public XACMLTargetEntity add(XACMLTargetEntity targetEntity) throws Exception {
        if (targetEntity.getId() == null) {
            targetEntity.setId(xacmlTargetDAO.add(targetEntity).getId());
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_ADD_TARGET, "Policy ID has already existed");
        }
        return targetEntity;
    }

    @Override
    public XACMLTargetEntity update(XACMLTargetEntity targetEntity) throws Exception {
        if (targetEntity.getId() == null) {
            throw new XACMLException(XACMLError.CAN_NOT_UPDATE_TARGET, "Target ID is undefined! Can't update");
        } else {
            xacmlTargetDAO.merge(targetEntity);
        }
        return targetEntity;
    }


    @Override
    public List<XACMLTargetEntity> findAll() throws Exception {
        return xacmlTargetDAO.findAll();
    }

    @Override
    public XACMLTargetEntity findById(String id) throws Exception {
        return xacmlTargetDAO.findById(id);
    }

    @Override
    public void delete(String id) throws Exception {
        XACMLTargetEntity targetEntity = xacmlTargetDAO.findById(id);
        if (targetEntity != null) {
            xacmlTargetDAO.delete(targetEntity);
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_DELETE_TARGET, "Can't find target with such ID");
        }
    }

    @Override
    public List<XACMLTargetEntity> findBeans(XACMLTargetSearchBean policySearchBean, int from, int size) throws Exception {
        XACMLTargetEntity entity = xacmlTargetSearchBeanConverter.convert(policySearchBean);
        return xacmlTargetDAO.getByExample(entity, from, size);
    }

}
