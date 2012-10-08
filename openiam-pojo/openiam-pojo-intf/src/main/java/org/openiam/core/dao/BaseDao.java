package org.openiam.core.dao;

import java.util.Collection;
import java.util.List;

public interface BaseDao<T> {

  T findById(String id);

  T findById(String id, String ... fetchFields);

  List<T> findAll();

  Long countAll();

  void save(T t);

  void delete(T t);

  void save(Collection<T> entities);
}
