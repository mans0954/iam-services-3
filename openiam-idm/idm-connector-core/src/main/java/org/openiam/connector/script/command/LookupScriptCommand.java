package org.openiam.connector.script.command;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/18/13 Time: 1:41 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("lookupScriptCommand")
public class LookupScriptCommand extends AbstractScriptCommand<LookupRequest, SearchResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.LOOKUP;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        return msys.getLookupHandler();
    }

}
