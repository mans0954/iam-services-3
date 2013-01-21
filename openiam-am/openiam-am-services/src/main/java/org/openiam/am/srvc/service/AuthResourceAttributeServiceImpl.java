package org.openiam.am.srvc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthResourceAttributeDao;
import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.am.srvc.dto.Attribute;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthResourceAttributeServiceImpl implements AuthResourceAttributeService{
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private AuthResourceAttributeDao authResourceAttributeDao;
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    protected UserDataService userManager;


    @Override
    public AuthResourceAttributeEntity getAttributeMap(String attributeId) throws Exception {
        return authResourceAttributeDao.findById(attributeId);
    }

    @Override
    public List<AuthResourceAttributeEntity> getAttributeMapCollection(String resourceId) throws Exception {
        AuthResourceAttributeEntity example = new AuthResourceAttributeEntity();
        example.setResourceId(resourceId);
        return getAttributeMapCollection(resourceId, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<AuthResourceAttributeEntity> getAttributeMapCollection(String resourceId, Integer from, Integer size) throws Exception {
        if (!StringUtils.hasText(resourceId)) throw new NullPointerException("resourceId is null or empty");
        log.debug("try to get attribute list by resource id:" + resourceId);
        AuthResourceAttributeEntity example = new AuthResourceAttributeEntity();
        example.setResourceId(resourceId);
        return  getAttributeMapCollection(example,from, size);
    }
    @Override
    public List<AuthResourceAttributeEntity> getAttributeMapCollection(AuthResourceAttributeEntity searchBean) throws Exception{
          return  getAttributeMapCollection(searchBean, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<AuthResourceAttributeEntity> getAttributeMapCollection(AuthResourceAttributeEntity searchBean, Integer from, Integer size) throws Exception{
        return authResourceAttributeDao.getByExample(searchBean,from, size);
    }

    @Override
    @Transactional
    public AuthResourceAttributeEntity addAttributeMap(AuthResourceAttributeEntity attribute) throws Exception {
        if (attribute == null) throw new NullPointerException("Attribute is null");
        if (attribute.getResourceId() == null) throw new NullPointerException("resourceId is null");
        if (attribute.getTargetAttributeName() == null) throw new NullPointerException("TargetAttributeName is null");

        AuthResourceAttributeEntity example = new AuthResourceAttributeEntity();
        example.setResourceId(attribute.getResourceId());
        example.setTargetAttributeName(attribute.getTargetAttributeName());
        List<AuthResourceAttributeEntity> res = authResourceAttributeDao.getByExample(example, 0, 1);
        if(res!=null && !res.isEmpty())
            throw new Exception("AttributeMap for [resourceId: "+attribute.getResourceId()+", targetAttributeName:"+attribute.getTargetAttributeName()+"]  - already exists");

        attribute.setAttributeMapId(null);
        return authResourceAttributeDao.add(attribute);
    }

    @Override
    @Transactional
    public void addAttributeMapCollection(List<AuthResourceAttributeEntity> attributeList) throws Exception {
        if (attributeList == null) throw new NullPointerException("Attribute collection is null");

        for (AuthResourceAttributeEntity attribute : attributeList) {
            addAttributeMap(attribute);
        }
    }

    @Override
    @Transactional
    public AuthResourceAttributeEntity updateAttributeMap(AuthResourceAttributeEntity attribute) throws Exception {
        if (attribute == null) throw new NullPointerException("Attribute is null");
        if (attribute.getAttributeMapId() == null) throw new NullPointerException("AttributeMapId is null");
        AuthResourceAttributeEntity res = authResourceAttributeDao.findById(attribute.getAttributeMapId());
        if(res!=null){
            res.setTargetAttributeName(attribute.getTargetAttributeName());
            res.setAmAttributeName(attribute.getAmAttributeName());
            res.setResourceId(attribute.getResourceId());
            res.setAmPolicyUrl(attribute.getAmPolicyUrl());
            authResourceAttributeDao.update(res);
        }
        return res;
    }

    @Override
    @Transactional
    public void removeAttributeMap(String attributeId) throws Exception {
        if (!StringUtils.hasText(attributeId)) throw new NullPointerException("attributeId is null or empty");
        authResourceAttributeDao.deleteById(attributeId);
    }

    @Override
    @Transactional
    public int removeResourceAttributeMaps(String resourceId) throws Exception {
        if (!StringUtils.hasText(resourceId)) throw new NullPointerException("resourceId is null or empty");
        return authResourceAttributeDao.deleteByResourceId(resourceId);
    }

    @Override
    public List<Attribute> getSSOAttributes(String resourceId, String principalName, String securityDomain,
                                            String managedSysId, Integer from, Integer size) {
        List<Attribute> resultList = new ArrayList<Attribute>();
        try {
            log.debug("try to get attribute list by resource id:" + resourceId);
            // get attribute list for resource Id
            List<AuthResourceAttributeEntity> attributeMapList = getAttributeMapCollection(resourceId, from, size);
            if (attributeMapList == null || attributeMapList.isEmpty()) {
                throw new NullPointerException("Empty attribute map collection");
            }
            // get default identity object
            LoginEntity identityObject = loginManager.getLoginByManagedSys(securityDomain, principalName, "0");
            if (identityObject == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Default identity object for { securityDomain: ").append(securityDomain).append(", principalName: ")
                   .append(principalName).append(", managedSysId:").append(managedSysId)
                   .append("} has not been found ");
                throw new NullPointerException(msg.toString());
            }
            UserEntity user = userManager.getUser(identityObject.getUserId());
            if (user == null)
                throw new NullPointerException("User object has not been found");


            LoginEntity login = loginManager.getByUserIdManagedSys(user.getUserId(), managedSysId);
            if (login == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Login object for { securityDomain: ").append(securityDomain).append(", principalName: ")
                   .append(principalName).append(", managedSysId:").append(managedSysId)
                   .append("} has not been found. Using the default identity object.");
                log.warn(msg.toString());
                login= identityObject;
            }

            for (AuthResourceAttributeEntity attr : attributeMapList) {
                resultList.add(parseAttribute(attr, login, user));
            }
        } catch (Exception ex) {
            resultList.clear();
            log.error(ex.getMessage());
        } finally {
                return resultList;
        }
    }

    private Attribute parseAttribute(AuthResourceAttributeEntity attr, LoginEntity login, UserEntity user) throws Exception {
        Attribute attribute = new Attribute();
        if (attr.getAmAttributeName() == null) {
            throw new NullPointerException("AccessManagerAttributeName is null");
        }

        String[] map = attr.getAmAttributeName().split("\\.");
        String attrValue = "";
        if (map != null && map.length > 1) {
            if ("Login".equals(map[0])) {
                attrValue = getAttributeValue(login, map[1],1,map);
            } else  if ("User".equals(map[0])) {
                attrValue = getAttributeValue(user, map[1],1,map);
            } else {
                throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                        .getAmAttributeName());
            }
        } else {
            throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                    .getAmAttributeName());
        }

        attribute.setTargetAttributeName(attr.getTargetAttributeName());
        attribute.setAttributeValue(attrValue);


        return attribute;
    }

    private String getAttributeValue(Object obj, String fieldName, int currentMapIndex, String[] map) throws Exception{
        String result = "";
        if(currentMapIndex==map.length-1){
            // get field value
            Object res =  getFieldValue(obj, fieldName);
            if(res!=null){
                result = res.toString();
                if(fieldName.contains("password")){
                    String userId =  (String)getFieldValue(obj, "userId");
                    result = loginManager.decryptPassword(userId,result);
                }
            }
        } else{
            // try to find subfield
            Object subField = getFieldValue(obj, fieldName);
            if(subField==null)
                throw new NullPointerException("The attribute mapping doesn't exist.");
            int nextMapIndex =currentMapIndex+1;
            result =  getAttributeValue(subField, map[nextMapIndex], nextMapIndex, map);
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

    public Integer getNumOfAttributeMapList(String resourceId) throws Exception{
        AuthResourceAttributeEntity example = new AuthResourceAttributeEntity();
        example.setResourceId(resourceId);
        return  getNumOfAttributeMapList(example);
    }

    public Integer getNumOfAttributeMapList(AuthResourceAttributeEntity searchBean) throws Exception{
          return authResourceAttributeDao.count(searchBean);
    }


}
