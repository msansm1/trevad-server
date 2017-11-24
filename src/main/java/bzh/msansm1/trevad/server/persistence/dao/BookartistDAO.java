package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Bookartist;

/**
 * DAO for BOOKARTIST table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class BookartistDAO extends Dao {

    public void updateBookartist(Bookartist bookartist) {
        em.merge(bookartist);
    }

    public void saveBookartist(Bookartist bookartist) {
        em.persist(bookartist);
        em.refresh(bookartist);
    }

    public void removeBookartist(Bookartist bookartist) {
        em.remove(em.merge(bookartist));
        em.flush();
    }

    public List<Bookartist> getBookartists() {
        return em.createQuery("from Bookartist", Bookartist.class).getResultList();
    }

    public Bookartist getBookartist(Integer id) {
        return em.find(Bookartist.class, id);
    }
}
