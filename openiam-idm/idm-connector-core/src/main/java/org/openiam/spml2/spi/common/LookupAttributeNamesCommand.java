package org.openiam.spml2.spi.common;

import org.openiam.connector.type.response.LookupAttributeResponse;
import org.openiam.connector.type.request.LookupRequest;

@Deprecated
public interface LookupAttributeNamesCommand {
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType);
}
