package org.openiam.idm.srvc.key.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;

/**
 * Created by: Alexander Duckardt
 * Date: 09.10.12
 */
@Service
public class KeyManagementServiceImpl implements KeyManagementService{
    static protected ResourceBundle securityProperties = ResourceBundle.getBundle("securityconf");
    private String jksFile;

    @PostConstruct
    public void init(){
        this.jksFile = securityProperties.getString("jks.file");
        if(!StringUtils.hasText(this.jksFile)){
            this.jksFile = System.getProperty("java.home");
        }
    }


    @Override
    public String getUserKey(String userId, String keyName) throws Exception {
        return null;
    }

    @Override
    public Long refreshKeys() throws Exception {
        return null;
    }

    @Override
    public Long refreshUserKey(String userId) throws Exception {
        return null;
    }

    @Override
    public void generateUserKeys(String userId) throws Exception {

    }
}
