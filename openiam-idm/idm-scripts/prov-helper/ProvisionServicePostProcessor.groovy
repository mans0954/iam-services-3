import org.openiam.idm.srvc.mngsys.domain.AssociationType
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService
import org.openiam.idm.srvc.user.domain.UserAttributeEntity
import org.openiam.idm.srvc.user.service.UserDataService
import org.openiam.idm.srvc.user.util.DelegationFilterHelper

import java.util.*;

import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.PreProcessor;
import org.openiam.provision.service.ProvisioningConstants;
import org.openiam.provision.service.AbstractPostProcessor;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.user.dto.UserStatusEnum
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;

/**
 * Post-processor script that is used with the Provisioning service.
 */
public class ProvisionServicePostProcessor extends AbstractPostProcessor {
    private String ORGANIZATION_ADMIN_ROLEID = "4028818341d1319b0141d20fff360048";

    public int addUser(ProvisionUser user, Map<String, Object> bindingMap) {
        // context to look up spring beans

        println("ProvisionServicePostProcessor: AddUser called.");
        println("ProvisionServicePostProcessor: User=" + user.toString());

        ManagedSystemWebService managedSystemWebService = (ManagedSystemWebService)context.getBean("managedSysService");

        showBindingMap(bindingMap);
        //Add ApproverAssociation for selected user if he is in Organization Admin and selected Org for him
        for(Role role : user.getRoles()) {
            if (ORGANIZATION_ADMIN_ROLEID.equals(role.getRoleId())) {
                println("Organization Admin");
                Organization organization = user.getPrimaryOrganization();
                if (organization != null) {
                    ApproverAssociation approverAssociation = new ApproverAssociation();
                    approverAssociation.setAssociationEntityId(organization.getId());
                    approverAssociation.setAssociationType(AssociationType.ORGANIZATION);
                    approverAssociation.setApproverEntityType(AssociationType.USER)
                    approverAssociation.setApproverEntityId(user.getUserId());
                    managedSystemWebService.saveApproverAssociation(approverAssociation);
                }
            }
        }


        return ProvisioningConstants.SUCCESS;
    }

    public int modifyUser(ProvisionUser user, Map<String, Object> bindingMap){

        // context to look up spring beans


        println("ProvisionServicePostProcessor: ModifyUser called.");
        println("ProvisionServicePostProcessor: User=" + user.toString());


        // if the status is active, then set the flag for sending emails
        User origUser = (User)bindingMap.get("userBeforeModify");

        showBindingMap(bindingMap);

        return ProvisioningConstants.SUCCESS;

    }



    public int deleteUser(ProvisionUser user, Map<String, Object> bindingMap){

        // context to look up spring beans

        println("ProvisionServicePostProcessor: DeleteUser called.");
        println("ProvisionServicePostProcessor: User=" + user.toString());

        showBindingMap(bindingMap);


        return ProvisioningConstants.SUCCESS;
    }

    public int setPassword( PasswordSync passwordSync, Map<String, Object> bindingMap){


        println("ProvisionServicePostProcessor: SetPassword called.");

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
