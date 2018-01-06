package bzh.msansm1.trevad.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.conf.Conf;
import bzh.msansm1.trevad.server.json.JsonSimpleResponse;
import bzh.msansm1.trevad.server.json.tvshow.JsonMyShow;
import bzh.msansm1.trevad.server.json.tvshow.JsonShow;
import bzh.msansm1.trevad.server.persistence.dao.StorygenreDAO;
import bzh.msansm1.trevad.server.persistence.dao.SupportDAO;
import bzh.msansm1.trevad.server.persistence.dao.TvshowDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.dao.UsertvDAO;
import bzh.msansm1.trevad.server.persistence.model.Tvshow;
import bzh.msansm1.trevad.server.persistence.model.Usertv;
import bzh.msansm1.trevad.server.persistence.model.UsertvPK;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/tvshows")
@Api(value = "tvshows", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TVShowService extends Application {

    private static final Logger LOGGER = Logger.getLogger(TVShowService.class);

    @Inject
    TvshowDAO showDao;
    @Inject
    StorygenreDAO storygenreDao;
    @Inject
    SupportDAO supportDao;
    @Inject
    Conf conf;
    @Inject
    UsertvDAO usertvDAO;
    @Inject
    UserDAO userDAO;

    public TVShowService() {
    }

    /**
     * GET /shows : retrieve all shows
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all shows", notes = "Retreive all shows", response = JsonShow.class,
            responseContainer = "List")
    public List<JsonShow> getAllWithParams(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir) {
        List<Tvshow> shows = showDao.getTvshowsForList(from, limit, orderBy, orderDir);
        LOGGER.info("find " + shows.size() + " shows in the database");
        ArrayList<JsonShow> ls = new ArrayList<JsonShow>();
        for (Tvshow s : shows) {
            Usertv mys = usertvDAO.getUsertv(s.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
            ls.add(new JsonShow(s.getId(), s.getTitle(), s.getDescription(), s.getReleasedate(), s.getCover(),
                    (s.getSupportBean() != null) ? s.getSupportBean().getName() : "",
                    (s.getSupportBean() != null) ? s.getSupportBean().getId() : null,
                    (s.getStorygenre() != null) ? s.getStorygenre().getName() : "",
                    (s.getStorygenre() != null) ? s.getStorygenre().getId() : null, s.getLength(), s.getSeason(),
                    s.getSeries(), s.getIsseriedone(), null, null, (mys != null) ? true : false,
                    (mys != null) ? mys.getRating() : 0));
        }
        return ls;
    }

    /**
     * GET /shows/user : retrieve shows for one user
     * 
     * 
     * @return
     */
    @GET
    @Path(value = "/user")
    @ApiOperation(value = "Retreive shows for one user", notes = "Retreive shows for one user",
            response = JsonShow.class, responseContainer = "List")
    public List<JsonShow> getUserShows(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir, @QueryParam("userId") Integer userId) {
        return showDao.getUserTvshows(from, limit, orderBy, orderDir, userId);
    }

    /**
     * GET /shows/{id} : retrieve one show
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retreive one show", notes = "Retreive one show", response = JsonShow.class)
    public JsonShow getOne(@PathParam(value = "id") Integer id) {
        Tvshow s = showDao.getTvshow(id);
        LOGGER.info("find " + s.getTitle() + " show in the database");
        return new JsonShow(s.getId(), s.getTitle(), s.getDescription(), s.getReleasedate(), s.getCover(),
                (s.getSupportBean() != null) ? s.getSupportBean().getName() : "",
                (s.getSupportBean() != null) ? s.getSupportBean().getId() : null,
                (s.getStorygenre() != null) ? s.getStorygenre().getName() : "",
                (s.getStorygenre() != null) ? s.getStorygenre().getId() : null, s.getLength(), s.getSeason(),
                s.getSeries(), s.getIsseriedone(), null, null, false, 0);
    }

    /**
     * POST /shows : create / update one show
     * 
     * @param id
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one show", notes = "Create / update one show", response = JsonShow.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonShow createUpdateOne(JsonShow show) {
        JsonShow jshow = show;
        if (show.getId() == null) {
            Tvshow s = new Tvshow();
            s.setCover(show.getCover());
            s.setDescription(show.getDescription());
            s.setTitle(show.getTitle());
            s.setIsseriedone(show.getIsSeriesDone());
            s.setLength(show.getLength());
            s.setReleasedate(show.getReleaseDate());
            s.setSeason(show.getSeason());
            s.setSeries(show.getSeries());
            s.setStorygenre(storygenreDao.getStorygenre(show.getGenreId()));
            s.setSupportBean(supportDao.getSupport(show.getSupportId()));
            showDao.saveTvshow(s);
            jshow.setId(s.getId());
        } else {
            Tvshow s = showDao.getTvshow(show.getId());
            s.setCover(show.getCover());
            s.setDescription(show.getDescription());
            s.setTitle(show.getTitle());
            s.setIsseriedone(show.getIsSeriesDone());
            s.setLength(show.getLength());
            s.setReleasedate(show.getReleaseDate());
            s.setSeason(show.getSeason());
            s.setSeries(show.getSeries());
            s.setStorygenre(storygenreDao.getStorygenre(show.getGenreId()));
            s.setSupportBean(supportDao.getSupport(show.getSupportId()));
            showDao.updateTvshow(s);
        }
        return jshow;
    }

    // /**
    // * POST : upload new cover for show
    // *
    // * @param newcover
    // * @return
    // */
    // @POST
    // @Path("{id}/coverupload")
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // public Response uploadAttach(@PathParam("id") Integer id,
    // MultipartFormDataInput newcover) {
    // Map<String, List<InputPart>> uploadForm = newcover.getFormDataMap();
    // // Get file data to save
    // List<InputPart> inputParts = uploadForm.get("file");
    // String filename = null;
    // for (InputPart inputPart : inputParts) {
    // // convert the uploaded file to inputstream and write it to disk
    // InputStream inputStream = null;
    // OutputStream out = null;
    // try {
    // inputStream = inputPart.getBody(InputStream.class, null);
    // List<String> contDisp = inputPart.getHeaders().get("Content-Disposition");
    // for (String cd : contDisp) {
    // if (cd.contains("filename")) {
    // filename = "cover.jpg";
    // LOGGER.info("FILENAME : " + filename);
    // }
    // }
    // String path = conf.getTvshowsFS() + id + "/";
    // File pathtest = new File(path);
    // if (!pathtest.exists()) {
    // if (!pathtest.mkdirs()) {
    // LOGGER.error("While saving cover : " + "unable to create repository tmp dir
    // => " + path);
    // }
    // }
    // File up = new File(path + filename);
    // if (!up.createNewFile()) {
    // if (up.exists()) {
    // up.delete();
    // if (!up.createNewFile()) {
    // LOGGER.error("While saving cover : " + "unable to overwrite existing file =>
    // "
    // + up.getAbsolutePath());
    // }
    // } else {
    // LOGGER.error("While saving cover : " + "unable to create new file => " +
    // up.getAbsolutePath());
    // }
    // }
    // out = new FileOutputStream(up);
    //
    // int read = 0;
    // byte[] bytes = new byte[2048];
    // while ((read = inputStream.read(bytes)) != -1) {
    // out.write(bytes, 0, read);
    // }
    // inputStream.close();
    // out.flush();
    // out.close();
    // } catch (IOException e) {
    // LOGGER.error("While saving cover : ", e);
    // return Response.ok(new JsonSimpleResponse(false),
    // MediaType.APPLICATION_JSON).build();
    // } finally {
    // if (inputStream != null) {
    // try {
    // inputStream.close();
    // } catch (IOException e) {
    // LOGGER.error("While saving cover - closing inputstream : ", e);
    // }
    // }
    // if (out != null) {
    // try {
    // out.close();
    // } catch (IOException e) {
    // LOGGER.error("While saving cover - closing outputstream : ", e);
    // }
    // }
    // }
    // }
    // return Response.ok(new JsonSimpleResponse(true),
    // MediaType.APPLICATION_JSON).build();
    // }

    /**
     * POST /addtocollec : add tvshow to user's collection
     * 
     * @return
     */
    @POST
    @Path("addtocollec")
    @ApiOperation(value = "Add tvshow to user's collection", notes = "Add tvshow to user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response addToCollection(JsonMyShow show) {
        Usertv ut = new Usertv();
        UsertvPK umid = new UsertvPK();
        umid.setTvshow(show.getSerieId().intValue());
        umid.setUser(show.getUserId().intValue());
        ut.setId(umid);
        ut.setTvshowBean(showDao.getTvshow(show.getSerieId()));
        ut.setUserBean(userDAO.getUser(show.getUserId()));
        ut.setComment(show.getComment());
        ut.setRating(show.getRating());
        usertvDAO.saveUsertv(ut);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

    /**
     * POST /removefromcollec : remove tvshow from user's collection
     * 
     * @return
     */
    @POST
    @Path("removefromcollec")
    @ApiOperation(value = "Remove tvshow to user's collection", notes = "Remove tvshow to user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response removeFromCollection(JsonMyShow show) {
        Usertv ut = new Usertv();
        UsertvPK umid = new UsertvPK();
        umid.setTvshow(show.getSerieId().intValue());
        umid.setUser(show.getUserId().intValue());
        ut.setId(umid);
        ut.setTvshowBean(showDao.getTvshow(show.getSerieId()));
        ut.setUserBean(userDAO.getUser(show.getUserId()));
        ut.setComment(show.getComment());
        ut.setRating(show.getRating());
        usertvDAO.removeUsertv(ut);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

}
