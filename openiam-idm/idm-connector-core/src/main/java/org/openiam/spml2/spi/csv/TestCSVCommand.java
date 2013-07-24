package org.openiam.spml2.spi.csv;

import org.openiam.connector.type.ResponseType;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

@Service
public class TestCSVCommand extends AbstractCSVCommand {
	public ResponseType test(ManagedSysEntity managedSys) {
		ResponseType response = new ResponseType();
		try {
			this.getUsersFromCSV(managedSys);
		} catch (Exception e) {
			response.setStatus(StatusCodeType.FAILURE);
			response.setRequestID(managedSys.getManagedSysId());
		}
		response.setStatus(StatusCodeType.SUCCESS);
		return response;
	}
}
