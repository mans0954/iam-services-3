package org.openiam.imprt.util;


import org.openiam.imprt.constant.ImportPropertiesKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 10/15/13 Time: 12:48 AM To
 * change this template use File | Settings | File Templates.
 */
public class DataHolder {
    private static volatile DataHolder instance = null;
    private Properties properties = new Properties();

    public static DataHolder getInstance() {
        if (instance == null) {
            synchronized (DataHolder.class) {
                if (instance == null) {
                    instance = new DataHolder();
                }
            }
        }
        return instance;
    }

    public void loadProperties(InputStream stream) {
        try {
            this.properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            stream = null;
        }
    }

    public void loadProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    public void setProperty(ImportPropertiesKey key, Object value) {
        properties.put(key.getPropertyKey(), value);
    }

    public String getProperty(ImportPropertiesKey key) {
        return properties.getProperty(key.getPropertyKey());
    }

    public String getProperty(ImportPropertiesKey key, String defaultValue) {
        return properties.getProperty(key.getPropertyKey(), defaultValue);
    }

    public Properties getAllProperties() {
        return properties;
    }
}
