package org.openiam.authmanager.ws.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuRequest", propOrder = {
        "menuRoot",
        "menuName"
})
public class MenuRequest extends UserRequest {

	private static final long serialVersionUID = -1L;
	
	private String menuRoot;
	private String menuName;

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

	@Override
	public String toString() {
		return String.format("MenuRequest [menuRoot=%s, menuName=%s, toString()=%s]", menuRoot, menuName, super.toString());
	}
	
	
}
