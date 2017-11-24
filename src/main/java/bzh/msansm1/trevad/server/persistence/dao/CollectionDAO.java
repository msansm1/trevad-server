package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Collection;

/**
 * DAO for COLLECTION table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class CollectionDAO extends Dao {

    public void updateCollection(Collection collection) {
        em.merge(collection);
    }

    public void saveCollection(Collection collection) {
        em.persist(collection);
        em.refresh(collection);
    }

    public void removeCollection(Collection collection) {
        em.remove(em.merge(collection));
        em.flush();
    }

    public List<Collection> getCollections() {
        return em.createQuery("from Collection", Collection.class).getResultList();
    }

    public Collection getCollection(Integer id) {
        return em.find(Collection.class, id);
    }
}
