package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.KeyEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_AND_MATCH")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "AND_ID"))
public class XACMLAndMatchEntity extends KeyEntity {

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TARGET_ID", referencedColumnName = "TARGET_ID")
    private XACMLTargetEntity targetEntity;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "andMatchEntity", fetch = FetchType.LAZY)
    private Set<XACMLOrMatchEntity> orMatchEntities = new HashSet<XACMLOrMatchEntity>(0);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public XACMLTargetEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(XACMLTargetEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Set<XACMLOrMatchEntity> getOrMatchEntities() {
        return orMatchEntities;
    }

    public void setOrMatchEntities(Set<XACMLOrMatchEntity> orMatchEntities) {
        this.orMatchEntities = orMatchEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XACMLAndMatchEntity)) return false;
        if (!super.equals(o)) return false;

        XACMLAndMatchEntity that = (XACMLAndMatchEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
