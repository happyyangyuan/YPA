package ypa.persistencecontext;

import javax.persistence.EntityManager;

/**
 * The JpaDao class gets entity managers from here
 * Created by happyyangyuan on 15/9/17.
 */
public interface IPersistenceContextHelper {

    /**
     * @return the default entity manager will be returned when no persistence unit name is provided.
     */
    EntityManager getEntityManager();

    /**
     * Get the unitName specified entity manager.
     *
     * @param unitName Please refer to persistence.xml for the unitName.
     * @return An entity manager
     */
    EntityManager getEnttiyManager(String unitName);
}
