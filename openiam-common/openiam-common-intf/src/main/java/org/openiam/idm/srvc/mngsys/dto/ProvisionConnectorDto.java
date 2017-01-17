package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * ProvisionConnector represents a connector for provisioning.  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionConnectorDto", propOrder = {
    "metadataType",
    "stdComplianceLevel",
    "clientCommProtocol",
    "serviceUrl",
    "className",
    "serviceNameSpace",
    "servicePort",
    "wsdlUrl",
    "connectorInterface"
})
@DozerDTOCorrespondence(ProvisionConnectorEntity.class)
public class ProvisionConnectorDto extends KeyNameDTO {


	private static final long serialVersionUID = -6981651498633257018L;
	protected MetadataType metadataType;
	protected String stdComplianceLevel;
	protected String clientCommProtocol;
	protected String serviceUrl;
	protected String className;
	protected String serviceNameSpace;
	protected String servicePort;
	protected String wsdlUrl;
	protected String connectorInterface;
	

	public ProvisionConnectorDto() {
	}

	public ProvisionConnectorDto(String id) {
		this.id = id;
	}

	public ProvisionConnectorDto(String id, String name,
			MetadataType metdataType, String stdComplianceLevel,
                                 String clientCommProtocol, String serviceUrl, String className) {
		this.id = id;
		this.name_ = name;
		this.metadataType = metdataType;
		this.stdComplianceLevel = stdComplianceLevel;
		this.clientCommProtocol = clientCommProtocol;
		this.serviceUrl = serviceUrl;
		this.className = className;
	}

	public MetadataType getMetadataType() {
		return this.metadataType;
	}

	public void setMetadataType(MetadataType metdataType) {
		this.metadataType = metdataType;
	}

	public String getStdComplianceLevel() {
		return this.stdComplianceLevel;
	}

	public void setStdComplianceLevel(String stdComplianceLevel) {
		this.stdComplianceLevel = stdComplianceLevel;
	}

	public String getClientCommProtocol() {
		return this.clientCommProtocol;
	}

	public void setClientCommProtocol(String clientCommProtocol) {
		this.clientCommProtocol = clientCommProtocol;
	}

	public String getServiceUrl() {
		return this.serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getServiceNameSpace() {
		return serviceNameSpace;
	}

	public void setServiceNameSpace(String serviceNameSpace) {
		this.serviceNameSpace = serviceNameSpace;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	public String getConnectorInterface() {
		return connectorInterface;
	}

	public void setConnectorInterface(String connectorInterface) {
		this.connectorInterface = connectorInterface;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((clientCommProtocol == null) ? 0 : clientCommProtocol.hashCode());
		result = prime * result + ((connectorInterface == null) ? 0 : connectorInterface.hashCode());
		result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
		result = prime * result + ((serviceNameSpace == null) ? 0 : serviceNameSpace.hashCode());
		result = prime * result + ((servicePort == null) ? 0 : servicePort.hashCode());
		result = prime * result + ((serviceUrl == null) ? 0 : serviceUrl.hashCode());
		result = prime * result + ((stdComplianceLevel == null) ? 0 : stdComplianceLevel.hashCode());
		result = prime * result + ((wsdlUrl == null) ? 0 : wsdlUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProvisionConnectorDto other = (ProvisionConnectorDto) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (clientCommProtocol == null) {
			if (other.clientCommProtocol != null)
				return false;
		} else if (!clientCommProtocol.equals(other.clientCommProtocol))
			return false;
		if (connectorInterface == null) {
			if (other.connectorInterface != null)
				return false;
		} else if (!connectorInterface.equals(other.connectorInterface))
			return false;
		if (metadataType == null) {
			if (other.metadataType != null)
				return false;
		} else if (!metadataType.equals(other.metadataType))
			return false;
		if (serviceNameSpace == null) {
			if (other.serviceNameSpace != null)
				return false;
		} else if (!serviceNameSpace.equals(other.serviceNameSpace))
			return false;
		if (servicePort == null) {
			if (other.servicePort != null)
				return false;
		} else if (!servicePort.equals(other.servicePort))
			return false;
		if (serviceUrl == null) {
			if (other.serviceUrl != null)
				return false;
		} else if (!serviceUrl.equals(other.serviceUrl))
			return false;
		if (stdComplianceLevel == null) {
			if (other.stdComplianceLevel != null)
				return false;
		} else if (!stdComplianceLevel.equals(other.stdComplianceLevel))
			return false;
		if (wsdlUrl == null) {
			if (other.wsdlUrl != null)
				return false;
		} else if (!wsdlUrl.equals(other.wsdlUrl))
			return false;
		return true;
	}
}
