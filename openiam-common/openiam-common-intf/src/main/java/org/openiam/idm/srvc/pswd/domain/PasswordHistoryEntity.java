package org.openiam.idm.srvc.pswd.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;

@Entity
@Table(name = "PWD_HISTORY")
@DozerDTOCorrespondence(PasswordHistory.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PasswordHistoryEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "PWD_HISTORY_ID", length = 32, nullable = false)
	private String pwdHistoryId;
	
	@Column(name = "LOGIN_ID", length = 32, nullable = false)
    private String loginId;
	
	/* 
	 * this is a not nullable field.  Triggers not used in order to avoid 
	 * conflicts with DBs that don't support triggers (no-SQL?)
	 */
	@Column(name = "DATE_CREATED", length = 19, nullable=false)
    private Date dateCreated = new Date();
	
	@Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;
    
	public String getPwdHistoryId() {
		return pwdHistoryId;
	}
	public void setPwdHistoryId(String pwdHistoryId) {
		this.pwdHistoryId = pwdHistoryId;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((pwdHistoryId == null) ? 0 : pwdHistoryId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PasswordHistoryEntity other = (PasswordHistoryEntity) obj;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pwdHistoryId == null) {
			if (other.pwdHistoryId != null)
				return false;
		} else if (!pwdHistoryId.equals(other.pwdHistoryId))
			return false;
		return true;
	}
    
    
}
