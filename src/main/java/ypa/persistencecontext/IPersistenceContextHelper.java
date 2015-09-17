package ypa.persistencecontext;

import javax.persistence.EntityManager;

/**
 * The JpaDao class gets entity managers from here
 * Created by happyyangyuan on 15/9/17.
 */
public interface IPersistenceContextHelper {

    EntityManager getEntityManager();

    EntityManager getEnttiyManager(String unitName);
}
