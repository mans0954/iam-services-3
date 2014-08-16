package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

public interface ReconciliationObjectCommand<T> {
     boolean execute(IdentityDto identity, T object, List<ExtensibleAttribute> attributes);
}
