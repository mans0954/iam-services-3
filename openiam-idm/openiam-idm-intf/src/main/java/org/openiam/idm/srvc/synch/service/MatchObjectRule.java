package org.openiam.idm.srvc.synch.service;

import java.util.Map;

import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.recon.dto.MatchConfig;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.provision.type.Attribute;
import org.openiam.idm.srvc.user.dto.User;

/**
 * Interface to define the rule that will be used for matching objects during the synchronization process.
 * @author suneet
 *
 */
public interface MatchObjectRule {
	/**
	 * Look up the user contained in the user object with in the IDM system.
	 * The look up will be based on the match criteria defined in the config object.
     * @param matchConfig
	 * @param rowAttr
	 * @return
	 */
	User lookup(MatchConfig matchConfig, Map<String, Attribute> rowAttr);

    /**
     * Look up the group contained in the group object with in the IDM system.
     * The look up will be based on the match criteria defined in the config object.
     * @param matchConfig
     * @param rowAttr
     * @return
     */
    Group lookupGroup(MatchConfig matchConfig, Map<String, Attribute> rowAttr);

    /**
     * Look up the role contained in the role object with in the IDM system.
     * The look up will be based on the match criteria defined in the config object.
     * @param matchConfig
     * @param rowAttr
     * @return
     */
    Role lookupRole(MatchConfig matchConfig, Map<String, Attribute> rowAttr);

    /**
     * Look up the Organization contained in the Organization object with in the IDM system.
     * The look up will be based on the match criteria defined in the config object.
     * @param matchConfig
     * @param rowAttr
     * @return
     */
    Organization lookupOrganization(MatchConfig matchConfig, Map<String, Attribute> rowAttr);

	String getMatchAttrName() ;

	void setMatchAttrName(String matchAttrName) ;

	String getMatchAttrValue() ;

	void setMatchAttrValue(String matchAttrValue) ;
}
