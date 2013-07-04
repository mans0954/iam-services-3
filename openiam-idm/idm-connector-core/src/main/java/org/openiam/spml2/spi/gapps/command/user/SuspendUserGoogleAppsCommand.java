package org.openiam.spml2.spi.gapps.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.gapps.command.base.AbstractGoogleAppsCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service("suspendUserGoogleAppsCommand")
public class SuspendUserGoogleAppsCommand extends AbstractGoogleAppsCommand<SuspendRequestType, ResponseType> {

    @Override
    public ResponseType execute(SuspendRequestType suspendRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();

        this.init();

        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = suspendRequestType.getPsoID();
        String userName = psoID.getID();

        /* targetID - */
        String targetID = psoID.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        ManagedSystemObjectMatch matchObj = this.getMatchObject(targetID, "USER");

        UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);

        try {
            userService.setUserCredentials(managedSys.getUserId(), this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()));
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + userName);
            URL retrieveUrl = new URL(domainUrlBase + "/" + userName);

            UserEntry userEntry = userService.getEntry(retrieveUrl,
                    UserEntry.class);
            userEntry.getLogin().setSuspended(true);

            userService.update(updateUrl, userEntry);

        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, e.getMessage());
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.MALFORMED_REQUEST, e.getMessage());
        } catch (AppsForYourDomainException e) {
            log.error("Google AppsForYourDomainException=" + e.getCodeName());
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.INVALID_CONTAINMENT, e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } catch (ServiceException e) {
            log.error("Google ServiceException...=" + e.getCodeName());
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.CUSTOM_ERROR, e.getMessage());
        }
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }
}
