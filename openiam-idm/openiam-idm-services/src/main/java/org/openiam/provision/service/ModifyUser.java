package org.openiam.provision.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.id.UUIDGen;
import org.openiam.dozer.converter.*;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Helper class for the modifyUser operation in the Provisioning Service.
 *
 * @author administrator
 */
@Component
public class ModifyUser {
    @Autowired
    private RoleDataService roleDataService;
    @Autowired
    private GroupDataService groupManager;
    @Autowired
    private UserDataService userMgr;
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    private OrganizationDataService orgManager;
    
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    
    @Autowired
    private EmailAddressDozerConverter emailAddressDozerConverter;
    
    @Autowired
    private AddressDozerConverter addressDozerConverter;
    
    @Autowired
    private PhoneDozerConverter phoneDozerConverter;
    
    @Autowired
    private UserDozerConverter userDozerConverter;
    
    @Autowired
    private SupervisorDozerConverter supervisorDozerConverter;
    
    @Autowired
    private ManagedSystemService managedSysService;

    private static final Log log = LogFactory.getLog(ModifyUser.class);

    // these instance variables will be used later in the provisioning process
    // when we need to show the difference at the field level
    Map<String, UserAttribute> userAttributes = new HashMap<String, UserAttribute>();
    Set<EmailAddress> emailSet = new HashSet<EmailAddress>();
    Set<Phone> phoneSet = new HashSet<Phone>();
    Set<Address> addressSet = new HashSet<Address>();
    List<Group> groupList = new ArrayList<Group>();
    List<Role> roleList = new ArrayList<Role>();
    List<Role> deleteRoleList = new ArrayList<Role>();
    List<Login> principalList = new ArrayList<Login>();

    public ModifyUser() {
        log.debug("Creating new modifyUser object..");
        init();

    }

    public void init() {
        log.debug("Modify User initialized");
        userAttributes = new HashMap<String, UserAttribute>();
        emailSet = new HashSet<EmailAddress>();
        phoneSet = new HashSet<Phone>();
        addressSet = new HashSet<Address>();
        groupList = new ArrayList<Group>();
        roleList = new ArrayList<Role>();
        deleteRoleList = new ArrayList<Role>();
        principalList = new ArrayList<Login>();
    }

    /**
     * UI may pass an incomplete user object.
     *
     * @param user
     */
    public void addMissingUserComponents(ProvisionUser user) {

        log.debug("addMissingUserComponents() called.");

        // check addresses
        Set<Address> addressSet = user.getAddresses();

        if (addressSet == null || addressSet.isEmpty()) {

            log.debug("- Adding original addressSet to the user object");

            List<Address> addressList = addressDozerConverter.convertToDTOList(userMgr.getAddressList(user.getId()), true);
            if (addressList != null && !addressList.isEmpty()) {

                user.setAddresses(new HashSet<Address>(addressList));

            }
        }

        // check email addresses

        Set<EmailAddress> emailAddressSet = user.getEmailAddresses();
        if (emailAddressSet == null || emailAddressSet.isEmpty()) {

            log.debug("- Adding original emailSet to the user object");

            List<EmailAddress> emailList = emailAddressDozerConverter.convertToDTOList(userMgr.getEmailAddressList(user.getId()), true);
            if (emailList != null && !emailList.isEmpty()) {

                user.setEmailAddresses(new HashSet<EmailAddress>(emailList));

            }

        }

        // check the phone objects
        Set<Phone> phoneSet = user.getPhones();
        if (phoneSet == null || phoneSet.isEmpty()) {

            log.debug("- Adding original phoneSet to the user object");

            List<Phone> phoneList = phoneDozerConverter.convertToDTOList(userMgr.getPhoneList(user.getId()), true);
            if (phoneList != null && !phoneList.isEmpty()) {

                user.setPhones(new HashSet<Phone>(phoneList));

            }

        }

        // check the user attributes
        Map<String, UserAttribute> userAttrSet = user.getUserAttributes();
        if (userAttrSet == null || userAttrSet.isEmpty()) {

            log.debug("- Adding original user attributes to the user object");

            User u = userDozerConverter.convertToDTO(userMgr.getUser(user.getId()), true);
            if (u.getUserAttributes() != null) {
                user.setUserAttributes(u.getUserAttributes());
            }

        }

        // the affiliations
        Set<Organization> affiliationSet = user.getAffiliations();
        if (affiliationSet == null || affiliationSet.isEmpty()) {

            log.debug("- Adding original affiliationList to the user object");

            List<Organization> userAffiliations = orgManager.getOrganizationsForUserLocalized(user.getId(), null, 0,Integer.MAX_VALUE, null);
            if (userAffiliations != null && !userAffiliations.isEmpty()) {

                user.setAffiliations(new HashSet<Organization>(userAffiliations));
            }

        }

    }

    public String updateUser(ProvisionUser user, User origUser) {

        String requestId = UUIDGen.getUUID();

        log.debug("ModifyUser: updateUser called.");

        User newUser = user.getUser();
        updateUserObject(origUser, newUser);

        log.debug("User object pending update:" + origUser);

        final UserEntity entity = userDozerConverter.convertToEntity(origUser, true);
        userMgr.updateUserWithDependent(entity, true);

        return requestId;
    }

