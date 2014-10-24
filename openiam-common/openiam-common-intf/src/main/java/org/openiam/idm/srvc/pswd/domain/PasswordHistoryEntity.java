package org.openiam.idm.srvc.pswd.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
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

    @ManyToOne
    @JoinColumn(name="LOGIN_ID")
    private LoginEntity login;
	
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

    public LoginEntity getLogin() {
        return login;
    }

    public void setLogin(LoginEntity login) {
        this.login = login;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordHistoryEntity that = (PasswordHistoryEntity) o;

        if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (pwdHistoryId != null ? !pwdHistoryId.equals(that.pwdHistoryId) : that.pwdHistoryId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pwdHistoryId != null ? pwdHistoryId.hashCode() : 0;
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
