package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;

import javax.persistence.*;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_MATCH_ATTRIBUTES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "MATCH_ID"))
public class XACMLMatchAttributeEntity extends KeyEntity {

    @Column(name = "MATCH_OP", length = 32)
    private String matchOperation;

    @Column(name = "MATCH_VALUE", length = 255)
    private String matchValue;

    @Column(name = "DATA_TYPE", length = 255)
    private String dataType;

    @Column(name = "IS_MULTIVALUE", length = 20)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isMUltivalued;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MATCH_CAT_ID", referencedColumnName = "MATCH_CAT_ID")
    private XACMLMatchCategoryEntity matchCategoryEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RQ_ATTRIB_DESIGNATOR_ID", referencedColumnName = "ATTRIB_DESIGNATOR_ID")
    private XACMLAttributeDesignatorEntity attributeDesignatorEntity;

}
