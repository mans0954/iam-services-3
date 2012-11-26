package org.openiam.idm.srvc.meta.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.MetaDataElementDozerConverter;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Data service implementation for Metadata.
 * @author suneet
 * @version 1
 */
@Service("metadataService")
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    MetadataTypeDAO metadataTypeDao;
    @Autowired
    MetadataElementDAO metadataElementDao;

    @Autowired
    MetaDataTypeDozerConverter metaDataTypeDozerConverter;

    @Autowired
    MetaDataElementDozerConverter metaDataElementDozerConverter;

    private static final Log log = LogFactory.getLog(MetadataServiceImpl.class);

    public MetadataElement addMetadataElement(MetadataElement metadataElement) {
        if (metadataElement == null) {
            throw new NullPointerException("metadataElement is null");
        }
        MetadataElementEntity element = metaDataElementDozerConverter
                .convertToEntity(metadataElement, true);
        metadataElementDao.save(element);
        return metadataElement;
    }

    public MetadataType addMetadataType(MetadataType type) {
        if (type == null) {
            throw new NullPointerException("Metadatatype is null");
        }
        MetadataTypeEntity typeEntity = metaDataTypeDozerConverter
                .convertToEntity(type, true);

        metadataTypeDao.save(typeEntity);
        return type;

    }

    public void addTypeToCategory(String typeId, String categoryId) {
        if (typeId == null)
            throw new NullPointerException("typeId is null");
        if (categoryId == null)
            throw new NullPointerException("category is null");

        this.metadataTypeDao.addCategoryToType(typeId, categoryId);

    }

    public MetadataElement getMetadataElementById(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("elementId is null");
        }
        return metaDataElementDozerConverter.convertToDTO(
                metadataElementDao.findById(elementId), true);
    }

    public MetadataElement[] getMetadataElementByType(String typeId) {
        if (typeId == null) {
            throw new NullPointerException("typeId is null");
        }
        MetadataType type = metaDataTypeDozerConverter.convertToDTO(
                metadataTypeDao.findById(typeId), true);
        if (type == null)
            return null;
        Map<String, MetadataElement> elementMap = type.getElementAttributes();
        if (elementMap == null || elementMap.isEmpty()) {
            return null;
        }
        // convert to an array
        Collection<MetadataElement> elementCollection = elementMap.values();
        MetadataElement[] elementAry = new MetadataElement[elementCollection
                .size()];
        elementCollection.toArray(elementAry);
        return elementAry;
    }

    public MetadataType getMetadataType(String typeId) {
        if (typeId == null) {
            throw new NullPointerException("typeId is null");
        }
        return metaDataTypeDozerConverter.convertToDTO(
                metadataTypeDao.findById(typeId), true);
    }

    public MetadataType[] getMetadataTypes() {
        List<MetadataType> typeList = metaDataTypeDozerConverter
                .convertToDTOList(metadataTypeDao.findAll(), true);
        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        int size = typeList.size();
        MetadataType[] typeAry = new MetadataType[size];
        typeList.toArray(typeAry);

        return typeAry;
    }

    public MetadataType[] getTypesInCategory(String categoryId) {

        if (categoryId == null) {
            throw new NullPointerException("categoryId is null");
        }

        List<MetadataType> typeList = metaDataTypeDozerConverter
                .convertToDTOList(
                        metadataTypeDao.findTypesInCategory(categoryId), true);
        if (typeList == null || typeList.isEmpty()) {
            return null;
        }
        int size = typeList.size();
        MetadataType[] typeAry = new MetadataType[size];
        typeList.toArray(typeAry);
        return typeAry;
    }

    public void removeMetadataElement(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("elementId is null");
        }
        MetadataElement element = new MetadataElement(elementId);
        metadataElementDao.delete(metaDataElementDozerConverter
                .convertToEntity(element, false));
    }

    public void removeMetadataType(String typeId) {
        if (typeId == null) {
            throw new NullPointerException("typeId is null");
        }

        metadataElementDao.removeByParentId(typeId);

        MetadataType type = new MetadataType();
        type.setMetadataTypeId(typeId);
        metadataTypeDao.delete(metaDataTypeDozerConverter.convertToEntity(type,
                false));
    }

    public void removeTypeFromCategory(String typeId, String categoryId) {
        if (typeId == null)
            throw new NullPointerException("typeId is null");
        if (categoryId == null)
            throw new NullPointerException("category is null");

        metadataTypeDao.removeCategoryFromType(typeId, categoryId);
    }

    public MetadataElement updateMetadataElement(MetadataElement mv) {
        if (mv == null) {
            throw new NullPointerException("metadataElement is null");
        }
        metadataElementDao.update(metaDataElementDozerConverter
                .convertToEntity(mv, true));
        return mv;
    }

    public MetadataType updateMetdataType(MetadataType type) {
        if (type == null) {
            throw new NullPointerException("Metadatatype is null");
        }

        log.debug("updateMetdataType: " + type);

        metadataTypeDao.update(metaDataTypeDozerConverter.convertToEntity(type,
                true));
        return type;

    }

    public List<MetadataElement> getAllElementsForCategoryType(
            String categoryType) {
        if (categoryType == null) {
            throw new NullPointerException("categoryType is null");
        }
        return metaDataElementDozerConverter.convertToDTOList(
                metadataElementDao.findbyCategoryType(categoryType), true);
    }

    public MetadataTypeDAO getMetadataTypeDao() {
        return metadataTypeDao;
    }

    public void setMetadataTypeDao(MetadataTypeDAO metadataTypeDao) {
        this.metadataTypeDao = metadataTypeDao;
    }

    public MetadataElementDAO getMetadataElementDao() {
        return metadataElementDao;
    }

    public void setMetadataElementDao(MetadataElementDAO metadataElementDao) {
        this.metadataElementDao = metadataElementDao;
    }

}
