package org.openiam.spml2.spi.csv;

import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.connector.type.UserRequest;
import org.openiam.connector.type.UserResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service
public class AddCSVCommand extends AbstractCSVCommand {
	public UserResponse add(UserRequest reqType) {
        UserResponse response = new UserResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("add request called..");

		String targetID = reqType.getTargetID();
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		try {
			ExtensibleUser user = reqType.getUser();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.addUsersToCSV(reqType.getUserIdentity(), user, managedSys);
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
