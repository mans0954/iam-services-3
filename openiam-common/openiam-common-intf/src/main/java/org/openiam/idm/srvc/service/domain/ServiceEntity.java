package org.openiam.idm.srvc.service.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.Resource;

@Entity
@Table(name = "SERVICE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ServiceEntity implements Serializable {

	 @Id
	 //@GeneratedValue(generator = "system-uuid")
	 //@GenericGenerator(name = "system-uuid", strategy = "uuid")
	 @Column(name = "SERVICE_ID", length = 20)
	 private String id;

	 @Column(name = "SERVICE_NAME", length = 40)
	 private String serviceName;
	 
	 @Column(name = "STATUS", length = 20)
	 private String status;

	 @Column(name = "LOCATION_IP_ADDRESS", length = 80)
	 private String locationIpAddress;

	 @Column(name = "COMPANY_OWNER_ID", length = 20)
	 private String companyOwnerId;

	 @Column(name="START_DATE",length=19)
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date startDate;

	 @Column(name="END_DATE",length=19)
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date endDate;

	 @Column(name = "LICENSE_KEY")
	 private String licenseKey;

	 @Column(name = "SERVICE_TYPE", length=20)
	 private String serviceType;

	 @Column(name = "PARENT_SERVICE_ID", length=20)
	 private String parentServiceId;

	 @Column(name = "ROOT_RESOURCE_ID", length=20)
	 private String rootResourceId;

	 @Column(name = "ACCESS_CONTROL_MODEL", length=20)
	 private String accessControlModel;

	 @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "service")
	 private Set<ServiceConfigEnity> serviceConfigs = new HashSet<ServiceConfigEnity>(0);
	 
	 @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "service")
	 private Set<RequestFormEntity> requestForms = new HashSet<RequestFormEntity>(0);

	 public ServiceEntity() {
	 }
	
	 public String getId() {
		 return this.id;
	 }
	
	 public void setId(String id) {
	    this.id = id;
	}
	
	public String getServiceName() {
	    return this.serviceName;
	}
	
	public void setServiceName(String serviceName) {
	    this.serviceName = serviceName;
	}
	
	public String getStatus() {
	    return this.status;
	}
	
	public void setStatus(String status) {
	    this.status = status;
	}
	
	public String getLocationIpAddress() {
	    return this.locationIpAddress;
	}
	
	public void setLocationIpAddress(String locationIpAddress) {
	    this.locationIpAddress = locationIpAddress;
	}
	
	public String getCompanyOwnerId() {
	    return this.companyOwnerId;
	}
	
	public void setCompanyOwnerId(String companyOwnerId) {
	    this.companyOwnerId = companyOwnerId;
	}
	
	public Date getStartDate() {
	    return this.startDate;
	}
	
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}
	
	public Date getEndDate() {
	    return this.endDate;
	}
	
	public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	}
	
	public String getLicenseKey() {
	    return this.licenseKey;
	}
	
	public void setLicenseKey(String licenseKey) {
	    this.licenseKey = licenseKey;
	}
	
	public String getServiceType() {
	    return this.serviceType;
	}
	
	public void setServiceType(String serviceType) {
	    this.serviceType = serviceType;
	}
	
	public String getParentServiceId() {
	    return this.parentServiceId;
	}
	
	public void setParentServiceId(String parentServiceId) {
	    this.parentServiceId = parentServiceId;
	}
	
	public String getRootResourceId() {
	    return this.rootResourceId;
	}
	
	public void setRootResourceId(String rootResourceId) {
	    this.rootResourceId = rootResourceId;
	}
	
	public String getAccessControlModel() {
	    return this.accessControlModel;
	}
	
	public void setAccessControlModel(String accessControlModel) {
	    this.accessControlModel = accessControlModel;
	}
	
	public Set<ServiceConfigEnity> getServiceConfigs() {
	    return this.serviceConfigs;
	}
	
	public void setServiceConfigs(Set<ServiceConfigEnity> serviceConfigs) {
	    this.serviceConfigs = serviceConfigs;
	}
	
	public Set<RequestFormEntity> getRequestForms() {
	    return this.requestForms;
	}
	
	public void setRequestForms(Set<RequestFormEntity> requestForms) {
	    this.requestForms = requestForms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((accessControlModel == null) ? 0 : accessControlModel
						.hashCode());
		result = prime * result
				+ ((companyOwnerId == null) ? 0 : companyOwnerId.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((licenseKey == null) ? 0 : licenseKey.hashCode());
		result = prime
				* result
				+ ((locationIpAddress == null) ? 0 : locationIpAddress
						.hashCode());
		result = prime * result
				+ ((parentServiceId == null) ? 0 : parentServiceId.hashCode());
		result = prime * result
				+ ((rootResourceId == null) ? 0 : rootResourceId.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result
				+ ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceEntity other = (ServiceEntity) obj;
		if (accessControlModel == null) {
			if (other.accessControlModel != null)
				return false;
		} else if (!accessControlModel.equals(other.accessControlModel))
			return false;
		if (companyOwnerId == null) {
			if (other.companyOwnerId != null)
				return false;
		} else if (!companyOwnerId.equals(other.companyOwnerId))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (licenseKey == null) {
			if (other.licenseKey != null)
				return false;
		} else if (!licenseKey.equals(other.licenseKey))
			return false;
		if (locationIpAddress == null) {
			if (other.locationIpAddress != null)
				return false;
		} else if (!locationIpAddress.equals(other.locationIpAddress))
			return false;
		if (parentServiceId == null) {
			if (other.parentServiceId != null)
				return false;
		} else if (!parentServiceId.equals(other.parentServiceId))
			return false;
		if (rootResourceId == null) {
			if (other.rootResourceId != null)
				return false;
		} else if (!rootResourceId.equals(other.rootResourceId))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		if (serviceType == null) {
			if (other.serviceType != null)
				return false;
		} else if (!serviceType.equals(other.serviceType))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServiceEntity [id=" + id + ", serviceName=" + serviceName
				+ ", status=" + status + ", locationIpAddress="
				+ locationIpAddress + ", companyOwnerId=" + companyOwnerId
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", licenseKey=" + licenseKey + ", serviceType=" + serviceType
				+ ", parentServiceId=" + parentServiceId + ", rootResourceId="
				+ rootResourceId + ", accessControlModel=" + accessControlModel
				+ "]";
	}

	
}
