package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
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
// new in 3.3.1
    String findElementIdByAttrNameAndTypeId(String attrName, String typeId);

    MetadataElement findElementByAttrNameAndTypeId(String attrName, String typeId, final Language language);
// new in 3.3.1
    MetadataType findMetadataTypeByNameAndGrouping(String name, MetadataTypeGrouping grouping, final Language language);

	List<MetadataElement> findElementByName(final String name);

    MetadataElement findElementById(final String id, Language language);

    MetadataType findById(final String id);

	List<MetadataElement> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final Language language);

	List<MetadataType> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language);
	public List<MetadataElement> findBeans(MetadataElementSearchBean searchBean, int from, int size);
		public List<MetadataElementEntity> findEntityBeans(final MetadataElementSearchBean searchBean, final int from, final int size);
	public List<MetadataTypeEntity> findEntityBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language);
		int count(final MetadataElementSearchBean searchBean);
	    public List<MetadataType> findBeansNoLocalize(final MetadataTypeSearchBean searchBean, final int from, final int size);
	int count(final MetadataTypeSearchBean searchBean);
	
	void save(final MetadataElement entity);

	void deleteMetdataElement(final String id);
	
	void save(final MetadataType entity) throws BasicDataServiceException;
	
	void deleteMetdataType(final String id);
	
	//public void save(final MetadataValidValueEntity entity);
	void delteMetaValidValue(final String validValueId);
	
	List<MetadataType> getPhonesWithSMSOTPEnabled();
}
