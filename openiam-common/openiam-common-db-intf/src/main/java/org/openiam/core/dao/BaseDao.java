package org.openiam.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.openiam.idm.searchbeans.SearchBean;

public interface BaseDao<T extends Serializable, PrimaryKey extends Serializable> {

  T findById(PrimaryKey id);

  T findById(PrimaryKey id, String ... fetchFields);

  List<T> findByIds(Collection<PrimaryKey> idCollection);
  
  List<T> findByIds(Collection<PrimaryKey> idCollection, final int from, final int size);

  List<T> findAll();

  List<PrimaryKey> getAllIds();
  
  Class<T> getDomainClass();

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

  List<T> getByExample(SearchBean searchBean);
  List<T> getByExample(SearchBean searchBean, int from, int size);
  List<String> getIDsByExample(SearchBean searchBean, int from, int size);
  List<T> getByExampleNoLocalize(SearchBean searchBean, int from, int size);


  int count(SearchBean searchBean);
  
  void flush();
  void clear();
  List<T> find(int from, int size);
    void evictCollectionRegions();
    public void evictCache();

    public void evictFromSecondLevelCache(T t);
}
