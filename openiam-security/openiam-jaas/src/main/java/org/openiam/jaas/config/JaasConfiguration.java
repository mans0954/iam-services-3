package org.openiam.jaas.config;


import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class JaasConfiguration {
    private static final Logger log = Logger.getLogger(JaasConfiguration.class);
    private static JaasConfiguration jaasConfiguration=null;

    private static final String SERVICE_BASE_URL_KEY = "openiam.service_base_url";
    private static final String MANAGE_SYS_ID_KEY = "openiam.managedSysId";

    Properties properties = null;

    private  JaasConfiguration() {}
    private  JaasConfiguration(String propertiesFile) {
        loadProperties(propertiesFile);
    }


    public static JaasConfiguration getInstance() {
        return  getInstance("iam_client_config.properties");
    }

    public static JaasConfiguration getInstance(String properties){
        if(jaasConfiguration==null)
             jaasConfiguration= new  JaasConfiguration(properties);
        return  jaasConfiguration;
    }

    public String getServiceBaseUrl(){
        return this.getProperty(SERVICE_BASE_URL_KEY);
    }
    public String getManageSysId(){
        return this.getProperty(MANAGE_SYS_ID_KEY);
    }
    public String getProperty(String propertyKey){
        return this.properties.getProperty(propertyKey,"");
    }

    private void loadProperties(String propertiesFile) {
        this.properties = new Properties();
        try{
            InputStream propertyStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
            if(propertyStream==null){
                propertyStream = new FileInputStream(propertiesFile);
                if(propertyStream==null)
                    log.warn("Cannot load properties for JAAS");
            }

            this.properties.load(propertyStream);
            if(this.properties.isEmpty())
                log.warn("Cannot load properties for JAAS. There are no loaded properties");

        } catch (Exception ex){
            log.error("Error: " + ex.getMessage());
        }
    }

}
