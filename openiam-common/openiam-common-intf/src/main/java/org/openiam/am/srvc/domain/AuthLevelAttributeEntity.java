package org.openiam.am.srvc.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

@Entity
@Table(name = "AUTH_LEVEL_ATTRIBUTE")
@DozerDTOCorrespondence(AuthLevelAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthLevelAttributeEntity implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "AUTH_LEVEL_ATTRIBUTE_ID", length = 32, nullable = false)
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AUTH_LEVEL_GROUPING_ID", referencedColumnName = "AUTH_LEVEL_GROUPING_ID")
	private AuthLevelGroupingEntity grouping;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TYPE_ID", referencedColumnName="TYPE_ID")
    private MetadataTypeEntity type;
	
    @Column(name = "NAME", length = 100)
    private String name;
    
    @Lob
    @Column(name = "VALUE_AS_BYTE_ARRAY")
    private byte[] valueAsByteArray;
    
    @Column(name="VALUE_AS_STRING", length=4096)
    private String valueAsString;
    
    public AuthLevelAttributeEntity() {
    	
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AuthLevelGroupingEntity getGrouping() {
		return grouping;
	}

	public void setGrouping(AuthLevelGroupingEntity grouping) {
		this.grouping = grouping;
	}

	public MetadataTypeEntity getType() {
		return type;
	}

	public void setType(MetadataTypeEntity type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getValueAsByteArray() {
		return valueAsByteArray;
	}

	public void setValueAsByteArray(byte[] valueAsByteArray) {
		this.valueAsByteArray = valueAsByteArray;
	}
	
	public String getValueAsString() {
		return valueAsString;
	}

	public void setValueAsString(String valueAsString) {
		this.valueAsString = valueAsString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((valueAsByteArray == null) ? 0 : valueAsByteArray.hashCode());
		result = prime * result + ((valueAsString == null) ? 0 : valueAsString.hashCode());
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
		AuthLevelAttributeEntity other = (AuthLevelAttributeEntity) obj;
		if (grouping == null) {
			if (other.grouping != null)
				return false;
		} else if (!grouping.equals(other.grouping))
			return false;
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (valueAsByteArray == null) {
			if (other.valueAsByteArray != null)
				return false;
		} else if (!valueAsByteArray.equals(other.valueAsByteArray))
			return false;
		if (valueAsString == null) {
			if (other.valueAsString != null)
				return false;
		} else if (!valueAsString.equals(other.valueAsString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthLevelAttributeEntity [id=%s, grouping=%s, type=%s, name=%s]",
						id, grouping, type, name);
	}
    
    
}
