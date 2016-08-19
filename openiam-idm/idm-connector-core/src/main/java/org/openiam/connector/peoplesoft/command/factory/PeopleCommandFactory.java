package org.openiam.connector.peoplesoft.command.factory;

import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("peopleSoftCommandFactory")
public class PeopleCommandFactory extends AbstractCommandFactory {
	@Autowired
	@Qualifier("addUserPeopleSoftCommand")
	private ConnectorCommand addUserPeopleSoftCommand;
	@Autowired
	@Qualifier("modifyUserPeopleSoftCommand")
	private ConnectorCommand modifyUserPeopleSoftCommand;
	@Autowired
	@Qualifier("deleteUserPeopleSoftCommand")
	private ConnectorCommand deleteUserPeopleSoftCommand;
	@Autowired
	@Qualifier("lookupUserPeopleSoftCommand")
	private ConnectorCommand lookupUserPeopleSoftCommand;
	@Autowired
	@Qualifier("searchUserPeopleSoftCommand")
	private ConnectorCommand searchUserPeopleSoftCommand;
	@Autowired
	@Qualifier("resumeUserPeopleSoftCommand")
	private ConnectorCommand resumeUserPeopleSoftCommand;
	@Autowired
	@Qualifier("setPasswordPeopleSoftCommand")
	private ConnectorCommand setPasswordPeopleSoftCommand;
	@Autowired
	@Qualifier("suspendPeopleSoftCommand")
	private ConnectorCommand suspendPeopleSoftCommand;
	@Autowired
	@Qualifier("testPeopleSoftCommand")
    private ConnectorCommand testPeopleSoftCommand;

	public ConnectorCommand getConnectorCommand(CommandType commandType,
			ExtensibleObjectType extensibleObjectType)
			throws ConnectorDataException {
		String error = String.format(ERROR_PATTERN, commandType,
				extensibleObjectType, "PEOPLESOFT");
		if (ExtensibleObjectType.USER == extensibleObjectType) {
			switch (commandType) {
			case ADD:
				return addUserPeopleSoftCommand;
			case MODIFY:
				return modifyUserPeopleSoftCommand;
			case DELETE:
				return deleteUserPeopleSoftCommand;
			case RESUME:
				return resumeUserPeopleSoftCommand;
			case SET_PASSWORD:
				return setPasswordPeopleSoftCommand;
			case SUSPEND:
				return suspendPeopleSoftCommand;
			case SEARCH:
				return searchUserPeopleSoftCommand;
			 case TEST:
	            return testPeopleSoftCommand;
			case LOOKUP:
				return lookupUserPeopleSoftCommand;
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
