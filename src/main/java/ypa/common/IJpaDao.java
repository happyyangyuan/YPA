package ypa.common;

import java.util.List;

/**
 * Interface of common queries.
 * Created by happyyangyuan on 15/9/17.
 */
public interface IJpaDao {

    /**
     * The most common query. Provide an entity class and a simple condition,it returns you a collection of entities.
     *
     * @param clazz     Entity class
     * @param condition Query condition, it should be a serializable object.
     * @param <T>       The general type, it should be always an entity type.
     * @return Query result. Usually a list will be returned,depends on sub implementations.
     */
    <T> List<T> query(Class<T> clazz, Object condition);

    /**
     * Count query
     *
     * @param clazz     Entity class
     * @param condition Query condition, it should be a serializable object.
     * @param <T>       The general type, it should be always an entity type.
     * @return The number of results that match the query condition.
     */
    <T> Long queryCount(Class<T> clazz, Object condition);

    /**
     * Query single record via the entity id.
     *
     * @param clazz Entity class
     * @param id    Entity id,currently we only support none-composite id with the type of 'Long'.
     * @param <T>   The general type, it should be always an entity type.
     * @return Because we query via id,so only single result is returned.
     */
    <T> T querySingleRecord(Class<T> clazz, Long id);

    /**
     * Find the entity with a maximum value on specified column/property.
     *
     * @param clazz         Entity class
     * @param condition     Query condition, it should be a serializable object.
     * @param maxColumnName According to which property/column to look for the entity with a maximum value.
     * @param <T>           The general type, it should be always an entity type.
     * @return The entity with a maximum value on specified column/property.
     */
    <T> T queryMax(Class<T> clazz, Object condition, String maxColumnName);

    <T> T querySingleRecord(Class<T> clazz, Object condition);

    /**
     * 支持分页功能的orderby查询
     *
     * @param clazz
     * @param condition     查询条件
     * @param ascOrDesc     升序或者降序
     * @param orderByColumn orderby字段
     * @param limit         查询条数
     * @param firstResult   查询结果的起始号
     * @param <T>           The general type, it should be always an entity type.
     * @return
     */
    <T> List<T> orderByQuery(Class<T> clazz, Object condition, String ascOrDesc, String orderByColumn, int limit, int firstResult);

    /**
     * 标准的分页查询
     *
     * @param clazz
     * @param condition
     * @param limit
     * @param firstResult 从0开始取值
     * @param <T>
     * @return
     */
    <T> List<T> pagingQuery(Class<T> clazz, Object condition, int limit, int firstResult);

    <T> T merge(T entity);

    <T> List<T> mergeAll(List<T> entities);

    <T> T update(T entity);

    /**
     * 根据entity的id从数据库中删除该记录
     *
     * @param entity 要删除的实体，其id属性为删除依据，不能为空
     */
    <T> void delete(T entity);

    <T> T add(T entity);

    <T> List<T> addAll(List<T> entities);

    /**
     * 查询table的备注信息，目前仅适用于oracle
     *
     * @param user
     * @param table
     */
    String getTableComment(String user, String table);
}