    public void updateUserObject(User origUser, User newUser) {

        origUser.updateUser(newUser);

        updateUserEmail(origUser, newUser);
        updatePhone(origUser, newUser);
        updateAddress(origUser, newUser);
    }

    public Map<String, UserAttribute> getUserAttributes() {
        return userAttributes;
    }

    private void updateUserEmail(User origUser, User newUser) {
        Set<EmailAddress> origEmailSet = origUser.getEmailAddresses();
        Set<EmailAddress> newEmailSet = newUser.getEmailAddresses();

        if (origEmailSet == null && newEmailSet != null) {
            log.debug("New email list is not null");
            origEmailSet = new HashSet<EmailAddress>();
            origEmailSet.addAll(newEmailSet);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (EmailAddress em : newEmailSet) {
                em.setOperation(AttributeOperationEnum.ADD);
                this.emailSet.add(em);
            }
            return;
        }

        if ((origEmailSet != null && origEmailSet.size() > 0)
                && (newEmailSet == null || newEmailSet.size() == 0)) {
            log.debug("orig email list is not null and nothing was passed in for the newEmailSet - ie no change");
            for (EmailAddress em : origEmailSet) {
                em.setOperation(AttributeOperationEnum.NO_CHANGE);
                this.emailSet.add(em);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        if (newEmailSet != null) {
            for (EmailAddress em : newEmailSet) {
                if (em.getOperation() == AttributeOperationEnum.DELETE) {
                    // get the email object from the original set of emails so
                    // that we can remove it
                    EmailAddress e = getEmailAddress(em.getEmailId(),
                            origEmailSet);
                    if (e != null) {
                        origEmailSet.remove(e);
                    }
                    emailSet.add(em);
                } else {
                    // check if this address is in the current list
                    // if it is - see if it has changed
                    // if it is not - add it.
                    EmailAddress origEmail = getEmailAddress(em.getEmailId(),
                            origEmailSet);
                    if (origEmail == null) {
                        em.setOperation(AttributeOperationEnum.ADD);
                        origEmailSet.add(em);
                        emailSet.add(em);

                        log.debug("EMAIL ADDRESS -> ADD NEW ADDRESS = "
                                + em.getEmailAddress());

                    } else {
                        if (em.equals(origEmail)) {
                            // not changed
                            em.setOperation(AttributeOperationEnum.NO_CHANGE);
                            emailSet.add(em);
                            log.debug("EMAIL ADDRESS -> NO CHANGE = "
                                    + em.getEmailAddress());
                        } else {
                            // object changed
                            origEmail.updateEmailAddress(em);
                            origEmailSet.add(origEmail);
                            origEmail
                                    .setOperation(AttributeOperationEnum.REPLACE);
                            emailSet.add(origEmail);
                            log.debug("EMAIL ADDRESS -> REPLACE = "
                                    + em.getEmailAddress());
                        }
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        for (EmailAddress e : origEmailSet) {
            EmailAddress newEmail = getEmailAddress(e.getEmailId(), newEmailSet);
            if (newEmail == null) {
                e.setOperation(AttributeOperationEnum.NO_CHANGE);
                emailSet.add(e);
            }
        }

    }

    private EmailAddress getEmailAddress(String id, Set<EmailAddress> emailSet) {
        Iterator<EmailAddress> emailIt = emailSet.iterator();
        while (emailIt.hasNext()) {
            EmailAddress email = emailIt.next();
            if (email.getEmailId() != null) {
                if (email.getEmailId().equals(id)
                        && (id != null && id.length() > 0)) {
                    return email;
                }
            }
        }
        return null;

    }

    private Phone getPhone(String id, Set<Phone> phoneSet) {
        Iterator<Phone> phoneIt = phoneSet.iterator();
        while (phoneIt.hasNext()) {
            Phone phone = phoneIt.next();
            if (phone.getPhoneId() != null) {
                if (phone.getPhoneId().equals(id)
                        && (id != null && id.length() > 0)) {
                    return phone;
                }
            }
        }
        return null;

    }

    private Address getAddress(String id, Set<Address> addressSet) {
        Iterator<Address> addressIt = addressSet.iterator();
        while (addressIt.hasNext()) {
            Address adr = addressIt.next();
            if (adr.getAddressId() != null) {
                if (adr.getAddressId().equals(id)
                        && (id != null && id.length() > 0)) {
                    return adr;
                }
            }
        }
        return null;

    }

    private Group getGroup(String id, List<Group> origGroupList) {
        for (Group g : origGroupList) {
            if (g.getId().equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }

    private Role getRole(String id, List<Role> roleList) {
        for (Role rl : roleList) {
            if (rl.getId().equals(id)) {
                return rl;
            }
        }
        return null;
    }

    private Login getPrincipal(Login loginId, List<Login> loginList) {
        for (Login lg : loginList) {
            if (lg.getManagedSysId().equals(loginId.getManagedSysId())) {
                return lg;
            }
        }
        return null;
    }

    public Login getPrimaryIdentity(String managedSysId) {

        log.debug("Getting identity for ManagedSysId");

        if (principalList == null || principalList.size() == 0) {
            return null;
        }

        log.debug(" - principals ->" + principalList);

        for (Login l : principalList) {
            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {

                log.debug("getPrimaryIdentityEntity() return ->" + l);

                return l;
            }
        }
        log.debug("getPrimaryIdentityEntity() not found. returning null");
        return null;
    }

    private void updatePhone(User origUser, User newUser) {
        Set<Phone> origPhoneSet = origUser.getPhones();
        Set<Phone> newPhoneSet = newUser.getPhones();

        if (origPhoneSet == null && newPhoneSet != null) {
            log.debug("New email list is not null");
            origPhoneSet = new HashSet<Phone>();
            origPhoneSet.addAll(newPhoneSet);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (Phone ph : newPhoneSet) {
                ph.setOperation(AttributeOperationEnum.ADD);
                phoneSet.add(ph);
            }
            return;
        }

        if ((origPhoneSet != null && origPhoneSet.size() > 0)
                && (newPhoneSet == null || newPhoneSet.size() == 0)) {
            log.debug("orig phone list is not null and nothing was passed in for the newPhoneSet - ie no change");
            for (Phone ph : origPhoneSet) {
                ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                this.phoneSet.add(ph);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        if (newPhoneSet != null) {
            for (Phone ph : newPhoneSet) {
                if (ph.getOperation() == AttributeOperationEnum.DELETE) {

                    // get the email object from the original set of emails so
                    // that we can remove it
                    Phone e = getPhone(ph.getPhoneId(), origPhoneSet);
                    if (e != null) {
                        origPhoneSet.remove(e);
                    }
                    phoneSet.add(ph);
                } else {
                    // check if this address is in the current list
                    // if it is - see if it has changed
                    // if it is not - add it.

                    Phone origPhone = getPhone(ph.getPhoneId(), origPhoneSet);
                    if (origPhone == null) {
                        ph.setOperation(AttributeOperationEnum.ADD);
                        origPhoneSet.add(ph);
                        phoneSet.add(ph);
                    } else {
                        if (ph.equals(origPhone)) {
                            // not changed
                            ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                            phoneSet.add(ph);
                        } else {
                            // object changed
                            origPhone.updatePhone(ph);
                            origPhoneSet.add(origPhone);
                            origPhone
                                    .setOperation(AttributeOperationEnum.REPLACE);
                            phoneSet.add(origPhone);
                        }
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        for (Phone ph : origPhoneSet) {
            Phone newPhone = getPhone(ph.getPhoneId(), newPhoneSet);
            if (newPhone == null) {
                ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                phoneSet.add(ph);
            }
        }

    }

    private void updateAddress(User origUser, User newUser) {
        Set<Address> origAddressSet = origUser.getAddresses();
        Set<Address> newAddressSet = newUser.getAddresses();

        if (origAddressSet == null && newAddressSet != null) {
            log.debug("New email list is not null");
            origAddressSet = new HashSet<Address>();
            origAddressSet.addAll(newAddressSet);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (Address ph : newAddressSet) {
                ph.setOperation(AttributeOperationEnum.ADD);
                addressSet.add(ph);
            }
            return;
        }

        if ((origAddressSet != null && origAddressSet.size() > 0)
                && (newAddressSet == null || newAddressSet.size() == 0)) {
            log.debug("orig Address list is not null and nothing was passed in for the newAddressSet - ie no change");
            for (Address ph : origAddressSet) {
                ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                addressSet.add(ph);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        for (Address ph : newAddressSet) {
            if (ph.getOperation() == AttributeOperationEnum.DELETE) {

                // get the email object from the original set of emails so that
                // we can remove it
                Address e = getAddress(ph.getAddressId(), origAddressSet);
                if (e != null) {
                    origAddressSet.remove(e);
                }
                addressSet.add(ph);
            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Address");
                Address origAddress = getAddress(ph.getAddressId(),
                        origAddressSet);
                if (origAddress == null) {
                    ph.setOperation(AttributeOperationEnum.ADD);
                    origAddressSet.add(ph);
                    addressSet.add(ph);
                } else {
                    if (ph.equals(origAddress)) {
                        // not changed
                        ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                        addressSet.add(ph);
                    } else {
                        // object changed
                        origAddress.updateAddress(ph);
                        origAddressSet.add(origAddress);
                        origAddress
                                .setOperation(AttributeOperationEnum.REPLACE);
                        addressSet.add(origAddress);
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        for (Address ph : origAddressSet) {
            Address newAddress = getAddress(ph.getAddressId(), newAddressSet);
            if (newAddress == null) {
                ph.setOperation(AttributeOperationEnum.NO_CHANGE);
                addressSet.add(ph);
            }
        }

    }

    /* Update Group Associate */

    public void updateGroupAssociation(String userId,
            List<Group> origGroupList, List<Group> newGroupList) {

        log.debug("updating group associations..");
        log.debug("origGroupList =" + origGroupList);
        log.debug("newGroupList=" + newGroupList);

        if ((origGroupList == null || origGroupList.size() == 0)
                && (newGroupList == null || newGroupList.size() == 0)) {
            return;
        }

        if ((origGroupList == null || origGroupList.size() == 0)
                && (newGroupList != null || newGroupList.size() > 0)) {

            log.debug("New group list is not null");
            origGroupList = new ArrayList<Group>();
            origGroupList.addAll(newGroupList);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (Group g : newGroupList) {
                g.setOperation(AttributeOperationEnum.ADD);
                groupList.add(g);
                this.userMgr.addUserToGroup(g.getId(), userId);
            }
            return;
        }

        if ((origGroupList != null && origGroupList.size() > 0)
                && (newGroupList == null || newGroupList.size() == 0)) {
            log.debug("orig group list is not null and nothing was passed in for the newGroupList - ie no change");
            for (Group g : origGroupList) {
                g.setOperation(AttributeOperationEnum.NO_CHANGE);
                groupList.add(g);
            }
            return;
        }

        // if in new address, but not in old, then add it with operation 1
        // else add with operation 2
        for (Group g : newGroupList) {
            if (g.getOperation() == AttributeOperationEnum.DELETE) {
                log.debug("removing Group :" + g.getId());
                // get the email object from the original set of emails so that
                // we can remove it
                Group grp = getGroup(g.getId(), origGroupList);
                if (grp != null) {
                    this.userMgr.removeUserFromGroup(grp.getId(),
                            userId);
                }
                groupList.add(grp);
            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Group");
                Group origGroup = getGroup(g.getId(), origGroupList);
                if (origGroup == null) {
                    g.setOperation(AttributeOperationEnum.ADD);
                    groupList.add(g);
                    userMgr.addUserToGroup(g.getId(), userId);
                } else {
                    if (g.getId().equals(origGroup.getId())) {
                        // not changed
                        g.setOperation(AttributeOperationEnum.NO_CHANGE);
                        groupList.add(g);
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        for (Group g : origGroupList) {
            Group newGroup = getGroup(g.getId(), newGroupList);
            if (newGroup == null) {
                g.setOperation(AttributeOperationEnum.NO_CHANGE);
                groupList.add(g);
            }
        }

    }

    /* Update Principal List */

    public void updatePrincipalList(String userId, List<Login> origLoginList,
            List<Login> newLoginList, List<Resource> deleteResourceList) {

        log.debug("** updating Principals in modify User.");
        log.debug("- origPrincpalList =" + origLoginList);
        log.debug("- newPrincipalList=" + newLoginList);

        if ((origLoginList == null || origLoginList.size() == 0)
                && (newLoginList == null || newLoginList.size() == 0)) {
            return;
        }

        if ((origLoginList == null || origLoginList.size() == 0)
                && (newLoginList != null || newLoginList.size() > 0)) {

            log.debug("New Principal list is not null, but Original Principal List is null");
            origLoginList = new ArrayList<Login>();
            origLoginList.addAll(newLoginList);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (Login lg : newLoginList) {
                lg.setOperation(AttributeOperationEnum.ADD);
                lg.setUserId(userId);
                final LoginEntity entity = loginDozerConverter.convertToEntity(lg, true);
                principalList.add(lg);
                loginManager.addLogin(entity);
            }
            return;
        }

        if ((origLoginList != null && origLoginList.size() > 0)
                && (newLoginList == null || newLoginList.size() == 0)) {
            log.debug("orig Principal list is not null and nothing was passed in for the newPrincipal list - ie no change");
            for (Login l : origLoginList) {
                l.setOperation(AttributeOperationEnum.NO_CHANGE);
                if (notInDeleteResourceList(l, deleteResourceList)) {
                    l.setStatus(LoginStatusEnum.ACTIVE);
                    l.setAuthFailCount(0);
                    l.setIsLocked(0);
                    l.setPasswordChangeCount(0);
                    // reset the password from the primary identity
                    // get the primary identity for this user
                    LoginEntity primaryIdentity = loginManager.getPrimaryIdentity(l
                            .getUserId());
                    if (primaryIdentity != null) {
                        log.debug("Identity password reset to: "
                                + primaryIdentity.getPassword());
                        l.setPassword(primaryIdentity.getPassword());
                    }

                    final LoginEntity entity = loginDozerConverter.convertToEntity(l, true);
                    loginManager.updateLogin(entity);
                }
                principalList.add(l);
            }
            return;
        }

        // if in new login, but not in old, then add it with operation 1
        // else add with operation 2
        log.debug("New Principal List is not null and OriginalList is not null - Compare the list of identities.");

        for (Login l : newLoginList) {

            if (l.getOperation() == AttributeOperationEnum.DELETE) {

                log.debug("removing Login :" + l);
                // get the email object from the original set of emails so that
                // we can remove it
                Login lg = getPrincipal(l, origLoginList);

                if (lg != null) {
                    lg.setStatus(LoginStatusEnum.INACTIVE);
                    final LoginEntity entity = loginDozerConverter.convertToEntity(l, true);
                    loginManager.updateLogin(entity);

                    log.debug("Login updated with status of INACTIVE in IdM database.  ");
                }
                principalList.add(l);

            } else {

                // check if this login is in the current list
                // if it is - see if it has changed
                // if it is not - add it.
                log.debug("evaluate Login");
                Login origLogin = getPrincipal(l, origLoginList);
                log.debug("OrigLogin found=" + origLogin);
                if (origLogin == null) {
                    l.setOperation(AttributeOperationEnum.ADD);
                    l.setUserId(userId);
                    principalList.add(l);
                    final LoginEntity entity = loginDozerConverter.convertToEntity(l, true);
                    loginManager.addLogin(entity);

                } else {
                    if (l.equals(origLogin)) {
                        // not changed
                        log.debug("Identities are equal - No Change");
                        log.debug("OrigLogin status=" + origLogin.getStatus());

                        // if the request contains a password, then set the
                        // password
                        // as part of the modify request

                        if (l.getPassword() != null
                                && !l.getPassword().equals(
                                        origLogin.getPassword())) {
                            // update the password

                            log.debug("Password change detected during synch process");

                            Login newLg = loginDozerConverter.convertDTO(origLogin, true);
                            try {
                                newLg.setPassword(loginManager.encryptPassword(
                                        l.getUserId(), l.getPassword()));
                            } catch (Exception e) {
                                log.error(e);
                                e.printStackTrace();
                            }
                            loginManager.changeIdentityName(newLg
                                    .getLogin(), newLg.getPassword(), newLg
                                    .getUserId(), newLg
                                    .getManagedSysId());
                            principalList.add(newLg);
                        } else {
                            log.debug("Updating Identity in IDM repository");
                            if (l.getOperation() == AttributeOperationEnum.REPLACE) {
                                // user set the replace flag
                            	final LoginEntity entity = loginDozerConverter.convertToEntity(l, true);
                                loginManager.updateLogin(entity);
                                principalList.add(l);
                            } else {

                                log.debug("Flagged identity with NO_CHANGE attribute");

                                l.setOperation(AttributeOperationEnum.NO_CHANGE);
                                principalList.add(l);
                            }

                        }

                    } else {
                        log.debug("Identity changed - RENAME");

                        // clone the object
                        Login newLg = loginDozerConverter.convertDTO(origLogin, true);
                        // add it back with the changed identity
                        newLg.setOperation(AttributeOperationEnum.REPLACE);
                        newLg.setLogin(l.getLogin());

                        // encrypt the password and save it
                        String newPassword = l.getPassword();
                        if (newPassword == null) {
                            newLg.setPassword(null);
                        } else {
                            try {
                                newLg.setPassword(loginManager.encryptPassword(
                                        l.getUserId(), newPassword));
                            } catch (Exception e) {
                                log.error(e);
                                e.printStackTrace();
                            }
                        }
                        loginManager.changeIdentityName(newLg
                                .getLogin(), newLg.getPassword(), newLg
                                .getUserId(), newLg.getManagedSysId());
                        // loginManager.addLogin(newLg);

                        // we cannot send the encrypted password to the
                        // connector
                        // set the password back
                        newLg.setPassword(newPassword);
                        // used the match up the
                        newLg.setOrigPrincipalName(origLogin.getLogin());
                        principalList.add(newLg);
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        log.debug("Check if a value is in the original principal list but not in the new Principal List");
        for (Login lg : origLoginList) {
            Login newLogin = getPrincipal(lg, newLoginList);
            if (newLogin == null) {
                lg.setOperation(AttributeOperationEnum.NO_CHANGE);
                principalList.add(lg);
            }
        }

    }

    private boolean notInDeleteResourceList(Login l,
            List<Resource> deleteResourceList) {
        if (deleteResourceList == null) {
            return true;
        }
        for (Resource r : deleteResourceList) {
        	final ManagedSysEntity mSys = managedSysService.getManagedSysByResource(r.getId(), "ACTIVE");
            final String mSysId = (mSys != null) ? mSys.getId() : null;
            if(StringUtils.equalsIgnoreCase(l.getManagedSysId(), mSysId)) {
                return false;
            }
        }
        return true;
    }

    /* User Org Affiliation */

    public void updateUserOrgAffiliation(String userId,
            List<Organization> newOrgList) {
        List<Organization> currentOrgList = orgManager.getOrganizationsForUserLocalized(userId, null, 0, Integer.MAX_VALUE, null);

        if (newOrgList == null) {
            return;
        }

        for (Organization o : newOrgList) {

            boolean inCurList = isCurrentOrgInNewList(o, currentOrgList);

            if (o.getOperation() == null
                    || o.getOperation() == AttributeOperationEnum.ADD
                    || o.getOperation() == AttributeOperationEnum.NO_CHANGE) {

                if (!inCurList) {
                    orgManager.addUserToOrg(o.getId(), userId);
                }

            } else if (o.getOperation() == AttributeOperationEnum.DELETE) {
                if (inCurList) {
                    orgManager.removeUserFromOrg(o.getId(), userId);
                }
            }

        }

    }

    private boolean isCurrentOrgInNewList(Organization newOrg,
            List<Organization> curOrgList) {
        if (curOrgList != null) {
            for (Organization o : curOrgList) {
                if (o.getId().equals(newOrg.getId())) {

                    return true;
                }
            }
        }

        return false;
    }

    /* Role Association */

    public void updateRoleAssociation(String userId, List<Role> origRoleList,
            List<Role> newRoleList, ProvisionUser pUser, Login primaryIdentity) {

        log.debug("updateRoleAssociation():");
        log.debug("-origRoleList =" + origRoleList);
        log.debug("-newRoleList=" + newRoleList);

        // initialize the role lists
        roleList = new ArrayList<Role>();
        deleteRoleList = new ArrayList<Role>();

        UserEntity user = userMgr.getUser(userId);

        if ((origRoleList == null || origRoleList.size() == 0)
                && (newRoleList == null || newRoleList.size() == 0)) {
            return;
        }

        // scneario where the original role list is empty but new roles are
        // passed in on the request
        if ((origRoleList == null || origRoleList.size() == 0)
                && (newRoleList != null || newRoleList.size() > 0)) {

            log.debug("New Role list is not null");
            origRoleList = new ArrayList<Role>();
            origRoleList.addAll(newRoleList);
            // update the instance variable so that it can passed to the
            // connector with the right operation code
            for (Role rl : newRoleList) {
                rl.setOperation(AttributeOperationEnum.ADD);
                roleList.add(rl);

                roleDataService.addUserToRole(rl.getId(), userId);
                
                /*
                logList.add(auditHelper.createLogObject("ADD ROLE", pUser
                        .getRequestorDomain(), pUser.getRequestorLogin(),
                        "IDM SERVICE", user.getCreatedBy(), "0", "USER", user
                                .getUserId(), null, "SUCCESS", null,
                        "USER_STATUS", user.getStatus().toString(), "NA", null,
                        null, null, rl.getRoleId(), pUser.getRequestClientIP(),
                        primaryIdentity.getLogin(), primaryIdentity
                                .getDomainId()));
				*/
                // roleDataService.addUserToRole(rl.getServiceId(),
                // rl.getRoleId(), userId);
            }
            return;
        }

        // roles were originally assigned to this user, but this request does
        // not have any roles.
        // need to ensure that old roles are marked with the no-change operation
        // code.
        if ((origRoleList != null && origRoleList.size() > 0)
                && (newRoleList == null || newRoleList.size() == 0)) {
            log.debug("orig Role list is not null and nothing was passed in for the newRoleList - ie no change");
            for (Role r : origRoleList) {
                r.setOperation(AttributeOperationEnum.NO_CHANGE);
                roleList.add(r);
            }
            return;
        }

        // if in new roleList, but not in old, then add it with operation 1
        // else add with operation 2
        for (Role r : newRoleList) {
            if (r.getOperation() == AttributeOperationEnum.DELETE) {
                // get the email object from the original set of emails so that
                // we can remove it
                Role rl = getRole(r.getId(), origRoleList);
                if (rl != null) {
                    roleDataService.removeUserFromRole(rl.getId(), userId);
                    /*
                    logList.add(auditHelper.createLogObject("REMOVE ROLE",
                            pUser.getRequestorDomain(), pUser.getUser()
                                    .getRequestorLogin(), "IDM SERVICE", user
                                    .getCreatedBy(), "0", "USER", user
                                    .getUserId(), null, "SUCCESS", null,
                            "USER_STATUS", user.getStatus().toString(), "NA",
                            null, null, null, rl.getRoleId(), pUser.getUser()
                                    .getRequestClientIP(), primaryIdentity
                                    .getLogin(), primaryIdentity
                                    .getDomainId()));
					*/
                }
                log.debug("Adding role to deleteRoleList =" + rl);
                this.deleteRoleList.add(rl);

                // need to pass on to connector that a role has been removed so
                // that
                // the connector can also take action on this event.

                roleList.add(r);
            } else {
                // check if this address is in the current list
                // if it is - see if it has changed
                // if it is not - add it.

                Role origRole = getRole(r.getId(), origRoleList);

                log.debug("OrigRole found=" + origRole);

                if (origRole == null) {
                    r.setOperation(AttributeOperationEnum.ADD);
                    roleList.add(r);
                    roleDataService.addUserToRole(r.getId(), userId);
                    /*
                    logList.add(auditHelper.createLogObject("ADD ROLE", pUser.getUser()
                            .getRequestorDomain(), pUser.getRequestorLogin(),
                            "IDM SERVICE", user.getCreatedBy(), "0", "USER",
                            user.getUserId(), null, "SUCCESS", null,
                            "USER_STATUS", user.getStatus().toString(), "NA",
                            null, null, null, r.getRoleId(), pUser.getUser()
                                    .getRequestClientIP(), primaryIdentity
                                    .getLogin(), primaryIdentity
                                    .getDomainId()));
					*/
                    // roleDataService.addUserToRole(r.getServiceId(),
                    // r.getRoleId(), userId);
                } else {
                    // get the user role object
                    log.debug("checking if no_change or replace");
                    // if (r.equals(origRole)) {
                    // UserRole uRole = userRoleAttrEq(r, currentUserRole);
                    if (r.getId().equals(origRole.getId())
                            && userRoleAttrEq(r, origRole)) {
                        // not changed
                        log.debug("- no_change ");
                        r.setOperation(AttributeOperationEnum.NO_CHANGE);
                        roleList.add(r);
                    } else {
                        log.debug("- Attr not eq - replace");
                        r.setOperation(AttributeOperationEnum.REPLACE);
                        roleList.add(r);

                        // object changed
                        // UserRole ur = new UserRole(userId,
                        // r.getServiceId(),
                        // r.getRoleId());
                       /* UserRoleEntity ur = getUserRole(r, currentUserRole);
                        if (ur == null) {*/
                            roleDataService.addUserToRole(user.getId(),
                                    userId);
                       // }
                        /*
                         * if (ur != null) { if (r.getStartDate() != null) {
                         * ur.setStartDate(r.getStartDate()); } if
                         * (r.getEndDate() != null) {
                         * ur.setEndDate(r.getEndDate()); } if (r.getStatus() !=
                         * null) { ur.setStatus(r.getStatus()); }
                         * roleDataService.updateUserRoleAssoc(ur); } else {
                         * UserRole usrRl = new UserRole(user.getUserId(),
                         * r.getRoleId());
                         * roleDataService.assocUserToRole(usrRl);
                         * 
                         * }
                         */
                    }
                }
            }
        }
        // if a value is in original list and not in the new list - then add it
        // on
        for (Role rl : origRoleList) {
            Role newRole = getRole(rl.getId(), newRoleList);
            if (newRole == null) {
                rl.setOperation(AttributeOperationEnum.NO_CHANGE);
                roleList.add(rl);
            }
        }

    }

    private boolean userRoleAttrEq(Role r, Role origUserRole) {
        // boolean retval = true;

        if (r == null || origUserRole == null) {
            return false;
        }
        if (r.getStatus() != null) {
            if (!r.getStatus().equalsIgnoreCase(origUserRole.getStatus())) {
                return false;
            }
        }
        if (r.getStartDate() != null) {
            if (!r.getStartDate().equals(origUserRole.getStartDate())) {
                return false;
            }
        }
        if (r.getEndDate() != null) {
            if (!r.getEndDate().equals(origUserRole.getEndDate())) {
                return false;
            }
        }
        return true;
    }

    public void updateSupervisor(User user, Supervisor supervisor) {

        if (supervisor == null) {
            return;
        }
        // check the current supervisor - if different - remove it and add the
        // new one.
        List<UserEntity> supervisorList = userMgr.getSuperiors(user.getId(), 0, Integer.MAX_VALUE);
        for (UserEntity s : supervisorList) {
            log.debug("looking to match supervisor ids = "
                    + s.getId() + " "
                    + supervisor.getSupervisor().getId());
            if (s.getId()
                    .equalsIgnoreCase(supervisor.getSupervisor().getId())) {
                return;
            }
            userMgr.removeSupervisor(s.getId(), user.getId());
        }
        log.debug("adding supervisor: "
                + supervisor.getSupervisor().getId());
        supervisor.setEmployee(user);
        final SupervisorEntity entity = supervisorDozerConverter.convertToEntity(supervisor, true);
        userMgr.addSupervisor(entity);
    }

    //

    /**
     * Update the list of attributes with the correct operation values so that they can be
     * passed to the connector
     */
    public ExtensibleUser updateAttributeList(
            org.openiam.provision.type.ExtensibleUser extUser,
            Map<String, String> currentValueMap) {
        if (extUser == null) {
            return null;
        }
        log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        log.debug("updateAttributeList: Current attributeMap = "
                + currentValueMap);

        List<ExtensibleAttribute> extAttrList = extUser.getAttributes();
        if (extAttrList == null) {

            log.debug("Extended user attributes is null");

            return null;
        }

        log.debug("updateAttributeList: New Attribute List = " + extAttrList);
        if (extAttrList != null && currentValueMap == null) {
            for (ExtensibleAttribute attr : extAttrList) {
                attr.setOperation(1);
            }
        } else {

            for (ExtensibleAttribute attr : extAttrList) {
                String nm = attr.getName();
                if (currentValueMap == null) {
                    attr.setOperation(1);
                } else {
                    String curVal = currentValueMap.get(nm);
                    if (curVal == null) {
                        // temp hack
                        if (nm.equalsIgnoreCase("objectclass")) {
                            attr.setOperation(2);
                        } else {
                            log.debug("- Op = 1 - AttrName = " + nm);

                            attr.setOperation(1);
                        }
                    } else {
                        if (curVal.equalsIgnoreCase(attr.getValue())) {
                            log.debug("- Op = 0 - AttrName = " + nm);

                            attr.setOperation(0);
                        } else {

                            log.debug("- Op = 2 - AttrName = " + nm);

                            attr.setOperation(2);
                        }
                    }
                }
            }
        }
        return extUser;

    }

    public RoleDataService getRoleDataService() {
        return roleDataService;
    }

    public void setRoleDataService(RoleDataService roleDataService) {
        this.roleDataService = roleDataService;
    }

    public GroupDataService getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(GroupDataService groupManager) {
        this.groupManager = groupManager;
    }

    public UserDataService getUserMgr() {
        return userMgr;
    }

    public void setUserMgr(UserDataService userMgr) {
        this.userMgr = userMgr;
    }

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    /*
    public AuditHelper getAuditHelper() {
        return auditHelper;
    }

    public void setAuditHelper(AuditHelper auditHelper) {
        this.auditHelper = auditHelper;
    }
	*/
    
    public Set<EmailAddress> getEmailSet() {
        return emailSet;
    }

    public void setEmailSet(Set<EmailAddress> emailSet) {
        this.emailSet = emailSet;
    }

    public Set<Phone> getPhoneSet() {
        return phoneSet;
    }

    public void setPhoneSet(Set<Phone> phoneSet) {
        this.phoneSet = phoneSet;
    }

    public Set<Address> getAddressSet() {
        return addressSet;
    }

    public void setAddressSet(Set<Address> addressSet) {
        this.addressSet = addressSet;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Role> getActiveRoleList() {

        log.debug("Determining activeRole List. RoleList =" + roleList);

        List<Role> rList = new ArrayList<Role>();
        // create a list of roles that are not in the deleted list
        for (Role r : roleList) {

            boolean found = false;

            log.debug("- Evaluating Role=" + r);

            if (deleteRoleList != null && !deleteRoleList.isEmpty()) {
                for (Role delRl : deleteRoleList) {

                    log.debug("- Evaluating delRl = " + delRl);
                    if (delRl != null) {

                        if (!found && r.getId().equalsIgnoreCase(delRl.getId())) {
                            found = true;
                        }

                    }
                }
            }
            if (!found) {
                log.debug("- Adding Role to Active Role List=" + r);
                rList.add(r);
            }
        }
        return rList;
    }

//    public void validateIdentitiesExistforSecurityDomain(Login primaryIdentity,
//            List<Role> roleList) {
//
//        log.debug("validateIdentitiesExistforSecurityDomain");
//
//        List<LoginEntity> identityList = loginManager.getLoginByUser(primaryIdentity
//                .getUserId());
//        String managedSysId = primaryIdentity.getManagedSysId();
//
//        log.debug("Identitylist =" + identityList);
//
//        for (Role r : roleList) {
//            String secDomain = r.getServiceId();
//            if (!identityInDomain(secDomain, managedSysId, identityList)) {
//
//                log.debug("Adding identity to :" + secDomain);
//
//                addIdentity(secDomain, primaryIdentity);
//            }
//        }
//
//        // determine if we should remove an identity
//        for (LoginEntity l : identityList) {
//            if (l.getManagedSysId().equalsIgnoreCase(managedSysId)) {
//                boolean found = false;
//                for (Role r : roleList) {
//                    if (r.getServiceId().equalsIgnoreCase(
//                            l.getDomainId())) {
//                        found = true;
//                    }
//
//                }
//                if (!found) {
//                    if (l.getManagedSysId().equalsIgnoreCase("0")) {
//                        // primary identity - do not delete. Just disable its
//                        // status
//                        log.debug("Primary identity - chagne its status");
//                        l.setStatus(LoginStatusEnum.INACTIVE);
//                        loginManager.updateLogin(l);
//
//                    } else {
//
//                        log.debug("Removing identity for  :" + l);
//                        loginManager.removeLogin(l.getDomainId(), l
//                                .getLogin(), l
//                                .getManagedSysId());
//                    }
//                }
//            }
//
//        }
//
//    }

//    private boolean identityInDomain(String secDomain, String managedSysId,
//            List<LoginEntity> identityList) {
//
//        log.debug("IdentityinDomain =" + secDomain + "-" + managedSysId);
//
//        for (LoginEntity l : identityList) {
//            if (l.getDomainId().equalsIgnoreCase(secDomain)
//                    && l.getManagedSysId()
//                            .equalsIgnoreCase(managedSysId)) {
//                return true;
//            }
//
//        }
//        return false;
//
//    }

//    private void addIdentity(String secDomain, Login primaryIdentity) {
//        if (loginManager.getLoginByManagedSys(primaryIdentity
//                .getLogin(), primaryIdentity.getManagedSysId()) == null) {
//
//        	LoginEntity newLg = new LoginEntity();
//            newLg.setLogin(primaryIdentity.getLogin());
//            newLg.setManagedSysId(primaryIdentity.getManagedSysId());
//            newLg.setAuthFailCount(0);
//            newLg.setFirstTimeLogin(primaryIdentity.getFirstTimeLogin());
//            newLg.setIsLocked(primaryIdentity.getIsLocked());
//            newLg.setLastAuthAttempt(primaryIdentity.getLastAuthAttempt());
//            newLg.setGracePeriod(primaryIdentity.getGracePeriod());
//            newLg.setPassword(primaryIdentity.getPassword());
//            newLg.setPasswordChangeCount(primaryIdentity
//                    .getPasswordChangeCount());
//            newLg.setStatus(primaryIdentity.getStatus());
//            newLg.setIsLocked(primaryIdentity.getIsLocked());
//            newLg.setUserId(primaryIdentity.getUserId());
//            newLg.setResetPassword(primaryIdentity.getResetPassword());
//
//            log.debug("Adding identity = " + newLg);
//
//            loginManager.addLogin(newLg);
//        }
//
//    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Login> getPrincipalList() {
        return principalList;
    }

    public void setPrincipalList(List<Login> principalList) {
        this.principalList = principalList;
    }

    public void setUserAttributes(Map<String, UserAttribute> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public List<Role> getDeleteRoleList() {
        return deleteRoleList;
    }

    public void setDeleteRoleList(List<Role> deleteRoleList) {
        this.deleteRoleList = deleteRoleList;
    }

    public OrganizationDataService getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrganizationDataService orgManager) {
        this.orgManager = orgManager;
    }
}
