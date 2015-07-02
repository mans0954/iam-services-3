package org.openiam.idm.srvc.auth.service;

import java.lang.reflect.Field;
import java.util.EnumMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.base.KeyDTO;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("authAttributeProcessor")
public class AuthAttributeProcessorImpl implements AuthAttributeProcessor {

    @Autowired
    private LoginDataService loginManager;

    @Autowired
    private UserDataService userManager;
    private static final Log log = LogFactory.getLog(AuthAttributeProcessorImpl.class);

    @Override
    @Transactional
    public String process(final String reflectionKey, final String userId, final String managedSysId) throws Exception {
        if (userId != null) {
            LoginEntity identityObject = loginManager.getByUserIdManagedSys(userId, managedSysId);
            final UserEntity user = userManager.getUser(userId);

            EnumMap<AmAttributes, Object> objectMap = new EnumMap<AmAttributes, Object>(AmAttributes.class);
            objectMap.put(AmAttributes.Login, identityObject);
            objectMap.put(AmAttributes.User, user);
            return process(reflectionKey, objectMap);
        } else {
            return "";
        }
    }


    @Override
    public boolean process(String reflectionKey, Object object, Object setValue) throws Exception {
        if (reflectionKey == null) {
            throw new NullPointerException("AccessManagerAttributeName is null");
        }

        boolean result;
        final String[] map = reflectionKey.split("\\.");

        if (map != null && map.length > 1) {
            try {
                result = setAttributeValue(object, map, 1, setValue);
            } catch (Exception ex) {
                throw new Exception("Cannot set with key: " + reflectionKey);
            }
        } else {
            throw new Exception("Cannot set with key:  " + reflectionKey);
        }
        return result;
    }

    @Override
    public String process(String reflectionKey, EnumMap<AmAttributes, Object> objectMap) throws Exception {
        if (reflectionKey == null) {
            throw new NullPointerException("AccessManagerAttributeName is null");
        }

        String attrValue = "";
        final String[] map = reflectionKey.split("\\.");

        if (map != null && map.length > 1) {
            try {
                attrValue = getAttributeValue(objectMap.get(AmAttributes.valueOf(map[0])), map, 1);
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + reflectionKey);
            }
        } else {
            throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + reflectionKey);
        }
        return attrValue;
    }

    private String getAttributeValue(Object obj, String[] map, int currentMapIndex) throws Exception {
        String result = "";
        if (obj == null)
            return result;

        if (currentMapIndex == map.length - 1) {
            // get field value
            Object res = getFieldValue(obj, map[currentMapIndex]);
            if (res != null) {
                result = res.toString();
                if (map[currentMapIndex].contains("password")) {
                    String userId = (String) getFieldValue(obj, "userId");
                    result = loginManager.decryptPassword(userId, result);
                }
            }
        } else {
            // try to find subfield
            Object subField = getFieldValue(obj, map[currentMapIndex]);
            if (subField == null)
                throw new NullPointerException("The attribute mapping doesn't exist.");
            int nextMapIndex = currentMapIndex + 1;
            result = getAttributeValue(subField, map, nextMapIndex);
        }
        return result;
    }


    private boolean setAttributeValue(Object obj, String[] map, int currentMapIndex, Object value) throws Exception {
        boolean isSuccess = false;
        if (obj == null)
            return isSuccess;
        isSuccess = setFieldValue(obj, map[currentMapIndex], value);
        return isSuccess;
    }

    private boolean setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        boolean isSuccess = false;
        Class objClass = obj.getClass();
        if (!StringUtils.hasText(fieldName))
            throw new NoSuchFieldException("Field Name is null or empty");
        Field f = null;
        do {
            try {
                f = objClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                log.debug(String.format("No such field %s in Class %s", fieldName, objClass.getCanonicalName()));
            } finally {
                objClass = objClass.getSuperclass();
            }
        } while (f == null && objClass.getCanonicalName().startsWith("org.openiam"));
        if (f == null) {
            throw new NoSuchFieldException(String.format("No such field %s in Class %s", fieldName, objClass.getCanonicalName()));
        }
        //try to find in super class
        f.setAccessible(true);
        try {
            f.set(obj, value);
            isSuccess = true;
        } catch (IllegalArgumentException e) {
            log.error(String.format("Can't put data type %s to field %s with dataType %s", value.getClass().getCanonicalName(),
                    f.getName(), f.getType().getCanonicalName()));
        }
        return isSuccess;
    }

    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        Class objClass = obj.getClass();
        if (!StringUtils.hasText(fieldName))
            throw new NoSuchFieldException("Field Name is null or empty");
        Field f = null;
        do {
            try {
                f = objClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                log.debug(String.format("No such field %s in Class %s", fieldName, objClass.getCanonicalName()));
            } finally {
                objClass = objClass.getSuperclass();
            }
        } while (f == null && objClass.getCanonicalName().startsWith("org.openiam"));
        if (f == null) {
            throw new NoSuchFieldException(String.format("No such field %s in Class %s", fieldName, objClass.getCanonicalName()));
        }
        //try to find in super class
        f.setAccessible(true);
        return f.get(obj);
    }
}
