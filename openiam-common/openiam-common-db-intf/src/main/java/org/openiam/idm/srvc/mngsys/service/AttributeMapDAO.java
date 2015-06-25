/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.mngsys.service;

import java.util.List;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;

/**
 * @author suneet
 * 
 */
public interface AttributeMapDAO extends BaseDao<AttributeMapEntity, String> {

	List<AttributeMapEntity> findByResourceId(String resourceId);

    List<AttributeMapEntity> findByMngSysPolicyId(String mngSysPolicy);

    List<AttributeMapEntity> findBySynchConfigId(String synchConfigId);

	List<AttributeMapEntity> findAllAttributeMaps();

	void removeResourceAttributeMaps(String resourceId);

    void delete(List<String> ids);

    void deleteAttributesMapList(List<AttributeMapEntity> attrMap);

    AttributeMapEntity add(AttributeMapEntity entity);

    void update(AttributeMapEntity entity);

}