package org.openiam.spml2.spi.common;

import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface DeleteCommand {
    public ObjectResponse delete(final CrudRequest reqType);
}
