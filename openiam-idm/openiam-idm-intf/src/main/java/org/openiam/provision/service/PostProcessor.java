package org.openiam.provision.service;

import java.util.Map;

/**
 * Interface which all post process scripts should implement
 */
public interface PostProcessor <T> {
    int add(T object, Map<String, Object> bindingMap,boolean success);
    int modify(T object, Map<String, Object> bindingMap,boolean success);
    int delete(T object, Map<String, Object> bindingMap,boolean success);
    int setPassword( Map<String, Object> bindingMap,boolean success);

}
