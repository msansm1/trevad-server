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

import bzh.msansm1.trevad.server.json.JsonStorygenre;
import bzh.msansm1.trevad.server.persistence.dao.StorygenreDAO;
import bzh.msansm1.trevad.server.persistence.model.Storygenre;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/storygenres")
@Api(value = "storygenres", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StoryGenreService extends Application {

    private static final Logger LOGGER = Logger.getLogger(StoryGenreService.class);

    @Inject
    StorygenreDAO storygenreDao;

    public StoryGenreService() {
    }

    /**
     * GET /storygenres : retrieve all storygenres
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all storygenres", notes = "Retreive all storygenres", response = JsonStorygenre.class, responseContainer = "List")
    public List<JsonStorygenre> getAll() {
        List<Storygenre> storygenres = storygenreDao.getStorygenres();
        LOGGER.info("find " + storygenres.size() + " storygenres in the database");
        ArrayList<JsonStorygenre> ls = new ArrayList<JsonStorygenre>();
        for (Storygenre s : storygenres) {
            ls.add(new JsonStorygenre(s.getId(), s.getName()));
        }
        return ls;
    }

    /**
     * GET /storygenres/{id} : retrieve one storygenre
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one storygenre", notes = "Retrieve one storygenre", response = JsonStorygenre.class)
    public JsonStorygenre getOne(@PathParam(value = "id") Integer id) {
        Storygenre s = storygenreDao.getStorygenre(id);
        LOGGER.info("find " + s.getName() + " storygenre in the database");
        return new JsonStorygenre(s.getId(), s.getName());
    }

    /**
     * POST /storygenres : create / update one storygenre
     * 
     * @param JsonStorygenre
     *            storygenre
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one storygenre", notes = "Create / update one storygenre", response = JsonStorygenre.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonStorygenre createUpdateOne(JsonStorygenre storygenre) {
        JsonStorygenre jstorygenre = storygenre;
        if (storygenre.getId() == null) {
            Storygenre s = new Storygenre();
            s.setName(storygenre.getName());
            storygenreDao.saveStorygenre(s);
            jstorygenre.setId(s.getId());
        } else {
            if (storygenre.getName().equalsIgnoreCase(Constants.DELETED)) {
                storygenreDao.removeStorygenre(storygenreDao.getStorygenre(storygenre.getId()));
            } else {
                Storygenre s = storygenreDao.getStorygenre(storygenre.getId());
                s.setName(storygenre.getName());
                storygenreDao.updateStorygenre(s);
            }
        }
        return jstorygenre;
    }

}
