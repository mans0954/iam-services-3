package org.openiam.spml2.spi.ldap.command;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.spi.ldap.command.base.AbstractLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;
import java.util.List;

@Service("setPasswordLdapCommand")
public class SetPasswordLdapCommand extends AbstractLdapCommand<SetPasswordRequestType, ResponseType> {
    @Override
    public ResponseType execute(SetPasswordRequestType setPasswordRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = setPasswordRequestType.getPsoID();
        /* targetID - */
        String targetID = psoID.getTargetID();
        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        LdapContext ldapctx = null;
        try {
            log.debug("managedSys found for targetID=" + targetID + " "
                    + " Name=" + managedSys.getName());
            ldapctx = this.connect(managedSys);

            String ldapName = psoID.getID();

            // check if the identity exists before setting the password

            ManagedSystemObjectMatch matchObj = null;
            List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao
                    .findBySystemId(targetID, "USER");
            if (matchObjList != null && matchObjList.size() > 0) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
            }

            if (matchObj != null) {
                log.debug("setPassword:: Checking if identity exists before changing the password ");
                if (!isInDirectory(ldapName, matchObj, ldapctx))
                    throw new ConnectorDataException(ErrorCode.NO_SUCH_OBJECT);
            }

            Directory dirSpecificImp = DirectorySpecificImplFactory.create(managedSys.getHandler5());
            ModificationItem[] mods = dirSpecificImp.setPassword(setPasswordRequestType);

            ldapctx.modifyAttributes(ldapName, mods);

            // check if the request contains additional attributes
            List<ExtensibleObject> extObjList = setPasswordRequestType.getAny();
            if (extObjList != null && extObjList.size() > 0) {
                ExtensibleObject obj = extObjList.get(0);
                if (obj != null) {
                    List<ExtensibleAttribute> attrList = obj.getAttributes();
                    if (attrList != null && attrList.size() > 0) {
                        mods = new ModificationItem[attrList.size()];
                        for (ExtensibleAttribute a : attrList) {
                            mods[0] = new ModificationItem(a.getOperation(),
                                    new BasicAttribute(a.getName(),
                                            a.getValue()));
                        }
                        ldapctx.modifyAttributes(ldapName, mods);
                    }
                }
            }

        } catch(ConnectorDataException ce){
            log.error(ce.getMessage(), ce);
            throw ce;
        }catch (NamingException ne) {
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
