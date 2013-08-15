package org.openiam.connector.gapps.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service("deleteUserGoogleAppsCommand")
public class DeleteUserGoogleAppsCommand extends AbstractCrudGoogleAppsCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys) throws ConnectorDataException {
        String userName =  crudRequest.getObjectIdentity();
        ManagedSystemObjectMatch matchObj = this.getMatchObject(crudRequest.getTargetID(), "USER");

        UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);
        try {
            userService.setUserCredentials(managedSys.getUserId(),
                    this.getDecryptedPassword(managedSys.getPswd()));
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
