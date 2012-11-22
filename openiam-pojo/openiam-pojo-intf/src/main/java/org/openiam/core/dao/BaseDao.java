package org.openiam.core.dao;

import org.hibernate.LockOptions;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseDao<T, PrimaryKey extends Serializable> {

  T findById(PrimaryKey id);

  T findById(PrimaryKey id, String ... fetchFields);
  
  List<T> findByIds(Collection<PrimaryKey> idCollection);

  List<T> findAll();

  Long countAll();
  
  void update(T t);
  
  void merge(T t);

  void save(T t);

  void delete(T t);

  void save(Collection<T> entities);

  void deleteAll()  throws Exception;

  void attachDirty(T t);

  void attachClean(T t);
  
  List<T> getByExample(T t, int startAt, int size);
  List<T> getByExample(T t);
  int count(T t);
}
