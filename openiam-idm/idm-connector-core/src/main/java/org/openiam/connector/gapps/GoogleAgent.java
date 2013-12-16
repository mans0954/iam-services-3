package org.openiam.connector.gapps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;

/**
 * @author zaporozhec https://code.google.com/apis/console
 */
public class GoogleAgent {

    private static Credential authorize(FileDataStoreFactory dataStoreFactory,
            HttpTransport httpTransport, JsonFactory JSON_FACTORY,
            String clientSecret) throws Exception {

        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(new FileInputStream(
                        new File(clientSecret))));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret()
                        .startsWith("Enter ")) {
            return null;
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                DirectoryScopes.all()).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow,
                new LocalServerReceiver()).authorize(null);
    }

    public static Directory getClient(String applicationName,
            String clientSecretPath) {
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        /** Directory to store user credentials. */
        java.io.File DATA_STORE_DIR = new java.io.File(
                System.getProperty("user.home"), ".store/admin_sample");

        FileDataStoreFactory dataStoreFactory;

        HttpTransport httpTransport;

        Directory client = null;
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize(dataStoreFactory, httpTransport,
                    JSON_FACTORY, clientSecretPath);

            // set up global Directory instance
            client = new Directory.Builder(httpTransport, JSON_FACTORY,
                    credential).setApplicationName(applicationName).build();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return client;
    }
}
