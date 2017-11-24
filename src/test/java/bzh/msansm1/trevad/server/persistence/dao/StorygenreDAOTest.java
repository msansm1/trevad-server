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

import bzh.msansm1.trevad.server.persistence.model.Storygenre;

/**
 * Tests for storygenre DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class StorygenreDAOTest {
    private static final Logger LOGGER = Logger.getLogger(StorygenreDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "storygenredao.war").addClass(StorygenreDAO.class)
                .addClass(Dao.class).addPackage(Storygenre.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private StorygenreDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Storygenre storygenre = new Storygenre();

    public void saveStorygenreTest() {
        storygenre.setName("test storygenre");
        dao.saveStorygenre(storygenre);
        Assert.assertNotNull("Storygenre is not created", storygenre.getId());
    }

    public void getStorygenreTest() {
        Integer id = storygenre.getId();
        Storygenre created = dao.getStorygenre(id);
        Assert.assertNotNull("Storygenre is not found", created);
    }

    public void updateStorygenreTest() {
        Storygenre updated = dao.getStorygenre(storygenre.getId());
        updated.setName("changed :)");
        dao.updateStorygenre(updated);
        Assert.assertTrue("Storygenre is not updated",
                dao.getStorygenre(storygenre.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeStorygenreTest() {
        Integer id = storygenre.getId();
        Storygenre todel = dao.getStorygenre(id);
        dao.removeStorygenre(todel);
        Assert.assertNotNull("Storygenre is not removed", todel);
        Assert.assertNull("Storygenre is not removed(get request)", dao.getStorygenre(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveStorygenreTest();
        userTransaction.commit();

        getStorygenreTest();

        userTransaction.begin();
        updateStorygenreTest();
        userTransaction.commit();

        userTransaction.begin();
        removeStorygenreTest();
        userTransaction.commit();
    }

    @Test
    public void getAllStorygenresTest() {
        List<Storygenre> l = dao.getStorygenres();
        Assert.assertNotNull("No Storygenre found", l);
    }

}
