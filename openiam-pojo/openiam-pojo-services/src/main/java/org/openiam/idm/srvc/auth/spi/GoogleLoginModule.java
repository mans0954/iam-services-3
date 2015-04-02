package org.openiam.idm.srvc.auth.spi;

import org.openiam.idm.srvc.auth.spi.social.GoogleProfile;
import org.springframework.stereotype.Component;


/**
 * Created by alexander on 10.12.14.
 */
@Component("googleLoginModule")
public class GoogleLoginModule  extends AbstractSocialLoginModule<GoogleProfile> {
}
