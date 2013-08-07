package org.openiam.connector.shell.command.user;

import org.openiam.connector.shell.command.base.AbstractCrudShellCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/20/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteUserShellCommand")
public class DeleteUserShellCommand extends AbstractCrudShellCommand<ExtensibleUser> {
    @Override
    protected String getCommand(String host, String hostlogin, String hostpassword, String userName, ExtensibleUser object) {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\Del-AdUser.ps1 ");
        strBuf.append("'" + host + "' ");
        strBuf.append("'" + userName + "' \" ");
        return  strBuf.toString();
    }
}
