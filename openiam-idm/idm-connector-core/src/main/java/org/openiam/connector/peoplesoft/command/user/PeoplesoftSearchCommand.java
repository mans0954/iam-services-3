package org.openiam.connector.peoplesoft.command.user;

import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Implementation class for the Peoplesoft Connector
 */
public class PeoplesoftSearchCommand extends AbstractPeoplesoftCommand<SearchRequest<ExtensibleObject>, SearchResponse> {
    // private static final Log log =
    // LogFactory.getLog(PeoplesoftSearchCommand.class);

    @Override
    public SearchResponse execute(SearchRequest<ExtensibleObject> request) throws ConnectorDataException {
        // TODO Auto-generated method stub
        return null;
    }

}
