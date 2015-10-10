package ypa.common.multisource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ypa.persistencecontext.IPersistenceContextHelper;
import ypa.utils.JpqlUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ypa.utils.JpqlUtils.setParameters;

/**
 * Created by happyyangyuan on 15/7/20.
 * 支持多数据源的DAO，即我们可以使用它来连接多个mysql/orcle...
 */
public abstract class AbstractMultiDataSourceJpaDao implements IMultiDataSourceJapDao {

    private Logger log = LoggerFactory.getLogger(getClass());

    public EntityManager getEntityManager(String unitName) {
        return getPersistenceContextHelper().getEnttiyManager(unitName);
    }

    /**
     * It is strongly suggested that you implement this method and return a SpringPersistenceContextHelper
     * @return IPersistenceContextHelper implementation
     */
    protected abstract IPersistenceContextHelper getPersistenceContextHelper();

    /**
     * 通过实体和查询条件经过jpql拼装，进行查询并返回满足条件的查询结果
     *
     * @param unitName  persistenceUnitName持久化单元名
     * @param clazz     JPA实体类型
     * @param condition 查询条件
     * @return 查询结果实体列表
     */
    public <T> List<T> query(String unitName, Class<T> clazz, Object condition) {
        return getQuery(unitName, clazz, condition).getResultList();
    }

    public <T> Long queryCount(String unitName, Class<T> clazz, Object condition) {
        return (Long) getCountQuery(unitName, clazz, condition).getSingleResult();
    }

    public <T> T querySingleRecord(String unitName, Class<T> clazz, Long id) {
        return getEntityManager(unitName).find(clazz, id);
    }

    public <T> T queryMax(String unitName, Class<T> clazz, Object condition, String maxColumnName) {
        T result;
        try {
            result = (T) getMaxQuery(unitName, clazz, condition, maxColumnName).getSingleResult();
        } catch (NoResultException e) {//有可能满足条件的结果一条都没有，那么更不谈返回指定字段值最大的那条记录了，那么这个时候返回null
            result = null;
        }
        return result;
    }

    public <T> T querySingleRecord(String unitName, Class<T> clazz, Object condition) {
        Object entity = getQuery(unitName, clazz, condition).getSingleResult();
        return (T) entity;
    }

    /**
     * 支持分页功能的orderby查询
     *
     * @param clazz
     * @param condition     查询条件
     * @param ascOrDesc     升序或者降序
     * @param orderByColumn orderby字段
     * @param limit         查询条数
     * @param firstResult   查询结果的起始号
     * @param <T>
     * @return
     */
    public <T> List<T> orderByQuery(String unitName, Class<T> clazz, Object condition, String ascOrDesc, String orderByColumn, int limit, int firstResult) {
        log.info("orderByQuery : oderby=" + orderByColumn + " " + ascOrDesc + " ,limit=" + limit + " ,firstResult=" + firstResult);
        return getQueryWithOrderBy(unitName, clazz, condition, orderByColumn, ascOrDesc, limit, firstResult).getResultList();
    }

    /**
     * 标准的分页查询
     *
     * @param unitName    持久化单元名
     * @param clazz
     * @param condition
     * @param limit
     * @param firstResult 从0开始取值
     * @param <T>
     * @return
     */
    public <T> List<T> pagingQuery(String unitName, Class<T> clazz, Object condition, int limit, int firstResult) {
        log.info("pagingQuery : limit=" + limit + " ,firstResult=" + firstResult);
        return getPagingQuery(unitName, clazz, condition, limit, firstResult).getResultList();
    }

    public <T> T merge(String unitName, T entity) {
        return getEntityManager(unitName).merge(entity);
    }

    public <T> List<T> mergeAll(String unitName, List<T> entities) {
        List<T> mergedEntities = new ArrayList<T>();
        for (T entity : entities) {
            //由于entityManager.merge()返回的对象是受控的，这个受控对象是合并时新增的，它并没有修改传入的那个对象，也没有将其置为受控
            //所以，一定要重新构造返回列表
            entity = merge(unitName, entity);
            mergedEntities.add(entity);
        }
        return mergedEntities;
    }

    /**
     * 更新实体，也可以用来持久化实体
     *
     * @param entity 要更新的实体，主键id不可为空，被更新的记录是以该id定位实体的。
     */
    public <T> T update(String unitName, T entity) {
        return getEntityManager(unitName).merge(entity);
    }

    /**
     * 根据entity的id从数据库中删除该记录
     *
     * @param entity 要删除的实体，其id属性为删除依据，不能为空
     */
    public <T> void delete(String unitName, T entity) {
        EntityManager em = getEntityManager(unitName);
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }


