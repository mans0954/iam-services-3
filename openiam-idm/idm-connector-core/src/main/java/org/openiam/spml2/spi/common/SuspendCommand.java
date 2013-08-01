package org.openiam.spml2.spi.common;

import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.request.SuspendRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface SuspendCommand  {
    public ResponseType suspend(final SuspendRequest request);
}
