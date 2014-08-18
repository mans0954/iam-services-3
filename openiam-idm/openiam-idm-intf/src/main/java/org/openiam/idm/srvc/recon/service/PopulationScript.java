package org.openiam.idm.srvc.recon.service;

import java.util.Map;

public interface PopulationScript<T> {
    public int execute(Map<String, String> line, T pUser);
}
