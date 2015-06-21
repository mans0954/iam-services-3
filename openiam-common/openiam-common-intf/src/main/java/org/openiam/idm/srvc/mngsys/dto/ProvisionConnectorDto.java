package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * ProvisionConnector represents a connector for provisioning.  
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisionConnectorDto", propOrder = {
    "metadataTypeId",
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
	protected String metadataTypeId;
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
                                 String metdataTypeId, String stdComplianceLevel,
                                 String clientCommProtocol, String serviceUrl, String className) {
		this.id = id;
		this.name_ = name;
		this.metadataTypeId = metdataTypeId;
		this.stdComplianceLevel = stdComplianceLevel;
		this.clientCommProtocol = clientCommProtocol;
		this.serviceUrl = serviceUrl;
		this.className = className;
	}

	public String getMetadataTypeId() {
		return this.metadataTypeId;
	}

	public void setMetadataTypeId(String metdataTypeId) {
		this.metadataTypeId = metdataTypeId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProvisionConnectorDto that = (ProvisionConnectorDto) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (clientCommProtocol != null ? !clientCommProtocol.equals(that.clientCommProtocol) : that.clientCommProtocol != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (connectorInterface != null ? !connectorInterface.equals(that.connectorInterface) : that.connectorInterface != null)
            return false;
        if (metadataTypeId != null ? !metadataTypeId.equals(that.metadataTypeId) : that.metadataTypeId != null)
            return false;
        if (name_ != null ? !name_.equals(that.name_) : that.name_ != null) return false;
        if (serviceNameSpace != null ? !serviceNameSpace.equals(that.serviceNameSpace) : that.serviceNameSpace != null)
            return false;
        if (servicePort != null ? !servicePort.equals(that.servicePort) : that.servicePort != null) return false;
        if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null) return false;
        if (stdComplianceLevel != null ? !stdComplianceLevel.equals(that.stdComplianceLevel) : that.stdComplianceLevel != null)
            return false;
        if (wsdlUrl != null ? !wsdlUrl.equals(that.wsdlUrl) : that.wsdlUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name_ != null ? name_.hashCode() : 0);
        result = 31 * result + (metadataTypeId != null ? metadataTypeId.hashCode() : 0);
        result = 31 * result + (stdComplianceLevel != null ? stdComplianceLevel.hashCode() : 0);
        result = 31 * result + (clientCommProtocol != null ? clientCommProtocol.hashCode() : 0);
        result = 31 * result + (serviceUrl != null ? serviceUrl.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (serviceNameSpace != null ? serviceNameSpace.hashCode() : 0);
        result = 31 * result + (servicePort != null ? servicePort.hashCode() : 0);
        result = 31 * result + (wsdlUrl != null ? wsdlUrl.hashCode() : 0);
        result = 31 * result + (connectorInterface != null ? connectorInterface.hashCode() : 0);
        return result;
    }
}
