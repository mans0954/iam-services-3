package org.openiam.idm.srvc.recon.service;

import org.openiam.provision.dto.ProvisionUser;

import java.util.Map;

public interface PopulationScript {
    public int execute(Map<String, String> line, ProvisionUser pUser);
}
