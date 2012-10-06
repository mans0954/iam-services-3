package org.openiam.core.dao;

import java.util.Collection;
import java.util.List;

public interface BaseDao<T> {

  T findById(String id);

  T findById(String id, String ... fetchFields);

  Collection<T> findByIds(Collection<String> ids, String ... fetchFields);

  public List<T> findAll();

  public Long countAll();

  public void save(T t);

  public void delete(T t);

  public void save(Collection<T> entities);
}
