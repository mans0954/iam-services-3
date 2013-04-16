package org.openiam.idm.srvc.user.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageTempate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserProfileRequestModel", 
	propOrder = { 
		"user",
        "pageTemplate",
        "languageCode",
        "languageId",
        "locale",
        "emails",
        "phones",
        "addresses"
})
public class UserProfileRequestModel {

	private List<EmailAddress> emails;
	private List<Phone> phones;
	private List<Address> addresses;
	private User user;
	private PageTempate pageTemplate;
	private String languageCode;
	private String locale;
	private String languageId;
	
	public UserProfileRequestModel() {
		
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public PageTempate getPageTemplate() {
		return pageTemplate;
	}
	public void setPageTemplate(PageTempate pageTemplate) {
		this.pageTemplate = pageTemplate;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public List<EmailAddress> getEmails() {
		return emails;
	}

	public void setEmails(List<EmailAddress> emails) {
		this.emails = emails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	@Override
	public String toString() {
		return "UserProfileRequestModel [emails=" + emails + ", phones="
				+ phones + ", addresses=" + addresses + ", user=" + user
				+ ", pageTemplate=" + pageTemplate + ", languageCode="
				+ languageCode + ", locale=" + locale + ", languageId="
				+ languageId + "]";
	}

	
}
