package org.openiam.connector.gapps.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service("setPasswordGoogleAppsCommand")
public class SetPasswordGoogleAppsCommand extends AbstractGoogleAppsCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        this.init();
        String userName = passwordRequest.getObjectIdentity();
        ConnectorConfiguration config =  getConfiguration(passwordRequest.getTargetID(), ConnectorConfiguration.class);
        ManagedSystemObjectMatch matchObj = getMatchObject(passwordRequest.getTargetID(), "USER");

        UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);

        try {
            userService.setUserCredentials(config.getManagedSys().getUserId(),
                    this.getDecryptedPassword(config.getManagedSys().getUserId(), config.getManagedSys().getPswd()));
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn() + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + userName);

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setPassword(passwordRequest.getPassword());
            entry.addExtension(login);

            userService.update(updateUrl, entry);
            return respType;
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
