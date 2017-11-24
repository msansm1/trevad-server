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

import bzh.msansm1.trevad.server.persistence.model.Database;

/**
 * Tests for database DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class DatabaseDAOTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "databasedao.war").addClass(DatabaseDAO.class)
                .addClass(Dao.class).addPackage(Database.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private DatabaseDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Database database = new Database();

    public void saveDatabaseTest() {
        database.setVersion("test database");
        dao.saveDatabase(database);
        Assert.assertNotNull("Database is not created", database.getId());
    }

    public void getDatabaseTest() {
        Integer id = database.getId();
        Database created = dao.getDatabase(id);
        Assert.assertNotNull("Database is not found", created);
    }

    public void updateDatabaseTest() {
        Database updated = dao.getDatabase(database.getId());
        updated.setVersion("changed :)");
        dao.updateDatabase(updated);
        Assert.assertTrue("Database is not updated",
                dao.getDatabase(database.getId()).getVersion().equalsIgnoreCase("changed :)"));

    }

    public void removeDatabaseTest() {
        Integer id = database.getId();
        Database todel = dao.getDatabase(id);
        dao.removeDatabase(todel);
        Assert.assertNotNull("Database is not removed", todel);
        Assert.assertNull("Database is not removed(get request)", dao.getDatabase(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveDatabaseTest();
        userTransaction.commit();

        getDatabaseTest();

        userTransaction.begin();
        updateDatabaseTest();
        userTransaction.commit();

        userTransaction.begin();
        removeDatabaseTest();
        userTransaction.commit();
    }

    @Test
    public void getAllDatabasesTest() {
        List<Database> l = dao.getAllDatabases();
        Assert.assertNotNull("No Database found", l);
    }

}
