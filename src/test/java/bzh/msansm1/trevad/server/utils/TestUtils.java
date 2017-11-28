package bzh.msansm1.trevad.server.utils;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import bzh.msansm1.trevad.server.conf.ClientMessage;
import bzh.msansm1.trevad.server.error.TrevadError;
import bzh.msansm1.trevad.server.interceptor.RestRequestFilter;
import bzh.msansm1.trevad.server.json.JsonClientMsg;
import bzh.msansm1.trevad.server.json.admin.JsonConfParam;
import bzh.msansm1.trevad.server.json.admin.user.JsonAdminUser;
import bzh.msansm1.trevad.server.json.album.JsonAlbum;
import bzh.msansm1.trevad.server.json.artist.JsonArtist;
import bzh.msansm1.trevad.server.json.auth.JsonLogin;
import bzh.msansm1.trevad.server.json.book.JsonBook;
import bzh.msansm1.trevad.server.json.friend.JsonFriend;
import bzh.msansm1.trevad.server.json.home.JsonCollectionStats;
import bzh.msansm1.trevad.server.json.movie.JsonMovie;
import bzh.msansm1.trevad.server.json.tvshow.JsonShow;
import bzh.msansm1.trevad.server.json.user.JsonUser;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.model.Album;
import bzh.msansm1.trevad.server.rest.BookService;
import bzh.msansm1.trevad.server.rest.admin.AdminUserService;

public class TestUtils {

    public static synchronized WebArchive getWarFile(String warName) {
        // creation of the war for testing
        return ShrinkWrap.create(WebArchive.class, warName + ".war").addPackage(Album.class.getPackage())
                .addPackage(UserDAO.class.getPackage()).addPackage(BookService.class.getPackage())
                .addPackage(JsonClientMsg.class.getPackage()).addPackage(ClientMessage.class.getPackage())
                .addPackage(JsonAlbum.class.getPackage()).addPackage(JsonBook.class.getPackage())
                .addPackage(JsonMovie.class.getPackage()).addPackage(JsonShow.class.getPackage())
                .addPackage(JsonUser.class.getPackage()).addPackage(TrevadError.class.getPackage())
                .addPackage(Constants.class.getPackage()).addPackage(RestRequestFilter.class.getPackage())
                .addPackage(JsonLogin.class.getPackage()).addPackage(JsonArtist.class.getPackage())
                .addPackage(JsonAdminUser.class.getPackage()).addPackage(AdminUserService.class.getPackage())
                .addPackage(JsonCollectionStats.class.getPackage()).addPackage(JsonConfParam.class.getPackage())
                .addPackage(JsonFriend.class.getPackage()).addAsResource("project-test.yml", "project-defaults.yml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/load.sql", "META-INF/load.sql");
    }
}
