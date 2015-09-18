package ypa.utils;

import ypa.model.exception.ExceptionCode;
import ypa.model.exception.YpaRuntimeException;
import ypa.persistencecontext.IPersistenceContextHelper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This entity manager factory is only used for test.
 * It only creates thread none-safe entity managers.
 * Created by happyyangyuan on 15/9/18.
 */
public class PersistenceContextHelperForTest implements IPersistenceContextHelper {
    public EntityManager getEntityManager() {
        throw new YpaRuntimeException(ExceptionCode.NOT_SUPPORT_ERR,null,null);
    }

    public EntityManager getEnttiyManager(String unitName) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unitName);
        return emf.createEntityManager();
    }

}
