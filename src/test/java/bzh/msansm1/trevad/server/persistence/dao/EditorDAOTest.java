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

import bzh.msansm1.trevad.server.persistence.model.Editor;

/**
 * Tests for editor DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class EditorDAOTest {
    private static final Logger LOGGER = Logger.getLogger(EditorDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "editordao.war").addClass(EditorDAO.class)
                .addClass(Dao.class).addPackage(Editor.class.getPackage())
                .addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");

        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private EditorDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Editor editor = new Editor();

    public void saveEditorTest() {
        editor.setName("test editor");
        dao.saveEditor(editor);
        Assert.assertNotNull("Editor is not created", editor.getId());
    }

    public void getEditorTest() {
        Integer id = editor.getId();
        Editor created = dao.getEditor(id);
        Assert.assertNotNull("Editor is not found", created);
    }

    public void updateEditorTest() {
        Editor updated = dao.getEditor(editor.getId());
        updated.setName("changed :)");
        dao.updateEditor(updated);
        Assert.assertTrue("Editor is not updated",
                dao.getEditor(editor.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeEditorTest() {
        Integer id = editor.getId();
        Editor todel = dao.getEditor(id);
        dao.removeEditor(todel);
        Assert.assertNotNull("Editor is not removed", todel);
        Assert.assertNull("Editor is not removed(get request)", dao.getEditor(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveEditorTest();
        userTransaction.commit();

        getEditorTest();

        userTransaction.begin();
        updateEditorTest();
        userTransaction.commit();

        userTransaction.begin();
        removeEditorTest();
        userTransaction.commit();
    }

    @Test
    public void getAllEditorsTest() {
        List<Editor> l = dao.getEditors();
        Assert.assertNotNull("No Editor found", l);
    }

}