    public <T> T add(String unitName, T entity) {
        getEntityManager(unitName).persist(entity);
        return entity;
    }

    public <T> List<T> addAll(String unitName, List<T> entities) {
        for (T t : entities) {
            add(unitName, t);
        }
        return entities;
    }

    /**
     * @param entities
     * @param keyName
     * @param <T>
     * @deprecated 此方法目前还有问题，日后调试
     */
    public <T> void refreshAll(String unitName, Collection<T> entities, String keyName) {
        for (T entity : entities) {
            refresh(unitName, entity, keyName);
        }
    }

    /**
     * 查询table的备注信息，目前仅适用于oracle
     *
     * @param unitName
     * @param user
     * @param table
     */
    public String getTableComment(String unitName, String user, String table) {
        String sql = String.format(
                "select comments from dba_tab_comments where owner = '%s' and table_name = '%s'",
                user, table
        );
        return (String) getEntityManager(unitName).createNativeQuery(sql).getSingleResult();
    }


    /**
     * 通过实体和查询条件经过jpql拼装，获取JPA查询对象
     *
     * @param clazz     JPA实体类型
     * @param condition 查询条件
     * @return JPA查询
     */
    private Query getQuery(String unitName, Class<?> clazz, Object condition) {
        String jpql = getBasicQueryJpql(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        Query query = getEntityManager(unitName).createQuery(jpql, clazz);
        setParameters(query, condition);
        return query;
    }

    /**
     * 获取满足指定条件的实体条数查询对象
     *
     * @param clazz
     * @param condition
     * @return JPA查询对象
     */
    private Query getCountQuery(String unitName, Class<?> clazz, Object condition) {
        Query query = getEntityManager(unitName).createQuery(getQueryCountJpql(clazz, condition));
        setParameters(query, condition);
        return query;
    }

    private String getQueryCountJpql(Class<?> clazz, Object condition) {
        return getBasicQueryJpql(clazz, condition, JpqlUtils.SELECT_COUNT_DISTINCT_FROM);
    }

    private String getBasicQueryJpql(Class<?> clazz, Object condition, String selectWhat) {
        String jpql = selectWhat + " " + clazz.getName() + " alias "
                + JpqlUtils.getOuterJoinClause(condition, "alias") + JpqlUtils.getInnerJoinClause(condition, "alias")
                + " where 1 = 1 ";
        for (String property : JpqlUtils.getCaredProperties(condition)) {
            jpql += " and alias." + /*pathProperty(*/property/*)*/ + "= :" + property + " ";// 使用namedParameter的原因是防止字符串值需要加单引号的问题。
        }
        List<String> jpqlFragments = JpqlUtils.getDirectJpqlFragments(condition, "alias");
        for (String fragment : jpqlFragments) {
            jpql += " and " + fragment + " ";
        }
        log.info(jpql);
        return jpql;
    }

    /**
     * @param unitName          持久化单元名
     * @param clazz
     * @param condition
     * @param orderByColumnName
     * @param descOrAsc
     * @param maxRecordCount    即limit
     * @return
     */
    private Query getQueryWithOrderBy(String unitName, Class<?> clazz, Object condition, String orderByColumnName, String descOrAsc, Integer maxRecordCount, int firstResult) {
        String jpql = getBasicQueryJpql(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        jpql += " order by alias." + orderByColumnName + " " + descOrAsc;
        Query query = getEntityManager(unitName).createQuery(jpql, clazz);
        setParameters(query, condition);
        if (maxRecordCount != null && maxRecordCount > 0) {
            query.setMaxResults(maxRecordCount);
        }
        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        return query;
    }

    private Query getPagingQuery(String unitName, Class<?> clazz, Object condition, Integer maxRecordCount, int firstResult) {
        String jpql = getBasicQueryJpql(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        Query query = getEntityManager(unitName).createQuery(jpql, clazz);
        setParameters(query, condition);
        if (maxRecordCount != null && maxRecordCount > 0) {
            query.setMaxResults(maxRecordCount);
        }
        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        return query;
    }

    private Query getMaxQuery(String unitName, Class<?> clazz, Object condition, String maxColumnName) {
        //之所以不使用select max ，请参看 http://blog.chinaunix.net/uid-21879027-id-3475578.html
        int maxResult = 1, fristResult = -1;//firstresult = -1代表不限制firstresult
        return getQueryWithOrderBy(unitName, clazz, condition, maxColumnName, JpqlUtils.DESC, maxResult, fristResult);
    }

}
