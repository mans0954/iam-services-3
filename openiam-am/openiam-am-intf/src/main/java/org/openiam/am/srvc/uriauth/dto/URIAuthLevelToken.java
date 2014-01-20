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
}
