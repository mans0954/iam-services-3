package org.openiam.idm.parser.csv;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapUtil;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractCSVParser<T, E extends Enum<E>> {
    protected static char SEPARATOR = ',';
    protected static char END_OF_LINE = '\n';
    @Value("${iam.files.location}")
    private String pathToCSV;

    /**
     * @param pathToCSV
     *            the pathToCSV to set
     */
    public void setPathToCSV(String pathToCSV) {
        this.pathToCSV = pathToCSV;
    }

    protected static final Log log = LogFactory.getLog(AbstractCSVParser.class);

    private ReconciliationObject<T> csvToObject(Class<T> clazz,
            Class<E> clazz2, List<AttributeMapEntity> attrMap,
            Map<String, String> object) throws InstantiationException,
            IllegalAccessException {
        ReconciliationObject<T> csvObject = new ReconciliationObject<T>();
        T obj = clazz.newInstance();
        for (AttributeMapEntity a : attrMap) {
            String objValue = object.get(a.getAttributeName());
            if (StringUtils.hasText(objValue)) {
                String name = AttributeMapUtil.getAttributeIDMFieldName(a);
                if (name != null) {
                    E fieldValue;
                    try {
                        fieldValue = Enum.valueOf(clazz2, name);
                    } catch (IllegalArgumentException e) {
                        log.info(e.getMessage());
                        fieldValue = Enum.valueOf(clazz2, "DEFAULT");
                    }
                    this.putValueInDTO(obj, fieldValue, objValue.trim());
                    if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equals(a.getMapForObjectType())) {
                        csvObject.setPrincipal(objValue);
                    }
                }
            }
        }
        csvObject.setObject(obj);
        return csvObject;
    }

    protected T reconRowToObject(Class<T> clazz, Class<E> clazz2,
            List<ReconciliationResultField> header,
            List<ReconciliationResultField> fields, T baseEntity,
            boolean onlyKeyField) throws InstantiationException,
            IllegalAccessException {
        if (header == null || fields == null)
            return null;
        T obj = null;
        if (baseEntity == null) {
            obj = clazz.newInstance();
        } else {
            obj = baseEntity;
        }

        for (int i = 0; i < header.size(); i++) {
            if (CollectionUtils.isEmpty(header.get(i).getValues())
                    || CollectionUtils.isEmpty(fields.get(i).getValues())) {
                continue;
            }
            E fieldValue = null;
            try {
                if (onlyKeyField) {
                    if (header.get(i).isKeyField()) {
                        fieldValue = Enum.valueOf(clazz2, header.get(i)
                                .getValues().get(1));
                        this.putValueInDTO(obj, fieldValue, fields.get(i)
                                .getValues().get(0));
                        break;
                    }
                } else {
                    fieldValue = Enum.valueOf(clazz2, header.get(i).getValues()
                            .get(1));
                    this.putValueInDTO(obj, fieldValue, fields.get(i)
                            .getValues().get(0));
                }
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
                fieldValue = Enum.valueOf(clazz2, "DEFAULT");
            }
        }

        return obj;
    }

    protected abstract void putValueInDTO(T obj, E field, String value);

    protected abstract String putValueIntoString(T obj, E field);

    @SuppressWarnings("resource")
    private InputStream getCSVFile(ManagedSysEntity mngSys,
            List<AttributeMapEntity> attrMapList, Class<E> clazz,
            CSVSource source) throws Exception {
        if (attrMapList == null || attrMapList.isEmpty())
            return null;
        File file = new File(getFileName(mngSys, source));
        if (!file.exists()) {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(generateHeader(attrMapList));
            writer.flush();
            writer.close();
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        if (!StringUtils.hasText(br.readLine())) {
            FileWriter writer = new FileWriter(file);
            writer.write(generateHeader(attrMapList));
            writer.flush();
            writer.close();
        }
        return new FileInputStream(file);
    }

    /**
     * when create csv
     * 
     * @param attrMap
     * @return
     */
    private String generateHeader(List<AttributeMapEntity> attrMap) {
        if (attrMap == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (AttributeMapEntity a : attrMap) {
            if (StringUtils.hasText(a.getAttributeName())) {
                sb.append(a.getAttributeName());
                sb.append(SEPARATOR);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(END_OF_LINE);
        return sb.toString();
    }

    private String getObjectType(Class<E> clazz) {
        if (UserFields.class.getName().equals(clazz.getName()))
            return "USER";
        return "";
    }

    /*
     * from csv header
     */
    private String mergeValues(String[] fields) {
        if (fields == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String a : fields) {
            sb.append(a);
            sb.append(SEPARATOR);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(END_OF_LINE);
        return sb.toString();
    }

    /**
     * get file Name. File name was generated using ManagedSysId
     * 
     * @param mngSys
     * @return
     */
    public String getFileName(ManagedSysEntity mngSys, CSVSource source) {
        StringBuilder sb = new StringBuilder(pathToCSV);
        if (CSVSource.UPLOADED.equals(source)) {
            sb.append("recon_");
        }
        sb.append(mngSys.getResourceId());
        sb.append(".csv");
        return sb.toString();
    }

    /**
     * validate fileds in csv header and value in List<AttributeMap>
     * 
     * @param attrMap
     * @param headerFields
     * @return
     */
    private boolean validateCSVHeader(List<AttributeMapEntity> attrMap,
            String[] headerFields, Class<E> clazz) {
        String header = mergeValues(headerFields).toLowerCase();
        for (AttributeMapEntity am : attrMap) {
            if (!header.contains(am.getAttributeName().toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * generate Map <headerValue, value>
     * 
     * @param header
     * @param object
     * @return
     */
    private Map<String, String> generataPairs(String[] header, String[] object) {
        Map<String, String> pairs = new HashMap<String, String>(0);
        for (int i = 0; i < header.length; i++) {
            pairs.put(header[i], i < object.length ? object[i] : "");
        }
        return pairs;
    }

    /**
     * Parse obj to CSV
     * 
     * @param attrMap
     * @param obj
     * @param clazz
     * @return
     */
    private String[] objectToCSV(List<AttributeMapEntity> attrMap,
            ReconciliationObject<T> obj, Class<E> clazz) {
        List<String> values = new ArrayList<String>(0);

        for (String field : this.generateHeader(attrMap)
                .replace(String.valueOf(END_OF_LINE), "")
                .split(String.valueOf(SEPARATOR))) {
            AttributeMapEntity am = this.findAttributeMapByAttributeName(
                    attrMap, field);
            if (am != null) {
                if (StringUtils.hasText(am.getMapForObjectType())) {
                    if (this.getObjectType(clazz).equals(
                            am.getMapForObjectType())) {
                        E fields;
                        try {
                            String name = AttributeMapUtil
                                    .getAttributeIDMFieldName(am);
                            fields = Enum.valueOf(clazz, name);
                        } catch (IllegalArgumentException illegalArgumentException) {
                            log.info(illegalArgumentException.getMessage());
                            fields = Enum.valueOf(clazz, "DEFAULT");
                        }
                        values.add(this.putValueIntoString(obj.getObject(),
                                fields));
                    } else if (PolicyMapObjectTypeOptions.PRINCIPAL.name()
                            .equals(am.getMapForObjectType())) {
                        values.add(obj.getPrincipal() == null ? "" : obj
                                .getPrincipal());
                    }
                }
            } else
                values.add("");
        }
        return values.toArray(new String[0]);
    }

    private AttributeMapEntity findAttributeMapByAttributeName(
            List<AttributeMapEntity> attrMap, String field) {
        if (!StringUtils.hasText(field) && attrMap == null)
            return null;
        for (AttributeMapEntity am : attrMap) {
            if (am.getAttributeName() != null
                    && am.getAttributeName().equalsIgnoreCase(field)) {
                return am;
            }
        }
        return null;
    }

    /**
     * return "" when o is null
     * 
     * @param o
     * @return
     */
    protected static String toString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    /**
     * Parse from CSV to list of objects
     * 
     * @param managedSys
     * @param attrMapList
     *            \
     * @param clazz
     * @return
     * @throws Exception
     */
    protected List<ReconciliationObject<T>> getObjectList(
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            Class<T> clazz, Class<E> enumClass, CSVSource source)
            throws Exception {
        List<ReconciliationObject<T>> objects = new ArrayList<ReconciliationObject<T>>(
                0);
        InputStream input = this.getCSVFile(managedSys, attrMapList, enumClass, source);
        if (input == null) {
            return objects;
        }

        CSVHelper parser = new CSVHelper(input);
        String[][] fromParse = parser.getAllValues();
        input.close();

        if (fromParse.length > 1) {
            if (this.validateCSVHeader(attrMapList, fromParse[0], enumClass)) {
                for (int i = 1; i < fromParse.length; i++) {
                    objects.add(this.csvToObject(clazz, enumClass, attrMapList,
                            this.generataPairs(fromParse[0], fromParse[i])));
                }
            }
        }
        return objects;
    }

    public Map<String, ReconciliationResultField> convertToMap(
            List<AttributeMapEntity> attrMap, ReconciliationObject<T> obj,
            Class<E> clazz) {
        String[] values = this.objectToCSV(attrMap, obj, clazz);
        String[] header_ = this.generateHeader(attrMap)
                .replace(String.valueOf(END_OF_LINE), "")
                .split(String.valueOf(SEPARATOR));
        Map<String, ReconciliationResultField> result = new HashMap<String, ReconciliationResultField>(
                0);
        if (values.length != header_.length) {
            log.error("CSV internal error");
            return null;
        }
        for (int i = 0; i < header_.length; i++) {
            ReconciliationResultField field = new ReconciliationResultField();
            List<String> l = new ArrayList<String>();
            l.add(values[i]);
            field.setValues(l);
            result.put(header_[i], field);
        }
        return result;
    }

    public ReconciliationObject<T> toReconciliationObject(T pu,
            List<AttributeMapEntity> attrMap, Class<E> enumClass) {
        ReconciliationObject<T> object = new ReconciliationObject<T>();
        object.setObject(pu);
        for (AttributeMapEntity a : attrMap) {
            String name = a.getAttributeName();
            if (name != null) {
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equals(a.getMapForObjectType())) {
                    E fieldValue;
                    try {
                        fieldValue = Enum.valueOf(enumClass, name);
                    } catch (IllegalArgumentException e) {
                        log.info(e.getMessage());
                        fieldValue = Enum.valueOf(enumClass, "DEFAULT");
                    }
                    object.setPrincipal(this.putValueIntoString(pu, fieldValue));
                }
            }

        }
        return object;
    }

    /**
     * Add object in CSV -file. If file not exists this method create one
     * 
     * @param newObject
     *            - we add it in csv
     * @param managedSys
     *            - manage sysntem
     * @param attrMapList
     *            - list of data mapping
     * @param clazz
     *            - class of a Type
     * @throws Exception
     */
    protected void appendObjectToCSV(ReconciliationObject<T> newObject,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            Class<T> clazz, Class<E> enumClass, boolean append, CSVSource source)
            throws Exception {
        if (this.getCSVFile(managedSys, attrMapList, enumClass, source) != null) {
            String fName = this.getFileName(managedSys, source);
            FileWriter fw = new FileWriter(fName, append);
            fw.append(this.mergeValues(this.objectToCSV(attrMapList, newObject,
                    enumClass)));
            fw.flush();
            fw.close();
        } else {
            throw new Exception("Can't work with CSV");
        }
    }

    protected void updateCSV(List<ReconciliationObject<T>> newObjectList,
            ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
            Class<T> clazz, Class<E> enumClass, boolean append, CSVSource source)
            throws Exception {
        if (this.getCSVFile(managedSys, attrMapList, enumClass, source) != null) {
            String fName = this.getFileName(managedSys, source);
            FileWriter fw = new FileWriter(fName, append);
            fw.append(this.generateHeader(attrMapList));
            for (ReconciliationObject<T> t : newObjectList) {
                fw.append(this.mergeValues(this.objectToCSV(attrMapList, t,
                        enumClass)));
            }
            fw.flush();
            fw.close();
        } else {
            throw new Exception("Can't work with CSV");
        }
    }

    public String getObjectSimlpeClass(Class<T> clazz) {
        return clazz.getSimpleName();
    }
}
