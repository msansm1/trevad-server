package bzh.msansm1.trevad.server.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.json.home.AlbumStats;
import bzh.msansm1.trevad.server.json.home.BookStats;
import bzh.msansm1.trevad.server.json.home.JsonCollectionStats;
import bzh.msansm1.trevad.server.json.home.MovieStats;
import bzh.msansm1.trevad.server.json.home.SerieStats;
import bzh.msansm1.trevad.server.persistence.dao.AlbumDAO;
import bzh.msansm1.trevad.server.persistence.dao.BookDAO;
import bzh.msansm1.trevad.server.persistence.dao.MovieDAO;
import bzh.msansm1.trevad.server.persistence.dao.TvshowDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.model.User;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

/**
 * Auth service (/services/auth)
 * 
 * @author ebarona
 * 
 */
@ApplicationScoped
@Path(value = "/home")
@Api(value = "home", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HomeService extends Application {

    private static final Logger LOGGER = Logger.getLogger(HomeService.class);

    @Inject
    UserDAO userDao;
    @Inject
    AlbumDAO albumDAO;
    @Inject
    BookDAO bookDAO;
    @Inject
    MovieDAO movieDAO;
    @Inject
    TvshowDAO tvshowDAO;

    public HomeService() {
    }

    /**
     * GET /mycollec : collection stats for an user
     * 
     * @return
     */
    @GET
    @Path("/mycollec")
    @ApiOperation(value = "collection stats for an user", notes = "collection stats for an user", response = JsonCollectionStats.class)
    public JsonCollectionStats userStats(@Context HttpServletRequest request) {
        JsonCollectionStats stats = new JsonCollectionStats();
        User user = userDao.getUserByToken(request.getHeader(Constants.HTTP_HEADER_TOKEN));
        if (user != null) {
            stats.setAlbums(albumDAO.getUserStats(user.getId()));
            stats.setBooks(bookDAO.getUserStats(user.getId()));
            stats.setMovies(movieDAO.getUserStats(user.getId()));
            stats.setSeries(tvshowDAO.getUserStats(user.getId()));
        } else {
            stats.setAlbums(new AlbumStats(Long.valueOf(0)));
            stats.setBooks(new BookStats(Long.valueOf(0)));
            stats.setMovies(new MovieStats(Long.valueOf(0)));
            stats.setSeries(new SerieStats(Long.valueOf(0)));
        }
        return stats;
    }

    /**
     * GET /allcollec : collection stats (for all database)
     * 
     * @return
     */
    @GET
    @Path("/allcollec")
    @ApiOperation(value = "collection stats (for all database)", notes = "collection stats (for all database)", response = JsonCollectionStats.class)
    public JsonCollectionStats allStats() {
        JsonCollectionStats stats = new JsonCollectionStats();
        stats.setAlbums(new AlbumStats(Long.valueOf(albumDAO.getAlbums().size())));
        stats.setBooks(new BookStats(Long.valueOf(bookDAO.getBooks().size())));
        stats.setMovies(new MovieStats(Long.valueOf(movieDAO.getMovies().size())));
        stats.setSeries(new SerieStats(Long.valueOf(tvshowDAO.getTvshows().size())));
        return stats;
    }

}
