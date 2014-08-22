package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

public interface ReconciliationObjectCommand<T> {
     boolean execute(ReconciliationSituation config, String principal, String managedSysID,T object, List<ExtensibleAttribute> attributes);
}
