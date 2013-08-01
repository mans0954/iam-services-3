package org.openiam.spml2.spi.csv;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.connector.type.response.ResponseType;
import org.springframework.stereotype.Service;

//@Service
//@Deprecated
public class ReconcileCSVCommand extends AbstractCSVCommand {
	public ResponseType reconcile(ReconciliationConfig conf) {
		ResponseType response = super.reconcile(conf);
		return response;
	}
}
