package org.openiam.connector.soap.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("soapCommandFactory")
public class SoapCommandFactory extends AbstractCommandFactory {
	@Autowired
	@Qualifier("addUserSoapCommand")
	private ConnectorCommand addUserSoapCommand;
	@Autowired
	@Qualifier("modifyUserSoapCommand")
	private ConnectorCommand modifyUserSoapCommand;
	@Autowired
	@Qualifier("deleteUserSoapCommand")
	private ConnectorCommand deleteUserSoapCommand;
	@Autowired
	@Qualifier("lookupUserSoapCommand")
	private ConnectorCommand lookupUserSoapCommand;
	@Autowired
	@Qualifier("searchUserSoapCommand")
	private ConnectorCommand searchUserSoapCommand;
	@Autowired
	@Qualifier("resumeSoapCommand")
	private ConnectorCommand resumeSoapCommand;
	@Autowired
	@Qualifier("setPasswordSoapCommand")
	private ConnectorCommand setPasswordSoapCommand;
	@Autowired
	@Qualifier("suspendSoapCommand")
	private ConnectorCommand suspendSoapCommand;
	@Autowired
	@Qualifier("testSoapCommand")
    private ConnectorCommand testSoapCommand;

	public ConnectorCommand getConnectorCommand(CommandType commandType,
			ExtensibleObjectType extensibleObjectType)
			throws ConnectorDataException {
		String error = String.format(ERROR_PATTERN, commandType,
				extensibleObjectType, "Rest");
		System.out.println("CommandType="+commandType);
		if (ExtensibleObjectType.USER == extensibleObjectType) {
			switch (commandType) {
			case ADD:
				return addUserSoapCommand;
			case MODIFY:
				return modifyUserSoapCommand;
			case DELETE:
				return deleteUserSoapCommand;
			case RESUME:
				return resumeSoapCommand;
			case SET_PASSWORD:
				return setPasswordSoapCommand;
			case SUSPEND:
				return suspendSoapCommand;
			case SEARCH:
				return searchUserSoapCommand;
			 case TEST:
	            return testSoapCommand;
			case LOOKUP:
				return lookupUserSoapCommand;
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
