package org.openiam.idm.srvc.auth.login;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.auth.domain.LoginAttributeEntity;
import org.openiam.idm.srvc.auth.dto.LoginAttribute;

import java.util.List;

public interface LoginAttributeDAO extends BaseDao<LoginAttributeEntity, String> {

}