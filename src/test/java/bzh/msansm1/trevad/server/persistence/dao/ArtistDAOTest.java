package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import bzh.msansm1.trevad.server.persistence.model.Artist;
import bzh.msansm1.trevad.server.persistence.model.Artisttype;

/**
 * Tests for artist DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class ArtistDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ArtistDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "artistdao.war").addClass(ArtistDAO.class)
                .addClass(ArtisttypeDAO.class).addClass(Dao.class).addPackage(Artist.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private ArtistDAO dao;

    @Inject
    private ArtisttypeDAO atdao;

    @Resource
    private UserTransaction userTransaction;

    final Artist artist = new Artist();

    public void saveArtistTest() {
        Artisttype at = new Artisttype();
        at.setName("at");
        atdao.saveArtisttype(at);
        artist.setFirstname("toto");
        artist.setName("tata");
        artist.setArtisttype(at);
        dao.saveArtist(artist);
        Assert.assertNotNull("Artist is not created", artist.getId());
    }

    public void getArtistTest() {
        Integer id = artist.getId();
        Artist created = dao.getArtist(id);
        Assert.assertNotNull("Artist is not found", created);
    }

    public void updateArtistTest() {
        Artist updated = dao.getArtist(artist.getId());
        updated.setName("newtiti");
        dao.updateArtist(updated);
        Assert.assertTrue("Artist is not updated", dao.getArtist(artist.getId()).getName().equalsIgnoreCase("newtiti"));

    }

    public void removeArtistTest() {
        Integer id = artist.getId();
        Artist todel = dao.getArtist(id);
        dao.removeArtist(todel);
        Assert.assertNotNull("Artist is not removed", todel);
        Assert.assertNull("Artist is not removed(get request)", dao.getArtist(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveArtistTest();
        userTransaction.commit();

        getArtistTest();

        userTransaction.begin();
        updateArtistTest();
        userTransaction.commit();

        userTransaction.begin();
        removeArtistTest();
        userTransaction.commit();
    }

    @Test
    public void getAllArtistsTest() {
        List<Artist> l = dao.getArtists();
        Assert.assertNotNull("No Artist found", l);
    }

}
