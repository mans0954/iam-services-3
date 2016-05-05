package org.openiam.imprt.jdbc.parser;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.query.expression.GroupBy;
import org.openiam.imprt.query.expression.OrderByList;

import java.util.List;
import java.util.Map;

/**
 * Class IBaseParser is public interface for all parsers implementation
 *
 * @param <E>
 * @author D.Zaporozhec
 */
public interface IBaseParser<E> {

    /**
     * Get entity record by primary key for current table
     *
     * @param id
     * @return entity by primary key for current table
     * @throws Exception
     */
    E getById(String id) throws Exception;

    /**
     * Get record count for current table
     *
     * @return record count for current table
     * @throws Exception
     */
    int getCount() throws Exception;

    /**
     * Get record list for current table by SQL Expression
     *
     * @param res
     * @return record list for current table by SQL Expression
     * @throws Exception
     */
    List<E> get(Expression res) throws Exception;

    /**
     * Get all record list for current table
     *
     * @return all record list for current table
     * @throws Exception
     */
    List<E> getAll() throws Exception;

    /**
     * Add new record for current table
     *
     * @param e
     * @return new added record for current table
     */
    E add(E e);

    /**
     * Delete record by ID for current table
     *
     * @param id
     * @throws Exception
     */
    void delete(String id) throws Exception;

    /**
     * Delete record by some field for current table
     *
     * @param name
     * @param value
     * @throws Exception
     */
    void delete(ImportPropertiesKey name, String value) throws Exception;

    /**
     * Get record list for current table by SQL Expression and apply Sort Order
     *
     * @param expression
     * @param orderBy
     * @return record list for current table by SQL Expression and apply Sort
     * Order
     * @throws Exception
     */
    List<E> get(Expression expression, OrderByList orderBy) throws Exception;

    /**
     * Get record list for current table with specify columns
     *
     * @param columns
     * @return record list for current table with specify columns
     * @throws Exception
     */
    List<E> getAll(Column[] columns) throws Exception;

    /**
     * Get record list for current table by some SQL query with specify columns
     *
     * @param query
     * @param columns
     * @return record list for current table by some SQL query with specify
     * columns
     * @throws Exception
     */
    List<E> get(String query, List<Column> columns) throws Exception;

    /**
     * Get record list for current table with sorting and grouping for specify
     * columns
     *
     * @param expression
     * @param orderBy
     * @param groupBy
     * @param columns
     * @return record list for current table with sorting and grouping for
     * specify columns
     * @throws Exception
     */
    List<E> get(Expression expression, OrderByList orderBy, GroupBy groupBy, Column[] columns) throws Exception;

    /**
     * Get record list for current table with Expression apply sorting
     *
     * @param expression
     * @param orderBy
     * @param columns
     * @return record list for current table with Expression apply sorting
     * @throws Exception
     */
    List<E> get(Expression expression, OrderByList orderBy, Column[] columns) throws Exception;

    /**
     * Add list records for current table
     *
     * @param e
     * @throws Exception
     */
    void addAll(List<E> e) throws Exception;

    /**
     * @param query
     * @return
     * @throws Exception
     */
    public List<E> get(String query) throws Exception;

    /**
     * @param e
     * @return
     */
    public E update(E e, String pk);

    public void update(Map<String, E> map);

    public void executeNativeCRUD(String sql, List<List<Object>> values);
}
