package org.openiam.connector.rest.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("restCommandFactory")
public class RestCommandFactory extends AbstractCommandFactory {
	@Autowired
	@Qualifier("addUserRestCommand")
	private ConnectorCommand addUserRestCommand;
	@Autowired
	@Qualifier("modifyUserRestCommand")
	private ConnectorCommand modifyUserRestCommand;
	@Autowired
	@Qualifier("deleteUserRestCommand")
	private ConnectorCommand deleteUserRestCommand;
	@Autowired
	@Qualifier("lookupUserRestCommand")
	private ConnectorCommand lookupUserRestCommand;
	@Autowired
	@Qualifier("searchUserRestCommand")
	private ConnectorCommand searchUserRestCommand;
	@Autowired
	@Qualifier("resumeRestCommand")
	private ConnectorCommand resumeRestCommand;
	@Autowired
	@Qualifier("setPasswordRestCommand")
	private ConnectorCommand setPasswordRestCommand;
	@Autowired
	@Qualifier("suspendRestCommand")
	private ConnectorCommand suspendRestCommand;
	@Autowired
	@Qualifier("testRestCommand")
	private ConnectorCommand testRestCommand;

	public ConnectorCommand getConnectorCommand(CommandType commandType,
			ExtensibleObjectType extensibleObjectType)
			throws ConnectorDataException {
		String error = String.format(ERROR_PATTERN, commandType,
				extensibleObjectType, "Rest");
		//log.info("CommandType=" + commandType);
		if (ExtensibleObjectType.USER == extensibleObjectType) {
			switch (commandType) {
			case ADD:
				return addUserRestCommand;
			case MODIFY:
				return modifyUserRestCommand;
			case DELETE:
				return deleteUserRestCommand;
			case RESUME:
				return resumeRestCommand;
			case SET_PASSWORD:
				return setPasswordRestCommand;
			case SUSPEND:
				return suspendRestCommand;
			case SEARCH:
				return searchUserRestCommand;
			case TEST:
				return testRestCommand;
			case LOOKUP:
				return lookupUserRestCommand;
			default:
				throw new ConnectorDataException(
						ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
			}
		} else if (ExtensibleObjectType.GROUP == extensibleObjectType) {
			throw new ConnectorDataException(
					ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
		} else {
			throw new ConnectorDataException(
					ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
		}
	}
}
