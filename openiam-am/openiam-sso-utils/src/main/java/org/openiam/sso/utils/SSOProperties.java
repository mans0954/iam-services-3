package org.openiam.sso.utils;

import org.openiam.sso.constant.SSOPropertiesKey;

import java.util.EnumMap;

/**
 * Created by: Alexander Duckardt
 * Date: 20.09.12
 */
public class SSOProperties {
    private EnumMap<SSOPropertiesKey, Object> parameters = new EnumMap<SSOPropertiesKey, Object>(SSOPropertiesKey.class);

    public SSOProperties setAttribute(SSOPropertiesKey key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public Object getAttribute(SSOPropertiesKey key) {
        return parameters.get(key);
    }

    public Object popAttribute(SSOPropertiesKey key) {
        return parameters.remove(key);
    }

    public void clear() {
        parameters.clear();
    }

    public boolean isExists(){
        return parameters!=null && !parameters.isEmpty();
    }
}
