package org.openiam.xacml.srvc.service;

import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;

import java.util.List;

/**
 * Created by zaporozhec on 7/10/15.
 */
public interface AbstractXACMLService<Entity> {

    public void save(Entity policyEntity);

    public List<Entity> findAll();

    public Entity findById(String id);

    public void delete(String id);


}
