import org.openiam.connector.common.command.AbstractCommand
import org.openiam.connector.type.ConnectorDataException
import org.openiam.provision.constant.StatusCodeType
import org.openiam.provision.request.RequestType
import org.openiam.base.response.ResponseType

class TestScriptConnector extends AbstractCommand<RequestType, ResponseType>{

    @Override
    public ResponseType execute(RequestType request) throws ConnectorDataException {
        ResponseType rt =new ResponseType();
        rt.setStatus(StatusCodeType.SUCCESS);
        return rt;
    }
}
