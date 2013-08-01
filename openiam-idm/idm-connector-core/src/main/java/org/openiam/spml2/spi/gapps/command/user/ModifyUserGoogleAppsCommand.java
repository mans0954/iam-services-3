package org.openiam.spml2.spi.gapps.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.spi.gapps.command.base.AbstractModifyGoogleAppsCommand;
import org.springframework.stereotype.Service;

import javax.naming.directory.ModificationItem;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service("modifyUserGoogleAppsCommand")
public class ModifyUserGoogleAppsCommand extends AbstractModifyGoogleAppsCommand<ProvisionUser> {
    @Override
    protected void modifyObject(PSOIdentifierType psoID, ManagedSysEntity managedSys, List<ModificationType> modTypeList) throws ConnectorDataException {
        String userName = psoID.getID();
        String targetID = psoID.getTargetID();
        String firstName = null;
        String lastName = null;
        boolean change = false;

        ManagedSystemObjectMatch matchObj = this.getMatchObject(targetID, "USER");

        // check if its a rename request
        ExtensibleAttribute origIdentity = isRename(modTypeList);
        if (origIdentity != null) {
            log.debug("Renaming identity: " + origIdentity.getValue());
            renameIdentity(userName, origIdentity.getValue(), managedSys, matchObj);
        } else {
            // get the firstName and lastName values
            for (ModificationType mod : modTypeList) {
                ExtensibleType extType = mod.getData();
                List<ExtensibleObject> extobjectList = extType.getAny();
                for (ExtensibleObject obj : extobjectList) {

                    log.debug("Object:" + obj.getName() + " - operation=" + obj.getOperation());

                    List<ExtensibleAttribute> attrList = obj.getAttributes();
                    List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
                    for (ExtensibleAttribute att : attrList) {
                        if (att.getOperation() != 0 && att.getName() != null) {
                            if (att.getName().equalsIgnoreCase("firstName")) {
                                firstName = att.getValue();
                                change = true;
                            }
                            if (att.getName().equalsIgnoreCase("lastName")) {
                                lastName = att.getValue();
                                change = true;
                            }
                        }
                    }
                }
            }

            // assign to google
            if (change) {
                UserService userService = new UserService(GOOGLE_APPS_USER_SERVICE);

                try {
                    userService.setUserCredentials(managedSys.getUserId(), this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()));
                    String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn() + "/user/2.0";
                    URL updateUrl = new URL(domainUrlBase + "/" + userName);

                    UserEntry entry = new UserEntry();

                    Name n = new Name();
                    if (firstName != null) {
                        n.setGivenName(firstName);
                    }
                    if (lastName != null) {
                        n.setFamilyName(lastName);
                    }
                    entry.addExtension(n);

                    userService.update(updateUrl, entry);

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
    }

    private void renameIdentity(String newIdentity, String origIdentity, ManagedSysEntity managedSys,
                                              ManagedSystemObjectMatch matchObj) throws ConnectorDataException {
        UserService userService = new UserService("gdata-sample-AppsForYourDomain-UserService");

        try {
            userService.setUserCredentials(managedSys.getUserId(), this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()));
            String domainUrlBase = APPS_FEEDS_URL_BASE + matchObj.getBaseDn() + "/user/2.0";
            URL updateUrl = new URL(domainUrlBase + "/" + origIdentity);

            UserEntry entry = new UserEntry();
            Login login = new Login();
            login.setUserName(newIdentity);
            entry.addExtension(login);

            userService.update(updateUrl, entry);

        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, e.getMessage());
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.MALFORMED_REQUEST, e.getMessage());
        } catch (AppsForYourDomainException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.INVALID_CONTAINMENT, e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.CUSTOM_ERROR, e.getMessage());
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private ExtensibleAttribute isRename(List<ModificationType> modTypeList) {
        for (ModificationType mod : modTypeList) {
            ExtensibleType extType = mod.getData();
            List<ExtensibleObject> extobjectList = extType.getAny();
            for (ExtensibleObject obj : extobjectList) {

                log.debug("Object:" + obj.getName() + " - operation="
                        + obj.getOperation());

                List<ExtensibleAttribute> attrList = obj.getAttributes();
                List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
                for (ExtensibleAttribute att : attrList) {
                    if (att.getOperation() != 0 && att.getName() != null) {
                        if (att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                            return att;
                        }
                    }
                }
            }
        }
        return null;
    }
}
