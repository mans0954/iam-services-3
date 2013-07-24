package org.openiam.spml2.spi.csv;

import org.openiam.connector.type.*;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

@Service
public class LookupCSVCommand extends AbstractCSVCommand {
	public SearchResponse lookup(LookupRequest reqType) {
        SearchResponse response = new SearchResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("add request called..");

		String principal = reqType.getSearchValue();
		/*
		 * A) Use the targetID to look up the connection information under
		 * managed systems
		 */
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(reqType.getTargetID());

		// Initialise
		try {
            UserValue user = this.lookupObjectInCSV(principal, managedSys);
			if (user != null) {
				response.setStatus(StatusCodeType.SUCCESS);
                response.getUserList().add(user);
			} else
				response.setStatus(StatusCodeType.FAILURE);
		} catch (Exception e) {
			e.printStackTrace();

			log.error(e);
			// return a response object - even if it fails so that it can be
			// logged.
			response.setStatus(StatusCodeType.FAILURE);
			response.setError(ErrorCode.CSV_ERROR);
			response.addErrorMessage(e.toString());

		}
		return response;
	}
}
