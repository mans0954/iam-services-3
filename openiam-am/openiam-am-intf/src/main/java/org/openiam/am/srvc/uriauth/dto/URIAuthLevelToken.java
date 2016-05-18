package org.openiam.am.srvc.uriauth.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIAuthLevelToken", propOrder = {
	"authLevelId",
	"attributes"
})
public class URIAuthLevelToken implements Serializable {

	private String authLevelId;
	private List<URIAuthLevelAttribute> attributes;
	
	public URIAuthLevelToken() {
		
	}

	public String getAuthLevelId() {
		return authLevelId;
	}

	public void setAuthLevelId(String authLevelId) {
		this.authLevelId = authLevelId;
	}

	public List<URIAuthLevelAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<URIAuthLevelAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(final URIAuthLevelAttribute attribute) {
		if(attribute != null) {
			if(this.attributes == null) {
				this.attributes = new LinkedList<URIAuthLevelAttribute>();
			}
			this.attributes.add(attribute);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((authLevelId == null) ? 0 : authLevelId.hashCode());
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
		URIAuthLevelToken other = (URIAuthLevelToken) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (authLevelId == null) {
			if (other.authLevelId != null)
				return false;
		} else if (!authLevelId.equals(other.authLevelId))
			return false;
		return true;
	}
	
	
}
