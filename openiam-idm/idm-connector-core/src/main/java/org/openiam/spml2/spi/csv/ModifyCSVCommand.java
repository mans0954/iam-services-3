package org.openiam.spml2.spi.csv;

import java.util.List;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.connector.type.*;
import org.openiam.idm.parser.csv.CSVParser;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;

import org.openiam.provision.type.ExtensibleUser;

import org.springframework.stereotype.Service;

@Service
public class ModifyCSVCommand extends AbstractCSVCommand {

	public UserResponse modify(UserRequest reqType) {
        UserResponse response = new UserResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("modify request called..");

		/* targetID - */
		String targetID = reqType.getTargetID();

		// Data sent with request - Data must be present in the request per the
		// spec
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		// Initialise
		try {
			ExtensibleUser user = reqType.getUser();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.updatePUser(
					new ReconciliationObject<ExtensibleUser>(reqType.getUserIdentity(), user),
					managedSys);
		} catch (Exception e) {
			e.printStackTrace();

			log.error(e);
			response.setStatus(StatusCodeType.FAILURE);
			response.setError(ErrorCode.CSV_ERROR);
			response.addErrorMessage(e.toString());

		}
		return response;
	}

	public UserResponse delete(UserRequest reqType) {
        UserResponse response = new UserResponse();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("modify request called..");

		/* targetID - */
		String targetID = reqType.getTargetID();

		// Data sent with request - Data must be present in the request per the
		// spec
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		// Initialise
		try {
			ExtensibleUser user = reqType.getUser();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.deleteUser(reqType.getUserIdentity(), user, managedSys);
		} catch (Exception e) {
			e.printStackTrace();

			log.error(e);
			response.setStatus(StatusCodeType.FAILURE);
			response.setError(ErrorCode.CSV_ERROR);
			response.addErrorMessage(e.toString());

		}
		return response;
	}

	protected void updatePUser(ReconciliationObject<ExtensibleUser> newUser,
			ManagedSysEntity managedSys) throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		provisionUserCSVParser.update(newUser, managedSys, attrMapList,
				CSVSource.IDM);
	}

	/**
	 * @param provisionUserCSVParser
	 *            the provisionUserCSVParser to set
	 */
	public void setProvisionUserCSVParser(
			CSVParser<ExtensibleUser> provisionUserCSVParser) {
		this.provisionUserCSVParser = provisionUserCSVParser;
	}

}
