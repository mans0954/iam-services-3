package org.openiam.spml2.spi.csv.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.LookupRequestType;
import org.openiam.spml2.msg.LookupResponseType;

public abstract class AbstractGetCSVCommand <ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<LookupRequestType<ProvisionObject>, LookupResponseType> {
}
