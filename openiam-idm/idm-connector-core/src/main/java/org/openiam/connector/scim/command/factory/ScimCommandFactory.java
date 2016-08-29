package org.openiam.connector.scim.command.factory;

import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("scimCommandFactory")
public class ScimCommandFactory extends AbstractCommandFactory {
	@Autowired
	@Qualifier("addUserScimCommand")
	private ConnectorCommand addUserScimCommand;
	@Autowired
	@Qualifier("modifyUserScimCommand")
	private ConnectorCommand modifyUserScimCommand;
	@Autowired
	@Qualifier("deleteUserScimCommand")
	private ConnectorCommand deleteUserScimCommand;
	@Autowired
	@Qualifier("lookupUserScimCommand")
	private ConnectorCommand lookupUserScimCommand;
	@Autowired
	@Qualifier("searchUserScimCommand")
	private ConnectorCommand searchUserScimCommand;
	@Autowired
	@Qualifier("resumeScimCommand")
	private ConnectorCommand resumeScimCommand;
	@Autowired
	@Qualifier("setPasswordScimCommand")
	private ConnectorCommand setPasswordScimCommand;
	@Autowired
	@Qualifier("suspendScimCommand")
	private ConnectorCommand suspendScimCommand;
	@Autowired
	@Qualifier("testScimCommand")
    private ConnectorCommand testScimCommand;

	public ConnectorCommand getConnectorCommand(CommandType commandType,
			ExtensibleObjectType extensibleObjectType)
			throws ConnectorDataException {
		String error = String.format(ERROR_PATTERN, commandType,
				extensibleObjectType, "Scim");
		if (ExtensibleObjectType.USER == extensibleObjectType) {
			switch (commandType) {
			case ADD:
				return addUserScimCommand;
			case MODIFY:
				return modifyUserScimCommand;
			case DELETE:
				return deleteUserScimCommand;
			case RESUME:
				return resumeScimCommand;
			case SET_PASSWORD:
				return setPasswordScimCommand;
			case SUSPEND:
				return suspendScimCommand;
			case SEARCH:
				return searchUserScimCommand;
			 case TEST:
	            return testScimCommand;
			case LOOKUP:
				return lookupUserScimCommand;
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
