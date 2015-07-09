package org.openiam.xacml.srvc.service;

import org.openiam.xacml.srvc.dao.XACMLPolicyDAO;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.openiam.xacml.srvc.searchbeans.XACMLPolicySearchBean;
import org.openiam.xacml.srvc.searchbeans.converter.XACMLPolicySearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Service
public class XACMLPolicyServiceImpl implements XACMLPolicyService {
    @Autowired
    private XACMLPolicyDAO xacmlPolicyDao;
    @Autowired
    private XACMLPolicySearchBeanConverter xacmlPolicySearchBeanConverter;

    @Override
    public void save(XACMLPolicyEntity policyEntity) {
        if (policyEntity.getId() == null) {
            xacmlPolicyDao.save(policyEntity);
        } else {
            xacmlPolicyDao.merge(policyEntity);
        }
    }

    @Override
    public List<XACMLPolicyEntity> findAll() {
        return xacmlPolicyDao.findAll();
    }

    @Override
    public XACMLPolicyEntity findById(String id) {
        return xacmlPolicyDao.findById(id);
    }

    @Override
    public void delete(String id) {
        XACMLPolicyEntity policyEntity = xacmlPolicyDao.findById(id);
        if (policyEntity != null)
            xacmlPolicyDao.delete(policyEntity);
    }

    public List<XACMLPolicyEntity> findBeans(XACMLPolicySearchBean policySearchBean, int from, int size) {
        XACMLPolicyEntity entity = xacmlPolicySearchBeanConverter.convert(policySearchBean);
        return xacmlPolicyDao.getByExample(entity, from, size);
    }

    public List<XACMLPolicyEntity> findBeans(XACMLPolicySearchBean policySearchBean) {
        XACMLPolicyEntity entity = xacmlPolicySearchBeanConverter.convert(policySearchBean);
        return xacmlPolicyDao.getByExample(entity, -1, -1);
    }

}
