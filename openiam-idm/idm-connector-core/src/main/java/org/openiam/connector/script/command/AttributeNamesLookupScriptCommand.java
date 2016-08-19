package org.openiam.connector.script.command;


import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.provision.request.LookupRequest;
import org.openiam.base.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

@Service("attributeNamesLookupScriptCommand")
public class AttributeNamesLookupScriptCommand extends AbstractScriptCommand<LookupRequest, SearchResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.LOOKUP_ATTRIBUTE_NAME;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        return msys.getAttributeNamesHandler();
    }

}