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
import bzh.msansm1.trevad.server.json.JsonLang;
import bzh.msansm1.trevad.server.json.JsonSimpleResponse;
import bzh.msansm1.trevad.server.json.movie.JsonMovie;
import bzh.msansm1.trevad.server.json.movie.JsonMyMovie;
import bzh.msansm1.trevad.server.persistence.dao.MovieDAO;
import bzh.msansm1.trevad.server.persistence.dao.StorygenreDAO;
import bzh.msansm1.trevad.server.persistence.dao.SupportDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.dao.UsermovieDAO;
import bzh.msansm1.trevad.server.persistence.model.Lang;
import bzh.msansm1.trevad.server.persistence.model.Movie;
import bzh.msansm1.trevad.server.persistence.model.Movieartist;
import bzh.msansm1.trevad.server.persistence.model.Usermovie;
import bzh.msansm1.trevad.server.persistence.model.UsermoviePK;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/movies")
@Api(value = "movies", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieService extends Application {

    private static final Logger LOGGER = Logger.getLogger(MovieService.class);

    @Inject
    MovieDAO movieDao;
    @Inject
    SupportDAO supportDAO;
    @Inject
    StorygenreDAO storygenreDAO;
    @Inject
    Conf conf;
    @Inject
    UsermovieDAO usermovieDAO;
    @Inject
    UserDAO userDAO;

    public MovieService() {
    }

    /**
     * GET /movies : retrieve all movies
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all movies", notes = "Retreive all movies", response = JsonMovie.class,
            responseContainer = "List")
    public List<JsonMovie> getAllWithParams(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir) {
        List<Movie> movies = movieDao.getMoviesForList(from, limit, orderBy, orderDir);
        LOGGER.info("find " + movies.size() + " movies in the database");
        ArrayList<JsonMovie> lm = new ArrayList<JsonMovie>();
        String artistName = "";
        Integer artistId = 0;
        for (Movie m : movies) {
            if (!m.getMovieartists().isEmpty()) {
                artistName = m.getMovieartists().get(0).getArtistBean().getName() + " "
                        + m.getMovieartists().get(0).getArtistBean().getFirstname();
                artistId = m.getMovieartists().get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            Usermovie mym = usermovieDAO.getUsermovie(m.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
            lm.add(new JsonMovie(m.getId(), m.getTitle(), m.getDescription(), m.getReleasedate(), m.getCover(),
                    m.getSupportBean().getName(), m.getSupportBean().getId(), m.getStorygenre().getName(),
                    m.getStorygenre().getId(), m.getLength(), m.getIscollector(), artistName, artistId, "", null, "",
                    null, new ArrayList<JsonLang>(), new ArrayList<JsonLang>(), (mym != null) ? true : false,
                    (mym != null) ? mym.getRating() : 0));
        }
        return lm;
    }

    /**
     * GET /movies/user : retrieve movies for one user
     * 
     * @param id
     *            - user ID
     * @return
     */
    @GET
    @Path(value = "user")
    @ApiOperation(value = "Retreive movies for one user", notes = "Retreive movies for one user",
            response = JsonMovie.class, responseContainer = "List")
    public List<JsonMovie> getAllWithParams(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir, @QueryParam("userId") Integer userId) {
        List<JsonMovie> movies = movieDao.getUserMovies(from, limit, orderBy, orderDir, userId);
        LOGGER.info("find " + movies.size() + " movies in the database");
        String artistName = "";
        Integer artistId = 0;
        List<Movieartist> martists = null;
        for (JsonMovie m : movies) {
            martists = movieDao.getMovieArtists(m.getId());
            if (!martists.isEmpty()) {
                artistName = martists.get(0).getArtistBean().getName() + " "
                        + martists.get(0).getArtistBean().getFirstname();
                artistId = martists.get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            m.setRealisator(artistName);
            m.setRealisatorId(artistId);
        }
        return movies;
    }

    /**
     * GET /movies/{id} : retrieve one movie
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retreive one movie", notes = "Retreive one movie", response = JsonMovie.class)
    public JsonMovie getOne(@PathParam(value = "id") Integer id) {
        JsonMovie jm = movieDao.getJsonMovie(id);
        LOGGER.info("find " + jm.getTitle() + " movie in the database");
        Movie m = movieDao.getMovie(id);
        List<JsonLang> ll = new ArrayList<JsonLang>();
        for (Lang l : m.getLangs2()) {
            ll.add(new JsonLang(l.getId(), l.getName()));
        }
        jm.setLangs(ll);
        List<JsonLang> ls = new ArrayList<JsonLang>();
        for (Lang l : m.getLangs1()) {
            ls.add(new JsonLang(l.getId(), l.getName()));
        }
        jm.setSubtitles(ls);
        return jm;
    }

    /**
     * POST /movies : create / update one movie
     * 
     * @param id
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one movie", notes = "Create / update one movie", response = JsonMovie.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonMovie createUpdateOne(JsonMovie movie) {
        JsonMovie jmovie = movie;
        if (movie.getId() == null) {
            Movie m = new Movie();
            m.setTitle(movie.getTitle());
            m.setDescription(movie.getDescription());
            m.setReleasedate(movie.getReleaseDate());
            m.setLength(movie.getLength());
            m.setIscollector(movie.getIsCollector());
            m.setSupportBean(supportDAO.getSupport(movie.getSupportId()));
            m.setStorygenre(storygenreDAO.getStorygenre(movie.getGenreId()));
            movieDao.saveMovie(m);
            jmovie.setId(m.getId());
        } else {
            Movie m = movieDao.getMovie(movie.getId());
            m.setTitle(movie.getTitle());
            m.setDescription(movie.getDescription());
            m.setReleasedate(movie.getReleaseDate());
            m.setLength(movie.getLength());
            m.setIscollector(movie.getIsCollector());
            m.setSupportBean(supportDAO.getSupport(movie.getSupportId()));
            m.setStorygenre(storygenreDAO.getStorygenre(movie.getGenreId()));
            movieDao.updateMovie(m);
        }
        return jmovie;
    }

    // /**
    // * POST : upload new cover for movie
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
    // String path = conf.getMovieFS() + id + "/";
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
     * POST /addtocollec : add movie to user's collection
     * 
     * @return
     */
    @POST
    @Path("addtocollec")
    @ApiOperation(value = "Add movie to user's collection", notes = "Add movie to user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response addToCollection(JsonMyMovie movie) {
        Usermovie um = new Usermovie();
        UsermoviePK umid = new UsermoviePK();
        umid.setMovie(movie.getMovieId().intValue());
        umid.setUser(movie.getUserId().intValue());
        um.setId(umid);
        um.setMovieBean(movieDao.getMovie(movie.getMovieId()));
        um.setUserBean(userDAO.getUser(movie.getUserId()));
        um.setComment(movie.getComment());
        um.setRating(movie.getRating());
        usermovieDAO.saveUsermovie(um);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

    /**
     * POST /removefromcollec : remove movie from user's collection
     * 
     * @return
     */
    @POST
    @Path("removefromcollec")
    @ApiOperation(value = "Remove movie to user's collection", notes = "Remove movie to user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response removeFromCollection(JsonMyMovie movie) {
        Usermovie um = new Usermovie();
        UsermoviePK umid = new UsermoviePK();
        umid.setMovie(movie.getMovieId().intValue());
        umid.setUser(movie.getUserId().intValue());
        um.setId(umid);
        um.setMovieBean(movieDao.getMovie(movie.getMovieId()));
        um.setUserBean(userDAO.getUser(movie.getUserId()));
        um.setComment(movie.getComment());
        um.setRating(movie.getRating());
        usermovieDAO.removeUsermovie(um);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

}
