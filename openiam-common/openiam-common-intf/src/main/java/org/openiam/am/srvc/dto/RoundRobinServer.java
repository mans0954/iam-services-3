package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoundRobinServer", propOrder = {
})
public class RoundRobinServer extends AbstractServer {

	public RoundRobinServer() {}
	
	public RoundRobinServer(final AbstractServer server) {
		this.setId(server.getId());
		this.setServerURL(server.getServerURL());
	}
	
	
}
