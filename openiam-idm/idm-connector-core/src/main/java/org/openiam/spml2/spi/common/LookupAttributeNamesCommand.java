package org.openiam.spml2.spi.common;

import org.openiam.connector.type.LookupAttributeResponse;
import org.openiam.connector.type.LookupRequest;


public interface LookupAttributeNamesCommand {
    public LookupAttributeResponse lookupAttributeNames(LookupRequest reqType);
}
