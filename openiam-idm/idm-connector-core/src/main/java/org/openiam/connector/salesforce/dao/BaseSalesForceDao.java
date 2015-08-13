package org.openiam.connector.salesforce.dao;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.BaseModel;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 12:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BaseSalesForceDao<Model extends BaseModel> {
    void save(final Model model) throws ConnectionException, SalesForcePersistException;
    void update(final Model model) throws ConnectionException, SalesForcePersistException;
    void saveOrUpdate(Model model) throws ConnectionException, SalesForcePersistException;
    String getProfileIdByName(final String profileName) throws ConnectionException;

    Model findByName(final String name) throws ConnectionException;
}
