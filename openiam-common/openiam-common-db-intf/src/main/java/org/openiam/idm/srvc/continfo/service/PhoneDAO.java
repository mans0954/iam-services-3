package org.openiam.idm.srvc.continfo.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Phone;

import java.util.List;
import java.util.Map;

/**
 * Data access object for Phone. Phone usually exists with a parent entity
 * such as a user, organization, account, etc.  Client components should use
 * the service objects such as <code>UserMgr</code> instead of using the DAO
 * directly.
 *
 * @author Suneet Shah
 */
public interface PhoneDAO extends BaseDao<PhoneEntity, String> {
	public void removeByUserId(final String userId);

}
