package org.openiam.provision.service;

import java.util.Map;

public interface PrePostExecutor {
    int execute(Map<String, Object> bindingMap);
}
