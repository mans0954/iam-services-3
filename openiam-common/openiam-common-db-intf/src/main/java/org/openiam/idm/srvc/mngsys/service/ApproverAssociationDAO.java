package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;

public interface ApproverAssociationDAO extends BaseDao<ApproverAssociationEntity, String> {

	/**
	 * Finds approvers by request type.
	 * 
	 * @param requestType
	 *            the request type
	 * @param level
	 *            the level
	 * @return the list
	 */
	List<ApproverAssociationEntity> findApproversByRequestType(String requestType,int level);
}