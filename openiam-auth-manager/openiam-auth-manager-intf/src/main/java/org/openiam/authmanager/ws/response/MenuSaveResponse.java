package org.openiam.authmanager.ws.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuSaveResponse", propOrder = {
	"menuError",
	"problematicMenuName"
})
public class MenuSaveResponse extends AbstractResponse implements Serializable {
	
	private MenuError menuError;
	private String problematicMenuName;
	
	private static final long serialVersionUID = 1L;

	public MenuError getMenuError() {
		return menuError;
	}

	public void setMenuError(MenuError menuError) {
		this.menuError = menuError;
	}

	public String getProblematicMenuName() {
		return problematicMenuName;
	}

	public void setProblematicMenuName(String problematicMenuName) {
		this.problematicMenuName = problematicMenuName;
	}

	
}
