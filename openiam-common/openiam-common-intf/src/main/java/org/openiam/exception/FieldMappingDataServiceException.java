package org.openiam.exception;

import org.openiam.base.ws.ResponseCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 23/09/16.
 */
public class FieldMappingDataServiceException extends BasicDataServiceException {
    protected Map<String, String> fieldMappings;

    public FieldMappingDataServiceException(ResponseCode code) {
        super(code);
    }

    public FieldMappingDataServiceException(ResponseCode code, EsbErrorToken esbErrorToken) {
        super(code, esbErrorToken);
    }

    public FieldMappingDataServiceException(ResponseCode code, Throwable originalCause) {
        super(code, originalCause);
    }

    public FieldMappingDataServiceException(ResponseCode code, String responseValue) {
        super(code, responseValue);
    }
    public void addFieldMapping(final String field, final String value) {
        if(field != null && value != null) {
            if(this.fieldMappings == null) {
                this.fieldMappings = new HashMap<>();
            }
            this.fieldMappings.put(field, value);
        }
    }

    public Map<String, String> getFieldMappings() {
        return fieldMappings;
    }

    public void setFieldMappings(Map<String, String> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }
}
