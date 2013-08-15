package org.openiam.connector.csv.command.base;

import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractCSVCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
	@Value("${iam.files.location}")
	protected String pathToCSV;


}
