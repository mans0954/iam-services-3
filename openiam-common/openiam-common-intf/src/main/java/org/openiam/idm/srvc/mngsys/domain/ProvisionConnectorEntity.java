package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;

@Entity
@Table(name = "PROVISION_CONNECTOR")
@DozerDTOCorrespondence(ProvisionConnectorDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides(value= {
        @AttributeOverride(name = "id", column = @Column(name = "CONNECTOR_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "NAME", length=40, nullable = false)),
})
public class ProvisionConnectorEntity extends AbstractKeyNameEntity {

	private static final long serialVersionUID = -7525150161835841397L;

	@OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="METADATA_TYPE_ID", referencedColumnName = "TYPE_ID", insertable = true, updatable = true, nullable=true)
    private MetadataTypeEntity metadataType;

    @Column(name = "STD_COMPLIANCE_LEVEL")
    private String stdComplianceLevel;

    @Column(name = "CLIENT_COMM_PROTOCOL")
    private String clientCommProtocol;

    @Column(name = "SERVICE_URL")
    private String serviceUrl;

    @Column(name = "CLASS_NAME")
    private String className;

    @Column(name = "SERVICE_NAMESPACE")
    private String serviceNameSpace;

    @Column(name = "SERVICE_PORT")
    private String servicePort;

    @Column(name = "SERVICE_WSDL")
    private String wsdlUrl;

    @Column(name = "CONNECTOR_INTERFACE")
    private String connectorInterface;

    public MetadataTypeEntity getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(MetadataTypeEntity metadataType) {
        this.metadataType = metadataType;
    }

    public String getStdComplianceLevel() {
        return stdComplianceLevel;
    }

    public void setStdComplianceLevel(String stdComplianceLevel) {
        this.stdComplianceLevel = stdComplianceLevel;
    }

    public String getClientCommProtocol() {
        return clientCommProtocol;
    }

    public void setClientCommProtocol(String clientCommProtocol) {
        this.clientCommProtocol = clientCommProtocol;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getClassName() {
        return className;
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

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
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
		ProvisionConnectorEntity other = (ProvisionConnectorEntity) obj;
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

	@Override
    public String toString() {
        return "ProvisionConnectorEntity{" +
                "connectorId='" + id + '\'' +
                ", name='" + name + '\'' +
                ", metadataType='" + metadataType + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", wsdlUrl='" + wsdlUrl + '\'' +
                ", connectorInterface='" + connectorInterface + '\'' +
                ", servicePort='" + servicePort + '\'' +
                ", serviceNameSpace='" + serviceNameSpace + '\'' +
                ", className='" + className + '\'' +
                ", clientCommProtocol='" + clientCommProtocol + '\'' +
                '}';
    }
}
