package org.openiam.idm.srvc.secdomain.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;

@Entity
@Table(name = "SECURITY_DOMAIN")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SecurityDomain.class)
public class SecurityDomainEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "DOMAIN_ID", length = 32)
    private String domainId;
	
	@Column(name = "NAME", length = 40)
    private String name;
	
	@Column(name = "STATUS", length = 20)
    private String status;
    
    @Column(name = "LOGIN_MODULE", length = 100)
    private String defaultLoginModule;
    
    @Column(name = "AUTH_SYS_ID", length = 20)
    private String authSysId;
    
    @Column(name = "PASSWORD_POLICY", length = 20)
    private String passwordPolicyId;
    
    @Column(name = "AUTHENTICATION_POLICY", length = 20)
    private String authnPolicyId;
    
    @Column(name = "AUDIT_POLICY", length = 20)
    private String auditPolicyId;
    
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDefaultLoginModule() {
		return defaultLoginModule;
	}
	public void setDefaultLoginModule(String defaultLoginModule) {
		this.defaultLoginModule = defaultLoginModule;
	}
	public String getAuthSysId() {
		return authSysId;
	}
	public void setAuthSysId(String authSysId) {
		this.authSysId = authSysId;
	}
	public String getPasswordPolicyId() {
		return passwordPolicyId;
	}
	public void setPasswordPolicyId(String passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
	}
	public String getAuthnPolicyId() {
		return authnPolicyId;
	}
	public void setAuthnPolicyId(String authnPolicyId) {
		this.authnPolicyId = authnPolicyId;
	}
	public String getAuditPolicyId() {
		return auditPolicyId;
	}
	public void setAuditPolicyId(String auditPolicyId) {
		this.auditPolicyId = auditPolicyId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((auditPolicyId == null) ? 0 : auditPolicyId.hashCode());
		result = prime * result
				+ ((authSysId == null) ? 0 : authSysId.hashCode());
		result = prime * result
				+ ((authnPolicyId == null) ? 0 : authnPolicyId.hashCode());
		result = prime
				* result
				+ ((defaultLoginModule == null) ? 0 : defaultLoginModule
						.hashCode());
		result = prime * result
				+ ((domainId == null) ? 0 : domainId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((passwordPolicyId == null) ? 0 : passwordPolicyId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		SecurityDomainEntity other = (SecurityDomainEntity) obj;
		if (auditPolicyId == null) {
			if (other.auditPolicyId != null)
				return false;
		} else if (!auditPolicyId.equals(other.auditPolicyId))
			return false;
		if (authSysId == null) {
			if (other.authSysId != null)
				return false;
		} else if (!authSysId.equals(other.authSysId))
			return false;
		if (authnPolicyId == null) {
			if (other.authnPolicyId != null)
				return false;
		} else if (!authnPolicyId.equals(other.authnPolicyId))
			return false;
		if (defaultLoginModule == null) {
			if (other.defaultLoginModule != null)
				return false;
		} else if (!defaultLoginModule.equals(other.defaultLoginModule))
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (passwordPolicyId == null) {
			if (other.passwordPolicyId != null)
				return false;
		} else if (!passwordPolicyId.equals(other.passwordPolicyId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("SecurityDomainEntity [domainId=%s, name=%s, status=%s, defaultLoginModule=%s, authSysId=%s, passwordPolicyId=%s, authnPolicyId=%s, auditPolicyId=%s]",
						domainId, name, status, defaultLoginModule, authSysId,
						passwordPolicyId, authnPolicyId, auditPolicyId);
	}
    
    
}
