package org.openiam.connector.gapps.command.base;

import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public abstract class AbstractGoogleAppsCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    @Value("${KEYSTORE}")
    private String trustStore;

    @Value("${KEYSTORE_PSWD}")
    private String trustStorePassword;

    protected static final String APPS_FEEDS_URL_BASE = "https://apps-apis.google.com/a/feeds/";

    protected static final String GOOGLE_APPS_USER_SERVICE ="gdata-sample-AppsForYourDomain-UserService";

    public void init() {
        String filename = System.getProperty("java.home")
                + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println("filenname=" + filename);
        String password = "changeit";
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }


}
