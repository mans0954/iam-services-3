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
            ManagedSystemObjectMatch matchObj = getMatchObject(passwordRequest.getTargetID(), "USER");
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
                results = lookupSearch(matchObj, ldapctx, identity, null, objectBaseDN);
            } catch (NameNotFoundException nnfe) {
                log.debug("results=NULL");
                log.debug(" results has more elements=0");
                respType.setStatus(StatusCodeType.FAILURE);
                return respType;
            }

            String ldapName = null;
            while (results != null && results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                        Attribute attr = (Attribute) ae.next();
                        if ("entrydn".equalsIgnoreCase(attr.getID())) {
                            NamingEnumeration e = attr.getAll();
                            while (e.hasMore()) {
                                Object o = e.next();
                                if (o instanceof String) {
                                    ldapName = o.toString();
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isNotEmpty(ldapName)) {
                log.debug("New password will be set for user " + ldapName);
                Directory dirSpecificImp = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                ModificationItem[] mods = dirSpecificImp.setPassword(passwordRequest);
                ldapctx.modifyAttributes(ldapName, mods);
                log.debug("New password has been set for user " + ldapName);
            } else {
                log.debug("DN for user with identity=" + identity + " was not found");
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
