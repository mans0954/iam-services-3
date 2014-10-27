package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;

/**
 * Data service interface for Metadata. Metadata is used in OpenIAM to create
 * extend the capabilities of commonly used objects such as Users, Group,Role,
 * Organizations, and Resources.
 *
 * @author suneet
 * @version 1
 */

public interface MetadataService {
	
	public List<MetadataElementEntity> findElementByName(final String name);
	
	public List<MetadataElementEntity> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final LanguageEntity entity);
	
	public List<MetadataTypeEntity> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final LanguageEntity entity);
	
	public int count(final MetadataElementSearchBean searchBean);
	
	public int count(final MetadataTypeSearchBean searchBean);
	
	public void save(final MetadataElementEntity entity);

	public void deleteMetdataElement(final String id);
	
	public void save(final MetadataTypeEntity entity) throws BasicDataServiceException;
	
	public void deleteMetdataType(final String id);
	
	//public void save(final MetadataValidValueEntity entity);
	public void delteMetaValidValue(final String validValueId);
	
	public List<MetadataTypeEntity> getPhonesWithSMSOTPEnabled();
}
