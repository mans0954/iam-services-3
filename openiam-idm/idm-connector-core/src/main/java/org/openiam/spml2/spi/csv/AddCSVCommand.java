package org.openiam.spml2.spi.csv;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.AddResponseType;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.openiam.spml2.msg.StatusCodeType;
import org.springframework.stereotype.Service;

@Service
public class AddCSVCommand extends AbstractCSVCommand {
	public AddResponseType add(AddRequestType<ProvisionUser> reqType) {
		AddResponseType response = new AddResponseType();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("add request called..");

		PSOIdentifierType psoID = reqType.getPsoID();
		String targetID = reqType.getTargetID();
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		try {
			ProvisionUser user = reqType.getProvisionObject();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.addUsersToCSV(psoID.getID(), user.getUser(), managedSys);
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
