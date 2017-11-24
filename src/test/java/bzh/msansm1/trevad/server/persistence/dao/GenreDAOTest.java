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

import bzh.msansm1.trevad.server.persistence.model.Genre;

/**
 * Tests for genre DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class GenreDAOTest {
    private static final Logger LOGGER = Logger.getLogger(GenreDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "genredao.war").addClass(GenreDAO.class)
                .addClass(Dao.class).addPackage(Genre.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private GenreDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Genre genre = new Genre();

    public void saveGenreTest() {
        genre.setName("test genre");
        dao.saveGenre(genre);
        Assert.assertNotNull("Genre is not created", genre.getId());
    }

    public void getGenreTest() {
        Integer id = genre.getId();
        Genre created = dao.getGenre(id);
        Assert.assertNotNull("Genre is not found", created);
    }

    public void updateGenreTest() {
        Genre updated = dao.getGenre(genre.getId());
        updated.setName("changed :)");
        dao.updateGenre(updated);
        Assert.assertTrue("Genre is not updated", dao.getGenre(genre.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeGenreTest() {
        Integer id = genre.getId();
        Genre todel = dao.getGenre(id);
        dao.removeGenre(todel);
        Assert.assertNotNull("Genre is not removed", todel);
        Assert.assertNull("Genre is not removed(get request)", dao.getGenre(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveGenreTest();
        userTransaction.commit();

        getGenreTest();

        userTransaction.begin();
        updateGenreTest();
        userTransaction.commit();

        userTransaction.begin();
        removeGenreTest();
        userTransaction.commit();
    }

    @Test
    public void getAllGenresTest() {
        List<Genre> l = dao.getGenres();
        Assert.assertNotNull("No Genre found", l);
    }

}
