package org.openiam.am.srvc.service;

import java.util.Map;

public interface AuthResourceAttributeMapper {
    String mapAttribute();
    void init(Map<String, Object> bindingMap);
}
