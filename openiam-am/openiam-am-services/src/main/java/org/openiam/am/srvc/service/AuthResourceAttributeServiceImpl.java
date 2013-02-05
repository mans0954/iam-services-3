package org.openiam.am.srvc.service;

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
import org.openiam.script.ScriptFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    @Qualifier("scriptEngine")
    private String scriptEngine;
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
        if(attribute.getAmAttributeId()==null || attribute.getAmAttributeId().trim().isEmpty())
            throw new NullPointerException("AMAttribute ID not set");
        if(attribute.getAttributeName()==null || attribute.getAttributeName().trim().isEmpty())
            throw new NullPointerException("AMAttribute NAME not set");

        AuthResourceAMAttributeEntity entity = this.getAmAttribute(attribute.getAmAttributeId());
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
        if (attribute == null)
            throw new NullPointerException("Attribute is null");
        if (attribute.getProviderId() == null || attribute.getProviderId().trim().isEmpty())
            throw new NullPointerException("Provider is not set");
        if (attribute.getTargetAttributeName() == null || attribute.getTargetAttributeName().trim().isEmpty())
            throw new NullPointerException("TargetAttributeName is not set");
        if (attribute.getAttributeType() == null)
            throw new NullPointerException("Attribute type is not defined");
        if ((attribute.getAmAttributeId() == null || attribute.getAmAttributeId().trim().isEmpty())
            &&(attribute.getAttributeValue() == null || attribute.getAttributeValue().trim().isEmpty())
            &&(attribute.getAmPolicyUrl() == null || attribute.getAmPolicyUrl().trim().isEmpty()))
            throw new NullPointerException("Attribute map is not defined");

        AuthResourceAttributeMapEntity example = new AuthResourceAttributeMapEntity();
        example.setProviderId(attribute.getProviderId());
        example.setTargetAttributeName(attribute.getTargetAttributeName());
        List<AuthResourceAttributeMapEntity> attr = authResourceAttributeMapDao.getByExample(example, 0, 1);

        attribute.setAmAttribute(null);
        if(attr!=null && !attr.isEmpty()){
            AuthResourceAttributeMapEntity entity = attr.get(0);
            entity.setAmAttributeId(attribute.getAmAttributeId());
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
            log.debug("try to get attribute list by provider id:" + providerId);
            AuthProviderEntity provider = authProviderDao.findById(providerId);

            if(provider==null)
                throw new NullPointerException("Auth Provider with id: "+providerId+" not found");
            // get attribute list for resource Id
            List<AuthResourceAttributeMapEntity> attributeMapList = getAttributeMapList(providerId);
            if (attributeMapList == null || attributeMapList.isEmpty()) {
                return resultList;
            }
            // get default identity object
            EnumMap<AmAttributes, Object> objectMap = new EnumMap<AmAttributes, Object>(AmAttributes.class);
            objectMap.put(AmAttributes.Login, getLoginObject(userId, provider.getManagedSysId()));
            objectMap.put(AmAttributes.User, getUserObject(userId));
            Map<String, UserAttributeEntity> userAttributeEntityMap = userManager.getAllAttributes(userId);

            for (AuthResourceAttributeMapEntity attr : attributeMapList) {
                resultList.add(parseAttribute(attr, userAttributeEntityMap, objectMap));
            }
        } catch (Exception ex) {
            resultList.clear();
            log.error(ex.getMessage());
        } finally {
                return resultList;
        }
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


    private SSOAttribute parseAttribute(AuthResourceAttributeMapEntity attr, Map<String, UserAttributeEntity> userAttributeEntityMap, EnumMap<AmAttributes, Object> objectMap) throws Exception {
        SSOAttribute attribute = new SSOAttribute();

        String attrValue = "";
        if(attr.getAttributeValue()!=null){
            attrValue = attr.getAttributeValue();
        } else if(attr.getAmPolicyUrl()!=null){
            // TODO: run external groovy script
            attrValue = executeGroovyScript(userAttributeEntityMap,attr.getAmPolicyUrl());
        } else{
            if (attr.getAmAttributeId() == null) {
                throw new NullPointerException("AccessManagerAttributeName is null");
            }

            String[] map = attr.getAmAttributeId().split("\\.");

            if (map != null && map.length > 1) {
                try{
                    attrValue = getAttributeValue(objectMap.get(AmAttributes.valueOf(map[0])), map,1);
                } catch (IllegalArgumentException ex){
                    throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                            .getAmAttributeId());
                }
            } else {
                throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                        .getAmAttributeId());
            }
        }
        attribute.setAttributeType(attr.getAttributeType());
        attribute.setTargetAttributeName(attr.getTargetAttributeName());
        attribute.setAttributeValue(attrValue);
        return attribute;
    }

    private String executeGroovyScript(Map<String, UserAttributeEntity> userAttributeEntityMap, String amPolicyUrl) {
        String result="";
        if(this.scriptEngine!=null && !this.scriptEngine.trim().isEmpty()){
            try {
                if(userAttributeEntityMap!=null && !userAttributeEntityMap.isEmpty()){
                    ScriptIntegration se = ScriptFactory.createModule(this.scriptEngine);
                    Map<String, Object> bindingMap = new HashMap<String, Object>();
                    bindingMap.put("userAttributeMap", userAttributeEntityMap);
                    bindingMap.put("applicationContext",applicationContext);

                    if(!amPolicyUrl.startsWith("/"))
                        amPolicyUrl="/"+amPolicyUrl;
                    AuthResourceAttributeMapper mapper = (AuthResourceAttributeMapper) se.instantiateClass(null, amPolicyUrl);
                    mapper.init(bindingMap);
                    result=mapper.mapAttribute();
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
        return result;
    }

    private String getAttributeValue(Object obj, String[] map,int currentMapIndex) throws Exception{
        String result = "";
        if(obj==null)
            return result;

        if(currentMapIndex==map.length-1){
            // get field value
            Object res =  getFieldValue(obj, map[currentMapIndex]);
            if(res!=null){
                result = res.toString();
                if(map[currentMapIndex].contains("password")){
                    String userId =  (String)getFieldValue(obj, "userId");
                    result = loginManager.decryptPassword(userId,result);
                }
            }
        } else{
            // try to find subfield
            Object subField = getFieldValue(obj, map[currentMapIndex]);
            if(subField==null)
                throw new NullPointerException("The attribute mapping doesn't exist.");
            int nextMapIndex =currentMapIndex+1;
            result =  getAttributeValue(subField, map, nextMapIndex);
        }
        return result;
    }

    private Object getFieldValue(Object obj, String fieldName) throws Exception{
        Class objClass = obj.getClass();

        if(!StringUtils.hasText(fieldName))
            throw new NoSuchFieldException("Field Name is null or empty");

        Field f = objClass.getDeclaredField(fieldName);
        if(f==null)
            throw new NoSuchFieldException("Field with name "+fieldName+"not found");

        f.setAccessible(true);
        return f.get(obj);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
