package ypa.persistencecontext.spring;

import ypa.persistencecontext.IPersistenceContextHelper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Spring framework supports @PersitenceContext/@PersistenceUnit annotation to inject entityManager(transaction/extended).
 * If you use spring, extend this class to provide the entity manager.
 * It is strongly advised to use spring @PersistenceContext to inject the entity manager into IPersistenceContextHelper subclass,
 * because the spring PersistenceContextProxy is thread safe both in Local resource transaction and JtaTransaction.
 * Created by happyyangyuan on 15/9/17.
 */
public abstract class SpringPersistenceContextHelper implements IPersistenceContextHelper {

    @PersistenceContext(unitName = "please specify the persistence unit name here")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public EntityManager getEntityManager(String uintName){
        return em;
    }


}
