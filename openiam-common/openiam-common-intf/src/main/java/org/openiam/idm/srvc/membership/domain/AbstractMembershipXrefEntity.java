package org.openiam.idm.srvc.membership.domain;

import java.util.Set;

import javax.persistence.MappedSuperclass;

import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;

@MappedSuperclass
public abstract class AbstractMembershipXrefEntity<Parent extends KeyEntity, Child extends KeyEntity> extends KeyEntity {
	
	public abstract Set<AccessRightEntity> getRights();
	public abstract Parent getEntity();
	public abstract Child getMemberEntity();
	
}
