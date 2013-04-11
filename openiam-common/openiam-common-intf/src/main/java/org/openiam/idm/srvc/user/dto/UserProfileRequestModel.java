package org.openiam.idm.srvc.user.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.dto.PageTempate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserProfileRequestModel", 
	propOrder = { 
		"user",
        "pageTemplate",
        "languageCode",
        "languageId",
        "locale"
})
public class UserProfileRequestModel {

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

	@Override
	public String toString() {
		return "UserProfileRequestModel [user=" + user + ", pageTemplate="
				+ pageTemplate + ", languageCode=" + languageCode + ", locale="
				+ locale + ", languageId=" + languageId + "]";
	}
	
	
}
