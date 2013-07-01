package org.openiam.spml2.spi.gapps.command.base;

import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.AbstractCommand;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public abstract class AbstractGoogleAppsCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    @Value("${KEYSTORE}")
    private String trustStore;

    @Value("${KEYSTORE_PSWD}")
    private String trustStorePassword;

    protected static final String APPS_FEEDS_URL_BASE = "https://apps-apis.google.com/a/feeds/";

    protected static final String GOOGLE_APPS_USER_SERVICE ="gdata-sample-AppsForYourDomain-UserService";

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Autowired
    private KeyManagementService keyManagementService;

    public void init() {
        String filename = System.getProperty("java.home")
                + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println("filenname=" + filename);
        String password = "changeit";
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    protected String getDecryptedPassword(String userId, String encPwd) throws ConnectorDataException{
        String result = null;
        if(encPwd!=null){
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(userId, KeyName.password.name()), encPwd);
            } catch (Exception e) {
                log.error(e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
        return null;
    }
}
