package org.openiam.idm.srvc.user.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.ProfilePicture;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public interface UserProfileService {

	void saveUserProfile(final UserProfileRequestModel request) throws Exception;
	//public CreateUserToken createNewUserProfile(final NewUserProfileRequestModel request) throws Exception;
    void validate(final NewUserProfileRequestModel request) throws Exception;

    ProfilePicture getProfilePictureById(String picId);

    ProfilePictureEntity getProfilePictureByUserId(String userId);

    void saveProfilePicture(ProfilePictureEntity pic) throws Exception;

    void deleteProfilePictureById(String picId) throws Exception;

    void deleteProfilePictureByUserId(String userId) throws Exception;

}
