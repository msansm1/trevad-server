package bzh.msansm1.trevad.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import bzh.msansm1.trevad.server.json.JsonLang;
import bzh.msansm1.trevad.server.json.JsonSimpleResponse;
import bzh.msansm1.trevad.server.json.JsonStorygenre;
import bzh.msansm1.trevad.server.json.JsonSupport;
import bzh.msansm1.trevad.server.json.album.JsonAlbum;
import bzh.msansm1.trevad.server.json.album.JsonGenre;
import bzh.msansm1.trevad.server.json.album.JsonTrack;
import bzh.msansm1.trevad.server.json.artist.JsonArtist;
import bzh.msansm1.trevad.server.json.artist.JsonArtisttype;
import bzh.msansm1.trevad.server.json.auth.JsonAuth;
import bzh.msansm1.trevad.server.json.auth.JsonLogin;
import bzh.msansm1.trevad.server.json.book.JsonBook;
import bzh.msansm1.trevad.server.json.book.JsonBooktype;
import bzh.msansm1.trevad.server.json.book.JsonCollection;
import bzh.msansm1.trevad.server.json.book.JsonEditor;
import bzh.msansm1.trevad.server.json.book.JsonMyBook;
import bzh.msansm1.trevad.server.json.friend.JsonFriend;
import bzh.msansm1.trevad.server.json.home.JsonCollectionStats;
import bzh.msansm1.trevad.server.json.movie.JsonMovie;
import bzh.msansm1.trevad.server.json.movie.JsonMyMovie;
import bzh.msansm1.trevad.server.json.tvshow.JsonMyShow;
import bzh.msansm1.trevad.server.json.tvshow.JsonShow;
import bzh.msansm1.trevad.server.json.user.JsonUser;
import bzh.msansm1.trevad.server.utils.Constants;
import bzh.msansm1.trevad.server.utils.TestConstants;
import bzh.msansm1.trevad.server.utils.TestUtils;

/**
 * Test class for album REST service
 * 
 * @author msansm1
 *
 */
@RunWith(Arquillian.class)
// Run the tests of the class as a client
@RunAsClient
public class ServicesTest {
    private static final Logger LOGGER = Logger.getLogger(ServicesTest.class);

    private static final String svc_album_root = "/api/v1/albums";

