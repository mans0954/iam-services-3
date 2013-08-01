package org.openiam.spml2.spi.ldap;

import org.openiam.connector.type.*;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.List;

/**
 * Implements lookup furnctionality for the ldapconnector.
 * User: suneetshah
 */
public class LdapLookupCommand extends LdapAbstractCommand {

    public SearchResponse lookup(LookupRequest reqType) {
        log.debug("LOOKUP operation called.");
        boolean found = false;
        ConnectionMgr conMgr = null;

        SearchResponse respType = new SearchResponse();

        if (reqType == null) {
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.MALFORMED_REQUEST);
            return respType;
        }

        String identity = reqType.getSearchValue();
        String rdn = null;
        String objectBaseDN = null;

        int indx = identity.indexOf(",");
        if (indx > 0) {
            rdn = identity.substring(0, identity.indexOf(","));
            objectBaseDN = identity.substring(indx+1);
        } else {
            rdn = identity;
        }

        log.debug("looking up identity: " + identity);

        ManagedSysDto managedSys = managedSysService.getManagedSys(reqType.getTargetID());
        try {

            conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
            conMgr.setApplicationContext(ac);
            LdapContext ldapctx = conMgr.connect(managedSys);



            if (ldapctx == null) {
                respType.setStatus(StatusCodeType.FAILURE);
                respType.setError(ErrorCode.DIRECTORY_ERROR);
                respType.addErrorMessage("Unable to connect to directory.");
                return respType;
            }

            ManagedSystemObjectMatch[] matchObj = managedSysService.managedSysObjectParam(reqType.getTargetID(), "USER");
            String resourceId = managedSys.getResourceId();

            log.debug("Resource id = " + resourceId);
            List<AttributeMap> attrMap = managedSysService.getResourceAttributeMaps(resourceId);

            if (attrMap != null) {
                List<String> attrList = getAttributeNameList(attrMap);
                String[] attrAry = new String[attrList.size()];
                attrList.toArray(attrAry);
                log.debug("Attribute array=" + attrAry);

                NamingEnumeration results = lookupSearch(matchObj[0], ldapctx, rdn, attrAry, objectBaseDN);

                log.debug("results=" + results);
                log.debug(" results has more elements=" + results.hasMoreElements());

                UserValue userValue = new UserValue();
                userValue.setUserIdentity(identity);

                while (results != null && results.hasMoreElements()) {
                    SearchResult sr = (SearchResult) results.next();
                    Attributes attrs = sr.getAttributes();
                    if (attrs != null) {
                        found = true;
                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                            ExtensibleAttribute extAttr = new ExtensibleAttribute();
                            Attribute attr = (Attribute) ae.next();

                            boolean addToList = false;

                            extAttr.setName(attr.getID());

                            NamingEnumeration e = attr.getAll();

                            while (e.hasMore()) {
                                Object o = e.next();
                                if (o instanceof String) {
                                    extAttr.setValue(o.toString());
                                    addToList = true;
                                }
                            }
                            if (addToList) {
                                userValue.getAttributeList().add(extAttr);
                            }
                        }
                        //return only one Result - first row
                        respType.getUserList().add(userValue);
                        UserValue extObj = new UserValue();
                        extObj.setUserIdentity(identity);
                    }
                }
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage());
            respType.setStatus(StatusCodeType.FAILURE);
            respType.setError(ErrorCode.DIRECTORY_ERROR);
            respType.addErrorMessage(ne.toString());

            return respType;
        } catch (Exception e) {
            log.error(e.getMessage());
            respType.setStatus(StatusCodeType.FAILURE);

            respType.setError(ErrorCode.OTHER_ERROR);
            respType.addErrorMessage(e.toString());

            return respType;

        } finally {
            /* close the connection to the directory */
            try {
                if (conMgr != null) {
                    conMgr.close();
                }
            } catch (NamingException n) {
                log.error(n);
            }

        }


        log.debug("LOOKUP successful");

        if (!found) {
            respType.setStatus(StatusCodeType.FAILURE);
        } else {
            respType.setStatus(StatusCodeType.SUCCESS);
        }

        return respType;


    }






}


