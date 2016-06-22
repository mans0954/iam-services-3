package org.openiam.idm.srvc.sysprop.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseConstants;
import org.openiam.base.OrderConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.service.*;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.UserIdentityAnswerDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.searchbean.converter.AddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.EmailAddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.PhoneSearchBeanConverter;
import org.openiam.idm.srvc.sysprop.dao.SystemPropertyDao;
import org.openiam.idm.srvc.sysprop.domain.SystemPropertyEntity;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.user.dao.UserSearchDAO;
import org.openiam.idm.srvc.user.domain.*;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.service.*;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author zaporozhec
 */
@Service("systemPropertyService")
public class SystemPropertyServiceImpl implements SystemPropertyService {
    @Autowired
    @Qualifier("systemPropertyDao")
    private SystemPropertyDao systemPropertyDao;
    @Autowired
    private MetadataTypeDAO metadataTypeDao;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "systemProperties", key = "{#name}")
    public List<SystemPropertyDto> getByName(String name) {
        List<SystemPropertyEntity> list = systemPropertyDao.getByName(name);
        if (list == null) {
            return null;
        } else {
            return SystemPropertyEntity.toDtoList(list);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "systemProperties", key = "{#mdTypeId}")
    public List<SystemPropertyDto> getByType(String mdTypeId) {
        List<SystemPropertyEntity> list = systemPropertyDao.getByMetadataType(mdTypeId);
        if (list == null) {
            return null;
        } else {
            return SystemPropertyEntity.toDtoList(list);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "systemProperties", key = "{#id}")
    public SystemPropertyDto getById(String id) {
        SystemPropertyEntity entity = systemPropertyDao.findById(id);
        if (entity == null) {
            return null;
        } else {
            return entity.toDTO();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "systemProperties", allEntries = true)
    public void save(SystemPropertyDto propertyDto) {
        //Now only Value changes is allowed
        //FIXME
        if (propertyDto != null) {
            SystemPropertyEntity dbEntity = systemPropertyDao.findById(propertyDto.getName());
            if (dbEntity == null) {
                throw new NullPointerException("No such System property");
            }
            dbEntity.setValue(propertyDto.getValue());
            systemPropertyDao.save(dbEntity);
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "systemProperties")
    public List<SystemPropertyDto> getAll() {
        return SystemPropertyEntity.toDtoList(systemPropertyDao.findAll());
    }
}
