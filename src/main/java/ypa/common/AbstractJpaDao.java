package ypa.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ypa.model.exception.ExceptionCode;
import ypa.model.exception.YpaRuntimeException;
import ypa.persistencecontext.IPersistenceContextHelper;
import ypa.utils.JpqlUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ypa.utils.JpqlUtils.setParameters;

/**
 *
 */
public abstract class AbstractJpaDao implements IJpaDao {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Due to dao beans are always defined as singleton in bean container and EntityManager is not thread safe, you may worry about that.
     * But don't worry, the entity manager injected here is actually not the real JPA entity manager but a delegate.The spring
     * framework can assure you that separate entity managers used in separate spring transactions.
     * Here is the spring doc describes the fact:"Although EntityManagerFactory instances are thread-safe, EntityManager instances are not. The injected JPA EntityManager behaves like an EntityManager fetched from an application server’s JNDI environment, as defined by the JPA specification. It delegates all calls to the current transactional EntityManager, if any; otherwise, it falls back to a newly created EntityManager per operation, in effect making its usage thread-safe."
     */
    @PersistenceContext
    private EntityManager em;

    public <T> List<T> query(Class<T> clazz, Object condition) {
        return getQuery(clazz, condition).getResultList();
    }

    public <T> Long queryCount(Class<T> clazz, Object condition) {
        return (Long) getCountQuery(clazz, condition).getSingleResult();
    }

    public <T> T querySingleRecord(Class<T> clazz, Long id) {
        return em.find(clazz, id);
    }

    public <T> T queryMax(Class<T> clazz, Object condition, String maxColumnName) {
        T result;
        try {
            result = (T) getMaxQuery(clazz, condition, maxColumnName).getSingleResult();
        } catch (NoResultException e) {//有可能满足条件的结果一条都没有，那么更不谈返回指定字段值最大的那条记录了，那么这个时候返回null
            result = null;
        }
        return result;
    }

    public <T> T querySingleRecord(Class<T> clazz, Object condition) {
        Object entity = getQuery(clazz, condition).getSingleResult();
        return (T) entity;
    }

    public <T> List<T> orderByQuery(Class<T> clazz, Object condition, String ascOrDesc, String orderByColumn, int limit, int firstResult) {
        log.info("orderByQuery : oderby=" + orderByColumn + " " + ascOrDesc + " ,limit=" + limit + " ,firstResult=" + firstResult);
        return getQueryWithOrderBy(clazz, condition, orderByColumn, ascOrDesc, limit, firstResult).getResultList();
    }

    public <T> List<T> pagingQuery(Class<T> clazz, Object condition, int limit, int firstResult) {
        log.info("pagingQuery : limit=" + limit + " ,firstResult=" + firstResult);
        return getPagingQuery(clazz, condition, limit, firstResult).getResultList();
    }

    public <T> T merge(T entity) {
        return em.merge(entity);
    }

    public <T> List<T> mergeAll(List<T> entities) {
        List<T> mergedEntities = new ArrayList<T>();
        for (T entity : entities) {
            //由于entityManager.merge()返回的对象是受控的，这个受控对象是合并时新增的，它并没有修改传入的那个对象，也没有将其置为受控
            //所以，一定要重新构造返回列表
            entity = merge(entity);
            mergedEntities.add(entity);
        }
        return mergedEntities;
    }

    public <T> T update(T entity) {
        return em.merge(entity);
    }

    public <T> void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }


    public <T> T add(T entity) {
        em.persist(entity);
        return entity;
    }

    public <T> List<T> addAll(List<T> entities) {
        for (T t : entities) {
            add(t);
        }
        return entities;
    }

    public <T> void refresh(T entity, String keyName) {
        if (!em.contains(entity)) {
            try {
                entity = (T) em.find(entity.getClass(), entity.getClass().getMethod("get" + StringUtils.capitalize(keyName)).invoke(entity));
            } catch (Throwable e) {
                throw new YpaRuntimeException(ExceptionCode.REFLECTION_ERR, "Maybe " + keyName + " is not found in " + entity, e);
            }
        }
        em.refresh(entity);
    }

    public String getTableComment(String user, String table) {
        String sql = String.format(
                "select comments from dba_tab_comments where owner = '%s' and table_name = '%s'",
                user, table
        );
        return (String) em.createNativeQuery(sql).getSingleResult();
    }

    private Query getQuery(Class<?> clazz, Object condition) {
        String jpql = getBasicQueryJqpl(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        Query query = em.createQuery(jpql, clazz);
        setParameters(query, condition);
        return query;
    }

    private Query getCountQuery(Class<?> clazz, Object condition) {
        Query query = em.createQuery(getQueryCountJpql(clazz, condition));
        setParameters(query, condition);
        return query;
    }

    private String getQueryCountJpql(Class<?> clazz, Object condition) {
        return getBasicQueryJqpl(clazz, condition, JpqlUtils.SELECT_COUNT_DISTINCT_FROM);
    }

    private String getBasicQueryJqpl(Class<?> clazz, Object condition, String selectWhat) {
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
     * @param clazz
     * @param condition
     * @param orderByColumnName
     * @param descOrAsc
     * @param maxRecordCount    即limit
     * @return
     */
    private Query getQueryWithOrderBy(Class<?> clazz, Object condition, String orderByColumnName, String descOrAsc, Integer maxRecordCount, int firstResult) {
        String jpql = getBasicQueryJqpl(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        jpql += " order by alias." + orderByColumnName + " " + descOrAsc;
        Query query = em.createQuery(jpql, clazz);
        setParameters(query, condition);
        if (maxRecordCount != null && maxRecordCount > 0) {
            query.setMaxResults(maxRecordCount);
        }
        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        return query;
    }

    private Query getPagingQuery(Class<?> clazz, Object condition, Integer maxRecordCount, int firstResult) {
        String jpql = getBasicQueryJqpl(clazz, condition, JpqlUtils.SELECT_ALIAS_FROM);
        Query query = em.createQuery(jpql, clazz);
        setParameters(query, condition);
        if (maxRecordCount != null && maxRecordCount > 0) {
            query.setMaxResults(maxRecordCount);
        }
        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        return query;
    }

    private Query getMaxQuery(Class<?> clazz, Object condition, String maxColumnName) {
        //之所以不使用select max ，请参看 http://blog.chinaunix.net/uid-21879027-id-3475578.html
        int maxResult = 1, firstResult = -1;//firstResult = -1代表不限制firstResult
        return getQueryWithOrderBy(clazz, condition, maxColumnName, JpqlUtils.DESC, maxResult, firstResult);
    }

}
