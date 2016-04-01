package org.openiam.dozer.converter;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.dozer.ConfigurableCustomConverter;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zaporozhec
 */
public class DisplayNameDozerConverter extends DozerConverter<Map, String> implements MapperAware {
    private final static String USER_DISPLAY_NAME = "USER_DISPLAY_NAME";
    private Mapper mapper;

    public DisplayNameDozerConverter() {
        super(Map.class, String.class);
    }


    @Override
    public String convertTo(Map source, String destination) {
        if (source == null || source.size() == 0) {
            return null;
        }
        UserAttributeEntity attributeEntity = (UserAttributeEntity) source.get(USER_DISPLAY_NAME);
        if (attributeEntity == null || StringUtils.isBlank(attributeEntity.getValue())) {
            return null;
        }
        return attributeEntity.getValue();
    }

    @Override
    public Map convertFrom(String source, Map destination) {
        return null;
    }

    @Override
    public void setMapper(Mapper mapper) {

    }
}
