package org.openiam.xacml.srvc.service;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySearchBean;
import org.openiam.xacml.srvc.constants.XACMLError;
import org.openiam.xacml.srvc.dao.XACMLPolicyDAO;
import org.openiam.xacml.srvc.dao.XACMLTargetDAO;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLPolicySearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Service("xacmlPolicyService")
public class XACMLPolicyServiceImpl implements XACMLPolicyService {

    @Autowired
    private XACMLPolicyDAO xacmlPolicyDao;
    @Autowired
    private XACMLPolicySearchBeanConverter xacmlPolicySearchBeanConverter;

    @Override
    public XACMLPolicyEntity add(XACMLPolicyEntity policyEntity) throws Exception {
        if (policyEntity.getId() == null) {
            xacmlPolicyDao.save(policyEntity);
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_ADD_POLICY, "Policy ID has already existed");
        }
        return policyEntity;
    }

    @Override
    public XACMLPolicyEntity update(XACMLPolicyEntity policyEntity) throws Exception {
        if (policyEntity.getId() == null) {
            throw new XACMLException(XACMLError.CAN_NOT_UPDATE_POLICY, "Policy ID is undefined! Can't update");
        } else {
            xacmlPolicyDao.update(policyEntity);
        }
        return policyEntity;
    }


    @Override
    public List<XACMLPolicyEntity> findAll() throws Exception {
        return xacmlPolicyDao.findAll();
    }

    @Override
    public XACMLPolicyEntity findById(String id) throws Exception {
        return xacmlPolicyDao.findById(id);
    }

    @Override
    public void delete(String id) throws Exception {
        XACMLPolicyEntity policyEntity = xacmlPolicyDao.findById(id);
        if (policyEntity != null) {
            xacmlPolicyDao.delete(policyEntity);
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_DELETE_POLICY, "Can't find policy with such ID");
        }
    }

    @Override
    public List<XACMLPolicyEntity> findBeans(XACMLPolicySearchBean policySearchBean, int from, int size) throws Exception {
        XACMLPolicyEntity entity = xacmlPolicySearchBeanConverter.convert(policySearchBean);
        return xacmlPolicyDao.getByExample(entity, from, size);
    }

}
