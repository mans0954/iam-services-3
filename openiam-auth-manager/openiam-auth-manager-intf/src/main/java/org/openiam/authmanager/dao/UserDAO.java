package org.openiam.authmanager.dao;

import org.openiam.authmanager.common.model.AuthorizationManagerLoginId;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserDAO extends AbstractDAO<AuthorizationUser> {
	/**
	 * @param date - the earliest last login timestamp
	 * @return all users who have logged in after <p>date</p>
	 */
	public List<AuthorizationUser> getAllUsersLoggedInAfter(final Date date);
	
	/**
	 * @param date - the earliest last login timestamp
	 * @return all LoginIds for users who have logged in after <p>date</p>
	 */
	public List<AuthorizationManagerLoginId> getLoginIdsForUsersLoggedInAfter(final Date date);
	
	/**
	 * @param userId - userId
	 * @return the fully populated User
	 */
	public InternalAuthroizationUser getFullUser(final String userId);
	
	/**
	 * @param loginId - LoginId object
	 * @return the fully populatedUser
	 */
	public InternalAuthroizationUser getFullUser(final AuthorizationManagerLoginId loginId);


    public List<String> getUserIdsList();

    public List<String> getUserIdsForRoles(Set<String> roleIds);
    public List<String> getUserIdsForGroups(Set<String> groupIds);
    public List<String> getUserIdsForResources(Set<String> resourceIds);

}
