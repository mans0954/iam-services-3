package org.openiam.connector.csv.command.base;

import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractCSVCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
	@Value("${iam.files.location}")
	protected String pathToCSV;


}
