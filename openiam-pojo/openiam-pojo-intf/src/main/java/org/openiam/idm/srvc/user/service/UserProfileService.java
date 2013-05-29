package org.openiam.idm.srvc.user.service;

import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.token.CreateUserToken;

public interface UserProfileService {

	public void saveUserProfile(final UserProfileRequestModel request) throws Exception;
	public CreateUserToken createNewUserProfile(final NewUserProfileRequestModel request) throws Exception;
	public void validate(final NewUserProfileRequestModel request) throws Exception;
}
