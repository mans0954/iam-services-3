import java.util.Map;
import org.openiam.am.srvc.service.AuthResourceAttributeMapper;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.context.ApplicationContext;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.org.dto.Organization;

public class DivisionNameAMAttribute implements AuthResourceAttributeMapper {
	
	private ApplicationContext ctx;
	private Map<String, UserAttributeEntity> entityMap;
	private UserEntity user;
	
	public String mapAttribute() {
		String retVal = null;
		final OrganizationDataService service = ctx.getBean("orgManager");
		if(user != null && user.getDeptCd() != null && !user.getDeptCd().isEmpty()) {
			final Organization org = service.getOrganization(user.getDeptCd());
			if(org != null) {
				retVal = org.getOrganizationName();
			}
		}
		return retVal;
	}
    
	public void init(Map<String, Object> bindingMap) {
		this.entityMap = bindingMap.get("userAttributeMap");
		this.ctx = bindingMap.get("applicationContext");
		this.user = bindingMap.get("user");
	}
}