package org.openiam.xacml.srvc.service;

import org.openiam.idm.searchbeans.xacml.XACMLPolicySearchBean;
import org.openiam.idm.searchbeans.xacml.XACMLPolicySetSearchBean;
import org.openiam.xacml.srvc.constants.XACMLError;
import org.openiam.xacml.srvc.dao.XACMLPolicyDAO;
import org.openiam.xacml.srvc.dao.XACMLPolicySetDAO;
import org.openiam.xacml.srvc.dao.XACMLTargetDAO;
import org.openiam.xacml.srvc.domain.XACMLPolicySetEntity;
import org.openiam.xacml.srvc.exception.XACMLException;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLPolicySearchBeanConverter;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLPolicySetSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Service("xacmlPolicySetService")
public class XACMLPolicySetServiceImpl implements XACMLPolicySetService {

    @Autowired
    private XACMLPolicySetDAO xacmlPolicySetDao;
    @Autowired
    private XACMLPolicySetSearchBeanConverter xacmlPolicySetSearchBeanConverter;

    @Override
    public XACMLPolicySetEntity add(XACMLPolicySetEntity policyEntity) throws Exception {
        if (policyEntity.getId() == null) {
            xacmlPolicySetDao.save(policyEntity);
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_ADD_POLICY, "Policy ID has already existed");
        }
        return policyEntity;
    }

    @Override
    public XACMLPolicySetEntity update(XACMLPolicySetEntity policyEntity) throws Exception {
        if (policyEntity.getId() == null) {
            throw new XACMLException(XACMLError.CAN_NOT_UPDATE_POLICY, "Policy ID is undefined! Can't update");
        } else {
            xacmlPolicySetDao.update(policyEntity);
        }
        return policyEntity;
    }


    @Override
    public List<XACMLPolicySetEntity> findAll() throws Exception {
        return xacmlPolicySetDao.findAll();
    }

    @Override
    public XACMLPolicySetEntity findById(String id) throws Exception {
        return xacmlPolicySetDao.findById(id);
    }

    @Override
    public void delete(String id) throws Exception {
        XACMLPolicySetEntity policyEntity = xacmlPolicySetDao.findById(id);
        if (policyEntity != null) {
            xacmlPolicySetDao.delete(policyEntity);
        } else {
            throw new XACMLException(XACMLError.CAN_NOT_DELETE_POLICY, "Can't find policy with such ID");
        }
    }

    @Override
    public List<XACMLPolicySetEntity> findBeans(XACMLPolicySetSearchBean policySearchBean, int from, int size) throws Exception {
        XACMLPolicySetEntity entity = xacmlPolicySetSearchBeanConverter.convert(policySearchBean);
        return xacmlPolicySetDao.getByExample(entity, from, size);
    }

}
