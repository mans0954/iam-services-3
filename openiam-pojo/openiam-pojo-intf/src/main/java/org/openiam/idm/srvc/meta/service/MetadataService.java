package org.openiam.idm.srvc.meta.service;

import java.util.List;

import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
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

    /**
     * Gets the Metadata Element based on the Type Id.It also returns the MetadataOptions
     * with the MetadataElements.
     *
     * @param typeId the MetadataType for which the MetadataElements are required.
     * @return the Map which contains MetadataId as Key and MetadataElementValue
     *         objects as Values.
     */
	public List<MetadataElementEntity> getMetadataElementByType(String typeId);

	public List<MetadataElementEntity> getAllElementsForCategoryType(String categoryType);

    /**
     * Returns a list of MetadataTypes that are associated with a Category
     *
     * @param categoryId
     */
	public List<MetadataTypeEntity> getTypesInCategory(String categoryId);
	
	public List<MetadataElementEntity> findBeans(final MetadataElementSearchBean searchBean, final int from, final int size);
	
	public List<MetadataTypeEntity> findBeans(final MetadataTypeSearchBean searchBean, final int from, final int size);
	
	public int count(final MetadataElementSearchBean searchBean);
	
	public int count(final MetadataTypeSearchBean searchBean);
	
	public void save(final MetadataElementEntity entity);

	public void deleteMetdataElement(final String id);
	
	public void save(final MetadataTypeEntity entity);
	
	public void deleteMetdataType(final String id);
}
