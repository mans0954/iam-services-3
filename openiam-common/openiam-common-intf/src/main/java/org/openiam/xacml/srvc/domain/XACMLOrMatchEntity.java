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
@Table(name = "XACML_OR_MATCH")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "OR_ID"))
public class XACMLOrMatchEntity extends KeyEntity {

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "AND_ID", referencedColumnName = "AND_ID", nullable = false)
    private XACMLAndMatchEntity andMatchEntity;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "orMatchEntity", fetch = FetchType.LAZY)
    private Set<XACMLMatchCategoryEntity> matchCategoryEntities = new HashSet<XACMLMatchCategoryEntity>(0);


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public XACMLAndMatchEntity getAndMatchEntity() {
        return andMatchEntity;
    }

    public void setAndMatchEntity(XACMLAndMatchEntity andMatchEntity) {
        this.andMatchEntity = andMatchEntity;
    }

    public Set<XACMLMatchCategoryEntity> getMatchCategoryEntities() {
        return matchCategoryEntities;
    }

    public void setMatchCategoryEntities(Set<XACMLMatchCategoryEntity> matchCategoryEntities) {
        this.matchCategoryEntities = matchCategoryEntities;
    }
}
