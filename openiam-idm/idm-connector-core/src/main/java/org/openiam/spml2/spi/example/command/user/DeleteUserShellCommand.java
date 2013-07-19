package org.openiam.spml2.spi.example.command.user;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.example.command.base.AbstractDeleteShellCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/20/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteUserShellCommand")
public class DeleteUserShellCommand extends AbstractDeleteShellCommand<ProvisionUser> {
    @Override
    protected String getDeleteCommand(String host, String hostlogin, String hostpassword, String objectId) {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("cmd /c powershell.exe -command \"& C:\\powershell\\ad\\Del-AdUser.ps1 ");
        strBuf.append("'" + host + "' ");
        strBuf.append("'" + objectId + "' \" ");
        return  strBuf.toString();
    }
}
