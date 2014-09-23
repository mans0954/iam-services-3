package org.openiam.core.dao.lucene;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HibernateSearchDao<T, Q, KeyType> {

    int count(Q query);
	List<T> find(int from, int size, SortType sortType, Q query);
	
	/**
	 * Get a list of entity ids
	 * @param startRow start row No 
	 * @param size size of rows
	 * @param sortType type of sorting
	 * @param query search bean
	 * @return list of entity ids 
	 */
	List<KeyType> findIds(int from, int size, SortType sortType, Q query);
	
	
	/**
	 * Get a list of entity ids
	 * @param sortType type of sorting
	 * @param query search bean
	 * @return list of entity ids 
	 */
	List<KeyType> findIds(final SortType sort, final Q query);

	/**
	 * synchronize indexes state with database state
	 */
	void synchronizeIndexes(boolean forcePurgeAll);
	
	/**
	 * Returns {@code Class<T>} type of an entity for a given hibernate search service
	 * @return class
	 */
    Class<T> getSearchEntityClass();
    
    /**
     * Checks if indexes of corresponding entity are being synchronized.
     * @return {@code true} if indexes are being synchronized, {@code false} otherwise 
     */
    boolean isSynchronizing();
    
    /**
     * Returns last synchronization duration.
     * @return last synchronization duration
     */
    long getLastSynchronizationDuration();

    /**
     * Returns when the last search index synchronization was made.
     * @return date time of the last search index synchronization
     */
    public Date getReindexingCompletedOn();
    
    /**
     * Returns when the last search index synchronization was made according to Db time.
     * @return date time of the last search index synchronization
     */
    Date getLastDbUpdateDate();

    /**
     * Start indexing the provided entities
     * @param idList
     */
    public void updateIndecies(List<String> idList) throws Exception;

    public void deleteIndecies(List<String> idList) throws Exception;
}
