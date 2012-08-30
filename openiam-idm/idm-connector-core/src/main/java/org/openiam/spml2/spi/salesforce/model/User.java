package org.openiam.spml2.spi.salesforce.model;

import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

/*  
 * The User object is described here: http://www.salesforce.com/us/developer/docs/api/Content/sforce_api_objects_user.htm#topic-title
 *  It is up to the customer to conform to the fields in this spec
 */
public class User extends SObject {
	
	private static final String DEFAULT_EMAIL_ENCODING_KEY = "UTF-8";
	private static final String DEFAULT_TIMEZONE = "America/New_York";
	private static final String DEFAULT_GROUP_NOTIFICATION_FREQUENCY = "W";
	private static final String DEFAULT_DIGEST_FREQUENCY = "W";
	private static final String DEFAULT_LOCALE = "en_US";

	public User(final SObject sObject) {
		setDefaults();
		setId(sObject.getId());
		setName(sObject.getName());
		setValue(sObject.getValue());
		for(Iterator<XmlObject> it = sObject.getChildren(); it.hasNext();) {
			final XmlObject node = it.next();
			final String name = node.getName().getLocalPart();
			final Object value = node.getValue();
			setField(name, value);
		}
	}
	
	public User(final String email) {
		setDefaults();
		setUsername(email);
	}
	
	private void setDefaults() {
		setType("User");
		setEmailEncodingKey(DEFAULT_EMAIL_ENCODING_KEY);
		setTimeZone(DEFAULT_TIMEZONE);
		setDefaultGroupNotificationFrequency(DEFAULT_GROUP_NOTIFICATION_FREQUENCY);
		setDefaultDigestFrequency(DEFAULT_DIGEST_FREQUENCY);
		setLanguageLocaleKey(DEFAULT_LOCALE);
		setLocaleSidKey(DEFAULT_LOCALE);
	}
	
	public void setUsername(final String email) {
		setField("Email", StringUtils.trimToNull(email));
		setField("Username", StringUtils.trimToNull(email));
	}
	
	public String getUserName() {
		return (String)getField("Username");
	}
	
	public void setEmailEncodingKey(final String emailEncodingKey) {
		String value = DEFAULT_EMAIL_ENCODING_KEY;
		if(StringUtils.isNotBlank(emailEncodingKey)) {
			value = emailEncodingKey;
		}
		setField("EmailEncodingKey", value);
	}
	
	public void setAlias(final String alias) {
		setField("Alias", StringUtils.substring(alias, 0, 8));
	}
	
	public void setTimeZone(final String timeZone) {
		String value = DEFAULT_TIMEZONE;
		if(StringUtils.isBlank(timeZone)) {
			value = timeZone;
		}
		setField("TimeZoneSidKey", value);
	}
	
	public void setDefaultGroupNotificationFrequency(final String defaultGroupNotificationFrequency) {
		String value = DEFAULT_GROUP_NOTIFICATION_FREQUENCY;
		if(StringUtils.isNotBlank(defaultGroupNotificationFrequency)) {
			value = defaultGroupNotificationFrequency;
		}
		setField("DefaultGroupNotificationFrequency", value);
	}
	
	public void setProfileId(final String profileId) {
		setField("ProfileId", StringUtils.trimToNull(profileId));
	}
	
	public void setDefaultDigestFrequency(final String defaultDigestFrequency) {
		String value = DEFAULT_DIGEST_FREQUENCY;
		if(StringUtils.isNotBlank(defaultDigestFrequency)) {
			value = defaultDigestFrequency;
		}
		setField("DigestFrequency", value);
	}
	
	public void setLastName(final String lastName) {
		setField("LastName", StringUtils.trimToNull(lastName));
	}
	
	public void setLanguageLocaleKey(final String languageLocaleKey) {
		String value = DEFAULT_LOCALE;
		if(StringUtils.isNotBlank(languageLocaleKey)) {
			value = languageLocaleKey;
		}
		setField("LanguageLocaleKey", value);
	}
	
	public void setLocaleSidKey(final String localeSidKey) {
		String value = DEFAULT_LOCALE;
		if(StringUtils.isNotBlank(localeSidKey)) {
			value = localeSidKey;
		}
		setField("LocaleSidKey", value);
	}
	
	public boolean isActive() {
		return Boolean.TRUE.equals(super.getField("IsActive"));
	}
	
	public void setActive(final boolean isActive) {
		setField("IsActive", isActive);
	}
}
