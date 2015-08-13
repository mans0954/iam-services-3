package org.openiam.idm.srvc.continfo.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;

import java.util.List;
import java.util.Map;

/**
 * Data access object for address. Address usually exists with a parent entity
 * such as a user, organization, account, etc. Client components should use
 * the service objects such as <code>UserMgr</code> instead of using the DAO
 * directly.
 *
 * @author Suneet Shah
 */
public interface AddressDAO extends BaseDao<AddressEntity, String> {


	void removeByUserId(final String userId);
}
