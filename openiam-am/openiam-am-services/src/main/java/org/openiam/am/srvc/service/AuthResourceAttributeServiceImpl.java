package org.openiam.am.srvc.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.AuthResourceAMAttributeDao;
import org.openiam.am.srvc.dao.AuthResourceAttributeMapDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class AuthResourceAttributeServiceImpl implements AuthResourceAttributeService,ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());

    private static ApplicationContext applicationContext;
    @Autowired
    private AuthResourceAttributeMapDao authResourceAttributeMapDao;
    @Autowired
    private AuthResourceAMAttributeDao authResourceAMAttributeDao;
    @Autowired
    private AuthProviderDao authProviderDao;
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    private UserDataService userManager;
    
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    @Autowired
    private AuthAttributeProcessor authAttributeProcessor;
    /*
    *==================================================
    * AuthResourceAMAttribute section
    *===================================================
    */
    @Override
    public AuthResourceAMAttributeEntity getAmAttribute(String attributeId) {
        return authResourceAMAttributeDao.findById(attributeId);
    }

    @Override
    public List<AuthResourceAMAttributeEntity> getAmAttributeList() {
        return authResourceAMAttributeDao.findAll();
    }

    @Override
    @Transactional
    public AuthResourceAMAttributeEntity saveAmAttribute(AuthResourceAMAttributeEntity attribute) {
        if(attribute==null)
            throw new NullPointerException("AMAttribute not set");
        if(attribute.getId()==null || attribute.getId().trim().isEmpty())
            throw new NullPointerException("AMAttribute ID not set");
        if(attribute.getReflectionKey()==null || attribute.getReflectionKey().trim().isEmpty())
            throw new NullPointerException("AMAttribute ReflectionKey not set");
        if(attribute.getAttributeName()==null || attribute.getAttributeName().trim().isEmpty())
            throw new NullPointerException("AMAttribute NAME not set");

        AuthResourceAMAttributeEntity entity = this.getAmAttribute(attribute.getId());
        if(entity==null)
            authResourceAMAttributeDao.save(attribute);
        else {
            entity.setAttributeName(attribute.getAttributeName());
            authResourceAMAttributeDao.save(entity);
        }
        return attribute;
    }

    @Override
    @Transactional
    public void deleteAmAttribute(String attributeId) {
        if(attributeId==null || attributeId.trim().isEmpty())
            throw new NullPointerException("AMAttribute ID not passed");

        authResourceAttributeMapDao.deleteByAMAttributeId(attributeId);
        authResourceAMAttributeDao.deleteById(attributeId);
    }

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */
    @Override
    public AuthResourceAttributeMapEntity getAttributeMap(String attributeMapId){
        return authResourceAttributeMapDao.findById(attributeMapId);
    }

    @Override
    public List<AuthResourceAttributeMapEntity> getAttributeMapList(String providerId) {
        AuthResourceAttributeMapEntity entity = new AuthResourceAttributeMapEntity();
        entity.setProviderId(providerId);
        return authResourceAttributeMapDao.getByExample(entity);
    }

    @Override
    @Transactional
    public AuthResourceAttributeMapEntity saveAttributeMap(AuthResourceAttributeMapEntity attribute) throws Exception {
        if (attribute == null) {
            throw new NullPointerException("Attribute is null");
        }
        if (StringUtils.isBlank(attribute.getProviderId())) {
            throw new NullPointerException("Provider is not set");
        }
        if (StringUtils.isBlank(attribute.getTargetAttributeName())) {
            throw new NullPointerException("TargetAttributeName is not set");
        }
        if (attribute.getAttributeType() == null) {
            throw new NullPointerException("Attribute type is not defined");
        }
        if (StringUtils.isBlank(attribute.getAmResAttributeId())
            &&(StringUtils.isBlank(attribute.getAttributeValue()))
            &&(StringUtils.isBlank(attribute.getAmPolicyUrl()))) {
            throw new NullPointerException("Attribute map is not defined");
        }

        final AuthResourceAttributeMapEntity example = new AuthResourceAttributeMapEntity();
        example.setProviderId(attribute.getProviderId());
        example.setTargetAttributeName(attribute.getTargetAttributeName());
        final List<AuthResourceAttributeMapEntity> attr = authResourceAttributeMapDao.getByExample(example, 0, 1);

        attribute.setAmAttribute(null);
        if(attr!=null && !attr.isEmpty()){
        	final AuthResourceAttributeMapEntity entity = attr.get(0);
            entity.setAmResAttributeId(attribute.getAmResAttributeId());
            entity.setAmPolicyUrl(attribute.getAmPolicyUrl());
            entity.setAttributeValue(attribute.getAttributeValue());
            entity.setAttributeType(attribute.getAttributeType());
            entity.setAmAttribute(null);
            authResourceAttributeMapDao.save(entity);
            return entity;
        }else{
            attribute.setAttributeMapId(null);
            return authResourceAttributeMapDao.add(attribute);
        }
    }

    @Override
    @Transactional
    public void saveAttributeMapCollection(List<AuthResourceAttributeMapEntity> attributeList) throws Exception {
        if (attributeList == null) throw new NullPointerException("Attribute collection is null");
        for (AuthResourceAttributeMapEntity attribute : attributeList) {
            saveAttributeMap(attribute);
        }
    }

    @Override
    @Transactional
    public void removeAttributeMap(String attributeMapId) throws Exception {
        if (attributeMapId == null || attributeMapId.trim().isEmpty())
            throw new NullPointerException("Attribute is not set");
        authResourceAttributeMapDao.deleteById(attributeMapId);
    }

    @Override
    @Transactional
    public void removeAttributeMaps(String providerId) throws Exception {
        if (providerId == null || providerId.trim().isEmpty())
            throw new NullPointerException("Provider is not set");
        authResourceAttributeMapDao.deleteByProviderId(providerId);
    }

    @Override
    public List<SSOAttribute> getSSOAttributes(String providerId, String userId) {
        List<SSOAttribute> resultList = new ArrayList<SSOAttribute>();
        try {
        	if(log.isDebugEnabled()) {
        		log.debug("try to get attribute list by provider id:" + providerId);
        	}
            AuthProviderEntity provider = authProviderDao.findById(providerId);

            if(provider==null)
                throw new NullPointerException("Auth Provider with id: "+providerId+" not found");
            // get attribute list for resource Id
            List<AuthResourceAttributeMapEntity> attributeMapList = getAttributeMapList(providerId);
            if (attributeMapList == null || attributeMapList.isEmpty()) {
                return resultList;
            }
            // get default identity object
            final UserEntity user = getUserObject(userId);
            
            EnumMap<AmAttributes, Object> objectMap = new EnumMap<AmAttributes, Object>(AmAttributes.class);
            objectMap.put(AmAttributes.Login, getLoginObject(userId, provider.getManagedSysId()));
            objectMap.put(AmAttributes.User, getUserObject(userId));
            Map<String, UserAttributeEntity> userAttributeEntityMap = userManager.getAllAttributes(userId);

            for (AuthResourceAttributeMapEntity attr : attributeMapList) {
                resultList.add(parseAttribute(user, attr, userAttributeEntityMap, objectMap));
            }
        } catch (Throwable ex) {
            resultList.clear();
            log.error("Can't get SSO Attibutes", ex);
        }
        
        return resultList;
    }

    private UserEntity getUserObject(String userId) {
        UserEntity user = userManager.getUser(userId);
        if (user == null)
            throw new NullPointerException("User object has not been found");
        return user;
    }

    private LoginEntity getLoginObject(String userId, String managedSysId){
        loginManager.getPrimaryIdentity(userId);
        LoginEntity identityObject = loginManager.getPrimaryIdentity(userId);
        if (identityObject == null) {
            StringBuilder msg = new StringBuilder();
            msg.append("Default identity object for { userId: ").append(userId).append("} has not been found ");
            throw new NullPointerException(msg.toString());
        }
        LoginEntity login = loginManager.getByUserIdManagedSys(userId, managedSysId);
        if (login == null) {
            StringBuilder msg = new StringBuilder();
            msg.append("Login object for { userId: ").append(userId).append(", managedSysId:").append(managedSysId)
               .append("} has not been found. Using the default identity object.");
            log.warn(msg.toString());
            login= identityObject;
        }
        return login;
    }


    private SSOAttribute parseAttribute(final UserEntity user,
    									final AuthResourceAttributeMapEntity attr, 
    									final Map<String, UserAttributeEntity> userAttributeEntityMap, 
    									final EnumMap<AmAttributes, Object> objectMap) throws Exception {
        SSOAttribute attribute = new SSOAttribute();

        String attrValue = "";
        if(attr.getAttributeValue()!=null){
            attrValue = attr.getAttributeValue();
        } else if(attr.getAmPolicyUrl()!=null){
            // TODO: run external groovy script
            attrValue = executeGroovyScript(user, userAttributeEntityMap,attr.getAmPolicyUrl());
        } else{
            AuthResourceAMAttributeEntity amAttributeEntity = authResourceAMAttributeDao.findById(attr.getAmResAttributeId());
            if(amAttributeEntity!=null)
                attrValue = authAttributeProcessor.process(amAttributeEntity.getReflectionKey(), objectMap);
        }
        attribute.setAttributeType(attr.getAttributeType());
        attribute.setTargetAttributeName(attr.getTargetAttributeName());
        attribute.setAttributeValue(attrValue);
        return attribute;
    }

    private String executeGroovyScript(final UserEntity user, final Map<String, UserAttributeEntity> userAttributeEntityMap, String amPolicyUrl) {
        String result="";
        try {
        	Map<String, Object> bindingMap = new HashMap<String, Object>();
        	bindingMap.put("userAttributeMap", userAttributeEntityMap);
        	bindingMap.put("applicationContext",applicationContext);
        	bindingMap.put("user", user);

        	if(!amPolicyUrl.startsWith("/"))
        		amPolicyUrl="/"+amPolicyUrl;
        	AuthResourceAttributeMapper mapper = (AuthResourceAttributeMapper) scriptRunner.instantiateClass(null, amPolicyUrl);
        	mapper.init(bindingMap);
        	result=mapper.mapAttribute();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
