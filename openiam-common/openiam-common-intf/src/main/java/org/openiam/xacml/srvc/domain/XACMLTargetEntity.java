package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;

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

}
