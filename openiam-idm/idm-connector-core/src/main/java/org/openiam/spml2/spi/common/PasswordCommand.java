package org.openiam.spml2.spi.common;

import org.openiam.connector.type.PasswordRequest;
import org.openiam.connector.type.ResponseType;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface PasswordCommand {
    public ResponseType setPassword(PasswordRequest reqType);
}
