package org.openiam.am.srvc.service;

import java.util.Map;

public interface AuthResourceAttributeMapper {
    public String mapAttribute();
    public void init(Map<String, Object> bindingMap);
}
