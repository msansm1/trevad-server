package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Trackartist;
import bzh.msansm1.trevad.server.persistence.model.TrackartistPK;

/**
 * DAO for TRACKARTIST table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class TrackartistDAO extends Dao {

    public void updateTrackartist(Trackartist trackartist) {
        em.merge(trackartist);
    }

    public void saveTrackartist(Trackartist trackartist) {
        em.persist(trackartist);
    }

    public void removeTrackartist(Trackartist trackartist) {
        em.remove(em.merge(trackartist));
        em.flush();
    }

    public List<Trackartist> getTrackartists() {
        return em.createQuery("from Trackartist", Trackartist.class).getResultList();
    }

    public Trackartist getTrackartist(TrackartistPK taid) {
        return em.find(Trackartist.class, taid);
    }
}
