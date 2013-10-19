import org.openiam.base.AttributeOperationEnum
import org.openiam.idm.srvc.org.dto.Organization
import org.openiam.idm.srvc.role.dto.Role
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.idm.srvc.user.util.DelegationFilterHelper

import java.util.*;

import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisioningConstants;
import org.openiam.provision.service.AbstractPreProcessor;
import org.openiam.idm.srvc.org.service.OrganizationDataService;

import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.res.service.ResourceDataService;


/**
 * Pre-processor script that is used with the Provisioning service.
 */
public class ProvisionServicePreProcessor extends AbstractPreProcessor {
    private String ORGANIZATION_ADMIN_ROLEID = "4028818341d1319b0141d20fff360048";

    public int addUser(ProvisionUser user, Map<String, Object> bindingMap) {

        // context to look up spring beans - commonly used beans. Included to help development

        OrganizationDataService orgManager = (OrganizationDataService)context.getBean("orgManager");
        RoleDataService roleDataService = (RoleDataService)context.getBean("roleDataService");
        LoginDataService loginService = (LoginDataService)context.getBean("loginManager");
        ResourceDataService resourceDataService = (ResourceDataService)context.getBean("resourceDataService");


        println("ProvisionServicePreProcessor: AddUser called.");
        println("ProvisionServicePreProcessor: User=" + user.toString());

        showBindingMap(bindingMap);

        //Add Delegation Filter for selected users org if processed user is in Organization Admin role
        for(Role role : user.getRoles()) {
            if (ORGANIZATION_ADMIN_ROLEID.equals(role.getRoleId())) {
                println("Organization Admin");
                Organization organization = user.getPrimaryOrganization();
                if (organization != null) {
                    UserAttribute attr = new UserAttribute(DelegationFilterHelper.DLG_FLT_ORG, organization.getId());
                    attr.setOperation(AttributeOperationEnum.ADD);
                    attr.setId(null);
                    user.getUserAttributes().put(attr.getName(),attr);
                }
            }
        }

        return ProvisioningConstants.SUCCESS;
    }

    public int modifyUser(ProvisionUser user, Map<String, Object> bindingMap){
        // context to look up spring beans

        println("ProvisionServicePreProcessor: ModifyUser called.");
        println("ProvisionServicePreProcessor: User=" + user.toString());

        showBindingMap(bindingMap);





        return ProvisioningConstants.SUCCESS;

    }



    public int deleteUser(ProvisionUser user, Map<String, Object> bindingMap){

        // context to look up spring beans

        println("ProvisionServicePreProcessor: DeleteUser called.");
        println("ProvisionServicePreProcessor: User=" + user.toString());

        showBindingMap(bindingMap);



        return ProvisioningConstants.SUCCESS;
    }

    public int setPassword( PasswordSync passwordSync, Map<String, Object> bindingMap){



        println("ProvisionServicePreProcessor: SetPassword called.");

        showBindingMap(bindingMap);


        return ProvisioningConstants.SUCCESS;

    }

    private void showBindingMap( Map<String, Object> bindingMap){

        // context to look up spring beans


        println("Show binding map");

        for (Map.Entry<String, Object> entry : bindingMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            println("- Key=" + key + "  value=" + value.toString() );
        }


    }





}
