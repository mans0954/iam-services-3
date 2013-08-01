package org.openiam.spml2.spi.csv;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

//@Service
@Deprecated
public class AddCSVCommand extends AbstractCSVCommand {
	public ObjectResponse add(CrudRequest reqType) {
        ObjectResponse response = new ObjectResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("add request called..");

		String targetID = reqType.getTargetID();
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		try {
			ExtensibleUser user = (ExtensibleUser) reqType.getExtensibleObject();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.addUsersToCSV(reqType.getObjectIdentity(), user, managedSys);
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
