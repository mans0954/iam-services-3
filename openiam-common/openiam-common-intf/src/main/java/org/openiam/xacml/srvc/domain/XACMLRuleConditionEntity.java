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
@Table(name = "XACML_RULE_CONDITION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "OR_ID"))
public class XACMLRuleConditionEntity extends KeyEntity {

    @Column(name = "CONDITION", length = 255)
    private String condition;

    @Column(name = "MATCH_OP", length = 255)
    private String matchOperation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RULE_ID", referencedColumnName = "RULE_ID")
    private XACMLRuleEntity ruleEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ATTRIB_DESIGNATOR_A", referencedColumnName = "ATTRIB_DESIGNATOR_ID")
    private XACMLAttributeDesignatorEntity attributeDesignatorEntityA;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ATTRIB_DESIGNATOR_B", referencedColumnName = "ATTRIB_DESIGNATOR_ID")
    private XACMLAttributeDesignatorEntity attributeDesignatorEntityB;

}
