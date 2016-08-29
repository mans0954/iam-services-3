package org.openiam.base.request;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuEntitlementsRequest", propOrder = {
        "principalId",
        "principalType",
        "newlyEntitled",
        "disentitled"
})
public class MenuEntitlementsRequest implements Serializable {

	private String principalId;
	private String principalType;
	private List<String> newlyEntitled;
	private List<String> disentitled;
	
	public String getPrincipalId() {
		return principalId;
	}
	
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
	
	public String getPrincipalType() {
		return principalType;
	}
	
	public void setPrincipalType(String principalType) {
		this.principalType = principalType;
	}
	
	public List<String> getNewlyEntitled() {
		return newlyEntitled;
	}
	
	public void setNewlyEntitled(List<String> newlyEntitled) {
		this.newlyEntitled = newlyEntitled;
	}
	
	public List<String> getDisentitled() {
		return disentitled;
	}
	
	public void setDisentitled(List<String> disentitled) {
		this.disentitled = disentitled;
	}
	
}
