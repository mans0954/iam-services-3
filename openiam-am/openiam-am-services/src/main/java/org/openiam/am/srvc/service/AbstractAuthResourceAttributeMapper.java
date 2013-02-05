package org.openiam.am.srvc.service;

import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public abstract class AbstractAuthResourceAttributeMapper implements AuthResourceAttributeMapper {
    private Map<String, UserAttributeEntity> userAttributeMap;
    private ApplicationContext applicationContext;

    @Override
    public void init(Map<String, Object> bindingMap){
        this.userAttributeMap = (Map<String, UserAttributeEntity>)bindingMap.get("userAttributeMap");
        this.applicationContext = (ApplicationContext)bindingMap.get("applicationContext");
    }
    @Override
    public String mapAttribute(){
        String attributeValue = "";
        System.out.println("Getting attribute with name:" + this.getAttributeName());
        UserAttributeEntity attribute = userAttributeMap.get(this.getAttributeName());
        if(attribute!=null && attribute.getValue()!=null && !attribute.getValue().trim().isEmpty()){
            System.out.println("Attribute found");
            attributeValue = mapValue(attribute.getValue());
        }   else{
            System.out.println("Attribute not found");
        }
        return attributeValue;
    }

    protected abstract String mapValue(String value);

    protected abstract String getAttributeName();
}
