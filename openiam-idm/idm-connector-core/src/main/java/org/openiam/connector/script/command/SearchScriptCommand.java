package org.openiam.connector.script.command;

import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.spml2.constants.CommandType;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/8/13
 * Time: 1:28 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("searchScriptCommand")
public class SearchScriptCommand extends AbstractScriptCommand<SearchRequest,SearchResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.SEARCH;
    }
}
