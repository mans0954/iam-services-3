package org.openiam.idm.srvc.pswd.dto;

// Generated Jan 23, 2010 1:06:13 AM by Hibernate Tools 3.2.2.GA

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;

import java.util.Date;

/**
 * Password history object
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordHistory", propOrder = {
        "pwdHistoryId",
        "loginId",
        "dateCreated",
        "password"
})
@DozerDTOCorrespondence(PasswordHistoryEntity.class)
public class PasswordHistory implements java.io.Serializable {

    private String pwdHistoryId;
    private String loginId;
    @XmlSchemaType(name = "dateTime")
    private Date dateCreated;
    private String password;

    public PasswordHistory() {
    }

    public String getPwdHistoryId() {
        return this.pwdHistoryId;
    }

    public void setPwdHistoryId(String pwdHistoryId) {
        this.pwdHistoryId = pwdHistoryId;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
}
