package bzh.msansm1.trevad.server.persistence.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 * Abstract class for DAO settings (All DAO classes must extend it)
 * 
 * @author PC_Projets02
 * 
 */
public abstract class Dao {

    @PersistenceContext(unitName = "trevaddb")
    EntityManager em;

    @Resource
    private UserTransaction userTransaction;

    <T> T find(Class<T> entity, int primaryKey) {
        T ent = em.find(entity, primaryKey);
        em.refresh(ent);
        return ent;
    }

}
