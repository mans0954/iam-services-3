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

    @Column(name = "GROOVY_SCRIPT", length = 255)
    private String groovyScript;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RULE_ID", referencedColumnName = "RULE_ID")
    private XACMLRuleEntity ruleEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ATTRIB_DESIGNATOR_A", referencedColumnName = "ATTRIB_DESIGNATOR_ID")
    private XACMLAttributeDesignatorEntity attributeDesignatorEntityA;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ATTRIB_DESIGNATOR_B", referencedColumnName = "ATTRIB_DESIGNATOR_ID")
    private XACMLAttributeDesignatorEntity attributeDesignatorEntityB;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMatchOperation() {
        return matchOperation;
    }

    public void setMatchOperation(String matchOperation) {
        this.matchOperation = matchOperation;
    }

    public String getGroovyScript() {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript) {
        this.groovyScript = groovyScript;
    }

    public XACMLRuleEntity getRuleEntity() {
        return ruleEntity;
    }

    public void setRuleEntity(XACMLRuleEntity ruleEntity) {
        this.ruleEntity = ruleEntity;
    }

    public XACMLAttributeDesignatorEntity getAttributeDesignatorEntityA() {
        return attributeDesignatorEntityA;
    }

    public void setAttributeDesignatorEntityA(XACMLAttributeDesignatorEntity attributeDesignatorEntityA) {
        this.attributeDesignatorEntityA = attributeDesignatorEntityA;
    }

    public XACMLAttributeDesignatorEntity getAttributeDesignatorEntityB() {
        return attributeDesignatorEntityB;
    }

    public void setAttributeDesignatorEntityB(XACMLAttributeDesignatorEntity attributeDesignatorEntityB) {
        this.attributeDesignatorEntityB = attributeDesignatorEntityB;
    }
}
