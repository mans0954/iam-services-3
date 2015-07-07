package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;

import javax.persistence.*;

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

    @Column(name = "IS_ADVICE", length = 255)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isAdvice;


    public Boolean getIsAdvice() {
        return isAdvice;
    }

    public void setIsAdvice(Boolean isAdvice) {
        this.isAdvice = isAdvice;
    }
}
