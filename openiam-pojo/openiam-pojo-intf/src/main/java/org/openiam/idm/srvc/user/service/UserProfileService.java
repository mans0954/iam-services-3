package org.openiam.idm.srvc.user.service;

import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public interface UserProfileService {

	public void saveUserProfile(final UserProfileRequestModel request) throws Exception;
	//public CreateUserToken createNewUserProfile(final NewUserProfileRequestModel request) throws Exception;
	public void validate(final NewUserProfileRequestModel request) throws Exception;
}
