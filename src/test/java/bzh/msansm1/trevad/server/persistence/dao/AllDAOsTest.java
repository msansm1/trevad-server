package bzh.msansm1.trevad.server.persistence.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import bzh.msansm1.trevad.server.json.album.JsonAlbum;
import bzh.msansm1.trevad.server.json.book.JsonBook;
import bzh.msansm1.trevad.server.json.movie.JsonMovie;
import bzh.msansm1.trevad.server.json.tvshow.JsonShow;
import bzh.msansm1.trevad.server.persistence.model.Album;
import bzh.msansm1.trevad.server.persistence.model.Artist;
import bzh.msansm1.trevad.server.persistence.model.Artisttype;
import bzh.msansm1.trevad.server.persistence.model.Book;
import bzh.msansm1.trevad.server.persistence.model.Collection;
import bzh.msansm1.trevad.server.persistence.model.Database;
import bzh.msansm1.trevad.server.persistence.model.Editor;
import bzh.msansm1.trevad.server.persistence.model.Genre;
import bzh.msansm1.trevad.server.persistence.model.Lang;
import bzh.msansm1.trevad.server.persistence.model.Movie;
import bzh.msansm1.trevad.server.persistence.model.Storygenre;
import bzh.msansm1.trevad.server.persistence.model.Support;
import bzh.msansm1.trevad.server.persistence.model.Track;
import bzh.msansm1.trevad.server.persistence.model.Tvshow;
import bzh.msansm1.trevad.server.persistence.model.User;
import bzh.msansm1.trevad.server.utils.TestUtils;

/**
 * Tests for album DAO
 * 
 * @author msansm1
 * 
 */
