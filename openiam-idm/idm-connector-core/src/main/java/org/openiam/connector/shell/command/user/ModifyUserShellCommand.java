package org.openiam.connector.shell.command.user;

import org.openiam.connector.shell.command.base.AbstractCrudShellCommand;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.directory.ModificationItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/20/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("modifyUserShellCommand")
public class ModifyUserShellCommand extends AbstractCrudShellCommand<ExtensibleUser> {
    @Override
    protected String getCommand(String host, String hostlogin, String hostpassword, String objectId, ExtensibleUser object) {
        String firstName = null;
        String lastName = null;
        String init = null;
        String displayName = null;
        String ou = null;
        String role = null;
        boolean change = false;
        String title = null;
        String userState = null;
        String sAMAccountName = null;

        List<ExtensibleAttribute> attrList = object.getAttributes();
        List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
        for (ExtensibleAttribute att : attrList) {
            if (att.getOperation() > 0 && att.getName() != null) {
                if (att.getName().equalsIgnoreCase("firstName")) {
                    firstName = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("lastName")) {
                    lastName = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("sAMAccountName")) {
                    sAMAccountName = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("displayName")) {

                    displayName = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("ou")) {
                    ou = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("role")) {
                    role = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("initials")) {
                    init = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("title")) {
                    title = att.getValue();
                    change = true;
                }
                if (att.getName().equalsIgnoreCase("userState")) {
                    userState = att.getValue();
                    change = true;
                }

            }
        }

        StringBuffer strBuf = new StringBuffer();

        // powershell -command "& c:\powershell\ad\Upd-AdUser.ps1 'cn=Raymond
        // Collins,cn=Users,dc=iamdev,dc=local' 'Ray' 'Coly' 'P' 'Coly, Ray'
        // 'Knight' 0"
        // strBuf.append(" '"+ displayName +"' ");
        // strBuf.append(" '"+ userName +"' ");

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\Upd-AdUser.ps1 ");
        // strBuf.append("cmd /c notepad.exe ");user
        strBuf.append(" 'CN=" + objectId + ",cn=USERS,DC=IAMDEV,DC=LOCAL' ");
        strBuf.append("'" + firstName + "' ");
        strBuf.append("'" + lastName + "' ");
        strBuf.append("'" + init + "' ");
        strBuf.append("'" + displayName + "' ");
        strBuf.append("'" + title + "' ");
        strBuf.append("" + userState + " \"");
        log.debug("**Command line string= " + strBuf.toString());
        // strBuf.append(" '"+ ou +"' ");
        // strBuf.append(" '"+ title +"' \"");
        return  strBuf.toString();
    }
}
