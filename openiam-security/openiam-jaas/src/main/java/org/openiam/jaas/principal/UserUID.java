package org.openiam.jaas.principal;

import java.security.Principal;

public class UserUID implements Principal {
    private String userUID;

    @Override
    public String getName() {
        return userUID;
    }

    public void setName(String name){
        this.userUID=name;
    }

    public UserUID(String userUID){
        this.userUID=userUID;
    }
}
