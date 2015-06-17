package org.openiam.idm.srvc.mngsys.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PROVISION_CONNECTOR")
@DozerDTOCorrespondence(ProvisionConnectorDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProvisionConnectorEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CONNECTOR_ID")
    private String connectorId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "METADATA_TYPE_ID")
    private String metadataTypeId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
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

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProvisionConnectorEntity that = (ProvisionConnectorEntity) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (clientCommProtocol != null ? !clientCommProtocol.equals(that.clientCommProtocol) : that.clientCommProtocol != null)
            return false;
        if (connectorId != null ? !connectorId.equals(that.connectorId) : that.connectorId != null) return false;
        if (connectorInterface != null ? !connectorInterface.equals(that.connectorInterface) : that.connectorInterface != null)
            return false;
        if (metadataTypeId != null ? !metadataTypeId.equals(that.metadataTypeId) : that.metadataTypeId != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (serviceNameSpace != null ? !serviceNameSpace.equals(that.serviceNameSpace) : that.serviceNameSpace != null)
            return false;
        if (servicePort != null ? !servicePort.equals(that.servicePort) : that.servicePort != null) return false;
        if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null) return false;
        if (stdComplianceLevel != null ? !stdComplianceLevel.equals(that.stdComplianceLevel) : that.stdComplianceLevel != null)
            return false;
        return !(wsdlUrl != null ? !wsdlUrl.equals(that.wsdlUrl) : that.wsdlUrl != null);

    }

    @Override
    public int hashCode() {
        int result = connectorId != null ? connectorId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
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

    @Override
    public String toString() {
        return "ProvisionConnectorEntity{" +
                "connectorId='" + connectorId + '\'' +
                ", name='" + name + '\'' +
                ", metadataTypeId='" + metadataTypeId + '\'' +
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
