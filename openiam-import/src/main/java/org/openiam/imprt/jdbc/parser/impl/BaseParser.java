package org.openiam.imprt.jdbc.parser.impl;


import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.jdbc.AbstractJDBCAgent;
import org.openiam.imprt.jdbc.parser.IBaseParser;
import org.openiam.imprt.query.AddQueryBuilder;
import org.openiam.imprt.query.Restriction;
import org.openiam.imprt.query.SelectQueryBuilder;
import org.openiam.imprt.query.UpdateQueryBuilder;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.query.expression.GroupBy;
import org.openiam.imprt.query.expression.OrderByList;
import org.openiam.imprt.util.DataHolder;
import org.openiam.imprt.util.Utils;

import java.util.*;

/**
 * Base class for implement IBaseParser
 *
 * @author D.Zaporozhec
 */
abstract public class BaseParser<E> extends AbstractJDBCAgent<E> implements IBaseParser<E> {

    public BaseParser() {
        this.initDB();
    }

    /**
     * 123qweasdzxc Prepare parser
     *
     * @throws Exception
     */
    protected abstract void init() throws Exception;

    /**
     * Parsing entity to list of column with values
     *
     * @param entity
     * @return list of records
     */
    private List<Object> parsing(E entity) {
        if (entity == null)
            return null;
        List<Object> list = new ArrayList<Object>();
        try {
            for (ImportPropertiesKey column : this.getColumnsName()) {
                parseToList(list, column, entity);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /**
     * Create new object of SelectQueryBuilder
     *
     * @return
     */
    private SelectQueryBuilder getSelectQuery() {
        return new SelectQueryBuilder(this.getTableName(), getAllColumns(true));
    }

    /**
     * Get column list for entity
     *
     * @param isWithPK
     * @return
     */
    private Column[] getAllColumns(boolean isWithPK) {
        ImportPropertiesKey[] fields = null;
        if (isWithPK) {
            fields = this.union(this.getColumnsName(), this.getPrimaryKeyName());
        } else {
            fields = this.getColumnsName();
        }
        Column[] cols = new Column[fields.length];
        for (int i = 0; i < fields.length; i++) {
            cols[i] = new Column(fields[i], null);
        }

        return cols;
    }

    @Override
    public void executeNativeCRUD(String sql, List<List<Object>> values) {
        try {
            this.executeNativeQuery(sql, values);
        } catch (Exception e) {
            System.out.println("Can't process '" + sql + "' " + e);
        }

    }

    /**
     * Get SelectQuery for column list
     *
     * @param columns
     * @return
     */
    private SelectQueryBuilder getSelectQuery(Column[] columns) {
        if (columns == null) {
            columns = getAllColumns(true);
        }
        return new SelectQueryBuilder(this.getTableName(), columns);
    }

    /**
     * Generate query for Add new record
     *
     * @return
     */
    private AddQueryBuilder getAddQuery() {
        return new AddQueryBuilder(this.getTableName(), this.getAllColumns(false));
    }

    /**
     * Generate query for Add new record
     *
     * @param size
     * @return
     */
    private AddQueryBuilder getAddQuery(int size) {
        return new AddQueryBuilder(this.getTableName(), size, this.getAllColumns(false));
    }

    private UpdateQueryBuilder getUpdateQuery() {
        return new UpdateQueryBuilder(this.getTableName(), this.getPkName(), this.getAllColumns(false));
    }

    /*
     * @inheritDoc
     * 
     * @see org.sfedu.cad.graph.jdbc.parser.IBaseParser#getById(long)
     */
    @Override
    public E getById(String id) throws Exception {
        init();
        SelectQueryBuilder query = this.getSelectQuery();
        query.addRestriction(Restriction.eq(this.getPkName(), String.valueOf(id)));
        List<E> sqlResult = this.executeQuery(query);
        if (sqlResult == null || sqlResult.isEmpty()) {
            return null;
        }
        if (sqlResult.size() > 1) {
            logger.debug("Error: Ununique id");
        }
        E e = sqlResult.get(0);
        finish();
        return e;
    }

    /**
     * Get primary key name
     *
     * @return
     */
    private String getPkName() {
        return DataHolder.getInstance().getProperty(getPrimaryKeyName());
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#get(org.sfedu.cad.graph.query
     * .expression.Expression, org.sfedu.cad.graph.query.expression.OrderByList,
     * org.sfedu.cad.graph.query.expression.Column[])
     */
    @Override
    public List<E> get(Expression expression, OrderByList orderBy, Column[] columns) throws Exception {
        return this.get(expression, orderBy, null, columns);
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#get(org.sfedu.cad.graph.query
     * .expression.Expression, org.sfedu.cad.graph.query.expression.OrderByList,
     * org.sfedu.cad.graph.query.expression.GroupBy,
     * org.sfedu.cad.graph.query.expression.Column[])
     */
    @Override
    public List<E> get(Expression expression, OrderByList orderBy, GroupBy groupBy, Column[] columns) throws Exception {
        init();
        SelectQueryBuilder query = this.getSelectQuery(columns);
        query.addRestriction(expression);
        query.orderBy(orderBy);
        query.groupBy(groupBy);
        List<E> sqlResult = this.executeQuery(query);
        if (sqlResult == null || sqlResult.isEmpty()) {
            return null;
        }
        finish();
        return sqlResult;
    }

    /*
     * @inheritDoc
     * 
     * @see org.sfedu.cad.graph.jdbc.parser.IBaseParser#get(java.lang.String,
     * java.util.List)
     */
    @Override
    public List<E> get(String query, List<Column> columns) throws Exception {
        init();
        List<E> sqlResult = this.executeQuery(query, columns);
        if (sqlResult == null || sqlResult.isEmpty()) {
            return null;
        }
        finish();
        return sqlResult;
    }

    @Override
    public List<E> get(String query) throws Exception {
        init();
        List<E> sqlResult = this.executeQuery(query, Arrays.asList(this.getAllColumns(false)));
        if (sqlResult == null || sqlResult.isEmpty()) {
            return null;
        }
        finish();
        return sqlResult;
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#get(org.sfedu.cad.graph.query
     * .expression.Expression, org.sfedu.cad.graph.query.expression.OrderByList)
     */
    @Override
    public List<E> get(Expression expression, OrderByList orderBy) throws Exception {
        return this.get(expression, orderBy, this.getAllColumns(true));
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#get(org.sfedu.cad.graph.query
     * .expression.Expression)
     */
    @Override
    public List<E> get(Expression expression) throws Exception {
        return get(expression, null);
    }

    /**
     * @param e1
     * @param e2
     * @return
     */
    private ImportPropertiesKey[] union(ImportPropertiesKey[] e1, ImportPropertiesKey[] e2) {
        if (e1 != null && e2 != null) {
            int count = 0;
            ImportPropertiesKey[] array = new ImportPropertiesKey[e1.length + e2.length];
            for (ImportPropertiesKey e : e1) {
                array[count++] = e;
            }
            for (ImportPropertiesKey e : e2) {
                array[count++] = e;
            }
            return array;
        } else
            return e1 == null ? e2 : e1;
    }

    /**
     * @param e1
     * @param e2
     * @return
     */
    private ImportPropertiesKey[] union(ImportPropertiesKey[] e1, ImportPropertiesKey e2) {
        ImportPropertiesKey[] e = null;
        if (e2 != null) {
            e = new ImportPropertiesKey[]{e2};
        }
        return union(e1, e);
    }

    /*
     * @inheritDoc
     * 
     * @see org.sfedu.cad.graph.jdbc.parser.IBaseParser#getCount()
     */
    @Override
    public int getCount() throws Exception {
        SelectQueryBuilder query = this.getSelectQuery();
        List<E> sqlResult = this.executeQuery(query);
        if (sqlResult == null || sqlResult.isEmpty()) {
            return 0;
        } else
            return sqlResult.size();
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#getAll(org.sfedu.cad.graph
     * .query.expression.Column[])
     */
    @Override
    public List<E> getAll(Column[] columns) throws Exception {
        init();
        SelectQueryBuilder query = this.getSelectQuery(columns);
        List<E> sqlResult = this.executeQuery(query);
        if (Utils.isEmpty(sqlResult)) {
            return null;
        }
        finish();
        return sqlResult;
    }

    /*
     * @inheritDoc
     * 
     * @see org.sfedu.cad.graph.jdbc.parser.IBaseParser#getAll()
     */
    @Override
    public List<E> getAll() throws Exception {
        return getAll(null);
    }

    abstract void finish();

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#add(org.sfedu.cad.graph.model
     * .entity.BaseEntity)
     */
    @Override
    public E add(E e) {
        AddQueryBuilder addQuery = this.getAddQuery();
        try {
            this.addAll(addQuery, Arrays.asList(this.parsing(e)));
        } catch (Exception e1) {
            System.out.println(String.valueOf(e1));
        }
        return null;
    }

    @Override
    public E update(E e, String pk) {
        UpdateQueryBuilder updateQuery = this.getUpdateQuery();
        try {
            Map<String, List<Object>> map = new HashMap<>();
            map.put(pk, this.parsing(e));
            this.updateAll(updateQuery, map);
        } catch (Exception e1) {
            System.out.println("Error during update. " + e);
        }
        return null;
    }

    @Override
    public void update(Map<String, E> map) {
        UpdateQueryBuilder updateQuery = this.getUpdateQuery();
        try {

            Map<String, List<Object>> mapO = new HashMap<>();
            for (String m : map.keySet()) {
                mapO.put(m, this.parsing(map.get(m)));
            }
            this.updateAll(updateQuery, mapO);
        } catch (Exception e1) {
            System.out.println("Error during update. " + e1);
        }
    }

    /*
     * @inheritDoc
     * 
     * @see org.sfedu.cad.graph.jdbc.parser.IBaseParser#addAll(java.util.List)
     */
    @Override
    public void addAll(List<E> e) throws Exception {
        AddQueryBuilder addQuery = this.getAddQuery(e.size());
        List<List<Object>> lists = new ArrayList<List<Object>>();
        for (E e1 : e) {
            lists.add(this.parsing(e1));
        }
        this.addAll(addQuery, lists);
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @throws Exception
     */
    protected void deleteCommon(ImportPropertiesKey fieldName, String fieldValue) throws Exception {
        DataHolder holder = DataHolder.getInstance();
        this.delete(holder.getProperty(this.getTableName()), holder.getProperty(fieldName), fieldValue);
    }

    @Override
    public void delete(String id) throws Exception {
        this.deleteCommon(this.getPrimaryKeyName(), id);
    }

    /*
     * @inheritDoc
     * 
     * @see
     * org.sfedu.cad.graph.jdbc.parser.IBaseParser#delete(org.sfedu.cad.graph
     * .constant.ImportPropertiesKey, long)
     */
    @Override
    public void delete(ImportPropertiesKey name, String value) throws Exception {
        this.deleteCommon(name, value);
    }

    protected MetadataTypeEntity getMetadataType(String value) {
        MetadataTypeEntity mt = new MetadataTypeEntity();
        mt.setId(value);
        return mt;
    }


    protected MetadataElementEntity getMetadataElementEntity(String value) {
        MetadataElementEntity mt = new MetadataElementEntity();
        mt.setId(value);
        return mt;
    }

}
