package org.openiam.jaas.module;

import org.apache.log4j.Logger;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.jaas.config.JaasConfiguration;
import org.openiam.jaas.credential.TokenCredential;
import org.openiam.jaas.group.UserRoleGroup;
import org.openiam.jaas.handler.AbstractCalbackHandler;
import org.openiam.jaas.principal.UserIdentity;
import org.openiam.jaas.principal.UserUID;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractLoginModule implements LoginModule {
    protected final Logger log = Logger.getLogger(this.getClass());

    protected JaasConfiguration jaasConfiguration = JaasConfiguration.getInstance();

    /** <p>The authentication status.</p> */
    protected boolean success;

    /** <p>The commit status.</p> */
    protected boolean commitSuccess;
    /** <p>The Subject to be authenticated.</p> */
    protected javax.security.auth.Subject subject;

    /** <p>A CallbackHandler for communicating with the end user (prompting for usernames and passwords, for example).</p> */
    protected CallbackHandler callbackHandler;

    /** <p>State shared with other configured LoginModules.</p> */
    protected Map sharedState;

    /** <p>Options specified in the login Configuration for this particular LoginModule.</p> */
    protected Map options;
    /** <p>User Name.</p> */
    protected String username;
    /** <p>User Password.</p> */
    protected String password;

    protected org.openiam.idm.srvc.auth.dto.Subject iamSubject = null;

    public AbstractLoginModule() {
        log.debug(this.getClass().getSimpleName() + ": default constructor called.");
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        log.debug("LoginModule:initialize() called.");
        this.subject = subject;
        log.debug("CallbackHandler Type=" + callbackHandler);
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        System.out.println("LoginModule: login() method called. +1");
        // prompt for a user name and password
        if (callbackHandler == null) {
            System.out.println("- Callback handler is null");
            throw new LoginException("Error: no CallbackHandler available " +
                                     "to garner authentication information from the user");
        }
        gatheringUserInfo();
        return processLogin();
    }

    @Override
    public boolean commit() throws LoginException {
        log.debug("Commit called.");
        if (iamSubject != null) {
            log.debug("iamsubject is not null");
            convertToJAASSubject(iamSubject);
        }
        this.commitSuccess = true;
        return true;
    }


    @Override
    public boolean abort() throws LoginException {
        success = false;
        logout();
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public boolean logout() throws LoginException {
        cleanData();
        return(true);
    }

    private void convertToJAASSubject(org.openiam.idm.srvc.auth.dto.Subject iamSubject) {
        log.debug("Subject assignment operation callled.");

        if (iamSubject != null) {
            System.out.println("Setting subject in commit");
            // add principals
            Principal pUserId = new UserUID(iamSubject.getUserId());
            Principal pIdentity = new UserIdentity(iamSubject.getPrincipal());
            subject.getPrincipals().add(pUserId);
            subject.getPrincipals().add(pIdentity);

            // add the users roles into the subject
            List<Role> iamRoleList = iamSubject.getRoles();
            if(iamRoleList!=null){
                for (Role r  : iamRoleList) {
                    String roleId = r.getRoleId();
                    UserRoleGroup urg = new UserRoleGroup(roleId);
                    subject.getPrincipals().add(urg);
                }
            }
            if(iamSubject.getSsoToken()!=null && iamSubject.getSsoToken().getToken()!=null && !iamSubject.getSsoToken().getToken().isEmpty())
                subject.getPublicCredentials().add(new TokenCredential(iamSubject.getUserId(), iamSubject.getSsoToken().getToken()));
        }
    }

    protected void cleanData(){
        ((AbstractCalbackHandler)callbackHandler).clean();
        if(subject!=null){
            // remove the principals the login module added
            Iterator it = subject.getPrincipals(UserUID.class).iterator();
            while (it.hasNext()) {
                UserUID p = (UserUID)it.next();
                log.debug("Removing userUid: "+p.toString());
                subject.getPrincipals().remove(p);
            }
            it = subject.getPrincipals(UserIdentity.class).iterator();
            while (it.hasNext()) {
                UserIdentity p = (UserIdentity)it.next();
                log.debug("Removing UserIdentity: "+p.toString());
                subject.getPrincipals().remove(p);
            }
            it = subject.getPrincipals(UserRoleGroup.class).iterator();
            while (it.hasNext()) {
                UserRoleGroup p = (UserRoleGroup)it.next();
                log.debug("Removing UserRoleGroup: " + p.toString());
                subject.getPrincipals().remove(p);
            }

            // remove the credentials the login module added
            it = subject.getPublicCredentials(TokenCredential.class).iterator();
            while (it.hasNext()) {
                TokenCredential c = (TokenCredential)it.next();
                log.debug("Removing credential: " + c.toString());
                subject.getPublicCredentials().remove(c);
            }
        }
    }

    protected abstract boolean processLogin()throws LoginException;
    protected abstract void gatheringUserInfo() throws LoginException;

}
