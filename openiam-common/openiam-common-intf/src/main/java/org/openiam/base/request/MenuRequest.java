package org.openiam.base.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuRequest", propOrder = {
        "menuRoot",
        "menuName",
		"principalId",
		"principalType"
})
public class MenuRequest extends UserRequest {

	private static final long serialVersionUID = -1L;
	
	private String menuRoot;
	private String menuName;

	private String principalId;
	private String principalType;

	private String url;
	private boolean defaultResult;



	public String getMenuRoot() {
		return menuRoot;
	}

	public void setMenuRoot(String menuRoot) {
		this.menuRoot = menuRoot;
	}
	
	

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDefaultResult() {
		return defaultResult;
	}

	public void setDefaultResult(boolean defaultResult) {
		this.defaultResult = defaultResult;
	}

	@Override
	public String toString() {
		return String.format("MenuRequest [menuRoot=%s, menuName=%s, principalId=%s, principalType=%s, url=%s, defaultResult=%s, toString()=%s]",
				menuRoot, menuName, principalId, principalType, url, String.valueOf(defaultResult), super.toString());
	}
	
	
}
