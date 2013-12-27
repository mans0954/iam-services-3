package org.openiam.am.srvc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.WebResourceAttributeDao;
import org.openiam.am.srvc.dto.Attribute;
import org.openiam.am.srvc.dto.AttributeMap;
import org.openiam.am.srvc.domain.WebResourceAttribute;
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

/**
 * User: Alexander Duckardt
 * Date: 8/16/12
 */
@Service
@Deprecated
@Transactional
public class WebResourceAttributeServiceImpl implements WebResourceAttributeService {
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private WebResourceAttributeDao webResourceAttributeDao;
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    protected UserDataService userManager;


    @Override
    public AttributeMap getAttributeMap(String attributeId) throws Exception {
        if (!StringUtils.hasText(attributeId)) throw new NullPointerException("attributeId is null or empty");
        WebResourceAttribute webResourceAttribute = webResourceAttributeDao.findById(attributeId);
        AttributeMap attribute = null;
        if (webResourceAttribute != null) {
            attribute = fromEntity(webResourceAttribute);
        }
        return attribute;
    }

    @Override
    public List<AttributeMap> getAttributeMapCollection(String resourceId) throws Exception {
        if (!StringUtils.hasText(resourceId)) throw new NullPointerException("resourceId is null or empty");
        log.debug("try to get attribute list by resource id:" + resourceId);
        List<WebResourceAttribute> attributeList = webResourceAttributeDao.getAttributesByResourceId(resourceId);
        List<AttributeMap> resultList = new ArrayList<AttributeMap>();
        if (attributeList != null && !attributeList.isEmpty()) {
            log.debug("AttributeMap datas has been found for given resource id:" + resourceId + "; Attributes count: "
                      + attributeList.size());
            for (WebResourceAttribute attr : attributeList) {
                resultList.add(fromEntity(attr));
            }
        } else {
            log.debug("There no any attributes for given resource id:" + resourceId);
        }
        return resultList;
    }

    @Override
    @Transactional
    public AttributeMap addAttributeMap(AttributeMap attribute) throws Exception {
        if (attribute == null) throw new NullPointerException("Attribute is null");
        if (attribute.getResourceId() == null) throw new NullPointerException("resourceId is null");
        if (attribute.getTargetAttributeName() == null) throw new NullPointerException("TargetAttributeName is null");

        WebResourceAttribute res = webResourceAttributeDao.getByAttributeNameResource(attribute.getResourceId(),attribute.getTargetAttributeName());
        if(res!=null)
            throw new Exception("AttributeMap for [resourceId: "+attribute.getResourceId()+", targetAttributeName:"+attribute.getTargetAttributeName()+"]  - already exists");
        attribute.setAttributeMapId(null);
        return fromEntity(webResourceAttributeDao.add(fromAttributeMap(attribute)));
    }

    @Override
    @Transactional
    public void addAttributeMapCollection(List<AttributeMap> attributeList) throws Exception {
            if (attributeList == null) throw new NullPointerException("Attribute collection is null");

            for (AttributeMap attribute : attributeList) {
                addAttributeMap(attribute);
            }
    }

    @Override
    public AttributeMap updateAttributeMap(AttributeMap attribute) throws Exception {
        if (attribute == null) throw new NullPointerException("Attribute is null");
        if (attribute.getAttributeMapId() == null) throw new NullPointerException("AttributeMapId is null");
        return fromEntity(webResourceAttributeDao.update(fromAttributeMap(attribute)));
    }

    @Override
    public void removeAttributeMap(String attributeId) throws Exception {
        if (!StringUtils.hasText(attributeId)) throw new NullPointerException("attributeId is null or empty");
        webResourceAttributeDao.delete(attributeId);
    }

    @Override
    public int removeResourceAttributeMaps(String resourceId) throws Exception {
        if (!StringUtils.hasText(resourceId)) throw new NullPointerException("resourceId is null or empty");
        return webResourceAttributeDao.deleteByResourceId(resourceId);
    }

    @Override
    public List<Attribute> getSSOAttributes(String resourceId, String principalName, String managedSysId) {
        List<Attribute> resultList = new ArrayList<Attribute>();
        try {
            log.debug("try to get attribute list by resource id:" + resourceId);
            // get attribute list for resource Id
            List<AttributeMap> attributeMapList = getAttributeMapCollection(resourceId);
            if (attributeMapList == null || attributeMapList.isEmpty()) {
                throw new NullPointerException("Empty attribute map collection");
            }
            // get default identity object
            LoginEntity identityObject = loginManager.getLoginByManagedSys(principalName, "0");
            if (identityObject == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Default identity object for { principalName: ")
                        .append(principalName).append(", managedSysId:").append(managedSysId)
                        .append("} has not been found ");
                throw new NullPointerException(msg.toString());
            }
            UserEntity user = userManager.getUser(identityObject.getUserId());
            if (user == null)
                throw new NullPointerException("User object has not been found");


            LoginEntity login = loginManager.getByUserIdManagedSys(user.getId(), managedSysId);
            if (login == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Login object for { principalName: ")
                   .append(principalName).append(", managedSysId:").append(managedSysId)
                   .append("} has not been found. Using the default identity object.");
                log.warn(msg.toString());
                login= identityObject;
            }

            for (AttributeMap attr : attributeMapList) {
                resultList.add(parseAttribute(attr, login, user));
            }
        } catch (Exception ex) {
            resultList.clear();
            log.error(ex.getMessage());
        } finally {
            return resultList;
        }
    }

    private Attribute parseAttribute(AttributeMap attr, LoginEntity login, UserEntity user) throws Exception {
        Attribute attribute = new Attribute();
        if (attr.getAccessManagerAttributeName() == null) {
            throw new NullPointerException("AccessManagerAttributeName is null");
        }

        String[] map = attr.getAccessManagerAttributeName().split("\\.");
        String attrValue = "";
        if (map != null && map.length > 1) {
            if ("Login".equals(map[0])) {
                attrValue = getAttributeValue(login, map[1],1,map);
            } else  if ("User".equals(map[0])) {
                attrValue = getAttributeValue(user, map[1],1,map);
            } else {
                throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                        .getAccessManagerAttributeName());
            }
        } else {
            throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + attr
                    .getAccessManagerAttributeName());
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

    private WebResourceAttribute fromAttributeMap(AttributeMap attribute) {
        WebResourceAttribute webAttr = new WebResourceAttribute();
        webAttr.setTargetAttributeName(attribute.getTargetAttributeName());
        webAttr.setAmAttributeName(attribute.getAccessManagerAttributeName());
        webAttr.setResourceId(attribute.getResourceId());
        webAttr.setAttributeMapId(attribute.getAttributeMapId());
        webAttr.setAmPolicyUrl(attribute.getPolicyUrl());
        return webAttr;
    }


    private AttributeMap fromEntity(WebResourceAttribute attributeMap) {
        AttributeMap attribute = new AttributeMap();
        attribute.setAttributeMapId(attributeMap.getAttributeMapId());
        attribute.setResourceId(attributeMap.getResourceId());
        attribute.setTargetAttributeName(attributeMap.getTargetAttributeName());
        attribute.setAccessManagerAttributeName(attributeMap.getAmAttributeName());
        attribute.setPolicyUrl(attributeMap.getAmPolicyUrl());
        return attribute;
    }
}
