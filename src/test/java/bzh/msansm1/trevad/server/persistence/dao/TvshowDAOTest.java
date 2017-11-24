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

import bzh.msansm1.trevad.server.json.home.SerieStats;
import bzh.msansm1.trevad.server.json.tvshow.JsonShow;
import bzh.msansm1.trevad.server.persistence.model.Tvshow;

/**
 * Tests for tvshow DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class TvshowDAOTest {
    private static final Logger LOGGER = Logger.getLogger(TvshowDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "tvshowdao.war").addClass(TvshowDAO.class)
                .addClass(JsonShow.class).addClass(Dao.class).addPackage(Tvshow.class.getPackage())
                .addClass(SerieStats.class).addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private TvshowDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Tvshow tvshow = new Tvshow();

    public void saveTvshowTest() {
        tvshow.setTitle("test tvshow");
        tvshow.setCover("YOUHOU");
        dao.saveTvshow(tvshow);
        Assert.assertNotNull("Tvshow is not created", tvshow.getId());
    }

    public void getTvshowTest() {
        Integer id = tvshow.getId();
        Tvshow created = dao.getTvshow(id);
        Assert.assertNotNull("Tvshow is not found", created);
    }

    public void updateTvshowTest() {
        Tvshow updated = dao.getTvshow(tvshow.getId());
        updated.setCover("changed :)");
        dao.updateTvshow(updated);
        Assert.assertTrue("Tvshow is not updated",
                dao.getTvshow(tvshow.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeTvshowTest() {
        Integer id = tvshow.getId();
        Tvshow todel = dao.getTvshow(id);
        dao.removeTvshow(todel);
        Assert.assertNotNull("Tvshow is not removed", todel);
        Assert.assertNull("Tvshow is not removed(get request)", dao.getTvshow(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveTvshowTest();
        userTransaction.commit();

        getTvshowTest();

        userTransaction.begin();
        updateTvshowTest();
        userTransaction.commit();

        userTransaction.begin();
        removeTvshowTest();
        userTransaction.commit();
    }

    @Test
    public void getAllTvshowsTest() {
        List<Tvshow> l = dao.getTvshows();
        Assert.assertNotNull("No Tvshow found", l);
    }

    @Test
    public void getUserTvshowsTest() {
        List<JsonShow> l = dao.getUsersTvshows(1);
        Assert.assertNotNull("No Tvshow found", l);
    }

}
