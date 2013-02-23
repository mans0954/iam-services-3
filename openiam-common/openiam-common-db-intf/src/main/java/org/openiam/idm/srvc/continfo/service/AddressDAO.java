package org.openiam.idm.srvc.continfo.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.dto.Address;

import java.util.List;
import java.util.Map;

/**
 * Data access object for address. Address usually exists with a parent entity
 * such as a user, organization, account, etc. Client components should use
 * the service objects such as <code>UserMgr</code> instead of using the DAO
 * directly.
 *
 * @author Suneet Shah
 */
public interface AddressDAO extends BaseDao<AddressEntity, String> {


    /**
     * Returns a Map of Address objects for the parentId and parentType combination.
     * The map is keyed on the address.description. Address.description indicates
     * the type of address that we have; ie. Shipping, Billing, etc.
     *
     * @param parentId
     * @param parentType
     * @return
     */
    Map<String, AddressEntity> findByParent(String parentId, String parentType);

    /**
     * Returns a List of Address objects for the parentId and parentType combination.
     *
     * @param parentId
     * @param parentType
     * @return
     */
    public List<AddressEntity> findByParentAsList(String parentId, String parentType);


    /**
     * Removes all address for a parent
     *
     * @param parentId
     * @param parentType
     */
    void removeByParent(String parentId, String parentType);

    /**
     * Returns a default address for the parentId and parentType combination.
     * Returns null if a match is not found.
     *
     * @return
     */
    AddressEntity findDefault(String parentId, String parentType);

    /**
     * Return an address object that matches the Name field. Returns null if a match
     * is not found.
     *
     * @param name
     * @param parentId
     * @param parentType
     * @return
     */
    AddressEntity findByName(String name, String parentId, String parentType);


}
