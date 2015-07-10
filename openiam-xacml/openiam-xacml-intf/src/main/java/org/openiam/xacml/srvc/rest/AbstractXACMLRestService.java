package org.openiam.xacml.srvc.rest;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractXACMLRestService<DTO> {

    public void save(DTO policyEntity);

    public List<DTO> findAll();

    public DTO findById(String id);

    public void delete(String id);


}
