package org.openiam.idm.srvc.policy.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
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
	
	@Autowired
	private PolicyDefDAO policyDefDAO;

	@Override
	@Transactional(readOnly=true)
	public PolicyEntity getPolicy(String policyId) {
		return policyDao.findById(policyId);
	}

	@Override
	@Transactional
	public void save(final PolicyEntity pe) {
		if(CollectionUtils.isNotEmpty(pe.getPolicyAttributes())) {
			for(final PolicyAttributeEntity attribute : pe.getPolicyAttributes()) {
				attribute.setPolicy(pe);
				if(attribute.getDefParam() != null && StringUtils.isNotBlank(attribute.getDefParam().getId())) {
					attribute.setDefParam(policyDefParamDao.findById(attribute.getDefParam().getId()));
				} else {
					attribute.setDefParam(null);
				}
			}
		}
		
		if(pe.getPolicyDef() != null && pe.getPolicyDef().getId() != null) {
			pe.setPolicyDef(policyDefDAO.findById(pe.getPolicyDef().getId()));
		} else {
			pe.setPolicyDef(null);
		}
		
		if (StringUtils.isNotBlank(pe.getId())) {
			final PolicyEntity dbEntity = policyDao.findById(pe.getId());
			pe.setPasswordPolicyProviders(dbEntity.getPasswordPolicyProviders());
			pe.setAuthenticationPolicyProviders(dbEntity.getAuthenticationPolicyProviders());
			policyDao.merge(pe);
		} else {
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
	public void delete(final String policyId) throws BasicDataServiceException {
		final PolicyEntity entity = policyDao.findById(policyId);
		if(entity != null) {
			if(CollectionUtils.isNotEmpty(entity.getPasswordPolicyProviders())) {
				throw new BasicDataServiceException(ResponseCode.POLICY_HAS_AUTH_PROVIDERS);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getAuthenticationPolicyProviders())) {
				throw new BasicDataServiceException(ResponseCode.POLICY_HAS_AUTH_PROVIDERS);
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
