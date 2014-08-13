package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.SysConfiguration;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;

@Service("resumeLdapCommand")
public class ResumeLdapCommand extends AbstractLdapCommand<SuspendResumeRequest, ResponseType> {
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    @Qualifier("sysConfiguration")
    private SysConfiguration sysConfiguration;

    @Override
    public ResponseType execute(SuspendResumeRequest resumeRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        ConnectorConfiguration config =  getConfiguration(resumeRequestType.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapctx = this.connect(config.getManagedSys());

        try {
            String identityDN = getIdentityDN(resumeRequestType, managedSys, ldapctx);

            if (StringUtils.isNotEmpty(identityDN)) {

                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                dirSpecificImp.setAttributes("LDAP_NAME", identityDN);
                dirSpecificImp.setAttributes("LOGIN_MANAGER", loginManager);
                dirSpecificImp.setAttributes("CONFIGURATION", sysConfiguration);
                dirSpecificImp.setAttributes("TARGET_ID",resumeRequestType.getTargetID());

                ModificationItem[] mods = dirSpecificImp.resume(resumeRequestType);

                ldapctx.modifyAttributes(identityDN, mods);
            }

            return respType;

        } catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
        } finally {
	 		/* close the connection to the directory */
           this.closeContext(ldapctx);
        }
    }
}
