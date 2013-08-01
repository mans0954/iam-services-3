package org.openiam.spml2.spi.common;

import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface LookupCommand {
    public SearchResponse lookup(LookupRequest reqType);
}
