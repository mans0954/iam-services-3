package org.openiam.idm.srvc.batch.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.base.KeyNameDTO;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.batch.dto.BatchTask;

@Entity
@Table(name = "LOCK_TABLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "LOCK_TABLE_ID", length = 32)),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 50))
})
public class LockTableEntity extends AbstractKeyNameEntity {

	
}
