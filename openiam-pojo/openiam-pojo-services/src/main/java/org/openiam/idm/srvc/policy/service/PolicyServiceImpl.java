package org.openiam.idm.srvc.policy.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ITPolicyDozerConverter;
import org.openiam.dozer.converter.PolicyDefParamDozerConverter;
import org.openiam.dozer.converter.PolicyDozerConverter;
import org.openiam.dozer.converter.PolicyObjectAssocDozerConverter;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.idm.srvc.policy.domain.*;
import org.openiam.idm.srvc.policy.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final Log log = LogFactory.getLog(PolicyServiceImpl.class);

	@Autowired
	private PolicyDAO policyDao;

	@Autowired
	PolicyObjectAssocDAO policyObjectAssocDAO;

	@Autowired
	private PolicyDefParamDAO policyDefParamDao;

    /**
     * The policy dozer converter.
     */
    @Autowired
    private PolicyDozerConverter policyDozerConverter;

    @Autowired
    private PolicyObjectAssocDozerConverter policyAssocObjectDozerConverter;

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
	@Transactional(readOnly=true)
	public Policy getPolicy(String policyId) {
        PolicyEntity policyEntity = policyDao.findById(policyId);
        return policyDozerConverter.convertToDTO(policyEntity, true);
	}

	@Override
	@Transactional
	public String save(final Policy policy) {
        final PolicyEntity pe = policyDozerConverter.convertToEntity(policy, true);
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
        try {
            this.policyPostProcessor(pe);
        } catch (Exception e) {
            log.error("can't run policy post processor");
            log.error(e);
        }
        return pe.getPolicyId();
	}

    private void policyPostProcessor(PolicyEntity pe) {
        // turn on Task Password near expiration
        PolicyAttributeEntity pae = pe.getAttribute("PWD_EXP_WARN");
        boolean state = (pae == null) ? false : pae.isRequired();
        BatchTaskEntity bte = batchService.findById(passwordExpirationBatchTaskId);
        if (bte.isEnabled() != state) {
            bte.setEnabled(state);
            batchService.save(bte);
        }
    }

	@Override
	@Transactional(readOnly=true)
	public List<Policy> findPolicyByName(String policyDefId, String policyName) {
        List<PolicyEntity> policyEntities = policyDao.findPolicyByName(policyDefId, policyName);
        return policyDozerConverter.convertToDTOList(policyEntities, false);
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
	public List<Policy> findBeans(PolicySearchBean searchBean, int from,
			int size) {
        List<PolicyEntity> entities = policyDao.getByExample(searchBean, from, size);
        return policyDozerConverter.convertToDTOList(entities, true);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<PolicyDefParam> findPolicyDefParamByGroup(final String policyDefId, final String pswdGroup) {
		List<PolicyDefParamEntity> entities =  policyDefParamDao.findPolicyDefParamByGroup(policyDefId, pswdGroup);
        return policyDefParamDozerConverter.convertToDTOList(entities, true);
	}

    @Override
    @Transactional(readOnly = true)
    public List<PolicyObjectAssoc> getAssociationsForPolicy(String policyId) {

        return policyAssocObjectDozerConverter
                .convertToDTOList(policyObjectAssocDAO.findByPolicy(policyId),
                        true);
    }

    @Override
    @Transactional
    public String savePolicyAssoc(PolicyObjectAssoc poa) {
        if(poa == null) {
            return null;
        }
        PolicyObjectAssocEntity poaEntity = policyAssocObjectDozerConverter
                .convertToEntity(poa, true);
        if (poaEntity.getPolicyObjectId() == null) {
            poaEntity.setObjectId(null);
            poaEntity = policyObjectAssocDAO.add(poaEntity);
        } else {
            policyObjectAssocDAO.update(poaEntity);
        }
        return poaEntity.getPolicyObjectId();
    }

    @Override
    @Transactional(readOnly = true)
    public ITPolicy findITPolicy() {
        return itPolicyDozerConverter.convertToDTO(itPolicyDao.findITPolicy(), true);
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
    public String saveITPolicy(ITPolicy itPolicy) {
        ITPolicyEntity pe = itPolicyDozerConverter.convertToEntity(itPolicy, true);
        itPolicyDao.save(pe);
        return pe.getPolicyId();
    }
}
