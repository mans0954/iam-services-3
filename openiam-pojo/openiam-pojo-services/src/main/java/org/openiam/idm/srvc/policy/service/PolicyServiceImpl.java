package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.domain.PolicyObjectAssocEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyServiceImpl implements PolicyService {
	
	@Autowired
	private PolicyDAO policyDao;

	@Autowired
	PolicyObjectAssocDAO policyObjectAssocDAO;

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
		if (StringUtils.isNotBlank(pe.getPolicyId())) {
			final PolicyEntity poObject = policyDao.findById(pe.getPolicyId());
			
			if(CollectionUtils.isNotEmpty(pe.getPolicyAttributes())) {
				for(final PolicyAttributeEntity attribute : pe.getPolicyAttributes()) {
					attribute.setPolicyId(poObject.getPolicyId());
				}
			}

			// TODO: extend this merge
			poObject.setCreateDate(pe.getCreateDate());
			poObject.setCreatedBy(pe.getCreatedBy());
			poObject.setDescription(pe.getDescription());
			poObject.setPolicyId(pe.getPolicyId());
			poObject.setPolicyDefId(pe.getPolicyDefId());
			poObject.setName(pe.getName());
			poObject.setLastUpdate(pe.getLastUpdate());
			poObject.setLastUpdatedBy(pe.getLastUpdatedBy());
			poObject.setRule(pe.getRule());
			poObject.setRuleSrcUrl(pe.getRuleSrcUrl());
			poObject.setStatus(pe.getStatus());
			poObject.setPolicyAttributes(pe.getPolicyAttributes());
			// Updating Policy.
			policyDao.update(poObject);
		} else {
			// creating new Policy
			policyDao.save(pe);
		}
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
			List<PolicyObjectAssocEntity> assocList = policyObjectAssocDAO.findByPolicy(policyId);
            if (CollectionUtils.isNotEmpty(assocList)) {
                for (PolicyObjectAssocEntity assoc : assocList) {
                    policyObjectAssocDAO.delete(assoc);
                }
            }

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
