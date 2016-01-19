package org.openiam.idm.srvc.meta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 30/12/15.
 */
@Component
public class TemplateObjectHelper {

    @Autowired
    @Qualifier("groupTemplateProvider")
    private TemplateObjectProvider groupTemplateProvider;
    @Autowired
    @Qualifier("useTemplateProvider")
    private TemplateObjectProvider useTemplateProvider;


    public TemplateObjectProvider getProvider(String templateType){
        if("USER_TEMPLATE".equals(templateType)){
            return useTemplateProvider;
        } else if("GROUP_TEMPLATE".equals(templateType)){
            return groupTemplateProvider;
        } else {
            return null;
        }
    }

}
