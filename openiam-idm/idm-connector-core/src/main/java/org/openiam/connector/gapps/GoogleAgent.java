package org.openiam.connector.gapps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.google.gdata.client.appsforyourdomain.AppsPropertyService;
import com.google.gdata.client.appsforyourdomain.adminsettings.SingleSignOnService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;
import com.google.gdata.data.appsforyourdomain.generic.GenericFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author zaporozhec https://code.google.com/apis/console
 */
public class GoogleAgent {
    ;
    private static final String APP_URL = "https://apps-apis.google.com/a/feeds/user/2.0/";
    protected final Log log = LogFactory.getLog(this.getClass());

    public AppsPropertyService getService(String adminEmail, String password,
            String domainName) throws AuthenticationException {
        AppsPropertyService service = null;
        if (StringUtils.hasText(adminEmail) && StringUtils.hasText(password)) {
            service = new AppsPropertyService("OPENIAM-GOOGLE-CONNECTOR");
            service.setUserCredentials(adminEmail, password);
        } else if (StringUtils.hasText(domainName)) {
            service = new SingleSignOnService(domainName,
                    "OPENIAM-GOOGLE-CONNECTOR");
            // service.setUserToken("");
        }
        return service;
    }

    public List<GenericEntry> getAllUsers(String adminEmail, String password,
            String domain) throws AppsForYourDomainException,
            MalformedURLException, IOException, ServiceException {
        return this.getAllUsers(this.getService(adminEmail, password, domain),
                domain);
    }

    public List<GenericEntry> getAllUsers(AppsPropertyService service,
            String domain) throws AppsForYourDomainException,
            MalformedURLException, IOException, ServiceException {
        return retrieveAllPages(service, new URL(APP_URL + domain));
    }

    private List<GenericEntry> retrieveAllPages(AppsPropertyService service,
            URL feedUrl) throws IOException, ServiceException {
        List<GenericEntry> allEntries = new ArrayList<GenericEntry>();
        try {
            do {
                GenericFeed feed = service.getFeed(feedUrl, GenericFeed.class);
                allEntries.addAll(feed.getEntries());
                feedUrl = (feed.getNextLink() == null) ? null : new URL(feed
                        .getNextLink().getHref());
            } while (feedUrl != null);
        } catch (ServiceException se) {
            AppsForYourDomainException ae = AppsForYourDomainException
                    .narrow(se);
            throw (ae != null) ? ae : se;
        }
        return allEntries;
    }

    public GenericEntry getUser(String adminEmail, String password,
            String domain, String email) throws AppsForYourDomainException,
            MalformedURLException, IOException, ServiceException {
        return this.getService(adminEmail, password, domain).getEntry(
                new URL(APP_URL + domain + "/" + email + "@" + domain),
                GenericEntry.class);
    }

    public void addUser(String adminEmail, String password, String domain,
            Map<String, String> googleUserProps)
            throws AppsForYourDomainException, AuthenticationException,
            MalformedURLException, IOException, ServiceException {
        GenericEntry entry = new GenericEntry();
        entry.addProperties(googleUserProps);
        entry.addProperty("isAdmin", "false");
        entry.addProperty("isSuspended", "false");

        GenericEntry newE = this.getService(adminEmail, password, domain)
                .insert(new URL(APP_URL + domain), entry);
        log.info("Google connector add run:"
                + newE.getAllProperties().get("userEmail"));
    }

    public void updateUser(String adminEmail, String password, String domain,
            Map<String, String> googleUserProps, String id)
            throws AppsForYourDomainException, AuthenticationException,
            MalformedURLException, IOException, ServiceException {
        GenericEntry entry = new GenericEntry();
        entry.addProperties(googleUserProps);
        GenericEntry newE = this.getService(adminEmail, password, domain)
                .update(new URL(APP_URL + domain + "/" + id + "@" + domain),
                        entry);
        log.info("Google connector update run:"
                + newE.getAllProperties().get("userEmail"));
    }

    public void deleteUser(String adminEmail, String password, String domain,
            String email) throws AppsForYourDomainException,
            MalformedURLException, IOException, ServiceException {
        this.getService(adminEmail, password, domain).delete(
                new URL(APP_URL + domain + "/" + email + "@" + domain));
    }
}
