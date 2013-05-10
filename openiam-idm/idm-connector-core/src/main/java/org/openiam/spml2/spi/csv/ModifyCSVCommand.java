package org.openiam.spml2.spi.csv;

import java.util.List;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.ModifyResponseType;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("modifyCommand")
public class ModifyCSVCommand extends AbstractCSVCommand {
	@Autowired
	private org.openiam.idm.parser.csv.CSVParser<ProvisionUser> provisionUserCSVParser;

	public ModifyResponseType modify(ModifyRequestType reqType) {
		ModifyResponseType response = new ModifyResponseType();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("modify request called..");

		PSOIdentifierType psoID = reqType.getPsoID();
		/* targetID - */
		String targetID = psoID.getTargetID();

		// Data sent with request - Data must be present in the request per the
		// spec
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		// Initialise
		try {
			ProvisionUser user = reqType.getpUser();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.updatePUser(
					new ReconciliationObject<ProvisionUser>(psoID.getID(), user),
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

	public ResponseType delete(DeleteRequestType reqType) {
		ResponseType response = new ResponseType();
		response.setStatus(StatusCodeType.SUCCESS);
		log.debug("modify request called..");

		PSOIdentifierType psoID = reqType.getPsoID();
		/* targetID - */
		String targetID = psoID.getTargetID();

		// Data sent with request - Data must be present in the request per the
		// spec
		ManagedSysEntity managedSys = managedSysService
				.getManagedSysById(targetID);

		// Initialise
		try {
			ProvisionUser user = reqType.getpUser();
			if (user == null) {
				response.setStatus(StatusCodeType.FAILURE);
				response.setError(ErrorCode.CSV_ERROR);
				response.addErrorMessage("Sync object is null");
			}
			this.deleteUser(psoID.getID(), user, managedSys);
		} catch (Exception e) {
			e.printStackTrace();

			log.error(e);
			response.setStatus(StatusCodeType.FAILURE);
			response.setError(ErrorCode.CSV_ERROR);
			response.addErrorMessage(e.toString());

		}
		return response;
	}

	protected void updatePUser(ReconciliationObject<ProvisionUser> newUser,
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
			CSVParser<ProvisionUser> provisionUserCSVParser) {
		this.provisionUserCSVParser = provisionUserCSVParser;
	}
}
