package ypa.common.multisource;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Deal with applications which has multiple datasource , especially in JTA situation.
 * Created by happyyangyuan on 15/9/17.
 */
public interface IMultiDataSourceJapDao {

    EntityManager getEntityManager(String unitName);

    <T> Collection<T> query(String unitName, Class<T> clazz, Object condition);

    <T> Long queryCount(String unitName, Class<T> clazz, Object condition);

    <T> T querySingleRecord(String unitName, Class<T> clazz, Long id);

    <T> T queryMax(String unitName, Class<T> clazz, Object condition, String maxColumnName);

    <T> T querySingleRecord(String unitName, Class<T> clazz, Object condition);

    <T> Collection<T> orderByQuery(String unitName, Class<T> clazz, Object condition, String ascOrDesc, String orderByColumn, int limit, int firstResult);

    <T> Collection<T> pagingQuery(String unitName, Class<T> clazz, Object condition, int limit, int firstResult);

    <T> T merge(String unitName, T entity);

    <T> Collection<T> mergeAll(String unitName, Collection<T> entities);

    <T> T update(String unitName, T entity);

    <T> void delete(String unitName, T entity);

    <T> T add(String unitName, T entity);

    <T> Collection<T> addAll(String unitName, Collection<T> entities);

    <T> void refresh(String unitName, T entity, String keyName);

    <T> void refreshAll(String unitName, Collection<T> entities, String keyName);

    String getTableComment(String unitName,String user,String table);

}
