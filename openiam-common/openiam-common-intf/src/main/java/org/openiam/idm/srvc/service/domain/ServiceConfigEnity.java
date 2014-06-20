package org.openiam.idm.srvc.service.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "SERVICE_CONFIG")
public class ServiceConfigEnity implements Serializable {

	 @Id
	 //@GeneratedValue(generator = "system-uuid")
	 //@GenericGenerator(name = "system-uuid", strategy = "uuid")
	 @Column(name = "SERVICE_CONFIG_ID", length = 20)
	 private String id;
    
	 @ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	 @JoinColumn(name="SERVICE_ID", referencedColumnName = "SERVICE_ID")
	 private ServiceEntity service;
	 
	 @Column(name = "NAME", length = 40)
	 private String name;
	 
	 @Column(name = "VALUE", length = 40)
	 private String value;

	 public ServiceConfigEnity() {
	 }

	 public String getId() {
		return id;
	 }

	 public void setId(String id) {
		this.id = id;
	 }

	 public ServiceEntity getService() {
		return service;
	 }

	 public void setService(ServiceEntity service) {
		this.service = service;
	 }

	 public String getName() {
		return name;
	 }

	 public void setName(String name) {
		this.name = name;
	 }

	 public String getValue() {
		return value;
	 }

	 public void setValue(String value) {
		this.value = value;
	 }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ServiceConfigEnity other = (ServiceConfigEnity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServiceConfigEnity [id=" + id + ", service=" + service
				+ ", name=" + name + ", value=" + value + "]";
	}
	 
	 
}
