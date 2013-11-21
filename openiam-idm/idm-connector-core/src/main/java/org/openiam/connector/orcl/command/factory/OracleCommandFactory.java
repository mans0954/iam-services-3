package org.openiam.connector.orcl.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("oracleCommandFactory")
public class OracleCommandFactory extends AbstractCommandFactory {
	@Autowired
	@Qualifier("addUserOracleCommand")
	private ConnectorCommand addUserOracleCommand;
	@Autowired
	@Qualifier("modifyUserOracleCommand")
	private ConnectorCommand modifyUserOracleCommand;
	@Autowired
	@Qualifier("deleteUserOracleCommand")
	private ConnectorCommand deleteUserOracleCommand;
	@Autowired
	@Qualifier("lookupUserOracleCommand")
	private ConnectorCommand lookupUserOracleCommand;
	@Autowired
	@Qualifier("searchUserOracleCommand")
	private ConnectorCommand searchUserOracleCommand;
	@Autowired
	@Qualifier("resumeOracleCommand")
	private ConnectorCommand resumeOracleCommand;
	@Autowired
	@Qualifier("setPasswordOracleCommand")
	private ConnectorCommand setPasswordOracleCommand;
	@Autowired
	@Qualifier("suspendOracleCommand")
	private ConnectorCommand suspendOracleCommand;
	@Autowired
	@Qualifier("testOracleCommand")
    private ConnectorCommand testOracleCommand;

	public ConnectorCommand getConnectorCommand(CommandType commandType,
			ExtensibleObjectType extensibleObjectType)
			throws ConnectorDataException {
		String error = String.format(ERROR_PATTERN, commandType,
				extensibleObjectType, "ORACLE");
		if (ExtensibleObjectType.USER == extensibleObjectType) {
			switch (commandType) {
			case ADD:
				return addUserOracleCommand;
			case MODIFY:
				return modifyUserOracleCommand;
			case DELETE:
				return deleteUserOracleCommand;
			case RESUME:
				return resumeOracleCommand;
			case SET_PASSWORD:
				return setPasswordOracleCommand;
			case SUSPEND:
				return suspendOracleCommand;
			case SEARCH:
				return searchUserOracleCommand;
			 case TEST:
	            return testOracleCommand;
			case LOOKUP:
				return lookupUserOracleCommand;
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