@RunWith(Arquillian.class)
public class AllDAOsTest {
    private static final Logger LOGGER = Logger.getLogger(AllDAOsTest.class.getName());

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = TestUtils.getWarFile("testwar");
        LOGGER.info(war.toString(Formatters.VERBOSE));
        return war;
    }

    @Inject
    private AlbumDAO albumDao;

    @Resource
    private UserTransaction userTransaction;

    final Album album = new Album();

    public void saveAlbumTest() {
        album.setTitle("test album");
        album.setCover("YOUHOU");
        albumDao.saveAlbum(album);
        Assert.assertNotNull("Album is not created", album.getId());
    }

    public void getAlbumTest() {
        Integer id = album.getId();
        Album created = albumDao.getAlbum(id);
        Assert.assertNotNull("Album is not found", created);
    }

    public void updateAlbumTest() {
        Album updated = albumDao.getAlbum(album.getId());
        updated.setCover("changed :)");
        albumDao.updateAlbum(updated);
        Assert.assertTrue("Album is not updated",
                albumDao.getAlbum(album.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeAlbumTest() {
        Integer id = album.getId();
        Album todel = albumDao.getAlbum(id);
        albumDao.removeAlbum(todel);
        Assert.assertNotNull("Album is not removed", todel);
        Assert.assertNull("Album is not removed(get request)", albumDao.getAlbum(id));
    }

    @Test
    public void runAlbumTestsInOrder() throws Exception {
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
        List<Album> l = albumDao.getAlbums();
        Assert.assertNotNull("No Album found", l);
    }

    @Test
    public void getUserAlbumsTest() {
        List<JsonAlbum> l = albumDao.getUsersAlbums(1);
        Assert.assertFalse("No user Album found", l.isEmpty());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private ArtistDAO artistDao;

    @Inject
    private ArtisttypeDAO atdao;

    final Artist artist = new Artist();

    public void saveArtistTest() {
        Artisttype at = new Artisttype();
        at.setName("at");
        atdao.saveArtisttype(at);
        artist.setFirstname("toto");
        artist.setName("tata");
        artist.setArtisttype(at);
        artistDao.saveArtist(artist);
        Assert.assertNotNull("Artist is not created", artist.getId());
    }

    public void getArtistTest() {
        Integer id = artist.getId();
        Artist created = artistDao.getArtist(id);
        Assert.assertNotNull("Artist is not found", created);
    }

    public void updateArtistTest() {
        Artist updated = artistDao.getArtist(artist.getId());
        updated.setName("newtiti");
        artistDao.updateArtist(updated);
        Assert.assertTrue("Artist is not updated",
                artistDao.getArtist(artist.getId()).getName().equalsIgnoreCase("newtiti"));

    }

    public void removeArtistTest() {
        Integer id = artist.getId();
        Artist todel = artistDao.getArtist(id);
        artistDao.removeArtist(todel);
        Assert.assertNotNull("Artist is not removed", todel);
        Assert.assertNull("Artist is not removed(get request)", artistDao.getArtist(id));
    }

    @Test
    public void runArtistTestsInOrder() throws Exception {
        userTransaction.begin();
        saveArtistTest();
        userTransaction.commit();

        getArtistTest();

        userTransaction.begin();
        updateArtistTest();
        userTransaction.commit();

        userTransaction.begin();
        removeArtistTest();
        userTransaction.commit();
    }

    @Test
    public void getAllArtistsTest() {
        List<Artist> l = artistDao.getArtists();
        Assert.assertNotNull("No Artist found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private BookDAO bookDao;

    final Book book = new Book();

    public void saveBookTest() {
        book.setTitle("test book");
        book.setCover("YOUHOU");
        bookDao.saveBook(book);
        Assert.assertNotNull("Book is not created", book.getId());
    }

    public void getBookTest() {
        Integer id = book.getId();
        Book created = bookDao.getBook(id);
        Assert.assertNotNull("Book is not found", created);
    }

    public void updateBookTest() {
        Book updated = bookDao.getBook(book.getId());
        updated.setCover("changed :)");
        bookDao.updateBook(updated);
        Assert.assertTrue("Book is not updated",
                bookDao.getBook(book.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeBookTest() {
        Integer id = book.getId();
        Book todel = bookDao.getBook(id);
        bookDao.removeBook(todel);
        Assert.assertNotNull("Book is not removed", todel);
        Assert.assertNull("Book is not removed(get request)", bookDao.getBook(id));
    }

    @Test
    public void runBookTestsInOrder() throws Exception {
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
        List<Book> l = bookDao.getBooks();
        Assert.assertNotNull("No Book found", l);
    }

    @Test
    public void getUserBooksTest() {
        List<JsonBook> l = bookDao.getUsersBooks(1);
        Assert.assertNotNull("No user Book found", l);
    }

    @Test
    public void getUserBooksForListTest() {
        List<JsonBook> l = bookDao.getUserBooksForList(0, 5, "b.id", "desc", 1);
        Assert.assertNotNull("List NULL", l);
        Assert.assertFalse("No user Book found", l.isEmpty());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private CollectionDAO collectionDao;

    final Collection collection = new Collection();

    public void saveCollectionTest() {
        collection.setName("test collection");
        collectionDao.saveCollection(collection);
        Assert.assertNotNull("Collection is not created", collection.getId());
    }

    public void getCollectionTest() {
        Integer id = collection.getId();
        Collection created = collectionDao.getCollection(id);
        Assert.assertNotNull("Collection is not found", created);
    }

    public void updateCollectionTest() {
        Collection updated = collectionDao.getCollection(collection.getId());
        updated.setName("changed :)");
        collectionDao.updateCollection(updated);
        Assert.assertTrue("Collection is not updated",
                collectionDao.getCollection(collection.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeCollectionTest() {
        Integer id = collection.getId();
        Collection todel = collectionDao.getCollection(id);
        collectionDao.removeCollection(todel);
        Assert.assertNotNull("Collection is not removed", todel);
        Assert.assertNull("Collection is not removed(get request)", collectionDao.getCollection(id));
    }

    @Test
    public void runCollectionTestsInOrder() throws Exception {
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
        List<Collection> l = collectionDao.getCollections();
        Assert.assertNotNull("No Collection found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private DatabaseDAO databaseDao;

    final Database database = new Database();

    public void saveDatabaseTest() {
        database.setVersion("test database");
        databaseDao.saveDatabase(database);
        Assert.assertNotNull("Database is not created", database.getId());
    }

    public void getDatabaseTest() {
        Integer id = database.getId();
        Database created = databaseDao.getDatabase(id);
        Assert.assertNotNull("Database is not found", created);
    }

    public void updateDatabaseTest() {
        Database updated = databaseDao.getDatabase(database.getId());
        updated.setVersion("changed :)");
        databaseDao.updateDatabase(updated);
        Assert.assertTrue("Database is not updated",
                databaseDao.getDatabase(database.getId()).getVersion().equalsIgnoreCase("changed :)"));

    }

    public void removeDatabaseTest() {
        Integer id = database.getId();
        Database todel = databaseDao.getDatabase(id);
        databaseDao.removeDatabase(todel);
        Assert.assertNotNull("Database is not removed", todel);
        Assert.assertNull("Database is not removed(get request)", databaseDao.getDatabase(id));
    }

    @Test
    public void runDbTestsInOrder() throws Exception {
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
        List<Database> l = databaseDao.getAllDatabases();
        Assert.assertNotNull("No Database found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private EditorDAO editorDao;

    final Editor editor = new Editor();

    public void saveEditorTest() {
        editor.setName("test editor");
        editorDao.saveEditor(editor);
        Assert.assertNotNull("Editor is not created", editor.getId());
    }

    public void getEditorTest() {
        Integer id = editor.getId();
        Editor created = editorDao.getEditor(id);
        Assert.assertNotNull("Editor is not found", created);
    }

    public void updateEditorTest() {
        Editor updated = editorDao.getEditor(editor.getId());
        updated.setName("changed :)");
        editorDao.updateEditor(updated);
        Assert.assertTrue("Editor is not updated",
                editorDao.getEditor(editor.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeEditorTest() {
        Integer id = editor.getId();
        Editor todel = editorDao.getEditor(id);
        editorDao.removeEditor(todel);
        Assert.assertNotNull("Editor is not removed", todel);
        Assert.assertNull("Editor is not removed(get request)", editorDao.getEditor(id));
    }

    @Test
    public void runEditorTestsInOrder() throws Exception {
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
        List<Editor> l = editorDao.getEditors();
        Assert.assertNotNull("No Editor found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private GenreDAO genreDao;

    final Genre genre = new Genre();

    public void saveGenreTest() {
        genre.setName("test genre");
        genreDao.saveGenre(genre);
        Assert.assertNotNull("Genre is not created", genre.getId());
    }

    public void getGenreTest() {
        Integer id = genre.getId();
        Genre created = genreDao.getGenre(id);
        Assert.assertNotNull("Genre is not found", created);
    }

    public void updateGenreTest() {
        Genre updated = genreDao.getGenre(genre.getId());
        updated.setName("changed :)");
        genreDao.updateGenre(updated);
        Assert.assertTrue("Genre is not updated",
                genreDao.getGenre(genre.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeGenreTest() {
        Integer id = genre.getId();
        Genre todel = genreDao.getGenre(id);
        genreDao.removeGenre(todel);
        Assert.assertNotNull("Genre is not removed", todel);
        Assert.assertNull("Genre is not removed(get request)", genreDao.getGenre(id));
    }

    @Test
    public void runGenreTestsInOrder() throws Exception {
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
        List<Genre> l = genreDao.getGenres();
        Assert.assertNotNull("No Genre found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private LangDAO langDao;

    final Lang lang = new Lang();

    public void saveLangTest() {
        lang.setName("test lang");
        langDao.saveLang(lang);
        Assert.assertNotNull("Lang is not created", lang.getId());
    }

    public void getLangTest() {
        Integer id = lang.getId();
        Lang created = langDao.getLang(id);
        Assert.assertNotNull("Lang is not found", created);
    }

    public void updateLangTest() {
        Lang updated = langDao.getLang(lang.getId());
        updated.setName("changed :)");
        langDao.updateLang(updated);
        Assert.assertTrue("Lang is not updated",
                langDao.getLang(lang.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeLangTest() {
        Integer id = lang.getId();
        Lang todel = langDao.getLang(id);
        langDao.removeLang(todel);
        Assert.assertNotNull("Lang is not removed", todel);
        Assert.assertNull("Lang is not removed(get request)", langDao.getLang(id));
    }

    @Test
    public void runLangTestsInOrder() throws Exception {
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
        List<Lang> l = langDao.getLangs();
        Assert.assertNotNull("No Lang found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private MovieDAO movieDao;

    final Movie movie = new Movie();

    public void saveMovieTest() {
        movie.setTitle("test movie");
        movie.setCover("YOUHOU");
        movie.setIscollector(false);
        movieDao.saveMovie(movie);
        Assert.assertNotNull("Movie is not created", movie.getId());
    }

    public void getMovieTest() {
        Integer id = movie.getId();
        Movie created = movieDao.getMovie(id);
        Assert.assertNotNull("Movie is not found", created);
    }

    public void updateMovieTest() {
        Movie updated = movieDao.getMovie(movie.getId());
        updated.setCover("changed :)");
        movieDao.updateMovie(updated);
        Assert.assertTrue("Movie is not updated",
                movieDao.getMovie(movie.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeMovieTest() {
        Integer id = movie.getId();
        Movie todel = movieDao.getMovie(id);
        movieDao.removeMovie(todel);
        Assert.assertNotNull("Movie is not removed", todel);
        Assert.assertNull("Movie is not removed(get request)", movieDao.getMovie(id));
    }

    @Test
    public void runMovieTestsInOrder() throws Exception {
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
        List<Movie> l = movieDao.getMovies();
        Assert.assertNotNull("No Movie found", l);
    }

    @Test
    public void getUserMoviesTest() {
        List<JsonMovie> l = movieDao.getUsersMovies(1);
        Assert.assertNotNull("No user Movie found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private StorygenreDAO storygenreDao;

    final Storygenre storygenre = new Storygenre();

    public void saveStorygenreTest() {
        storygenre.setName("test storygenre");
        storygenreDao.saveStorygenre(storygenre);
        Assert.assertNotNull("Storygenre is not created", storygenre.getId());
    }

    public void getStorygenreTest() {
        Integer id = storygenre.getId();
        Storygenre created = storygenreDao.getStorygenre(id);
        Assert.assertNotNull("Storygenre is not found", created);
    }

    public void updateStorygenreTest() {
        Storygenre updated = storygenreDao.getStorygenre(storygenre.getId());
        updated.setName("changed :)");
        storygenreDao.updateStorygenre(updated);
        Assert.assertTrue("Storygenre is not updated",
                storygenreDao.getStorygenre(storygenre.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeStorygenreTest() {
        Integer id = storygenre.getId();
        Storygenre todel = storygenreDao.getStorygenre(id);
        storygenreDao.removeStorygenre(todel);
        Assert.assertNotNull("Storygenre is not removed", todel);
        Assert.assertNull("Storygenre is not removed(get request)", storygenreDao.getStorygenre(id));
    }

    @Test
    public void runStorygenreTestsInOrder() throws Exception {
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
        List<Storygenre> l = storygenreDao.getStorygenres();
        Assert.assertNotNull("No Storygenre found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private SupportDAO supportDao;

    final Support support = new Support();

    public void saveSupportTest() {
        support.setName("test support");
        supportDao.saveSupport(support);
        Assert.assertNotNull("Support is not created", support.getId());
    }

    public void getSupportTest() {
        Integer id = support.getId();
        Support created = supportDao.getSupport(id);
        Assert.assertNotNull("Support is not found", created);
    }

    public void updateSupportTest() {
        Support updated = supportDao.getSupport(support.getId());
        updated.setName("changed :)");
        supportDao.updateSupport(updated);
        Assert.assertTrue("Support is not updated",
                supportDao.getSupport(support.getId()).getName().equalsIgnoreCase("changed :)"));

    }

    public void removeSupportTest() {
        Integer id = support.getId();
        Support todel = supportDao.getSupport(id);
        supportDao.removeSupport(todel);
        Assert.assertNotNull("Support is not removed", todel);
        Assert.assertNull("Support is not removed(get request)", supportDao.getSupport(id));
    }

    @Test
    public void runSupportTestsInOrder() throws Exception {
        userTransaction.begin();
        saveSupportTest();
        userTransaction.commit();

        getSupportTest();

        userTransaction.begin();
        updateSupportTest();
        userTransaction.commit();

        userTransaction.begin();
        removeSupportTest();
        userTransaction.commit();
    }

    @Test
    public void getAllSupportsTest() {
        List<Support> l = supportDao.getSupports();
        Assert.assertNotNull("No Support found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private TrackDAO trackDao;

    final Track track = new Track();

    public void saveTrackTest() {
        Album a = new Album();
        a.setTitle("testa");
        albumDao.saveAlbum(a);
        track.setTitle("test track");
        track.setLength("2:30");
        track.setAlbumBean(a);
        trackDao.saveTrack(track);
        Assert.assertNotNull("Track is not created", track.getId());
    }

    public void getTrackTest() {
        Integer id = track.getId();
        Track created = trackDao.getTrack(id);
        Assert.assertNotNull("Track is not found", created);
    }

    public void updateTrackTest() {
        Track updated = trackDao.getTrack(track.getId());
        updated.setLength("10:10");
        trackDao.updateTrack(updated);
        Assert.assertTrue("Track is not updated",
                trackDao.getTrack(track.getId()).getLength().equalsIgnoreCase("10:10"));

    }

    public void removeTrackTest() {
        Integer id = track.getId();
        Track todel = trackDao.getTrack(id);
        trackDao.removeTrack(todel);
        Assert.assertNotNull("Track is not removed", todel);
        Assert.assertNull("Track is not removed(get request)", trackDao.getTrack(id));
    }

    @Test
    public void runTrackTestsInOrder() throws Exception {
        userTransaction.begin();
        saveTrackTest();
        userTransaction.commit();

        getTrackTest();

        userTransaction.begin();
        updateTrackTest();
        userTransaction.commit();

        userTransaction.begin();
        removeTrackTest();
        userTransaction.commit();
    }

    @Test
    public void getAllTracksTest() {
        List<Track> l = trackDao.getTracks();
        Assert.assertNotNull("No Track found", l);
    }

    @Test
    public void getTracksForAlbumTest() {
        List<Track> l = trackDao.getTracksForAlbum(Integer.valueOf(1));
        Assert.assertFalse("No Track found", l.isEmpty());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private TvshowDAO tvshowDao;

    final Tvshow tvshow = new Tvshow();

    public void saveTvshowTest() {
        tvshow.setTitle("test tvshow");
        tvshow.setCover("YOUHOU");
        tvshowDao.saveTvshow(tvshow);
        Assert.assertNotNull("Tvshow is not created", tvshow.getId());
    }

    public void getTvshowTest() {
        Integer id = tvshow.getId();
        Tvshow created = tvshowDao.getTvshow(id);
        Assert.assertNotNull("Tvshow is not found", created);
    }

    public void updateTvshowTest() {
        Tvshow updated = tvshowDao.getTvshow(tvshow.getId());
        updated.setCover("changed :)");
        tvshowDao.updateTvshow(updated);
        Assert.assertTrue("Tvshow is not updated",
                tvshowDao.getTvshow(tvshow.getId()).getCover().equalsIgnoreCase("changed :)"));

    }

    public void removeTvshowTest() {
        Integer id = tvshow.getId();
        Tvshow todel = tvshowDao.getTvshow(id);
        tvshowDao.removeTvshow(todel);
        Assert.assertNotNull("Tvshow is not removed", todel);
        Assert.assertNull("Tvshow is not removed(get request)", tvshowDao.getTvshow(id));
    }

    @Test
    public void runTvShowTestsInOrder() throws Exception {
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
        List<Tvshow> l = tvshowDao.getTvshows();
        Assert.assertNotNull("No Tvshow found", l);
    }

    @Test
    public void getUserTvshowsTest() {
        List<JsonShow> l = tvshowDao.getUsersTvshows(1);
        Assert.assertNotNull("No Tvshow found", l);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    @Inject
    private UserDAO userDao;

    final User user = new User();

    public void saveUserTest() {
        user.setLogin("login");
        user.setPassword("password");
        user.setEmail("log@test.tt");
        userDao.saveUser(user);
        Assert.assertNotNull("User is not created", user.getId());
    }

    public void getUserTest() {
        Integer id = user.getId();
        User created = userDao.getUser(id);
        Assert.assertNotNull("User is not found", created);
    }

    public void updateUserTest() {
        User updated = userDao.getUser(user.getId());
        updated.setLogin("changed :)");
        userDao.updateUser(updated);
        Assert.assertTrue("User is not updated",
                userDao.getUser(user.getId()).getLogin().equalsIgnoreCase("changed :)"));

    }

    public void removeUserTest() {
        Integer id = user.getId();
        User todel = userDao.getUser(id);
        userDao.removeUser(todel);
        Assert.assertNotNull("User is not removed", todel);
        Assert.assertNull("User is not removed(get request)", userDao.getUser(id));
    }

    @Test
    public void runUserTestsInOrder() throws Exception {
        userTransaction.begin();
        saveUserTest();
        userTransaction.commit();

        getUserTest();

        userTransaction.begin();
        updateUserTest();
        userTransaction.commit();

        userTransaction.begin();
        removeUserTest();
        userTransaction.commit();
    }

    @Test
    public void getAllUsersTest() {
        List<User> l = userDao.getUsers();
        Assert.assertNotNull("No User found", l);
    }

}