    // testable = false => it's for testing as a client (we don't test inside
    // the app)
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        // creation of the war for testing
        final WebArchive war = TestUtils.getWarFile("testwar");
        LOGGER.info(war.toString(Formatters.VERBOSE));
        return war;
    }

    /**
     * Test for /services/albums POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(1)
    public void callAlbumCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonAlbum album = new JsonAlbum(null, "la ouache", null, null, "BZH", 1, 12, "CD", 1, "Matmatah", 1, true, 4,
                false, new ArrayList<>());

        JsonAlbum response = client.target(TestConstants.SERVER_ROOT + svc_album_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(album, MediaType.APPLICATION_JSON), JsonAlbum.class);
        assertEquals("la ouache", response.getTitle());
    }

    /**
     * Test for /services/albums POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(2)
    public void callAlbumUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonAlbum album = new JsonAlbum(1, "Master of puppets", null, null, "Metal", 1, 9, "CD", 1, "Metallica", 1,
                true, 5, false, null);

        JsonAlbum response = client.target(TestConstants.SERVER_ROOT + svc_album_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(album, MediaType.APPLICATION_JSON), JsonAlbum.class);
        assertEquals("Master of puppets", response.getTitle());
    }

    /**
     * Test for /services/albums/user GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(3)
    public void callAlbumGetUserAlbums() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonAlbum> response = client
                .target(TestConstants.SERVER_ROOT + svc_album_root
                        + "/user?from=0&limit=5&orderBy=a.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No album found", response.isEmpty());
        assertEquals("List page 1 does not contains 1 entrie", Integer.valueOf(1), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/albums/{id}/tracks GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(4)
    public void callAlbumGetAlbumTracks() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonTrack> response = client.target(TestConstants.SERVER_ROOT + svc_album_root + "/1/tracks")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No user album found", response.isEmpty());
    }

    /**
     * Test for /services/albums GET with params Page 1
     * 
     * @throws Exception
     */
    @Test
    @InSequence(5)
    public void callAlbumGetListParamsPOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonAlbum> response = client
                .target(TestConstants.SERVER_ROOT + svc_album_root + "?from=0&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No album found", response.isEmpty());
        assertEquals("List page 1 does not contains 5 entries", Integer.valueOf(5), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/albums GET with params Page 2
     * 
     * @throws Exception
     */
    @Test
    @InSequence(6)
    public void callAlbumGetListParamsPTwo() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonAlbum> response = client
                .target(TestConstants.SERVER_ROOT + svc_album_root + "?from=6&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No album found", response.isEmpty());
        assertEquals("List page 2 does not contains 2 entries", Integer.valueOf(2), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/albums/addtocollec POST Test OK update
     * 
     * @throws Exception
     */
    // @Test
    // @InSequence(7)
    // public void callAddToCollec() throws Exception {
    // Client client =
    // ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
    // JsonMyAlbum album = new JsonMyAlbum(2, 1, 4, "", false);
    //
    // @SuppressWarnings("unchecked")
    // List<JsonAlbum> listbefore = client
    // .target(TestConstants.SERVER_ROOT + svc_root
    // + "/user?from=0&limit=5&orderBy=a.id&orderDir=asc&userId=1")
    // .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN,
    // TestConstants.USER_TOKEN)
    // .get(List.class);
    // int sizebefore = listbefore.size();
    //
    // JsonSimpleResponse response = client.target(TestConstants.SERVER_ROOT +
    // svc_root + "/addtocollec")
    // .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN,
    // TestConstants.USER_TOKEN)
    // .post(Entity.entity(album, MediaType.APPLICATION_JSON),
    // JsonSimpleResponse.class);
    // assertEquals("true", response.getOk());
    //
    // @SuppressWarnings("unchecked")
    // List<JsonAlbum> listafter = client
    // .target(TestConstants.SERVER_ROOT + svc_root
    // + "/user?from=0&limit=5&orderBy=a.id&orderDir=asc&userId=1")
    // .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN,
    // TestConstants.USER_TOKEN)
    // .get(List.class);
    // int sizeafter = listafter.size();
    // assertTrue("not added correctly : " + sizebefore + " | " + sizeafter,
    // (sizebefore + 1) == sizeafter);
    // }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_artist_root = "/api/v1/artists";

    /**
     * Test for /services/artists GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(11)
    public void callArtistGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtist> response = client.target(TestConstants.SERVER_ROOT + svc_artist_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artist found", response.isEmpty());
    }

    /**
     * Test for /services/artists/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(12)
    public void callArtistGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonArtist response = client.target(TestConstants.SERVER_ROOT + svc_artist_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonArtist.class);
        assertEquals("Metallica", response.getName());
    }

    /**
     * Test for /services/artists POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(13)
    public void callArtistCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonArtist artist = new JsonArtist(null, "testrest", "", "", 1, "", "");

        JsonArtist response = client.target(TestConstants.SERVER_ROOT + svc_artist_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(artist, MediaType.APPLICATION_JSON), JsonArtist.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/artists POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(14)
    public void callArtistUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonArtist artist = new JsonArtist(4, "msansm1", "", "", 1, "", "");

        JsonArtist response = client.target(TestConstants.SERVER_ROOT + svc_artist_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(artist, MediaType.APPLICATION_JSON), JsonArtist.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/artists/albums GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(15)
    public void callArtistGetListForAlbums() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtist> response = client.target(TestConstants.SERVER_ROOT + svc_artist_root + "/albums")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artist found", response.isEmpty());
    }

    /**
     * Test for /services/artists/books GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(16)
    public void callArtistGetListForBooks() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtist> response = client.target(TestConstants.SERVER_ROOT + svc_artist_root + "/books")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artist found", response.isEmpty());
    }

    /**
     * Test for /services/artists/movies GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(17)
    public void callArtistGetListForMovies() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtist> response = client.target(TestConstants.SERVER_ROOT + svc_artist_root + "/movies")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artist found", response.isEmpty());
    }

    /**
     * Test for /services/artists/movies GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(18)
    public void callArtistGetListForSeries() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtist> response = client.target(TestConstants.SERVER_ROOT + svc_artist_root + "/series")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artist found", response.isEmpty());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_artist_type_root = "/api/v1/artisttypes";

    /**
     * Test for /services/artisttypes GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(21)
    public void callArtistTypeGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtisttype> response = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No artisttype found", response.isEmpty());
    }

    /**
     * Test for /services/artisttypes/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(22)
    public void callArtistTypeGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonArtisttype response = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonArtisttype.class);
        assertEquals("Groupe de musique", response.getName());
    }

    /**
     * Test for /services/artisttypes POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(23)
    public void callArtistTypeCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonArtisttype artisttype = new JsonArtisttype(null, "testrest");

        JsonArtisttype response = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(artisttype, MediaType.APPLICATION_JSON), JsonArtisttype.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/artisttypes POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(24)
    public void callArtistTypeUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonArtisttype artisttype = new JsonArtisttype(1, "msansm1");

        JsonArtisttype response = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(artisttype, MediaType.APPLICATION_JSON), JsonArtisttype.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/artisttypes POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(25)
    public void callArtistTypeDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonArtisttype> listbefore = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonArtisttype artisttype = new JsonArtisttype(6, Constants.DELETED);
        JsonArtisttype response = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(artisttype, MediaType.APPLICATION_JSON), JsonArtisttype.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonArtisttype> listafter = client.target(TestConstants.SERVER_ROOT + svc_artist_type_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_auth_root = "/api/v1/auth";

    /**
     * Test for /services/auth/login Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(31)
    public void callLoginOK() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLogin login = new JsonLogin("msansm1", "m1pwd");

        JsonAuth response = client.target(TestConstants.SERVER_ROOT + svc_auth_root + "/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(login, MediaType.APPLICATION_JSON), JsonAuth.class);
        assertEquals(Integer.valueOf(1), response.getId());
    }

    /**
     * Test for /services/auth/login Test NOK => 401 wrong password
     * 
     * @throws Exception
     */
    @Test
    @InSequence(32)
    public void callLogin401() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLogin login = new JsonLogin("msansm1", "tototest");

        Response response = client.target(TestConstants.SERVER_ROOT + svc_auth_root + "/login")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity(login, MediaType.APPLICATION_JSON));
        assertEquals(401, response.getStatus());
    }

    /**
     * Test for /services/auth/login Test NOK => 401 wrong login
     * 
     * @throws Exception
     */
    @Test
    @InSequence(33)
    public void callLogin401LoginWrong() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLogin login = new JsonLogin("myop", "tototest");

        Response response = client.target(TestConstants.SERVER_ROOT + svc_auth_root + "/login")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity(login, MediaType.APPLICATION_JSON));
        assertEquals(401, response.getStatus());
    }

    /**
     * Test for /services/auth/logout Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(34)
    public void callLogout() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonAuth login = new JsonAuth(2, "testarq", "", "", "", "", "");

        Response response = client.target(TestConstants.SERVER_ROOT + svc_auth_root + "/logout")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(login, MediaType.APPLICATION_JSON));
        assertEquals(200, response.getStatus());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_book_root = "/api/v1/books";

    /**
     * Test for /services/books GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(41)
    public void callBookGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBook> response = client.target(TestConstants.SERVER_ROOT + svc_book_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No book found", response.isEmpty());
    }

    /**
     * Test for /services/books/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(42)
    public void callBookGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonBook response = client.target(TestConstants.SERVER_ROOT + svc_book_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonBook.class);
        assertEquals("The Hobbit", response.getTitle());
    }

    /**
     * Test for /services/books POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(43)
    public void callBookCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonBook book = new JsonBook(null, "Rainbow Six", "Tom Clancy", "Poche");

        JsonBook response = client.target(TestConstants.SERVER_ROOT + svc_book_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON), JsonBook.class);
        assertEquals("Rainbow Six", response.getTitle());
    }

    /**
     * Test for /services/books POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(44)
    public void callBookUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonBook book = new JsonBook(1, "Silmarillion", null, null);

        JsonBook response = client.target(TestConstants.SERVER_ROOT + svc_book_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON), JsonBook.class);
        assertEquals("Silmarillion", response.getTitle());
    }

    /**
     * Test for /services/books GET with params Page 1
     * 
     * @throws Exception
     */
    @Test
    @InSequence(45)
    public void callBookGetListParamsPOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBook> response = client
                .target(TestConstants.SERVER_ROOT + svc_book_root + "?from=0&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No book found", response.isEmpty());
        assertEquals("List page 1 does not contains 5 entries", Integer.valueOf(5), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/books GET with params Page 2
     * 
     * @throws Exception
     */
    @Test
    @InSequence(46)
    public void callBookGetListParamsPTwo() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBook> response = client
                .target(TestConstants.SERVER_ROOT + svc_book_root + "?from=6&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No book found", response.isEmpty());
        assertEquals("List page 2 does not contains 2 entries", Integer.valueOf(2), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/books/user GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(47)
    public void callBookGetUserBooks() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBook> response = client
                .target(TestConstants.SERVER_ROOT + svc_book_root
                        + "/user?from=0&limit=5&orderBy=b.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No book found", response.isEmpty());
        assertEquals("List page 1 does not contains 2 entry", Integer.valueOf(2), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/books/addtocollec POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(48)
    public void callBookAddToCollec() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonMyBook book = new JsonMyBook(4, 1, 4, "", false);

        @SuppressWarnings("unchecked")
        List<JsonBook> listbefore = client
                .target(TestConstants.SERVER_ROOT + svc_book_root
                        + "/user?from=0&limit=5&orderBy=b.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizebefore = listbefore.size();

        JsonSimpleResponse response = client.target(TestConstants.SERVER_ROOT + svc_book_root + "/addtocollec")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON), JsonSimpleResponse.class);
        assertEquals("true", response.getOk());

        @SuppressWarnings("unchecked")
        List<JsonBook> listafter = client
                .target(TestConstants.SERVER_ROOT + svc_book_root
                        + "/user?from=0&limit=5&orderBy=b.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizeafter = listafter.size();
        assertTrue("not added correctly : " + sizebefore + " | " + sizeafter, (sizebefore + 1) == sizeafter);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_booktypes_root = "/api/v1/booktypes";

    /**
     * Test for /services/booktypes GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(51)
    public void callBookTypeGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBooktype> response = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No booktype found", response.isEmpty());
    }

    /**
     * Test for /services/booktypes/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(52)
    public void callBookTypeGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonBooktype response = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonBooktype.class);
        assertEquals("Roman", response.getName());
    }

    /**
     * Test for /services/booktypes POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(53)
    public void callBookTypeCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonBooktype booktype = new JsonBooktype(null, "testrest");

        JsonBooktype response = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(booktype, MediaType.APPLICATION_JSON), JsonBooktype.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/booktypes POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(54)
    public void callBookTypeUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonBooktype booktype = new JsonBooktype(1, "msansm1");

        JsonBooktype response = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(booktype, MediaType.APPLICATION_JSON), JsonBooktype.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/booktypes POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(55)
    public void callBookTypeDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonBooktype> listbefore = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonBooktype booktype = new JsonBooktype(2, Constants.DELETED);
        JsonBooktype response = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(booktype, MediaType.APPLICATION_JSON), JsonBooktype.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonBooktype> listafter = client.target(TestConstants.SERVER_ROOT + svc_booktypes_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_collection_root = "/api/v1/collections";

    /**
     * Test for /services/collections GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(61)
    public void callCollectionGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonCollection> response = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No collection found", response.isEmpty());
    }

    /**
     * Test for /services/collections/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(62)
    public void callCollectionGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonCollection response = client.target(TestConstants.SERVER_ROOT + svc_collection_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonCollection.class);
        assertEquals("Yoko Tsuno", response.getName());
    }

    /**
     * Test for /services/collections POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(63)
    public void callCollectionCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonCollection collection = new JsonCollection(null, "testrest");

        JsonCollection response = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON), JsonCollection.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/collections POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(64)
    public void callCollectionUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonCollection collection = new JsonCollection(1, "msansm1");

        JsonCollection response = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON), JsonCollection.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/collections POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(65)
    public void callCollectionDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonCollection> listbefore = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonCollection collection = new JsonCollection(2, Constants.DELETED);
        JsonCollection response = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(collection, MediaType.APPLICATION_JSON), JsonCollection.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonCollection> listafter = client.target(TestConstants.SERVER_ROOT + svc_collection_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_editors_root = "/api/v1/editors";

    /**
     * Test for /services/editors GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(71)
    public void callEditorGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonEditor> response = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No editor found", response.isEmpty());
    }

    /**
     * Test for /services/editors/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(72)
    public void callEditorGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonEditor response = client.target(TestConstants.SERVER_ROOT + svc_editors_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonEditor.class);
        assertEquals("Dupuis", response.getName());
    }

    /**
     * Test for /services/editors POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(73)
    public void callEditorCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonEditor editor = new JsonEditor(null, "testrest");

        JsonEditor response = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(editor, MediaType.APPLICATION_JSON), JsonEditor.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/editors POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(74)
    public void callEditorUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonEditor editor = new JsonEditor(1, "msansm1");

        JsonEditor response = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(editor, MediaType.APPLICATION_JSON), JsonEditor.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/editors POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(75)
    public void callEditorDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonEditor> listbefore = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonEditor editor = new JsonEditor(3, Constants.DELETED);
        JsonEditor response = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(editor, MediaType.APPLICATION_JSON), JsonEditor.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonEditor> listafter = client.target(TestConstants.SERVER_ROOT + svc_editors_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_friend_root = "/api/v1/friends";

    /**
     * Test for /services/friends/{userId} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(81)
    public void callFriendGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonFriend> response = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No friend found", response.isEmpty());
    }

    /**
     * Test for /services/friends/{userId}/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(82)
    public void callFriendGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonFriend response = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1/2")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonFriend.class);
        assertEquals("testarq", response.getFriendLogin());
    }

    /**
     * Test for /services/friends/{userId}/{id} POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(83)
    public void callFriendCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonFriend friend = new JsonFriend(1, 3, "", false, false);

        JsonFriend response = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1/2")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(friend, MediaType.APPLICATION_JSON), JsonFriend.class);
        assertEquals(false, response.getSharedCollection());
    }

    /**
     * Test for /services/friends/{userId}/{id} POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(84)
    public void callFriendUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonFriend friend = new JsonFriend(1, 3, "", false, true);

        JsonFriend response = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1/2")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(friend, MediaType.APPLICATION_JSON), JsonFriend.class);
        assertEquals(true, response.getSharedCollection());
    }

    /**
     * Test for /services/friends/{userId}/{id}/delete GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(85)
    public void callFriendDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonSimpleResponse response = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1/3/delete")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonSimpleResponse.class);

        assertTrue("Delete failed", response != null);

        Response friend = client.target(TestConstants.SERVER_ROOT + svc_friend_root + "/1/3")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get();
        assertEquals("Must be 404", 404, friend.getStatus());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_genre_root = "/api/v1/genres";

    /**
     * Test for /services/genres GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(91)
    public void callGenreGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonGenre> response = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No genre found", response.isEmpty());
    }

    /**
     * Test for /services/genres/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(92)
    public void callGenreGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonGenre response = client.target(TestConstants.SERVER_ROOT + svc_genre_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonGenre.class);
        assertEquals("Metal", response.getName());
    }

    /**
     * Test for /services/genres POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(93)
    public void callGenreCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonGenre genre = new JsonGenre(null, "testrest");

        JsonGenre response = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(genre, MediaType.APPLICATION_JSON), JsonGenre.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/genres POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(94)
    public void callGenreUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonGenre genre = new JsonGenre(1, "msansm1");

        JsonGenre response = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(genre, MediaType.APPLICATION_JSON), JsonGenre.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/genres POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(95)
    public void callGenreDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonGenre> listbefore = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonGenre genre = new JsonGenre(3, Constants.DELETED);
        JsonGenre response = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(genre, MediaType.APPLICATION_JSON), JsonGenre.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonGenre> listafter = client.target(TestConstants.SERVER_ROOT + svc_genre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_home_root = "/api/v1/home";

    /**
     * Test for /services/home/mycollec Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(101)
    public void callMyCollec() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonCollectionStats response = client.target(TestConstants.SERVER_ROOT + svc_home_root + "/mycollec")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonCollectionStats.class);
        assertNotNull(response.getAlbums());
        // assertEquals(Integer.valueOf(1), response.getAlbums().getNb());
        // assertEquals(Integer.valueOf(1), response.getBooks().getNb());
        // assertEquals(Integer.valueOf(1), response.getMovies().getNb());
        // assertEquals(Integer.valueOf(1), response.getSeries().getNb());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_lang_root = "/api/v1/langs";

    /**
     * Test for /services/langs GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(111)
    public void callLangGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonLang> response = client.target(TestConstants.SERVER_ROOT + svc_lang_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No lang found", response.isEmpty());
    }

    /**
     * Test for /services/langs/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(112)
    public void callLangGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_lang_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonLang.class);
        assertEquals(Integer.valueOf(1), response.getId());
    }

    /**
     * Test for /services/langs POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(113)
    public void callLangCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLang lang = new JsonLang(null, "testrest");

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_lang_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/langs POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(114)
    public void callLangUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLang lang = new JsonLang(1, "msansm1");

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_lang_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/langs POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(115)
    public void callLangDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonLang> listbefore = client.target(TestConstants.SERVER_ROOT + svc_lang_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonLang lang = new JsonLang(3, Constants.DELETED);
        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_lang_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonLang> listafter = client.target(TestConstants.SERVER_ROOT + svc_lang_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_movie_root = "/api/v1/movies";

    /**
     * Test for /services/movies GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(121)
    public void callMovieGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonMovie> response = client.target(TestConstants.SERVER_ROOT + svc_movie_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No movie found", response.isEmpty());
    }

    /**
     * Test for /services/movies/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(122)
    public void callMovieGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonMovie response = client.target(TestConstants.SERVER_ROOT + svc_movie_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonMovie.class);
        assertEquals("Princesse Mononoke", response.getTitle());
    }

    /**
     * Test for /services/movies POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(123)
    public void callMovieCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonMovie movie = new JsonMovie(null, "Apocalypse now", "", new Date(), "", "", 1, "", 1, "", false);

        JsonMovie response = client.target(TestConstants.SERVER_ROOT + svc_movie_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(movie, MediaType.APPLICATION_JSON), JsonMovie.class);
        assertEquals("Apocalypse now", response.getTitle());
    }

    /**
     * Test for /services/movies POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(124)
    public void callMovieUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonMovie movie = new JsonMovie(1, "Totorro", "", new Date(), "", "", 1, "", 1, "", false);

        JsonMovie response = client.target(TestConstants.SERVER_ROOT + svc_movie_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(movie, MediaType.APPLICATION_JSON), JsonMovie.class);
        assertEquals("Totorro", response.getTitle());
    }

    /**
     * Test for /services/movies/user/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(125)
    public void callMovieGetUserMovies() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonMovie> response = client
                .target(TestConstants.SERVER_ROOT + svc_movie_root
                        + "/user?from=0&limit=5&orderBy=m.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No movie found", response.isEmpty());
        assertEquals("List page 1 does not contains 1 entrie", Integer.valueOf(1), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/movies GET with params Page 1
     * 
     * @throws Exception
     */
    @Test
    @InSequence(126)
    public void callMovieGetListParamsPOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonMovie> response = client
                .target(TestConstants.SERVER_ROOT + svc_movie_root + "?from=0&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No movie found", response.isEmpty());
        assertEquals("List page 1 does not contains 5 entries", Integer.valueOf(5), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/movies GET with params Page 2
     * 
     * @throws Exception
     */
    @Test
    @InSequence(127)
    public void callMovieGetListParamsPTwo() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonMovie> response = client
                .target(TestConstants.SERVER_ROOT + svc_movie_root + "?from=6&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No movie found", response.isEmpty());
        assertEquals("List page 2 does not contains 2 entries", Integer.valueOf(2), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/movies/addtocollec POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(128)
    public void callMovieAddToCollec() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonMyMovie movie = new JsonMyMovie(2, 1, 4, "");

        @SuppressWarnings("unchecked")
        List<JsonMovie> listbefore = client
                .target(TestConstants.SERVER_ROOT + svc_movie_root
                        + "/user?from=0&limit=5&orderBy=m.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizebefore = listbefore.size();

        JsonSimpleResponse response = client.target(TestConstants.SERVER_ROOT + svc_movie_root + "/addtocollec")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(movie, MediaType.APPLICATION_JSON), JsonSimpleResponse.class);
        assertEquals("true", response.getOk());

        @SuppressWarnings("unchecked")
        List<JsonMovie> listafter = client
                .target(TestConstants.SERVER_ROOT + svc_movie_root
                        + "/user?from=0&limit=5&orderBy=m.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizeafter = listafter.size();
        assertTrue("not added correctly : " + sizebefore + " | " + sizeafter, (sizebefore + 1) == sizeafter);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_storygenre_root = "/api/v1/storygenres";

    /**
     * Test for /services/storygenres GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(131)
    public void callStoryGenreGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonStorygenre> response = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No storygenre found", response.isEmpty());
    }

    /**
     * Test for /services/storygenres/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(132)
    public void callStoryGenreGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonStorygenre response = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonStorygenre.class);
        assertEquals("Aventure", response.getName());
    }

    /**
     * Test for /services/storygenres POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(133)
    public void callStoryGenreCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonStorygenre storygenre = new JsonStorygenre(null, "testrest");

        JsonStorygenre response = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(storygenre, MediaType.APPLICATION_JSON), JsonStorygenre.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/storygenres POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(134)
    public void callStoryGenreUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonStorygenre storygenre = new JsonStorygenre(1, "msansm1");

        JsonStorygenre response = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(storygenre, MediaType.APPLICATION_JSON), JsonStorygenre.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/storygenres POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(135)
    public void callStoryGenreDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonStorygenre> listbefore = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonStorygenre storygenre = new JsonStorygenre(2, Constants.DELETED);
        JsonStorygenre response = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(storygenre, MediaType.APPLICATION_JSON), JsonStorygenre.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonStorygenre> listafter = client.target(TestConstants.SERVER_ROOT + svc_storygenre_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_support_root = "/api/v1/supports";

    /**
     * Test for /services/supports GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(141)
    public void callSupportGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonSupport> response = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No support found", response.isEmpty());
    }

    /**
     * Test for /services/supports/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(142)
    public void callSupportGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonSupport response = client.target(TestConstants.SERVER_ROOT + svc_support_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonSupport.class);
        assertEquals("DVD", response.getName());
    }

    /**
     * Test for /services/supports POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(143)
    public void callSupportCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonSupport support = new JsonSupport(null, "testrest");

        JsonSupport response = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(support, MediaType.APPLICATION_JSON), JsonSupport.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/supports POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(144)
    public void callSupportUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonSupport support = new JsonSupport(1, "msansm1");

        JsonSupport response = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(support, MediaType.APPLICATION_JSON), JsonSupport.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/supports POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(145)
    public void callSupportDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonSupport> listbefore = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonSupport support = new JsonSupport(2, Constants.DELETED);
        JsonSupport response = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(support, MediaType.APPLICATION_JSON), JsonSupport.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonSupport> listafter = client.target(TestConstants.SERVER_ROOT + svc_support_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_track_root = "/api/v1/tracks";

    /**
     * Test for /services/tracks/{albumId} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(151)
    public void callTrackGetAlbumTracks() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonTrack> response = client.target(TestConstants.SERVER_ROOT + svc_track_root + "/album/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No track found", response.isEmpty());
    }

    /**
     * Test for /services/tracks/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(152)
    public void callTrackGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonTrack response = client.target(TestConstants.SERVER_ROOT + svc_track_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonTrack.class);
        assertEquals("Master of Puppets", response.getTitle());
    }

    /**
     * Test for /services/tracks POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(153)
    public void callTrackCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonTrack track = new JsonTrack(null, 1, "Welcome Home", 5, "7:00", "Metallica", 1);

        JsonTrack response = client.target(TestConstants.SERVER_ROOT + svc_track_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(track, MediaType.APPLICATION_JSON), JsonTrack.class);
        assertEquals("Welcome Home", response.getTitle());
    }

    /**
     * Test for /services/tracks POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(154)
    public void callTrackUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonTrack track = new JsonTrack(1, 1, "Battery", 3, "5:10", "Metallica", 1);

        JsonTrack response = client.target(TestConstants.SERVER_ROOT + svc_track_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(track, MediaType.APPLICATION_JSON), JsonTrack.class);
        assertEquals("Battery", response.getTitle());
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_tvshow_root = "/services/tvshows";

    /**
     * Test for /services/tvshows GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(161)
    public void callTvShowGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonShow> response = client.target(TestConstants.SERVER_ROOT + svc_tvshow_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No tvshow found", response.isEmpty());
    }

    /**
     * Test for /services/tvshows/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(162)
    public void callTvShowGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonShow response = client.target(TestConstants.SERVER_ROOT + svc_tvshow_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonShow.class);
        assertEquals("Justified s01", response.getTitle());
    }

    /**
     * Test for /services/tvshows POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(163)
    public void callTvShowCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonShow tvshow = new JsonShow(null, "Stargate SG1", "Mickael !!!!", null, "", "", 1, "", 1, "89", 3, "ui",
                true, new ArrayList<JsonLang>(), new ArrayList<JsonLang>(), false, 0);

        JsonShow response = client.target(TestConstants.SERVER_ROOT + svc_tvshow_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(tvshow, MediaType.APPLICATION_JSON), JsonShow.class);
        assertEquals(Integer.valueOf(8), response.getId());
    }

    /**
     * Test for /services/tvshows POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(164)
    public void callTvShowUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonShow tvshow = new JsonShow(2, "Stargate SG1 bis", "Mickael !!!!  !!!!", null, "", "", 1, "", 1, "89", 3,
                "ui", true, new ArrayList<JsonLang>(), new ArrayList<JsonLang>(), false, 0);

        JsonShow response = client.target(TestConstants.SERVER_ROOT + svc_tvshow_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(tvshow, MediaType.APPLICATION_JSON), JsonShow.class);
        assertEquals("Stargate SG1 bis", response.getTitle());
    }

    /**
     * Test for /services/tvshows/user/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(165)
    public void callTvShowGetUserTvshows() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonAlbum> response = client
                .target(TestConstants.SERVER_ROOT + svc_tvshow_root
                        + "/user?from=0&limit=5&orderBy=t.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No tvshow found", response.isEmpty());
        assertEquals("List page 1 does not contains 1 entrie", Integer.valueOf(1), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/tvshows GET with params
     * 
     * @throws Exception
     */
    @Test
    @InSequence(166)
    public void callTvShowGetListParamsPOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonShow> response = client
                .target(TestConstants.SERVER_ROOT + svc_tvshow_root + "?from=0&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No tvshow found", response.isEmpty());
        assertEquals("List page 1 does not contains 5 entries", Integer.valueOf(5), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/tvshows GET with params
     * 
     * @throws Exception
     */
    @Test
    @InSequence(167)
    public void callTvShowGetListParamsPTwo() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonShow> response = client
                .target(TestConstants.SERVER_ROOT + svc_tvshow_root + "?from=6&limit=5&orderBy=id&orderDir=asc")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No tvshow found", response.isEmpty());
        assertEquals("List page 2 does not contains 2 entries", Integer.valueOf(2), Integer.valueOf(response.size()));
    }

    /**
     * Test for /services/tvshows/addtocollec POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(168)
    public void callTvShowAddToCollec() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonMyShow show = new JsonMyShow(2, 1, 4, "");

        @SuppressWarnings("unchecked")
        List<JsonShow> listbefore = client
                .target(TestConstants.SERVER_ROOT + svc_tvshow_root
                        + "/user?from=0&limit=5&orderBy=t.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizebefore = listbefore.size();

        JsonSimpleResponse response = client.target(TestConstants.SERVER_ROOT + svc_tvshow_root + "/addtocollec")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(show, MediaType.APPLICATION_JSON), JsonSimpleResponse.class);
        assertEquals("true", response.getOk());

        @SuppressWarnings("unchecked")
        List<JsonShow> listafter = client
                .target(TestConstants.SERVER_ROOT + svc_tvshow_root
                        + "/user?from=0&limit=5&orderBy=t.id&orderDir=asc&userId=1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        int sizeafter = listafter.size();
        assertTrue("not added correctly : " + sizebefore + " | " + sizeafter, (sizebefore + 1) == sizeafter);
    }

    /**************************************************************************************/
    /**************************************************************************************/
    /**************************************************************************************/

    private static final String svc_user_root = "/api/v1/users";

    /**
     * Test for /services/users GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(171)
    public void callUserGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonUser> response = client.target(TestConstants.SERVER_ROOT + svc_user_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No user found", response.isEmpty());
    }

    /**
     * Test for /services/users/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(172)
    public void callUserGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonUser response = client.target(TestConstants.SERVER_ROOT + svc_user_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonUser.class);
        assertEquals("msansm1", response.getLogin());
    }

    /**
     * Test for /services/users POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(173)
    public void callUserCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonUser user = new JsonUser(null, "testrest", "test@rest.bzh");

        JsonUser response = client.target(TestConstants.SERVER_ROOT + svc_user_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON), JsonUser.class);
        assertEquals("testrest", response.getLogin());
    }

    /**
     * Test for /services/users POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(174)
    public void callUserUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonUser user = new JsonUser(1, "msansm1", "test@test.bzh");

        JsonUser response = client.target(TestConstants.SERVER_ROOT + svc_user_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON), JsonUser.class);
        assertEquals("msansm1", response.getLogin());
    }

}
