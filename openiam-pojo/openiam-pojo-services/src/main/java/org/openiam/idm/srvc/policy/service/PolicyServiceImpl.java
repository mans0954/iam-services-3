package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.exception.BasicDataServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ITPolicyDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyDefParamEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.idm.srvc.policy.domain.*;
import org.openiam.idm.srvc.policy.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final Log log = LogFactory.getLog(PolicyServiceImpl.class);

	@Autowired
	private PolicyDAO policyDao;
	
	@Autowired
	private PolicyDefParamDAO policyDefParamDao;
	
	@Autowired
	private PolicyDefDAO policyDefDAO;

/*    @Autowired
    PolicyObjectAssocDAO policyObjectAssocDAO;*/

    /**
     * The policy dozer converter.
     */
    @Autowired
    private PolicyDozerConverter policyDozerConverter;

/*    @Autowired
    private PolicyObjectAssocDozerConverter policyAssocObjectDozerConverter;*/

    @Autowired
    private ITPolicyDozerConverter itPolicyDozerConverter;

    @Autowired
    private ITPolicyDAO itPolicyDao;

    /**
     * The policy def param dozer converter.
     */
    @Autowired
    private PolicyDefParamDozerConverter policyDefParamDozerConverter;

    @Autowired
    private BatchService batchService;

    @Value("${batch.task.password.exp.id}")
    private String passwordExpirationBatchTaskId;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "policies", key = "{#policyId}")
    public Policy getPolicy(String policyId) {
        PolicyEntity policyEntity = policyDao.findById(policyId);
        return policyDozerConverter.convertToDTO(policyEntity, true);
	}

	@Override
    @Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("policies")
        }
    )
	public void save(final Policy policy) {
		final PolicyEntity pe = policyDozerConverter.convertToEntity(policy, true);
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
        try {
            this.policyPostProcessor(pe);
        } catch (Exception e) {
            log.error("can't run policy post processor");
            log.error(e);
        }
        policy.setId(pe.getId());
	}

	// TODO: check because added boolean parameter to call batchService.save(bte);
    private void policyPostProcessor(PolicyEntity pe) {
        // turn on Task Password near expiration
        PolicyAttributeEntity pae = pe.getAttribute("PWD_EXP_WARN");
        boolean state = (pae == null) ? false : pae.isRequired();
        BatchTaskEntity bte = batchService.findById(passwordExpirationBatchTaskId);
        if (bte.isEnabled() != state) {
            bte.setEnabled(state);
            batchService.save(bte, true);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "policies", key = "{#policyDefId, #policyName}")
    public List<Policy> findPolicyByName(String policyDefId, String policyName) {
        List<PolicyEntity> policyEntities = policyDao.findPolicyByName(policyDefId, policyName);
        return policyDozerConverter.convertToDTOList(policyEntities, false);
	}

	@Override
	@Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("policies")
        }
    )
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
    @Transactional(readOnly = true)
    public int count(PolicySearchBean searchBean) {
        return policyDao.count(searchBean);
    }

	@Override
	@Transactional(readOnly=true)
    @Cacheable(value = "policies", key = "{#searchBean, #from, #size}")
	public List<Policy> findBeans(PolicySearchBean searchBean, int from,
			int size) {
        List<PolicyEntity> entities = policyDao.getByExampleNoLocalize(searchBean, from, size);
        return policyDozerConverter.convertToDTOList(entities, true);
	}
	
	@Override
	@Transactional(readOnly=true)
    @Cacheable(value = "policyDefParams", key = "{#policyDefId, #pswdGroup}")
	public List<PolicyDefParam> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup) {
		List<PolicyDefParamEntity> entities =  policyDefParamDao.findPolicyDefParamByGroup(policyDefId, pswdGroup);
        return policyDefParamDozerConverter.convertToDTOList(entities, true);
	}

    @Override
    @Transactional(readOnly = true)
    public ITPolicy findITPolicy() {
        return itPolicyDozerConverter.convertToDTO(itPolicyDao.findITPolicy(), false);
    }

    @Override
    @Transactional
    public void resetITPolicy() {
        ITPolicyEntity itPolicyEntity = itPolicyDao.findITPolicy();
        if(itPolicyEntity != null){
            itPolicyDao.delete(itPolicyEntity);
        }
    }

    @Override
    @Transactional
    public void saveITPolicy(ITPolicy itPolicy) {
        ITPolicyEntity pe = itPolicyDozerConverter.convertToEntity(itPolicy, true);
        itPolicyDao.save(pe);
        itPolicy.setId(pe.getId());
    }
}
