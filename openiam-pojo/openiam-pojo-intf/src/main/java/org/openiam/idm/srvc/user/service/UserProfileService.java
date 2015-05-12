package org.openiam.idm.srvc.user.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.ProfilePicture;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

public interface UserProfileService {

	public void saveUserProfile(final UserProfileRequestModel request) throws Exception;
	//public CreateUserToken createNewUserProfile(final NewUserProfileRequestModel request) throws Exception;
	public void validate(final NewUserProfileRequestModel request) throws Exception;

    public ProfilePicture getProfilePictureById(String picId);

    public ProfilePictureEntity getProfilePictureByUserId(String userId);

    public void saveProfilePicture(ProfilePictureEntity pic) throws Exception;

    public void deleteProfilePictureById(String picId) throws Exception;

    public void deleteProfilePictureByUserId(String userId) throws Exception;

}
