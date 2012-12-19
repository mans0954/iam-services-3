package org.openiam.jaas.group;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class UserRoleGroup implements Serializable, Group {
    private String name = null;
    private Set<Principal> users = new HashSet<Principal>();

    public UserRoleGroup(String name) {
        super();
        this.name = name;
    }
    @Override
    public boolean addMember(Principal user) {
        return users.add(user);
    }

    @Override
    public boolean removeMember(Principal user) {
        return users.remove(user);
    }

    @Override
    public boolean isMember(Principal member) {
        return users.contains(member);
    }

    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(users);
    }

    @Override
    public String getName() {
        return name;
    }
}
