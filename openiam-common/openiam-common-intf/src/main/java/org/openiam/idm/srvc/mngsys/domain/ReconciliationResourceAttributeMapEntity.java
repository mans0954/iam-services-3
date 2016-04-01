package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ReconciliationResourceAttributeMap;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;

/**
 * @author zaporozhec
 */
@Entity
@Table(name = "RECON_RES_ATTR_MAP")
@DozerDTOCorrespondence(ReconciliationResourceAttributeMap.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "RECON_RES_ATTR_MAP_ID"))
public class ReconciliationResourceAttributeMapEntity extends KeyEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne(mappedBy = "reconResAttribute", orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private AttributeMapEntity attributeMap;

    @ManyToOne(optional = true)
    @JoinColumn(name = "ATTR_POLICY_ID", nullable = true, updatable = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)    
	private PolicyEntity attributePolicy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "DEF_RECON_ATTR_MAP_ID", nullable = true, updatable = true)
    private DefaultReconciliationAttributeMapEntity defaultAttributePolicy;

    public PolicyEntity getAttributePolicy() {
        return attributePolicy;
    }

    public void setAttributePolicy(PolicyEntity attributePolicy) {
        this.attributePolicy = attributePolicy;
    }

    public DefaultReconciliationAttributeMapEntity getDefaultAttributePolicy() {
        return defaultAttributePolicy;
    }

    public void setDefaultAttributePolicy(
            DefaultReconciliationAttributeMapEntity defaultAttributePolicy) {
        this.defaultAttributePolicy = defaultAttributePolicy;
    }

    public AttributeMapEntity getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(AttributeMapEntity attributeMap) {
        this.attributeMap = attributeMap;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributeMap == null) ? 0 : attributeMap.hashCode());
		result = prime * result
				+ ((attributePolicy == null) ? 0 : attributePolicy.hashCode());
		result = prime
				* result
				+ ((defaultAttributePolicy == null) ? 0
						: defaultAttributePolicy.hashCode());
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
		ReconciliationResourceAttributeMapEntity other = (ReconciliationResourceAttributeMapEntity) obj;
		if (attributeMap == null) {
			if (other.attributeMap != null)
				return false;
		} else if (!attributeMap.equals(other.attributeMap))
			return false;
		if (attributePolicy == null) {
			if (other.attributePolicy != null)
				return false;
		} else if (!attributePolicy.equals(other.attributePolicy))
			return false;
		if (defaultAttributePolicy == null) {
			if (other.defaultAttributePolicy != null)
				return false;
		} else if (!defaultAttributePolicy.equals(other.defaultAttributePolicy))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReconciliationResourceAttributeMapEntity [attributeMap="
				+ attributeMap + ", attributePolicy=" + attributePolicy
				+ ", defaultAttributePolicy=" + defaultAttributePolicy
				+ ", id=" + id + "]";
	}

    
}
