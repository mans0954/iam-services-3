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
@Table(name = "XACML_MATCH_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "MATCH_CAT_ID"))
public class XACMLMatchCategoryEntity extends KeyEntity {

    @Column(name = "CATEGORY", length = 20)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "OR_ID", referencedColumnName = "OR_ID", nullable = false)
    private XACMLOrMatchEntity orMatchEntity;


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "matchCategoryEntity", fetch = FetchType.LAZY)
    private Set<XACMLMatchAttributesEntity> matchAttributeEntities = new HashSet<XACMLMatchAttributesEntity>(0);

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public XACMLOrMatchEntity getOrMatchEntity() {
        return orMatchEntity;
    }

    public void setOrMatchEntity(XACMLOrMatchEntity orMatchEntity) {
        this.orMatchEntity = orMatchEntity;
    }

    public Set<XACMLMatchAttributesEntity> getMatchAttributeEntities() {
        return matchAttributeEntities;
    }

    public void setMatchAttributeEntities(Set<XACMLMatchAttributesEntity> matchAttributeEntities) {
        this.matchAttributeEntities = matchAttributeEntities;
    }
}
