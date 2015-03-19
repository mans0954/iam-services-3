package org.openiam.provision.service;

import org.mule.api.MuleContext;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.dto.PasswordSync;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Interface which all post process scripts should implement
 */
public interface PostProcessor <T> {
    int add(T object, Map<String, Object> bindingMap,boolean success);
    int modify(T object, Map<String, Object> bindingMap,boolean success);
    int delete(T object, Map<String, Object> bindingMap,boolean success);

    int disable(T object,Map<String, Object> bindingMap,boolean success);

    int setPassword(PasswordSync passwordSync, Map<String, Object> bindingMap,boolean success);
    int resetPassword(PasswordSync passwordSync, Map<String, Object> bindingMap,boolean success);

    int lookupRequest(SearchResponse request);

    void setMuleContext(MuleContext ctx);
    void setApplicationContext(ApplicationContext ctx);
}
