package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import bzh.msansm1.trevad.server.persistence.model.Storygenre;

/**
 * DAO for STORYGENRE table
 * 
 * @author msansm1
 *
 */
@RequestScoped
public class StorygenreDAO extends Dao {

    public void updateStorygenre(Storygenre storygenre) {
        em.merge(storygenre);
    }

    public void saveStorygenre(Storygenre storygenre) {
        em.persist(storygenre);
        em.refresh(storygenre);
    }

    public void removeStorygenre(Storygenre storygenre) {
        em.remove(em.merge(storygenre));
        em.flush();
    }

    public List<Storygenre> getStorygenres() {
        return em.createQuery("from Storygenre", Storygenre.class).getResultList();
    }

    public Storygenre getStorygenre(Integer id) {
        return em.find(Storygenre.class, id);
    }
}
