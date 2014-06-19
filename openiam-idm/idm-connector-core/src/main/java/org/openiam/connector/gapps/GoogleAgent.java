package org.openiam.connector.gapps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
	private static final String APP_URL_GROUP = "https://apps-apis.google.com/a/feeds/group/2.0/";
	private static final String APP_URL_USER = "https://apps-apis.google.com/a/feeds/user/2.0/";
	private static final String APP_URL_OU = "https://apps-apis.google.com/a/feeds/orguser/2.0/";
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

	// get ALL USERS
	public List<GenericEntry> getAllUsers(String adminEmail, String password,
			String domain) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return this.getAllUsers(this.getService(adminEmail, password, domain),
				domain);
	}

	public List<GenericEntry> getAllUsers(AppsPropertyService service,
			String domain) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return retrieveAllPages(service, new URL(APP_URL_USER + domain));
	}

	// GET ALL GROUPS
	public List<GenericEntry> getAllGroup(String adminEmail, String password,
			String domain) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return this.getAllGroup(this.getService(adminEmail, password, domain),
				domain);
	}

	public List<GenericEntry> getAllGroup(AppsPropertyService service,
			String domain) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return retrieveAllPages(service, new URL(APP_URL_GROUP + domain));
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

	public GenericEntry getEntity(String adminEmail, String password,
			String domain, String entity, String URL)
			throws AppsForYourDomainException, MalformedURLException,
			IOException, ServiceException {
		if (!StringUtils.hasText(entity))
			return null;
		String id = entity;
		return this.getService(adminEmail, password, domain).getEntry(
				new URL(URL + domain + "/"
						+ GoogleUtils.makeGoogleId(id.toLowerCase(), domain)),
				GenericEntry.class);
	}

	public GenericEntry getUser(String adminEmail, String password,
			String domain, String email) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return this
				.getEntity(adminEmail, password, domain, email, APP_URL_USER);
	}

	public GenericEntry getGroup(String adminEmail, String password,
			String domain, String group) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		return this.getEntity(adminEmail, password, domain, group,
				APP_URL_GROUP);
	}

	private GenericEntry retrieveCustomerId(AppsPropertyService service,
			String domain) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry entry = service
				.getEntry(
						new URL(
								"https://apps-apis.google.com/a/feeds/customer/2.0/customerId"),
						GenericEntry.class);
		return entry;
	}

	public String addUser(String adminEmail, String password, String domain,
			Map<String, String> googleUserProps)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry entry = new GenericEntry();
		entry.addProperties(googleUserProps);
		entry.addProperty("isAdmin", "false");
		entry.addProperty("isSuspended", "false");

		GenericEntry newE = this.getService(adminEmail, password, domain)
				.insert(new URL(APP_URL_USER + domain), entry);
		if (googleUserProps.get("groups") != null)
			log.info("Google connector add run:"
					+ newE.getAllProperties().get("userEmail"));
		return newE.getAllProperties().get("userEmail");
	}

	public void addGroup(String adminEmail, String password, String domain,
			Map<String, String> googleGroupProps)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry entry = new GenericEntry();
		entry.addProperty("emailPermission", "Anyone");
		entry.addProperties(googleGroupProps);
		GenericEntry newG = this.getService(adminEmail, password, domain)
				.insert(new URL(APP_URL_GROUP + domain), entry);
		log.info("Google connector add run:"
				+ newG.getAllProperties().get("groupId"));
	}

	public void addUserToGroup(String adminEmail, String password,
			String domain, String groupId, String userId)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry user = this.getUser(adminEmail, password, domain, userId);
		user.addProperty("memberId", userId);
		this.getService(adminEmail, password, domain).insert(
				new URL("APP_URL_GROUP" + domain + "/" + groupId + "/member"),
				user);
	}

	public void deleteUserFromGroup(String adminEmail, String password,
			String domain, String groupId, String userId)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		this.getService(adminEmail, password, domain).delete(
				new URL("APP_URL_GROUP" + domain + "/" + groupId + "/member/"
						+ userId));
	}

	public List<GenericEntry> retrieveAllOrganizationUsersByOrgUnit(
			String adminEmail, String password, String domain,
			String orgUnitPath) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		AppsPropertyService service = this.getService(adminEmail, password,
				domain);
		String URL = APP_URL_OU
				+ this.retrieveCustomerId(service, domain).getProperty(
						"customerId") + "?get=children&orgUnitPath="
				+ URLEncoder.encode(orgUnitPath, "UTF-8");

		List<GenericEntry> allEntries = new ArrayList<GenericEntry>();
		URL feedURL = new URL(URL);
		do {
			GenericFeed feed = service.getFeed(feedURL, GenericFeed.class);
			allEntries.addAll(feed.getEntries());
			feedURL = (feed.getNextLink() == null) ? null : new URL(feed
					.getNextLink().getHref());
		} while (feedURL != null);
		List<GenericEntry> resultList = null;
		if (allEntries != null) {
			resultList = new ArrayList<>();
			for (GenericEntry e : allEntries) {
				resultList.add(this.getUser(adminEmail, password, domain,
						e.getProperty("orgUserEmail")));
			}
		}
		return resultList;
	}

	public void updateUser(String adminEmail, String password, String domain,
			Map<String, String> googleUserProps, String id)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry entry = new GenericEntry();
		entry.addProperties(googleUserProps);
		GenericEntry newE = this.getService(adminEmail, password, domain)
				.update(new URL(APP_URL_USER + domain + "/"
						+ GoogleUtils.makeGoogleId(id.toLowerCase(), domain)),
						entry);
		log.info("Google connector update run:"
				+ newE.getAllProperties().get("userEmail"));
	}

	public void updateGroup(String adminEmail, String password, String domain,
			Map<String, String> googleGroupProps, String id)
			throws AppsForYourDomainException, AuthenticationException,
			MalformedURLException, IOException, ServiceException {
		GenericEntry entry = new GenericEntry();
		entry.addProperties(googleGroupProps);
		GenericEntry newE = this.getService(adminEmail, password, domain)
				.update(new URL(APP_URL_GROUP + domain + "/"
						+ GoogleUtils.makeGoogleId(id.toLowerCase(), domain)),
						entry);
		log.info("Google connector update run:"
				+ newE.getAllProperties().get("groupId"));
	}

	public void deleteUser(String adminEmail, String password, String domain,
			String email) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		this.getService(adminEmail, password, domain)
				.delete(new URL(APP_URL_USER + domain + "/"
						+ GoogleUtils.makeGoogleId(email.toLowerCase(), domain)));
	}

	public void deleteGroup(String adminEmail, String password, String domain,
			String groupName) throws AppsForYourDomainException,
			MalformedURLException, IOException, ServiceException {
		this.getService(adminEmail, password, domain).delete(
				new URL(APP_URL_GROUP
						+ domain
						+ "/"
						+ GoogleUtils.makeGoogleId(groupName.toLowerCase(),
								domain)));
	}

	// test for groups
	// public static void main(String[] args) throws Exception {
	// GoogleAgent a = new GoogleAgent();
	// String adminEmail = "dmitry.zaporozhec@openiam.com";
	// String password = "12#DoomiDDqD";
	// String domain = "openiam.com";
	//
	// // System.out.println("------- 1. Get All groups");
	// // List<GenericEntry> groups = a.getAllUsers(adminEmail, password,
	// // domain);
	// // for (GenericEntry g : groups) {
	// // System.out.println(g.getAllProperties());
	// // }
	//
	// // System.out.println("------- 2. ADD NEW GROUP");
	// // Map<String, String> newGroup = new HashMap<String, String>();
	// // newGroup.put("groupName", "testZ");
	// // newGroup.put("description", "test group from " + adminEmail);
	// // newGroup.put("groupId", "testZ@" + domain);
	// // a.addGroup(adminEmail, password, domain, newGroup);
	// // a.deleteUserFromGroup(adminEmail, password, domain,
	// // "testZ@openiam.com", "dmitry.zaporozhec");
	// // a.addUserToGroup(adminEmail, password, domain, "testZ@openiam.com",
	// // "dmitry.zaporozhec");
	// // System.out.println("------- 3. Get All groups");
	// // groups = a.getAllGroup(adminEmail, password, domain);
	// // for (GenericEntry g : groups) {
	// // System.out.println(g.getAllProperties());
	// // }
	// //
	// // System.out.println("------- 4. Get MY groups");
	// // GenericEntry group = a.getGroup(adminEmail, password, domain,
	// // "testZ");
	// // System.out.println(group.getAllProperties());
	// //
	// // System.out.println("------- 5. DELETE NEW GROUP");
	// // a.deleteGroup(adminEmail, password, domain, "testZ");
	// //
	// // System.out.println("------- 6. Get All groups");
	// // groups = a.getAllGroup(adminEmail, password, domain);
	// // for (GenericEntry g : groups) {
	// // System.out.println(g.getAllProperties());
	// // }
	// }
}
