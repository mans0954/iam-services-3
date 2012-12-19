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
        return metaDataElementDozerConverter.convertToDTO(element, true);
    }

    public MetadataType addMetadataType(MetadataType type) {
        if (type == null) {
            throw new NullPointerException("Metadatatype is null");
        }

        try {
            MetadataTypeEntity mte = metaDataTypeDozerConverter
                    .convertToEntity(type, true);
            metadataTypeDao.save(mte);
            type = metaDataTypeDozerConverter.convertToDTO(mte, true);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

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
        MetadataElementEntity result = metadataElementDao.findById(elementId);
        if (result == null)
            return null;
        return metaDataElementDozerConverter.convertToDTO(result, true);
    }

    public MetadataElement[] getMetadataElementByType(String typeId) {
        if (typeId == null) {
            throw new NullPointerException("typeId is null");
        }
        MetadataTypeEntity result = metadataTypeDao.findById(typeId);
        if (result == null)
            return null;
        MetadataType type = metaDataTypeDozerConverter.convertToDTO(result,
                true);
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
        MetadataTypeEntity result = metadataTypeDao.findById(typeId);
        if (result == null)
            return null;
        return metaDataTypeDozerConverter.convertToDTO(result, true);
    }

    public MetadataType[] getMetadataTypes() {
        List<MetadataTypeEntity> typeListEntity = metadataTypeDao.findAll();
        if (typeListEntity == null || typeListEntity.isEmpty()) {
            return null;
        }
        List<MetadataType> typeList = metaDataTypeDozerConverter
                .convertToDTOList(typeListEntity, true);

        int size = typeList.size();
        MetadataType[] typeAry = new MetadataType[size];
        typeList.toArray(typeAry);

        return typeAry;
    }

    public List<MetadataType> getTypesInCategory(String categoryId) {
        List<MetadataTypeEntity> resultList = metadataTypeDao
                .findTypesInCategory(categoryId);
        return metaDataTypeDozerConverter.convertToDTOList(resultList, true);
    }

    public void removeMetadataElement(String elementId) {
        if (elementId == null) {
            throw new NullPointerException("elementId is null");
        }
        metadataElementDao.delete(metadataElementDao.findById(elementId));
    }

    public void removeMetadataType(String typeId) {
        if (typeId == null) {
            throw new NullPointerException("typeId is null");
        }
        MetadataTypeEntity type = metadataTypeDao.findById(typeId);
        if (type != null)
            metadataTypeDao.delete(type);
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
}
