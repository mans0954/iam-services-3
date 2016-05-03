package org.openiam.imprt.util;


import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.imprt.model.LineObject;

import java.util.List;

public class Transformation {

    public int execute(LineObject rowObj, UserEntity user, List<OrganizationEntity> allOrganizations, List<RoleEntity> allRoles, List<GroupEntity> allGroups) {
        try {
            populateObject(rowObj, user, allOrganizations, allRoles, allGroups);
        } catch (Exception ex) {
            return -1;
        }
        user.setStatus(UserStatusEnum.ACTIVE);

        return 0;
    }


    public void populateObject(LineObject lo, UserEntity user, List<OrganizationEntity> allOrganizations, List<RoleEntity> allRoles, List<GroupEntity> allGroups) throws Exception {

    }
}