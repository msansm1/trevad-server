package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Tvartist;

/**
 * DAO for TVARTIST table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class TvartistDAO extends Dao {

    public void updateTvartist(Tvartist tvartist) {
        em.merge(tvartist);
    }

    public void saveTvartist(Tvartist tvartist) {
        em.persist(tvartist);
        em.refresh(tvartist);
    }

    public void removeTvartist(Tvartist tvartist) {
        em.remove(em.merge(tvartist));
        em.flush();
    }

    public List<Tvartist> getTvartists() {
        return em.createQuery("from Tvartist", Tvartist.class).getResultList();
    }

    public Tvartist getTvartist(Integer id) {
        return em.find(Tvartist.class, id);
    }
}
