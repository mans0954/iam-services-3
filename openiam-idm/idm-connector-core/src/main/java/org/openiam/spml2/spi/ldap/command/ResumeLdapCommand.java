package org.openiam.spml2.spi.ldap.command;

import org.openiam.base.SysConfiguration;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.spi.ldap.command.base.AbstractLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;

@Service("resumeLdapCommand")
public class ResumeLdapCommand extends AbstractLdapCommand<ResumeRequestType, ResponseType> {
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    @Qualifier("sysConfiguration")
    private SysConfiguration sysConfiguration;

    @Override
    public ResponseType execute(ResumeRequestType resumeRequestType) throws ConnectorDataException {
		/* PSO - Provisioning Service Object -
		 *     -  ID must uniquely specify an object on the target or in the target's namespace
		 *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = resumeRequestType.getPsoID();
		/* targetID -  */
        String targetID = psoID.getTargetID();


		/* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        LdapContext ldapctx = null;
        try {
            log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
            ldapctx = this.connect(managedSys);

            log.debug("Ldapcontext = " + ldapctx);
            String ldapName = psoID.getID();

            // check if this object exists in the target system
            // dont try to enable and object that does not exist
            if (identityExists(ldapName, ldapctx)) {

                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
                dirSpecificImp.setAttributes("LDAP_NAME", ldapName);
                dirSpecificImp.setAttributes("LOGIN_MANAGER", loginManager);
                dirSpecificImp.setAttributes("CONFIGURATION", sysConfiguration);
                dirSpecificImp.setAttributes("TARGET_ID",targetID);

                ModificationItem[] mods = dirSpecificImp.resume(resumeRequestType);

                ldapctx.modifyAttributes(ldapName, mods);
            }

        }catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
        }finally {
	 		/* close the connection to the directory */
           this.closeContext(ldapctx);
        }

        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }
}
