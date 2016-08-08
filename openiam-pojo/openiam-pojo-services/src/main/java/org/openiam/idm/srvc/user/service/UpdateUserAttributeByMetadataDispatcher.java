package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.mq.dispatcher.UpdateAttributeByMetadataDispatcher;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 29/07/16.
 */
@Component
public class UpdateUserAttributeByMetadataDispatcher extends UpdateAttributeByMetadataDispatcher {
    @Autowired
    private UserDataService userManager;

    protected void process(UpdateAttributeByMetadataRequest request) {
        try{
            UserSearchBean searchBean = new UserSearchBean();
            searchBean.setUserType(request.getMetadataTypeId());
            List<UserEntity> userList = userManager.findBeans(searchBean, -1, -1);
            if (CollectionUtils.isNotEmpty(userList)) {
                for (UserEntity user : userList) {
                    Map<String, UserAttributeEntity> userAttributes = user.getUserAttributes();
                    if (userAttributes == null) {
                        userManager.addAttribute(buildUserAttribute(user, request));
                    } else {
                        boolean isFound = false;
                        for (String key : userAttributes.keySet()) {
                            UserAttributeEntity attr = userAttributes.get(key);
                            if (attr != null
                                    && request.getMetadataElementId().equals(attr.getMetadataElementId())) {
                                isFound = true;
                                if (StringUtils.isBlank(attr.getValue())
                                        && CollectionUtils.isEmpty(attr.getValues())) {
                                    attr.setValue(request.getDefaultValue());
                                    attr.setUser(user);
                                    attr.setMetadataElementId(request.getMetadataElementId());
                                    userManager.updateAttribute(attr);
                                }
                            }
                        }
                        if (!isFound) {
                            userManager.addAttribute(buildUserAttribute(user, request));
                        }
                    }
                }
            }
        } catch (BasicDataServiceException e){
            log.error(e.getMessage(), e);
        }
    }

    public UserAttributeEntity buildUserAttribute(UserEntity user, UpdateAttributeByMetadataRequest request){
        UserAttributeEntity attribute = new UserAttributeEntity();
        attribute.setUser(user);

        if(request!=null){
            attribute.setMetadataElementId(request.getMetadataElementId());
            attribute.setName(request.getName());
            attribute.setValue(StringUtils.isNotBlank(request.getDefaultValue()) ? request.getDefaultValue():null);
        }
        return attribute;
    }
}
