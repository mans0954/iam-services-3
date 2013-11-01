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
    private String id = null;
    private Set<Principal> users = new HashSet<Principal>();

    public UserRoleGroup(String name, String id) {
        super();
        this.name = name;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
