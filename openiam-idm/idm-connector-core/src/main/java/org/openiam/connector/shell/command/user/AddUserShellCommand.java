package org.openiam.connector.shell.command.user;

import org.openiam.connector.shell.command.base.AbstractCrudShellCommand;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/19/13
 * Time: 11:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addUserShellCommand")
public class AddUserShellCommand extends AbstractCrudShellCommand<ExtensibleUser> {
    @Override
    protected String getCommand(String host, String hostlogin, String hostpassword, String userName, ExtensibleUser object) {
        String password = null;
        String givenName = null;
        String lastName = null;
        String displayName = null;
        String principalName = null;
        String sAMAccountName = null;
        String middleInit = null;
        String email = null;
        String nickname = null;
        String title = null;
        String getExchange = null;
        String userState = null;

        List<ExtensibleAttribute> attrList = object.getAttributes();
        for (ExtensibleAttribute att : attrList) {
            String name = att.getName();
            String value = att.getValue();

            if (name.equalsIgnoreCase("password")) {
                password = getAttributeValue("password", attrList);
            }
            if (name.equalsIgnoreCase("firstName")) {
                givenName = value;

            }
            if (name.equalsIgnoreCase("lastName")) {
                lastName = value;
            }
            if (name.equalsIgnoreCase("displayName")) {
                displayName = value;

            }
            if (name.equalsIgnoreCase("principalName")) {
                principalName = value;
            }
            if (name.equalsIgnoreCase("sAMAccountName")) {
                sAMAccountName = value;
            }
            if (name.equalsIgnoreCase("middleInit")) {
                middleInit = value;
            }
            if (name.equalsIgnoreCase("email")) {
                email = value;
            }
            if (name.equalsIgnoreCase("nickname")) {
                nickname = value;
            }
            if (name.equalsIgnoreCase("getExchange")) {
                getExchange = value;
            }
            if (name.equalsIgnoreCase("title")) {
                title = value;
            }
            if (name.equalsIgnoreCase("userState")) {
                userState = value;
            }
        }

        /*
         * $adHost = $args[0] $user= $args[1] $userpswd= $args[2] $cn= $args[3]
         * $principalName = $args[4] $samAccountName = $args[5] $pswd = $args[6]
         * $givenname = $args[7] $sn = $args[8] $middleInit = $args[9]
         * $displayName = $args[10] $ou = $args[11]
         */

        // powershell.exe -command "&
        // C:\appserver\apache-tomcat-6.0.26\powershell\create.ps1 displayName
        // principalName firstName middleInit lastName password"

        StringBuffer strBuf = new StringBuffer();

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\Add-UserActiveDir.ps1 ");
        strBuf.append("'" + host + "' ");
        strBuf.append("'" + hostlogin + "' ");
        strBuf.append("'" + hostpassword + "' ");
        strBuf.append("'" + userName + "' ");
        strBuf.append("'" + principalName + "' ");
        strBuf.append("'" + sAMAccountName + "' ");
        strBuf.append("'" + password + "' ");
        strBuf.append("'" + givenName + "' ");
        strBuf.append("'" + lastName + "' ");
        strBuf.append("'" + middleInit + "' ");
        strBuf.append("'" + displayName + "' ");
        strBuf.append("'" + userState + "' ");
        strBuf.append("'" + email + "' ");
        strBuf.append("'" + nickname + "' ");
        strBuf.append("'" + getExchange + "' ");
        strBuf.append("'" + title + "' \" ");

        return strBuf.toString();
    }
}
