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
	List<AuthorizationUser> getAllUsersLoggedInAfter(final Date date);
	
	/**
	 * @param date - the earliest last login timestamp
	 * @return all LoginIds for users who have logged in after <p>date</p>
	 */
	List<AuthorizationManagerLoginId> getLoginIdsForUsersLoggedInAfter(final Date date);
	
	/**
	 * @param userId - userId
	 * @return the fully populated User
	 */
	InternalAuthroizationUser getFullUser(final String userId);
	
	/**
	 * @param loginId - LoginId object
	 * @return the fully populatedUser
	 */
	InternalAuthroizationUser getFullUser(final AuthorizationManagerLoginId loginId);


    List<String> getUserIdsList();

    List<String> getUserIdsForRoles(Set<String> roleIds);
    List<String> getUserIdsForGroups(Set<String> groupIds);
    List<String> getUserIdsForResources(Set<String> resourceIds);

}
