package org.openiam.idm.srvc.user.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.BaseObject;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageTempate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserProfileRequestModel", 
	propOrder = { 
		"activitiRequestType",
		"user",
        "pageTemplate",
        "languageId",
        "emails",
        "phones",
        "addresses"
})
public class UserProfileRequestModel extends BaseObject {

	private ActivitiRequestType activitiRequestType;
	private List<EmailAddress> emails;
	private List<Phone> phones;
	private List<Address> addresses;
	private User user;
	private PageTempate pageTemplate;
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

	public ActivitiRequestType getActivitiRequestType() {
		return activitiRequestType;
	}

	public void setActivitiRequestType(ActivitiRequestType activitiRequestType) {
		this.activitiRequestType = activitiRequestType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((activitiRequestType == null) ? 0 : activitiRequestType
						.hashCode());
		result = prime * result
				+ ((addresses == null) ? 0 : addresses.hashCode());
		result = prime * result + ((emails == null) ? 0 : emails.hashCode());
		result = prime * result
				+ ((languageId == null) ? 0 : languageId.hashCode());
		result = prime * result
				+ ((pageTemplate == null) ? 0 : pageTemplate.hashCode());
		result = prime * result + ((phones == null) ? 0 : phones.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserProfileRequestModel other = (UserProfileRequestModel) obj;
		if (activitiRequestType != other.activitiRequestType)
			return false;
		if (addresses == null) {
			if (other.addresses != null)
				return false;
		} else if (!addresses.equals(other.addresses))
			return false;
		if (emails == null) {
			if (other.emails != null)
				return false;
		} else if (!emails.equals(other.emails))
			return false;
		if (languageId == null) {
			if (other.languageId != null)
				return false;
		} else if (!languageId.equals(other.languageId))
			return false;
		if (pageTemplate == null) {
			if (other.pageTemplate != null)
				return false;
		} else if (!pageTemplate.equals(other.pageTemplate))
			return false;
		if (phones == null) {
			if (other.phones != null)
				return false;
		} else if (!phones.equals(other.phones))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("UserProfileRequestModel [activitiRequestType=%s, emails=%s, phones=%s, addresses=%s, user=%s, pageTemplate=%s, languageId=%s]",
						activitiRequestType, emails, phones, addresses, user,
						pageTemplate, languageId);
	}

	
	
}
