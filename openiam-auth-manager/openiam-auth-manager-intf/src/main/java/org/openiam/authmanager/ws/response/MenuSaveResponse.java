package org.openiam.authmanager.ws.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuSaveResponse", propOrder = {
	"problematicMenuName"
})
public class MenuSaveResponse extends Response implements Serializable {
	
	private String problematicMenuName;
	
	private static final long serialVersionUID = 1L;

	public String getProblematicMenuName() {
		return problematicMenuName;
	}

	public void setProblematicMenuName(String problematicMenuName) {
		this.problematicMenuName = problematicMenuName;
	}

	
}
