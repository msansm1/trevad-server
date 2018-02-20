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

import bzh.msansm1.trevad.server.json.artist.JsonArtist;
import bzh.msansm1.trevad.server.persistence.dao.ArtistDAO;
import bzh.msansm1.trevad.server.persistence.dao.ArtisttypeDAO;
import bzh.msansm1.trevad.server.persistence.model.Artist;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/artists")
@Api(value = "artists", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArtistService extends Application {

    private static final Logger LOGGER = Logger.getLogger(ArtistService.class);

    @Inject
    ArtistDAO artistDao;
    @Inject
    ArtisttypeDAO artisttypeDAO;

    public ArtistService() {
    }

    /**
     * GET /artists : retrieve all artists
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all artists", notes = "Retreive all artists", response = JsonArtist.class, responseContainer = "List")
    public List<JsonArtist> getAll() {
        List<Artist> artists = artistDao.getArtists();
        LOGGER.info("find " + artists.size() + " artists in the database");
        ArrayList<JsonArtist> la = new ArrayList<JsonArtist>();
        for (Artist a : artists) {
            la.add(new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                    a.getArtisttype().getId(), a.getNationality(), a.getBiolink()));
        }
        return la;
    }

    /**
     * GET /artists/{id} : retrieve one artist
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one artist", notes = "Retrieve one artist", response = JsonArtist.class)
    public JsonArtist getOne(@PathParam(value = "id") Integer id) {
        Artist a = artistDao.getArtist(id);
        LOGGER.info("find " + a.getName() + " artist in the database");
        return new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                a.getArtisttype().getId(), a.getNationality(), a.getBiolink());
    }

    /**
     * POST /artists : create / update one artist
     * 
     * @param JsonArtist
     *            artist
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one artist", notes = "Create / update one artist", response = JsonArtist.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonArtist createUpdateOne(JsonArtist artist) {
        JsonArtist jartist = artist;
        if (artist.getId() == null) {
            Artist a = new Artist();
            a.setName(artist.getName());
            a.setFirstname(artist.getFirstname());
            a.setBiolink(artist.getBiolink());
            a.setNationality(artist.getNationality());
            if (artist.getTypeId() != null) {
                a.setArtisttype(artisttypeDAO.getArtisttype(artist.getTypeId()));
            }
            artistDao.saveArtist(a);
            jartist.setId(a.getId());
        } else {
            Artist a = artistDao.getArtist(artist.getId());
            a.setName(artist.getName());
            a.setFirstname(artist.getFirstname());
            a.setBiolink(artist.getBiolink());
            a.setNationality(artist.getNationality());
            if (artist.getTypeId() != null) {
                a.setArtisttype(artisttypeDAO.getArtisttype(artist.getTypeId()));
            }
            artistDao.updateArtist(a);
        }
        return jartist;
    }

    /**
     * GET /artists : retrieve all album artists
     * 
     * @return
     */
    @GET
    @Path("/albums")
    @ApiOperation(value = "Retreive all album artists", notes = "Retreive all album artists", response = JsonArtist.class, responseContainer = "List")
    public List<JsonArtist> getAllForAlbums() {
        List<Artist> artists = artistDao.getArtistsForAlbum();
        LOGGER.info("find " + artists.size() + " album artists in the database");
        ArrayList<JsonArtist> la = new ArrayList<JsonArtist>();
        for (Artist a : artists) {
            la.add(new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                    a.getArtisttype().getId(), a.getNationality(), a.getBiolink()));
        }
        return la;
    }

    /**
     * GET /books : retrieve all book artists
     * 
     * @return
     */
    @GET
    @Path("/books")
    @ApiOperation(value = "Retreive all book artists", notes = "Retreive all book artists", response = JsonArtist.class, responseContainer = "List")
    public List<JsonArtist> getAllForBooks() {
        List<Artist> artists = artistDao.getArtistsForBook();
        LOGGER.info("find " + artists.size() + " book artists in the database");
        ArrayList<JsonArtist> la = new ArrayList<JsonArtist>();
        for (Artist a : artists) {
            la.add(new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                    a.getArtisttype().getId(), a.getNationality(), a.getBiolink()));
        }
        return la;
    }

    /**
     * GET /movies : retrieve all movie artists
     * 
     * @return
     */
    @GET
    @Path("/movies")
    @ApiOperation(value = "Retreive all movie artists", notes = "Retreive all movie artists", response = JsonArtist.class, responseContainer = "List")
    public List<JsonArtist> getAllForMovies() {
        List<Artist> artists = artistDao.getArtistsForMovie();
        LOGGER.info("find " + artists.size() + " movi artists in the database");
        ArrayList<JsonArtist> la = new ArrayList<JsonArtist>();
        for (Artist a : artists) {
            la.add(new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                    a.getArtisttype().getId(), a.getNationality(), a.getBiolink()));
        }
        return la;
    }

    /**
     * GET /series : retrieve all tvshows artists
     * 
     * @return
     */
    @GET
    @Path("/series")
    @ApiOperation(value = "Retreive all tvshows artists", notes = "Retreive all tvshows artists", response = JsonArtist.class, responseContainer = "List")
    public List<JsonArtist> getAllForSeries() {
        List<Artist> artists = artistDao.getArtistsForSeries();
        LOGGER.info("find " + artists.size() + " series artists in the database");
        ArrayList<JsonArtist> la = new ArrayList<JsonArtist>();
        for (Artist a : artists) {
            la.add(new JsonArtist(a.getId(), a.getName(), a.getFirstname(), a.getArtisttype().getName(),
                    a.getArtisttype().getId(), a.getNationality(), a.getBiolink()));
        }
        return la;
    }

}
