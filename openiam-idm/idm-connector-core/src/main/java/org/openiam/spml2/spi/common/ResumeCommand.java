package org.openiam.spml2.spi.common;

import org.openiam.connector.type.ResponseType;
import org.openiam.connector.type.ResumeRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public interface ResumeCommand {
    public ResponseType resume(ResumeRequest request);
}
