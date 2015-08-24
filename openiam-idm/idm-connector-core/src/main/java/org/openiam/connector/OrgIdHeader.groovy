package org.openiam.connector

import org.openiam.am.srvc.dto.ContentProvider
import org.openiam.am.srvc.dto.URIPattern
import org.openiam.am.srvc.dto.URIPatternMetaValue
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor
import org.openiam.idm.srvc.org.dto.Organization
import org.openiam.idm.srvc.org.service.OrganizationDataService
import org.openiam.idm.srvc.user.service.UserDataService
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.searchbeans.OrganizationSearchBean;

class OrgIdsHeader extends URIFederationGroovyProcessor {

    @Override
    public String getValue(String userId, ContentProvider contentProvider,
                           URIPattern pattern, URIPatternMetaValue metaValue, URI uri) {
        if (userId == null)
            return null;
        println("*** Call: OrgIdsHeader. UPDATED");
        String retVal = "";
        OrganizationDataService orgManager = ((OrganizationDataService) context.getBean("orgManager"));
        UserDataService userManager = ((UserDataService) context.getBean("userManager"));
        UserEntity u = userManager.getUser(userId);
        Set<RoleEntity> roles = u.getRoles();
        println("OrgIdsHeader. Roles: " + roles);
        RoleEntity role = null;
        for (RoleEntity re : roles) {
            if ("GLOBAL_ACCESS".equals(re.getName())) {
                role = re; break;
            }
        }
        println("OrgIdsHeader. Role: " + role);
        String orgs = null;

        if (role) {
            println("ALL ORGS  _ GLOBAL ACCESS!!!!")
            orgs = orgManager.getOrganizationsAliases(null);
        } else {
            orgs = orgManager.getOrganizationsAliases(userId);
        }

        println("***OrgIdsHeader. RetVal=" + orgs);
        return orgs;
    }
}

