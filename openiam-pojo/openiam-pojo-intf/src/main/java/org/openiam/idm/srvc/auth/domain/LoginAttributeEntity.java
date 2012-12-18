package org.openiam.idm.srvc.auth.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.LoginAttribute;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11

@Entity
@Table(name="LOGIN_ATTRIBUTE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(LoginAttribute.class)
public class LoginAttributeEntity implements java.io.Serializable {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="LOGIN_ATTR_ID", length=32, nullable = false)
    protected String loginAttrId;
    
    @Column(name="NAME",length = 20)
    protected String name;
    
    @Column(name="VALUE")
    protected String value;
    
    @Column(name="METADATA_ID",length = 20)
    protected String metadataId;
    
    @Column(name="ATTR_GROUP",length = 20)
    protected String attrGroup;
    
    @Column(name="LOGIN_ID",length = 32)
    private String loginId;

    public LoginAttributeEntity() {
    }


    public LoginAttributeEntity(String loginAttrId) {
        this.loginAttrId = loginAttrId;
    }

    public String getLoginAttrId() {
        return this.loginAttrId;
    }

    public void setLoginAttrId(String loginAttrId) {
        this.loginAttrId = loginAttrId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMetadataId() {
        return this.metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }


    public String getAttrGroup() {
        return attrGroup;
    }


    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }


	public String getLoginId() {
		return loginId;
	}


	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrGroup == null) ? 0 : attrGroup.hashCode());
		result = prime * result
				+ ((loginAttrId == null) ? 0 : loginAttrId.hashCode());
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
		result = prime * result
				+ ((metadataId == null) ? 0 : metadataId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		LoginAttributeEntity other = (LoginAttributeEntity) obj;
		if (attrGroup == null) {
			if (other.attrGroup != null)
				return false;
		} else if (!attrGroup.equals(other.attrGroup))
			return false;
		if (loginAttrId == null) {
			if (other.loginAttrId != null)
				return false;
		} else if (!loginAttrId.equals(other.loginAttrId))
			return false;
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		if (metadataId == null) {
			if (other.metadataId != null)
				return false;
		} else if (!metadataId.equals(other.metadataId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return String
				.format("LoginAttributeEntity [loginAttrId=%s, name=%s, value=%s, metadataId=%s, attrGroup=%s, loginId=%s]",
						loginAttrId, name, value, metadataId, attrGroup,
						loginId);
	}

    
}


