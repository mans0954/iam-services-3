package org.openiam.jaas.principal;

import java.security.Principal;

public class UserIdentity implements Principal {
    private String userIdentity;

    @Override
    public String getName() {
        return userIdentity;
    }

    public void setName(String name){
        this.userIdentity=name;
    }

    public UserIdentity(String userIdentity){
        this.userIdentity=userIdentity;
    }
}
