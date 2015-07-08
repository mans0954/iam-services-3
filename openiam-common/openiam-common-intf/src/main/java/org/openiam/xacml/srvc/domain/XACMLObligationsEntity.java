package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_OBLIGATIONS")
//@DozerDTOCorrespondence(IdentityQuestGroup.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "OBLIGATIONS_ID", length = 32)),
        @AttributeOverride(name = "name", column = @Column(name = "OBLIGATIONS_NAME", length = 255))
})
public class XACMLObligationsEntity extends AbstractKeyNameEntity {

    @Column(name = "IS_ADVICE")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isAdvice;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "obligationsEntity", fetch = FetchType.LAZY)
    private Set<XACMLObligationEntity> obligationEntities = new HashSet<XACMLObligationEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "obligationsEntity", fetch = FetchType.LAZY)
    private Set<XACMLRuleEntity> ruleEntities = new HashSet<XACMLRuleEntity>(0);

    public Boolean getIsAdvice() {
        return isAdvice;
    }

    public void setIsAdvice(Boolean isAdvice) {
        this.isAdvice = isAdvice;
    }

    public Set<XACMLObligationEntity> getObligationEntities() {
        return obligationEntities;
    }

    public void setObligationEntities(Set<XACMLObligationEntity> obligationEntities) {
        this.obligationEntities = obligationEntities;
    }
}
