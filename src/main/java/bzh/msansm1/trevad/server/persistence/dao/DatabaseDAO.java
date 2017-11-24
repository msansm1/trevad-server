package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Database;

/**
 * DAO for DATABASE table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class DatabaseDAO extends Dao {

    public void updateDatabase(Database dataBase) {
        em.merge(dataBase);
    }

    public void saveDatabase(Database dataBase) {
        em.persist(dataBase);
        em.refresh(dataBase);
    }

    public void removeDatabase(Database dataBase) {
        em.remove(em.merge(dataBase));
        em.flush();
    }

    public List<Database> getAllDatabases() {
        return em.createQuery("from Database", Database.class).getResultList();
    }

    public Database getDatabase(Integer id) {
        return em.find(Database.class, id);
    }
}
