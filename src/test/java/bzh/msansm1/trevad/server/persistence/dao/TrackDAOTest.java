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

import bzh.msansm1.trevad.server.json.album.JsonAlbum;
import bzh.msansm1.trevad.server.json.home.AlbumStats;
import bzh.msansm1.trevad.server.persistence.model.Album;
import bzh.msansm1.trevad.server.persistence.model.Track;

/**
 * Tests for track DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class TrackDAOTest {
    private static final Logger LOGGER = Logger.getLogger(TrackDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "trackdao.war").addClass(TrackDAO.class)
                .addClass(AlbumDAO.class).addClass(JsonAlbum.class).addClass(Dao.class)
                .addPackage(Track.class.getPackage()).addClass(AlbumStats.class)
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private TrackDAO dao;
    @Inject
    private AlbumDAO albumDao;

    @Resource
    private UserTransaction userTransaction;

    final Track track = new Track();

    public void saveTrackTest() {
        Album a = new Album();
        a.setTitle("testa");
        albumDao.saveAlbum(a);
        track.setTitle("test track");
        track.setLength("2:30");
        track.setAlbumBean(a);
        dao.saveTrack(track);
        Assert.assertNotNull("Track is not created", track.getId());
    }

    public void getTrackTest() {
        Integer id = track.getId();
        Track created = dao.getTrack(id);
        Assert.assertNotNull("Track is not found", created);
    }

    public void updateTrackTest() {
        Track updated = dao.getTrack(track.getId());
        updated.setLength("10:10");
        dao.updateTrack(updated);
        Assert.assertTrue("Track is not updated", dao.getTrack(track.getId()).getLength().equalsIgnoreCase("10:10"));

    }

    public void removeTrackTest() {
        Integer id = track.getId();
        Track todel = dao.getTrack(id);
        dao.removeTrack(todel);
        Assert.assertNotNull("Track is not removed", todel);
        Assert.assertNull("Track is not removed(get request)", dao.getTrack(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveTrackTest();
        userTransaction.commit();

        getTrackTest();

        userTransaction.begin();
        updateTrackTest();
        userTransaction.commit();

        userTransaction.begin();
        removeTrackTest();
        userTransaction.commit();
    }

    @Test
    public void getAllTracksTest() {
        List<Track> l = dao.getTracks();
        Assert.assertNotNull("No Track found", l);
    }

    @Test
    public void getTracksForAlbumTest() {
        List<Track> l = dao.getTracksForAlbum(Integer.valueOf(1));
        Assert.assertFalse("No Track found", l.isEmpty());
    }

}
