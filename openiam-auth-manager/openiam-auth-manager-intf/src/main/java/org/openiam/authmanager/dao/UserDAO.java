package org.openiam.authmanager.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;

public interface UserDAO extends AbstractDAO<AuthorizationUser> {
	/**
	 * @param date - the earliest last login timestamp
	 * @return all users who have logged in after <p>date</p>
	 */
	public List<AuthorizationUser> getAllUsersLoggedInAfter(final Date date);
	
	/**
	 * @param userId - userId
	 * @return the fully populated User
	 */
	public InternalAuthroizationUser getFullUser(final String userId);

    public List<String> getUserIdsList();

    public List<String> getUserIdsForRoles(Set<String> roleIds);
    public List<String> getUserIdsForGroups(Set<String> groupIds);
    public List<String> getUserIdsForResources(Set<String> resourceIds);

}
