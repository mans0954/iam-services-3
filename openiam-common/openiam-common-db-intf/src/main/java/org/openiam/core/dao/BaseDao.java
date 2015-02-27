package org.openiam.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.openiam.idm.searchbeans.SearchBean;

public interface BaseDao<T, PrimaryKey extends Serializable> {

  T findById(PrimaryKey id);
  
  T findInitializedObjectById(PrimaryKey id);

  T findById(PrimaryKey id, String ... fetchFields);

  T findByIdNoLocalized(PrimaryKey id, String ... fetchFields);

  List<T> findByIds(Collection<PrimaryKey> idCollection);
  
  List<T> findByIds(Collection<PrimaryKey> idCollection, final int from, final int size);

  List<T> findAll();

  List<PrimaryKey> getAllIds();

  Long countAll();
  
  void update(T t);

  T merge(T t);

  void refresh(T t);

  void save(T t);

  T add(T t);

  void persist(T t);

  void delete(T t);

  void save(Collection<T> entities);

  void deleteAll()  throws Exception;

  void attachDirty(T t);

  void attachClean(T t);

  void evict(T t);

  List<T> getByExample(T t, int startAt, int size);
  List<T> getByExample(T t);
  List<T> getByExample(SearchBean searchBean);
  List<T> getByExample(SearchBean searchBean, int from, int size);
  List<String> getIDsByExample(SearchBean searchBean, int from, int size);

  int count(SearchBean searchBean);
  int count(T t);

  void flush();
  void clear();

}
