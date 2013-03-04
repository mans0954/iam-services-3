import java.util.Map;
import org.openiam.am.srvc.service.AuthResourceAttributeMapper;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.context.ApplicationContext;

public class ZcaseEndUserGroovy implements AuthResourceAttributeMapper {
	
	private ApplicationContext ctx;
	private Map<String, UserAttributeEntity> entityMap;
	
	public String mapAttribute() {
		String retVal = null;
		if(entityMap != null && entityMap.containsKey("ZCASE_ENDUSER")) {
			retVal = entityMap.get("ZCASE_ENDUSER").getValue();
		}
		return retVal;
	}
    
	public void init(Map<String, Object> bindingMap) {
		this.entityMap = bindingMap.get("userAttributeMap");
		this.ctx = bindingMap.get("applicationContext");
	}
}