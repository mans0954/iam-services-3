package org.openiam.spml2.spi.salesforce.dao;

import com.sforce.ws.ConnectionException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.BaseModel;
import org.openiam.spml2.spi.salesforce.model.User;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 12:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BaseSalesForceDao<Model extends BaseModel> {
    public void save(final Model model) throws ConnectionException, SalesForcePersistException;
    public void update(final Model model) throws ConnectionException, SalesForcePersistException;
    public void saveOrUpdate(Model model) throws ConnectionException, SalesForcePersistException;
    public String getProfileIdByName(final String profileName) throws ConnectionException;

    public Model findByName(final String name) throws ConnectionException;
}
