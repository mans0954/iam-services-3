package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "AUTH_RESOURCE_AM_ATTRIBUTE")
@DozerDTOCorrespondence(AuthResourceAMAttribute.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "AM_RES_ATTRIBUTE_ID")),
	@AttributeOverride(name = "name", column = @Column(name="ATTRIBUTE_NAME", length=100, nullable = false))
})
public class AuthResourceAMAttributeEntity extends AbstractKeyNameEntity {
   
    @Column(name="REFLECTION_KEY", length=255, nullable = false)
    private String reflectionKey;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "amAttribute")
    private Set<URIPatternMetaValueEntity> metaValues;

    public String getReflectionKey() {
        return reflectionKey;
    }

    public void setReflectionKey(String reflectionKey) {
        this.reflectionKey = reflectionKey;
    }

	public Set<URIPatternMetaValueEntity> getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(Set<URIPatternMetaValueEntity> metaValues) {
		this.metaValues = metaValues;
	}

}
