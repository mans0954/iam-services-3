package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

public interface ReconciliationObjectCommand<T> {
     boolean execute(ReconciliationSituation config, IdentityDto identity, T object, List<ExtensibleAttribute> attributes);
}
