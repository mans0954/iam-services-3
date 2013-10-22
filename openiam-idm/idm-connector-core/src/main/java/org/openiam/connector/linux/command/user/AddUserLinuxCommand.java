package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.util.connect.FileUtil;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.linux.ssh.SSHException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service("addUserLinuxCommand")
public class AddUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected String getCommandScriptHandler(String mSysId) {
        return managedSysService.getManagedSysById(mSysId).getAddHandler();
    }
}
