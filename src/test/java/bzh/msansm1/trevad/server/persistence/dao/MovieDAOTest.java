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

import bzh.msansm1.trevad.server.json.home.MovieStats;
import bzh.msansm1.trevad.server.json.movie.JsonMovie;
import bzh.msansm1.trevad.server.persistence.model.Movie;

/**
 * Tests for movie DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class MovieDAOTest {
    private static final Logger LOGGER = Logger.getLogger(MovieDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "moviedao.war").addClass(MovieDAO.class)
                .addClass(JsonMovie.class).addClass(Dao.class).addPackage(Movie.class.getPackage())
                .addClass(MovieStats.class).addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private MovieDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Movie movie = new Movie();

    public void saveMovieTest() {
        movie.setTitle("test movie");
        movie.setCover("YOUHOU");
        movie.setIscollector(false);
        dao.saveMovie(movie);
        Assert.assertNotNull("Movie is not created", movie.getId());
    }

    public void getMovieTest() {
        Integer id = movie.getId();
        Movie created = dao.getMovie(id);
        Assert.assertNotNull("Movie is not found", created);
    }

    public void updateMovieTest() {
        Movie updated = dao.getMovie(movie.getId());
        updated.setCover("changed :)");
        dao.updateMovie(updated);
        Assert.assertTrue("Movie is not updated",
                dao.getMovie(movie.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeMovieTest() {
        Integer id = movie.getId();
        Movie todel = dao.getMovie(id);
        dao.removeMovie(todel);
        Assert.assertNotNull("Movie is not removed", todel);
        Assert.assertNull("Movie is not removed(get request)", dao.getMovie(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveMovieTest();
        userTransaction.commit();

        getMovieTest();

        userTransaction.begin();
        updateMovieTest();
        userTransaction.commit();

        userTransaction.begin();
        removeMovieTest();
        userTransaction.commit();
    }

    @Test
    public void getAllMoviesTest() {
        List<Movie> l = dao.getMovies();
        Assert.assertNotNull("No Movie found", l);
    }

    @Test
    public void getUserMoviesTest() {
        List<JsonMovie> l = dao.getUsersMovies(1);
        Assert.assertNotNull("No user Movie found", l);
    }

}
