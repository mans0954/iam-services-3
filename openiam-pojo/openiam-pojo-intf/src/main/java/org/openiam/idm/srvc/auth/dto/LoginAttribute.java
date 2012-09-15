package org.openiam.idm.srvc.auth.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


/**
 * Attributes of a Login object.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoginAttribute", propOrder = {
        "loginAttrId",
        "domainId",
        "login",
        "name",
        "value",
        "metadataId",
        "attrGroup"
})
public class LoginAttribute implements java.io.Serializable {


    protected String loginAttrId;
    protected String domainId;
    protected String login;

    protected String name;
    protected String value;
    protected String metadataId;
    protected String attrGroup;


    public LoginAttribute() {
    }


    public LoginAttribute(String loginAttrId) {
        this.loginAttrId = loginAttrId;
    }

    public LoginAttribute(String loginAttrId, String name, String value, String metadataId,
                          String serviceId, String login, String attrGroup) {
        this.loginAttrId = loginAttrId;
        this.name = name;
        this.value = value;
        this.metadataId = metadataId;
        this.domainId = serviceId;
        this.login = login;
        this.attrGroup = attrGroup;
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


    public String getLogin() {
        return login;
    }


    public void setLogin(String login) {
        this.login = login;
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


    public String getDomainId() {
        return domainId;
    }


    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loginAttrId == null) ? 0 : loginAttrId.hashCode());
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
		LoginAttribute other = (LoginAttribute) obj;
		if (attrGroup == null) {
			if (other.attrGroup != null)
				return false;
		} else if (!attrGroup.equals(other.attrGroup))
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (loginAttrId == null) {
			if (other.loginAttrId != null)
				return false;
		} else if (!loginAttrId.equals(other.loginAttrId))
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
				.format("LoginAttribute [loginAttrId=%s, domainId=%s, login=%s, name=%s, value=%s, metadataId=%s, attrGroup=%s]",
						loginAttrId, domainId, login, name, value, metadataId,
						attrGroup);
	}
}


