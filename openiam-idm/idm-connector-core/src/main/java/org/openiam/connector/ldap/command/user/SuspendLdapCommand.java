package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("suspendLdapCommand")
public class SuspendLdapCommand extends AbstractLdapCommand<SuspendResumeRequest, ResponseType>{
    @Override
    public ResponseType execute(SuspendResumeRequest suspendRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        ConnectorConfiguration config =  getConfiguration(suspendRequestType.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapctx = this.connect(config.getManagedSys());


        try {
            ManagedSystemObjectMatch matchObj = getMatchObject(suspendRequestType.getTargetID(), ManagedSystemObjectMatch.USER);

            String identity = suspendRequestType.getObjectIdentity();
            //Check identity on CN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;
            if(matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField()+"="+identity;
                objectBaseDN =  suspendRequestType.getObjectIdentity().substring(CN.length()+1);
            } else {
				objectBaseDN = matchObj.getBaseDn();
				Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
				boolean isLookupUserInOu = getResourceBoolean(rpSet, "LOOKUP_USER_IN_OU", true);
				if (isLookupUserInOu) {
					// if identity is not in DN format try to find OU info in attributes
					String OU = getAttrValue(suspendRequestType.getExtensibleObject(), OU_ATTRIBUTE);
					if(StringUtils.isNotEmpty(OU)) {
						objectBaseDN = OU+","+matchObj.getBaseDn();
					}
				}
            }
            // check if this object exists in the target system
            // dont try to disable and object that does not exist

            //Important!!! For add new record in LDAP we must to create identity in DN format
//            String identityDN = matchObj.getKeyField() + "=" + identity+","+objectBaseDN;

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
                // Each directory
                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                log.debug("Directory specific object name = " + dirSpecificImp.getClass().getName());
                ModificationItem[] mods = dirSpecificImp.suspend(suspendRequestType);

                log.debug("Modifying for Suspend.. users in ldap.." + identityDN);
                ldapctx.modifyAttributes(identityDN, mods);
            }

            return respType;

        } catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER,ne.getMessage());
        } finally {
	 		/* close the connection to the directory */
            this.closeContext(ldapctx);
        }
    }
}
