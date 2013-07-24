package org.openiam.spml2.spi.csv;

import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.connector.type.ResponseType;
import org.springframework.stereotype.Service;

@Service
public class ReconcileCSVCommand extends AbstractCSVCommand {
	public ResponseType reconcile(ReconciliationConfig conf) {
		ResponseType response = super.reconcile(conf);
		return response;
	}
}
