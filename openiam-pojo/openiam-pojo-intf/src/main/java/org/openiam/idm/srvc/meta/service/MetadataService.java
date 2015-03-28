package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
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

    String findElementIdByAttrNameAndTypeId(String attrName, String typeId);

    MetadataElement findElementByAttrNameAndTypeId(String attrName, String typeId, final Language language);

    MetadataType findMetadataTypeByNameAndGrouping(String name, MetadataTypeGrouping grouping, final Language language);

	public List<MetadataElement> findElementByName(final String name);

    public MetadataElement findElementById(final String id, Language language);

    public MetadataType findById(final String id);

	public List<MetadataElement> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size, final Language language);

    public List<MetadataElement> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size);

	public List<MetadataType> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size, final Language language);

    public List<MetadataType> findBeansNoLocalize(final MetadataTypeSearchBean searchBean, final int from, final int size);

	public int count(final MetadataElementSearchBean searchBean);
	
	public int count(final MetadataTypeSearchBean searchBean);
	
	public String save(final MetadataElement entity);

	public void deleteMetdataElement(final String id);
	
	public String save(final MetadataTypeEntity entity);
	
	public void deleteMetdataType(final String id);
	
	//public void save(final MetadataValidValueEntity entity);
	public void delteMetaValidValue(final String validValueId);
}
