package org.openiam.idm.srvc.mngsys.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MNG_SYS_POLICY")
@DozerDTOCorrespondence(MngSysPolicyDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MngSysPolicyEntity  extends AbstractKeyNameEntity {

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "managedSystem")
    private Set<AttributeMapEntity> attributeMaps = new HashSet<AttributeMapEntity>(0);

    

}
