import org.openiam.connector.common.command.AbstractCommand
import org.openiam.connector.type.ConnectorDataException
import org.openiam.connector.type.constant.StatusCodeType
import org.openiam.connector.type.request.RequestType
import org.openiam.connector.type.response.ResponseType

class TestScriptConnector extends AbstractCommand<RequestType, ResponseType>{

    @Override
    public ResponseType execute(RequestType request) throws ConnectorDataException {
        ResponseType rt =new ResponseType();
        rt.setStatus(StatusCodeType.SUCCESS);
        return rt;
    }
}
