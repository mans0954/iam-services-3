package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;

public interface ApproverAssociationDAO extends BaseDao<ApproverAssociationEntity, String> {

	public List<ApproverAssociationEntity> getByAssociation(final String associationId, final AssociationType associationType);
}