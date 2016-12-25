package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
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
    MetadataType findMetadataTypeByNameAndGrouping(String name, MetadataTypeGrouping grouping);

	List<MetadataElement> findElementByName(final String name);

    MetadataElement findElementById(final String id);

    MetadataType findById(final String id);

	List<MetadataElement> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size);

	List<MetadataType> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size);
	List<MetadataElementEntity> findEntityBeans(final MetadataElementSearchBean searchBean, final int from, final int size);
	List<MetadataTypeEntity> findEntityBeans(final MetadataTypeSearchBean searchBean, final int from, final int size);
	int count(final MetadataElementSearchBean searchBean);
	List<MetadataType> findBeansNoLocalize(final MetadataTypeSearchBean searchBean, final int from, final int size);
	int count(final MetadataTypeSearchBean searchBean);

	String save(final MetadataElement entity) throws BasicDataServiceException;

	void deleteMetdataElement(final String id) throws BasicDataServiceException;
	
	String save(final MetadataType entity) throws BasicDataServiceException;
	
	void deleteMetdataType(final String id) throws BasicDataServiceException;
	
	//public void save(final MetadataValidValueEntity entity);
	void delteMetaValidValue(final String validValueId);
	
	List<MetadataType> getPhonesWithSMSOTPEnabled();
}
