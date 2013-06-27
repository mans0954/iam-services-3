package org.openiam.spml2.spi.common;

import org.openiam.spml2.msg.LookupAttributeRequestType;
import org.openiam.spml2.msg.LookupAttributeResponseType;

@Deprecated
public interface LookupAttributeNamesCommand {
    public LookupAttributeResponseType lookupAttributeNames(LookupAttributeRequestType reqType);
}
