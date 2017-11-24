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

import bzh.msansm1.trevad.server.persistence.model.Lang;

/**
 * Tests for lang DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class LangDAOTest {
    private static final Logger LOGGER = Logger.getLogger(LangDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "langdao.war").addClass(LangDAO.class)
                .addClass(Dao.class).addPackage(Lang.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private LangDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Lang lang = new Lang();

    public void saveLangTest() {
        lang.setName("test lang");
        dao.saveLang(lang);
        Assert.assertNotNull("Lang is not created", lang.getId());
    }

    public void getLangTest() {
        Integer id = lang.getId();
        Lang created = dao.getLang(id);
        Assert.assertNotNull("Lang is not found", created);
    }

    public void updateLangTest() {
        Lang updated = dao.getLang(lang.getId());
        updated.setName("changed :)");
        dao.updateLang(updated);
        Assert.assertTrue("Lang is not updated", dao.getLang(lang.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeLangTest() {
        Integer id = lang.getId();
        Lang todel = dao.getLang(id);
        dao.removeLang(todel);
        Assert.assertNotNull("Lang is not removed", todel);
        Assert.assertNull("Lang is not removed(get request)", dao.getLang(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveLangTest();
        userTransaction.commit();

        getLangTest();

        userTransaction.begin();
        updateLangTest();
        userTransaction.commit();

        userTransaction.begin();
        removeLangTest();
        userTransaction.commit();
    }

    @Test
    public void getAllLangsTest() {
        List<Lang> l = dao.getLangs();
        Assert.assertNotNull("No Lang found", l);
    }

}
