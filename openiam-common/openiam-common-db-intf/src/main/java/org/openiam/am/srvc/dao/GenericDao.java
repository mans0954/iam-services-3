package org.openiam.am.srvc.dao;

import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;

/**
 * User: Alexander Duckardt
 * Date: 8/15/12
 */
public interface GenericDao<Entity, PrimaryKey extends Serializable> {

    /**
     * Get data by primary key
     * @param id
     * @return
     * @throws Exception
     */
    Entity findById(PrimaryKey id)throws Exception;

    /**
     * Gets all data from database
     * @return
     * @throws Exception
     */
    List<Entity> getAll() throws Exception;
    /**
     * Adds a new instance
     *
     * @param instance
     */
    Entity add(Entity instance)throws Exception;

    /**
     * Updates an existing instance
     *
     * @param instance
     */
    Entity update(Entity instance)throws Exception;

    /**
     * Removes an existing instance
     *
     * @param instance
     */
    void delete(Entity instance)throws Exception;
    /**
     * Removes  data by primary key
     *
     * @param id
     */
    void delete(PrimaryKey id)throws Exception;

    Session getCurrentSession();
}
