import org.openiam.am.srvc.service.AbstractAuthResourceAttributeMapper

public class TestAttributeMapper extends AbstractAuthResourceAttributeMapper {

    @Override
    protected String mapValue(String value) {
        return value.substring(value.length()-5);
    }

    @Override
    protected String getAttributeName() {
        return "DriverLicense";
    }
}