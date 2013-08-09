package org.openiam.connector.gapps.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.ServiceException;
import org.openiam.connector.gapps.command.base.AbstractCrudGoogleAppsCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.type.ConnectorDataException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service("addUserGoogleAppsCommand")
public class AddUserGoogleAppsCommand extends AbstractCrudGoogleAppsCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(CrudRequest<ExtensibleUser> crudRequest, ManagedSysEntity managedSys) throws ConnectorDataException {
        String userName = null;
        String password = null;
        String givenName = null;
        String lastName = null;

        ManagedSystemObjectMatch matchObj = getMatchObject(crudRequest.getTargetID(), "USER");

        ExtensibleObject obj = crudRequest.getExtensibleObject();

        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {
                log.debug("Attr Name=" + att.getName() + " " + att.getValue());

                String name = att.getName();
                String value = att.getValue();

                if (name.equalsIgnoreCase("password")) {
                    password = value;
                }
                if (name.equalsIgnoreCase("firstName")) {
                    givenName = value;
                }
                if (name.equalsIgnoreCase("lastName")) {
                    lastName = value;
                }
        }

        try {
            String decryptedPassword = this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd());

            UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);

            log.debug("google connector login:" + managedSys.getUserId());
            log.debug("google connector PASSWORD:"  + decryptedPassword);

            userService.setUserCredentials(managedSys.getUserId(),decryptedPassword);

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setUserName(userName);
            login.setPassword(password);
            entry.addExtension(login);

            Name name = new Name();
            name.setGivenName(givenName);
            name.setFamilyName(lastName);
            entry.addExtension(name);

            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn()
                    + "/user/2.0";

            log.debug("BASE URL=" + APPS_FEEDS_URL_BASE);

            URL insertUrl = new URL(domainUrlBase);
            userService.insert(insertUrl, entry);

        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.ALREADY_EXISTS);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.MALFORMED_REQUEST);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.UNSUPPORTED_OPERATION);
        }
    }
}