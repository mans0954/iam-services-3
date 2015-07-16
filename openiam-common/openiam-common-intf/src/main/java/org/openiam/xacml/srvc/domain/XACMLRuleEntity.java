package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.xacml.srvc.constants.XACMLEffect;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_RULE")
//@DozerDTOCorrespondence(IdentityQuestGroup.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "RULE_ID", length = 32)),
        @AttributeOverride(name = "name", column = @Column(name = "RULE_NAME", length = 255))
})
public class XACMLRuleEntity extends AbstractKeyNameEntity {

    @Column(name = "DESCRIPTION", length = 255)
    private String description;


    @Column(name = "EFFECT", length = 32)
    @Enumerated(EnumType.STRING)
    private XACMLEffect effect = XACMLEffect.DENY;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "ruleEntity", fetch = FetchType.LAZY)
    private Set<XACMLRuleConditionEntity> matchCategoryEntities = new HashSet<XACMLRuleConditionEntity>(0);

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "OBLIGATIONS_ID", referencedColumnName = "OBLIGATIONS_ID")
    private XACMLObligationsEntity obligationsEntity;


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TARGET_ID", referencedColumnName = "TARGET_ID")
    private XACMLTargetEntity targetEntity;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public XACMLEffect getEffect() {
        return effect;
    }

    public void setEffect(XACMLEffect effect) {
        this.effect = effect;
    }

    public Set<XACMLRuleConditionEntity> getMatchCategoryEntities() {
        return matchCategoryEntities;
    }

    public void setMatchCategoryEntities(Set<XACMLRuleConditionEntity> matchCategoryEntities) {
        this.matchCategoryEntities = matchCategoryEntities;
    }

    public XACMLObligationsEntity getObligationsEntity() {
        return obligationsEntity;
    }

    public void setObligationsEntity(XACMLObligationsEntity obligationsEntity) {
        this.obligationsEntity = obligationsEntity;
    }

    public XACMLTargetEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(XACMLTargetEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

}
