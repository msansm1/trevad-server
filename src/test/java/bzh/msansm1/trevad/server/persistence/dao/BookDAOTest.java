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

import bzh.msansm1.trevad.server.json.book.JsonBook;
import bzh.msansm1.trevad.server.json.home.BookStats;
import bzh.msansm1.trevad.server.persistence.model.Book;

/**
 * Tests for book DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class BookDAOTest {
    private static final Logger LOGGER = Logger.getLogger(BookDAOTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "bookdao.war").addClass(BookDAO.class)
                .addClass(JsonBook.class).addClass(Dao.class).addPackage(Book.class.getPackage())
                .addClass(BookStats.class).addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");
        LOGGER.info(war.toString(Formatters.VERBOSE));

        return war;
    }

    @Inject
    private BookDAO dao;

    @Resource
    private UserTransaction userTransaction;

    final Book book = new Book();

    public void saveBookTest() {
        book.setTitle("test book");
        book.setCover("YOUHOU");
        dao.saveBook(book);
        Assert.assertNotNull("Book is not created", book.getId());
    }

    public void getBookTest() {
        Integer id = book.getId();
        Book created = dao.getBook(id);
        Assert.assertNotNull("Book is not found", created);
    }

    public void updateBookTest() {
        Book updated = dao.getBook(book.getId());
        updated.setCover("changed :)");
        dao.updateBook(updated);
        Assert.assertTrue("Book is not updated", dao.getBook(book.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeBookTest() {
        Integer id = book.getId();
        Book todel = dao.getBook(id);
        dao.removeBook(todel);
        Assert.assertNotNull("Book is not removed", todel);
        Assert.assertNull("Book is not removed(get request)", dao.getBook(id));
    }

    @Test
    public void runTestsInOrder() throws Exception {
        userTransaction.begin();
        saveBookTest();
        userTransaction.commit();

        getBookTest();

        userTransaction.begin();
        updateBookTest();
        userTransaction.commit();

        userTransaction.begin();
        removeBookTest();
        userTransaction.commit();
    }

    @Test
    public void getAllBooksTest() {
        List<Book> l = dao.getBooks();
        Assert.assertNotNull("No Book found", l);
    }

    @Test
    public void getUserBooksTest() {
        List<JsonBook> l = dao.getUsersBooks(1);
        Assert.assertNotNull("No user Book found", l);
    }

    @Test
    public void getUserBooksForListTest() {
        List<JsonBook> l = dao.getUserBooksForList(0, 5, "b.id", "desc", 1);
        Assert.assertNotNull("List NULL", l);
        Assert.assertFalse("No user Book found", l.isEmpty());
    }

}
