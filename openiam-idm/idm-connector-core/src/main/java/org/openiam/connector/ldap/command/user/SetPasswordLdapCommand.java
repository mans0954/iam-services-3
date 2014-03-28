package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("setPasswordLdapCommand")
public class SetPasswordLdapCommand extends AbstractLdapCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(passwordRequest.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapctx = this.connect(managedSys);

        try {
            ManagedSystemObjectMatch matchObj = getMatchObject(passwordRequest.getTargetID(), ManagedSystemObjectMatch.USER);
            String identity = passwordRequest.getObjectIdentity();

            //Check identity on CN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;

            if(matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField() + "=" + identity;
                objectBaseDN =  passwordRequest.getObjectIdentity().substring(CN.length()+1);

            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getOU(passwordRequest.getExtensibleObject());
                if(StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }

            NamingEnumeration results = null;
            try {
                log.debug("Looking for user with identity=" +  identity + " in " +  objectBaseDN);
                results = lookupSearch(managedSys, matchObj, ldapctx, identity, null, objectBaseDN);

            } catch (NameNotFoundException nnfe) {
                log.debug("results=NULL");
                log.debug(" results has more elements=0");
                respType.setStatus(StatusCodeType.FAILURE);
                return respType;
            }

            String identityDN = null;
            int count = 0;
            while (results != null && results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                identityDN = sr.getNameInNamespace();
                count++;
            }

            if (count == 0) {
                String err = String.format("User %s was not found in %s", identity, objectBaseDN);
                log.error(err);
                respType.setStatus(StatusCodeType.FAILURE);
                return respType;
            } else if (count > 1) {
                String err = String.format("More then one user %s was found in %s", identity, objectBaseDN);
                log.error(err);
                respType.setStatus(StatusCodeType.FAILURE);
                return respType;
            }

            if (StringUtils.isNotEmpty(identityDN)) {
                log.debug("New password will be set for user " + identityDN);
                Directory dirSpecificImp = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                ModificationItem[] mods = dirSpecificImp.setPassword(passwordRequest);
                ldapctx.modifyAttributes(identityDN, mods);
                log.debug("New password has been set for user " + identityDN);
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(), ne);
            log.debug("Returning response object from set password with Status of Failure...");
            ConnectorDataException ex =null;
            if (ne instanceof OperationNotSupportedException) {
                ex = new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);
            } else {
                ex = new ConnectorDataException(ErrorCode.DIRECTORY_ERROR);
            }
            throw  ex;
        } catch (Exception ne) {
            log.error(ne.getMessage(), ne);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR);
        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }
        return respType;
    }
}
