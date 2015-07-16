package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.AbstractKeyNameEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_TARGET")
//@DozerDTOCorrespondence(IdentityQuestGroup.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "TARGET_ID", length = 32)),
        @AttributeOverride(name = "name", column = @Column(name = "TARGET_NAME", length = 255))
})
public class XACMLTargetEntity extends AbstractKeyNameEntity {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "targetEntity", fetch = FetchType.LAZY)
    private Set<XACMLAndMatchEntity> andMatchEntities = new HashSet<XACMLAndMatchEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "targetEntity", fetch = FetchType.LAZY)
    private Set<XACMLRuleEntity> ruleEntities = new HashSet<XACMLRuleEntity>(0);

    public Set<XACMLAndMatchEntity> getAndMatchEntities() {
        return andMatchEntities;
    }

    public void setAndMatchEntities(Set<XACMLAndMatchEntity> andMatchEntities) {
        this.andMatchEntities = andMatchEntities;
    }

    public Set<XACMLRuleEntity> getRuleEntities() {
        return ruleEntities;
    }

    public void setRuleEntities(Set<XACMLRuleEntity> ruleEntities) {
        this.ruleEntities = ruleEntities;
    }
}
