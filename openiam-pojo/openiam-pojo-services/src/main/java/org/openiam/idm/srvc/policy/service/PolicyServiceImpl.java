package org.openiam.idm.srvc.policy.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyServiceImpl implements PolicyService {
	
	@Autowired
	private PolicyDAO policyDao;
	
	@Autowired
	private PolicyDefParamDAO policyDefParamDao;

	@Override
	@Transactional(readOnly=true)
	public PolicyEntity getPolicy(String policyId) {
		return policyDao.findById(policyId);
	}

	@Override
	@Transactional
	public void save(final PolicyEntity pe) {
		if (StringUtils.isNotBlank(pe.getId())) {
			
			if(CollectionUtils.isNotEmpty(pe.getPolicyAttributes())) {
				for(final PolicyAttributeEntity attribute : pe.getPolicyAttributes()) {
					attribute.setPolicy(pe);
				}
			}
			policyDao.merge(pe);
		} else {
			if(CollectionUtils.isNotEmpty(pe.getPolicyAttributes())) {
				for(final PolicyAttributeEntity attribute : pe.getPolicyAttributes()) {
					attribute.setPolicy(pe);
				}
			}
			
			// creating new Policy
			policyDao.save(pe);
		}
	}
	
    private void mergeAttribute(final PolicyEntity bean, final PolicyEntity dbObject) {

        Set<PolicyAttributeEntity> beanProps = (bean.getPolicyAttributes() != null) ? bean.getPolicyAttributes() : new HashSet<PolicyAttributeEntity>();
        Set<PolicyAttributeEntity> dbProps = (dbObject.getPolicyAttributes() != null) ? new HashSet<PolicyAttributeEntity>(dbObject.getPolicyAttributes()) : new HashSet<PolicyAttributeEntity>();

        /* update */
        Iterator<PolicyAttributeEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final PolicyAttributeEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final PolicyAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                	dbProp.setDefParamId(beanProp.getDefParamId());
                	dbProp.setName(beanProp.getName());
                	dbProp.setOperation(beanProp.getOperation());
                	dbProp.setRequired(beanProp.isRequired());
                	dbProp.setRule(beanProp.getRule());
                	dbProp.setValue1(beanProp.getValue1());
                	dbProp.setValue2(beanProp.getValue2());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<PolicyAttributeEntity> toAdd = new HashSet<>();
        for (final PolicyAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final PolicyAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setPolicy(bean);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setPolicyAttributes(dbProps);
    }

	@Override
	@Transactional(readOnly=true)
	public List<PolicyEntity> findPolicyByName(String policyDefId, String policyName) {
		return policyDao.findPolicyByName(policyDefId, policyName);
	}

	@Override
	@Transactional
	public void delete(final String policyId) {
		final PolicyEntity entity = policyDao.findById(policyId);
		if(entity != null) {
			policyDao.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public int count(PolicySearchBean searchBean) {
		return policyDao.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PolicyEntity> findBeans(PolicySearchBean searchBean, int from,
			int size) {
		return policyDao.getByExample(searchBean, from, size);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<PolicyDefParamEntity> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup) {
		return policyDefParamDao.findPolicyDefParamByGroup(policyDefId, pswdGroup);
	}
}
