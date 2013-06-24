package org.openiam.am.srvc.service;

import java.lang.reflect.Field;
import java.util.EnumMap;

import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
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

	@Override
	@Transactional
	public String process(final String amAttributeId, final String userId) throws Exception {
		LoginEntity identityObject = loginManager.getPrimaryIdentity(userId);
		final UserEntity user = userManager.getUser(userId);
		
		EnumMap<AmAttributes, Object> objectMap = new EnumMap<AmAttributes, Object>(AmAttributes.class);
        objectMap.put(AmAttributes.Login, identityObject);
        objectMap.put(AmAttributes.User, user);
        return process(amAttributeId, objectMap);
	}

	
	@Override
	public String process(String amAttributeId, EnumMap<AmAttributes, Object> objectMap) throws Exception {
		if (amAttributeId == null) {
            throw new NullPointerException("AccessManagerAttributeName is null");
        }
		
		String attrValue = "";
        final String[] map = amAttributeId.split("\\.");

        if (map != null && map.length > 1) {
        	try{
        		attrValue = getAttributeValue(objectMap.get(AmAttributes.valueOf(map[0])), map,1);
        	} catch (IllegalArgumentException ex){
        		throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + amAttributeId);
        	}
        } else {
        	throw new IllegalStateException("Cannot parse object type from AccessManagerAttributeName: " + amAttributeId);
        }
        return attrValue;
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
}
