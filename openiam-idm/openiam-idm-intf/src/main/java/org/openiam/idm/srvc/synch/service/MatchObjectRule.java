package org.openiam.idm.srvc.synch.service;

import java.util.Map;

import org.openiam.idm.srvc.recon.dto.MatchConfig;
import org.openiam.idm.srvc.synch.dto.Attribute;
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

	String getMatchAttrName() ;

	void setMatchAttrName(String matchAttrName) ;

	String getMatchAttrValue() ;

	void setMatchAttrValue(String matchAttrValue) ;
}
