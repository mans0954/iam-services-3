/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.recon.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.dozer.converter.ReconciliationConfigDozerConverter;
import org.openiam.dozer.converter.ReconciliationSituationDozerConverter;
import org.openiam.idm.parser.csv.UserCSVParser;
import org.openiam.idm.parser.csv.UserSearchBeanCSVParser;
import org.openiam.idm.searchbeans.ManualReconciliationSearchBean;
import org.openiam.idm.searchbeans.ReconConfigSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.recon.domain.ReconciliationConfigEntity;
import org.openiam.idm.srvc.recon.domain.ReconciliationSituationEntity;
import org.openiam.idm.srvc.recon.dto.ReconExecStatusOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationResponse;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultBean;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultRow;
import org.openiam.idm.srvc.recon.result.dto.ReconcliationFieldComparatorByField;
import org.openiam.idm.srvc.recon.util.Serializer;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.provision.service.ConnectorAdapter;
import org.openiam.provision.service.PrePostExecutor;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suneet
 * 
 */
@Service
public class ReconciliationConfigServiceImpl implements ReconciliationConfigService {

    @Autowired
    protected ReconciliationSituationDAO reconSituationDAO;

    @Autowired
    protected ReconciliationConfigDAO reconConfigDao;

    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected ResourceDataService resourceDataService;
    @Autowired
    protected ManagedSystemObjectMatchDozerConverter objectMatchDozerConverter;
    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ConnectorAdapter connectorAdapter;
    @Autowired
    protected GroupDataService groupManager;
    @Autowired
    protected GroupDozerConverter groupDozerConverter;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    private ReconciliationConfigDozerConverter reconConfigDozerMapper;
    @Autowired
    private ReconciliationSituationDozerConverter reconSituationDozerMapper;
    @Autowired
    protected UserCSVParser userCSVParser;
    @Autowired
    public UserSearchBeanCSVParser userSearchCSVParser;
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;
    @Value("${iam.files.location}")
    private String absolutePath;

    @Autowired
    protected MatchRuleFactory matchRuleFactory;
    @Autowired
    protected AuditLogService auditLogService;

    @Transactional
    public ReconciliationConfig addConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }
        Set<ReconciliationSituation> sitSet = null;
        if (!CollectionUtils.isEmpty(config.getSituationSet())) {
            sitSet = new HashSet<>(config.getSituationSet());
        }
        config.setId(null);
        ReconciliationConfig result = reconConfigDozerMapper.convertToDTO(
                reconConfigDao.add(reconConfigDozerMapper.convertToEntity(config, false)), false);
        saveSituationSet(sitSet, result.getId());
        result.setSituationSet(sitSet);
        return result;
    }

    @Transactional
    private void saveSituationSet(Set<ReconciliationSituation> sitSet, String configId) {
        if (sitSet != null) {
            for (ReconciliationSituation s : sitSet) {
                if (StringUtils.isEmpty(s.getReconConfigId())) {
                    s.setReconConfigId(configId);
                }
                if (StringUtils.isEmpty(s.getId())) {
                    s.setId(null);
                    s.setId(reconSituationDAO.add(reconSituationDozerMapper.convertToEntity(s, false))
                            .getId());
                } else {
                    reconSituationDAO.update(reconSituationDozerMapper.convertToEntity(s, false));
                }
            }
        }
    }

    @Transactional
    public void updateConfig(ReconciliationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config parameter is null");
        }

        ReconciliationConfigEntity configEntity = reconConfigDozerMapper.convertToEntity(config, false);

        for (ReconciliationSituation s : config.getSituationSet()) {
            if (StringUtils.isEmpty(s.getReconConfigId())) {
                s.setReconConfigId(configEntity.getId());
            }
            ReconciliationSituationEntity situationEntity;
            if (StringUtils.isEmpty(s.getId())) {
                situationEntity = reconSituationDozerMapper.convertToEntity(s, false);
                reconSituationDAO.save(situationEntity);
            } else {
                situationEntity = reconSituationDAO.findById(s.getId());
                situationEntity.setScript(s.getScript());
                situationEntity.setSituation(s.getSituation());
                situationEntity.setSituationResp(s.getSituationResp());
                situationEntity.setCustomCommandScript(s.getCustomCommandScript());
                reconSituationDAO.save(situationEntity);
            }
            configEntity.getSituationSet().add(situationEntity);
        }

        reconConfigDao.merge(configEntity);

    }

    @Transactional
    public void removeConfig(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        ReconciliationConfigEntity config = reconConfigDao.findById(configId);
        reconConfigDao.delete(config);

    }

    @Transactional(readOnly = true)
    public ReconciliationConfig getConfigByResourceByType(String resourceId, String type) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfigDao.findByResourceIdByType(resourceId, type);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ReconciliationConfig> findReconConfig(ReconConfigSearchBean searchBean, int from, int size) {
        List<ReconciliationConfigEntity> reconciliationConfigEntities = reconConfigDao.getByExample(searchBean, from, size);
        if(reconciliationConfigEntities == null) {
            return Collections.EMPTY_LIST;
        }
        return reconConfigDozerMapper.convertToDTOList(reconciliationConfigEntities, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int countReconConfig(final ReconConfigSearchBean searchBean) {
        return reconConfigDao.count(searchBean);
    }

    @Transactional(readOnly = true)
    public List<ReconciliationConfig> getConfigsByResource(final String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId parameter is null");
        }
        
        final ReconConfigSearchBean sb = new ReconConfigSearchBean();
        sb.setResourceId(resourceId);
        List<ReconciliationConfigEntity> result = reconConfigDao.getByExample(sb);
        if (result == null)
            return new LinkedList<ReconciliationConfig>();
        else
            return reconConfigDozerMapper.convertToDTOList(result, false);
    }

    @Transactional(readOnly = true)
    public ReconciliationConfig getConfigById(String configId) {
        if (configId == null) {
            throw new IllegalArgumentException("configId parameter is null");
        }
        ReconciliationConfigEntity result = reconConfigDao.findById(configId);
        if (result == null)
            return null;
        else
            return reconConfigDozerMapper.convertToDTO(result, true);
    }

	@Transactional(readOnly = true)
	public ReconExecStatusOptions getExecStatus(String configId) {
		ReconciliationConfigEntity config = reconConfigDao.findById(configId);
		if (config != null) {
			reconConfigDao.refresh(config);
			return config.getExecStatus();
		} else {
			return null;
		}
	}

	@Transactional
	public void updateExecStatus(String configId, ReconExecStatusOptions status) {
		ReconciliationConfigEntity config = reconConfigDao.findById(configId);
		config.setExecStatus(status);
		reconConfigDao.update(config);
	}

    @Override
    public void clearSession() {
        reconConfigDao.flush();
        reconConfigDao.clear();
    }
}
