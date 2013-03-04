import java.util.Map;
import org.openiam.am.srvc.service.AuthResourceAttributeMapper;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.context.ApplicationContext;

public class JuiceRolesGroovy implements AuthResourceAttributeMapper {
	
	private ApplicationContext ctx;
	private Map<String, UserAttributeEntity> entityMap;
	
	public String mapAttribute() {
		System.out.println("Attempting to get juice roles");
		String retVal = "foo|bar";
		return retVal;
	}
    
	public void init(Map<String, Object> bindingMap) {
		this.entityMap = bindingMap.get("userAttributeMap");
		this.ctx = bindingMap.get("applicationContext");
	}
}