package bzh.msansm1.trevad.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.json.album.JsonGenre;
import bzh.msansm1.trevad.server.persistence.dao.GenreDAO;
import bzh.msansm1.trevad.server.persistence.model.Genre;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/genres")
@Api(value = "genres", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GenreService extends Application {

    private static final Logger LOGGER = Logger.getLogger(GenreService.class);

    @Inject
    GenreDAO genreDao;

    public GenreService() {
    }

    /**
     * GET /genres : retrieve all genres
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all genres", notes = "Retreive all genres", response = JsonGenre.class, responseContainer = "List")
    public List<JsonGenre> getAll() {
        List<Genre> genres = genreDao.getGenres();
        LOGGER.info("find " + genres.size() + " genres in the database");
        ArrayList<JsonGenre> ls = new ArrayList<JsonGenre>();
        for (Genre s : genres) {
            ls.add(new JsonGenre(s.getId(), s.getName()));
        }
        return ls;
    }

    /**
     * GET /genres/{id} : retrieve one genre
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one genre", notes = "Retrieve one genre", response = JsonGenre.class)
    public JsonGenre getOne(@PathParam(value = "id") Integer id) {
        Genre s = genreDao.getGenre(id);
        LOGGER.info("find " + s.getName() + " genre in the database");
        return new JsonGenre(s.getId(), s.getName());
    }

    /**
     * POST /genres : create / update one genre
     * 
     * @param JsonGenre
     *            genre
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one genre", notes = "Create / update one genre", response = JsonGenre.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonGenre createUpdateOne(JsonGenre genre) {
        JsonGenre jgenre = genre;
        if (genre.getId() == null) {
            Genre s = new Genre();
            s.setName(genre.getName());
            genreDao.saveGenre(s);
            jgenre.setId(s.getId());
        } else {
            if (genre.getName().equalsIgnoreCase(Constants.DELETED)) {
                genreDao.removeGenre(genreDao.getGenre(genre.getId()));
            } else {
                Genre s = genreDao.getGenre(genre.getId());
                s.setName(genre.getName());
                genreDao.updateGenre(s);
            }
        }
        return jgenre;
    }

}
