package org.openiam.am.srvc.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "AUTH_RESOURCE_AM_ATTRIBUTE")
@DozerDTOCorrespondence(AuthResourceAMAttribute.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthResourceAMAttributeEntity implements Serializable {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="AM_RES_ATTRIBUTE_ID", length=32, nullable = false)
    private String id;

    @Column(name="REFLECTION_KEY", length=255, nullable = false)
    private String reflectionKey;
    @Column(name="ATTRIBUTE_NAME", length=100, nullable = false)
    private String attributeName;
    
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "amAttribute")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<URIPatternMetaValueEntity> metaValues;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReflectionKey() {
        return reflectionKey;
    }

    public void setReflectionKey(String reflectionKey) {
        this.reflectionKey = reflectionKey;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

	public Set<URIPatternMetaValueEntity> getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(Set<URIPatternMetaValueEntity> metaValues) {
		this.metaValues = metaValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((reflectionKey == null) ? 0 : reflectionKey.hashCode());
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
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
		AuthResourceAMAttributeEntity other = (AuthResourceAMAttributeEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
        if (reflectionKey == null) {
            if (other.reflectionKey != null)
                return false;
        } else if (!reflectionKey.equals(other.reflectionKey))
            return false;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthResourceAMAttributeEntity [amAttributeId=%s, reflectionKey=%s, attributeName=%s]", id,reflectionKey, attributeName);
	}
    
    
}
