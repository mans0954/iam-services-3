package org.openiam.provision.service;

import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.dto.ProvisionUser;

import java.util.Map;

/**
 * Interface which all pre-processor scripts used in the provisioning process should implement
 *
 * Add User:
 * The following objects are passed to addUser: ProvisionUser and BindingMap. The binding map contains the following keys:
 * matchParam
 * operation : ADD, UPDATE, DELTE
 * sysId : Managed SystemID
 * targetSystemAttributes : Attributes that will be sent to the target system
 * targetSystemIdentity : TargetSystemIdentity
 * lg : Identity
 * context: Spring - WebApplicationContext
 * userRole: Roles that a user belongs to
 * org: Organization
 * targetSystemIdentityStatus : Operation in the resource
 * password
 * user
 *
 * Delete User:
 * The following objects are passed to deleteUser: ProvisionUser and BindingMap. The binding map contains the following keys:
 * IDENTITY: Identity that is being deleted
 * RESOURCE: Resource for which you are deleting a user

 *
 */
public interface PreProcessor <T> {

    /**
     * Provides pre-processing capabilities for each resource that a user is being provisioned into
     *
     * @param object - provisioning object
     * @param bindingMap
     * @return
     */
    int add(T object, Map<String, Object> bindingMap);
    int modify(T object, Map<String, Object> bindingMap);
    int delete(T object, Map<String, Object> bindingMap);
    int setPassword( Map<String, Object> bindingMap);

}
