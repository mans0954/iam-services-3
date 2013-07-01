package org.openiam.spml2.spi.gapps.command;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.gapps.command.base.AbstarctDeleteGoogleAppsCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service("deleteUserGoogleAppsCommand")
public class DeleteUserGoogleAppsCommand extends AbstarctDeleteGoogleAppsCommand<ProvisionUser> {
    @Override
    protected void deleteObject(PSOIdentifierType psoID, ManagedSysEntity managedSys) throws ConnectorDataException {
        String userName =  psoID.getID();
        ManagedSystemObjectMatch matchObj = this.getMatchObject(psoID.getTargetID(), "USER");

        UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);
        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()));
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn() + "/user/2.0";

            URL deleteUrl = new URL(domainUrlBase + "/" + userName);
            userService.delete(deleteUrl);

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
    }
}
