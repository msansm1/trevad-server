package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Booktype;

/**
 * DAO for BOOKTYPE table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class BooktypeDAO extends Dao {

    public void updateBooktype(Booktype booktype) {
        em.merge(booktype);
    }

    public void saveBooktype(Booktype booktype) {
        em.persist(booktype);
        em.refresh(booktype);
    }

    public void removeBooktype(Booktype booktype) {
        em.remove(em.merge(booktype));
        em.flush();
    }

    public List<Booktype> getBooktypes() {
        return em.createQuery("from Booktype", Booktype.class).getResultList();
    }

    public Booktype getBooktype(Integer id) {
        return em.find(Booktype.class, id);
    }
}
