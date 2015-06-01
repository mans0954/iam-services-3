package org.openiam.idm.srvc.prov.request.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.dto.RequestAttribute;

@Entity
@Table(name="REQUEST_ATTRIBUTE")
@DozerDTOCorrespondence(RequestAttribute.class)
public class RequestAttributeEntity {

	@Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="REQUEST_ATTR_ID", length=32)
	private String id;
	
	@Column(name="REQUEST_ID",length=32, nullable=false)
	private String provRequestId;
	
	@Column(name="NAME",length=100)
	private String name;
	
	@Column(name="VALUE",length=4000)
	private String value;
	
	@Column(name="METADATA_ID",length=20)
	private String metadataTypeId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProvRequestId() {
		return provRequestId;
	}
	public void setProvRequestId(String provRequestId) {
		this.provRequestId = provRequestId;
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
	public String getMetadataTypeId() {
		return metadataTypeId;
	}
	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((provRequestId == null) ? 0 : provRequestId.hashCode());
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
		RequestAttributeEntity other = (RequestAttributeEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (provRequestId == null) {
			if (other.provRequestId != null)
				return false;
		} else if (!provRequestId.equals(other.provRequestId))
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
		return "RequestAttributeEntity [id=" + id + ", provRequestId="
				+ provRequestId + ", name=" + name + ", value=" + value
				+ ", metadataTypeId=" + metadataTypeId + "]";
	}
	
	
}
