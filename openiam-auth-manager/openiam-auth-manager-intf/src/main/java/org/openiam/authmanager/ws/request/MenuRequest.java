package org.openiam.authmanager.ws.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuRequest", propOrder = {
        "menuRoot"
})
public class MenuRequest extends UserRequest {

	private static final long serialVersionUID = -1L;
	
	private String menuRoot;

	public String getMenuRoot() {
		return menuRoot;
	}

	public void setMenuRoot(String menuRoot) {
		this.menuRoot = menuRoot;
	}

	@Override
	public String toString() {
		return String.format("MenuRequest [menuRoot=%s, toString()=%s]",
				menuRoot, super.toString());
	}
	
	
}
