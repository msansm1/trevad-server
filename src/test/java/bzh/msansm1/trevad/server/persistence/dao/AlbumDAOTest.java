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

/**
 * Tests for album DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class AlbumDAOTest {
    private static final Logger LOGGER = Logger.getLogger(AlbumDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "albumdao.war").addClass(AlbumDAO.class)
                .addPackage(JsonAlbum.class.getPackage()).addClass(Dao.class).addPackage(Album.class.getPackage())
                .addClass(AlbumStats.class).addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private AlbumDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Album album = new Album();

    public void saveAlbumTest() {
        album.setTitle("test album");
        album.setCover("YOUHOU");
        dao.saveAlbum(album);
        Assert.assertNotNull("Album is not created", album.getId());
    }

    public void getAlbumTest() {
        Integer id = album.getId();
        Album created = dao.getAlbum(id);
        Assert.assertNotNull("Album is not found", created);
    }

    public void updateAlbumTest() {
        Album updated = dao.getAlbum(album.getId());
        updated.setCover("changed :)");
        dao.updateAlbum(updated);
        Assert.assertTrue("Album is not updated",
                dao.getAlbum(album.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeAlbumTest() {
        Integer id = album.getId();
        Album todel = dao.getAlbum(id);
        dao.removeAlbum(todel);
        Assert.assertNotNull("Album is not removed", todel);
        Assert.assertNull("Album is not removed(get request)", dao.getAlbum(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveAlbumTest();
        userTransaction.commit();

        getAlbumTest();

        userTransaction.begin();
        updateAlbumTest();
        userTransaction.commit();

        userTransaction.begin();
        removeAlbumTest();
        userTransaction.commit();
    }

    @Test
    public void getAllAlbumsTest() {
        List<Album> l = dao.getAlbums();
        Assert.assertNotNull("No Album found", l);
    }

    @Test
    public void getUserAlbumsTest() {
        List<JsonAlbum> l = dao.getUsersAlbums(1);
        Assert.assertFalse("No user Album found", l.isEmpty());
    }

}
