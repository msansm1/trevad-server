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

import bzh.msansm1.trevad.server.persistence.model.Collection;

/**
 * Tests for collection DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class CollectionDAOTest {
    private static final Logger LOGGER = Logger.getLogger(CollectionDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "collectiondao.war").addClass(CollectionDAO.class)
                .addClass(Dao.class).addPackage(Collection.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private CollectionDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Collection collection = new Collection();

    public void saveCollectionTest() {
        collection.setName("test collection");
        dao.saveCollection(collection);
        Assert.assertNotNull("Collection is not created", collection.getId());
    }

    public void getCollectionTest() {
        Integer id = collection.getId();
        Collection created = dao.getCollection(id);
        Assert.assertNotNull("Collection is not found", created);
    }

    public void updateCollectionTest() {
        Collection updated = dao.getCollection(collection.getId());
        updated.setName("changed :)");
        dao.updateCollection(updated);
        Assert.assertTrue("Collection is not updated",
                dao.getCollection(collection.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeCollectionTest() {
        Integer id = collection.getId();
        Collection todel = dao.getCollection(id);
        dao.removeCollection(todel);
        Assert.assertNotNull("Collection is not removed", todel);
        Assert.assertNull("Collection is not removed(get request)", dao.getCollection(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveCollectionTest();
        userTransaction.commit();

        getCollectionTest();

        userTransaction.begin();
        updateCollectionTest();
        userTransaction.commit();

        userTransaction.begin();
        removeCollectionTest();
        userTransaction.commit();
    }

    @Test
    public void getAllCollectionsTest() {
        List<Collection> l = dao.getCollections();
        Assert.assertNotNull("No Collection found", l);
    }

}
