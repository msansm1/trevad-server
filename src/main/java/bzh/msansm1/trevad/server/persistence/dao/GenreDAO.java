package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Genre;

/**
 * DAO for GENRE table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class GenreDAO extends Dao {

    public void updateGenre(Genre genre) {
        em.merge(genre);
    }

    public void saveGenre(Genre genre) {
        em.persist(genre);
        em.refresh(genre);
    }

    public void removeGenre(Genre genre) {
        em.remove(em.merge(genre));
        em.flush();
    }

    public List<Genre> getGenres() {
        return em.createQuery("from Genre", Genre.class).getResultList();
    }

    public Genre getGenre(Integer id) {
        return em.find(Genre.class, id);
    }
}
