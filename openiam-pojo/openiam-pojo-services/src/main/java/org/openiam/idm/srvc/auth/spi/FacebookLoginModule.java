package org.openiam.idm.srvc.auth.spi;

import org.openiam.idm.srvc.auth.spi.social.FacebookProfile;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 30.03.15.
 */
@Component("facebookLoginModule")
public class FacebookLoginModule extends AbstractSocialLoginModule<FacebookProfile> {

}
