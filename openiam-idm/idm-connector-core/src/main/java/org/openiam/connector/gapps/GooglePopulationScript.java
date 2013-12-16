package org.openiam.connector.gapps;

import org.openiam.provision.type.ExtensibleUser;

import com.google.api.services.admin.directory.model.User;

public interface GooglePopulationScript {
    public int execute(User googleUser, ExtensibleUser user, boolean direction);
}
