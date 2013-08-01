package org.openiam.spml2.spi.common;

import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface AddCommand {
    public ObjectResponse add(CrudRequest reqType);
}
