package org.openiam.spml2.spi.common;

import org.openiam.connector.type.UserRequest;
import org.openiam.connector.type.UserResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface DeleteCommand {
    public UserResponse delete(final UserRequest reqType);
}
