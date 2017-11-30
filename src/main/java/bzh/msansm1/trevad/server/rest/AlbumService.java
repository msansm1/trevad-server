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
import bzh.msansm1.trevad.server.json.album.JsonAddSearch;
import bzh.msansm1.trevad.server.json.album.JsonAlbum;
import bzh.msansm1.trevad.server.json.album.JsonMyAlbum;
import bzh.msansm1.trevad.server.json.album.JsonTrack;
import bzh.msansm1.trevad.server.persistence.dao.AlbumDAO;
import bzh.msansm1.trevad.server.persistence.dao.AlbumartistDAO;
import bzh.msansm1.trevad.server.persistence.dao.ArtistDAO;
import bzh.msansm1.trevad.server.persistence.dao.ArtisttypeDAO;
import bzh.msansm1.trevad.server.persistence.dao.GenreDAO;
import bzh.msansm1.trevad.server.persistence.dao.SupportDAO;
import bzh.msansm1.trevad.server.persistence.dao.TrackDAO;
import bzh.msansm1.trevad.server.persistence.dao.TrackartistDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.dao.UseralbumDAO;
import bzh.msansm1.trevad.server.persistence.model.Album;
import bzh.msansm1.trevad.server.persistence.model.Albumartist;
import bzh.msansm1.trevad.server.persistence.model.AlbumartistPK;
import bzh.msansm1.trevad.server.persistence.model.Artist;
import bzh.msansm1.trevad.server.persistence.model.Track;
import bzh.msansm1.trevad.server.persistence.model.Trackartist;
import bzh.msansm1.trevad.server.persistence.model.TrackartistPK;
import bzh.msansm1.trevad.server.persistence.model.Useralbum;
import bzh.msansm1.trevad.server.persistence.model.UseralbumPK;
import bzh.msansm1.trevad.server.utils.Constants;

@ApplicationScoped
@Path(value = "/albums")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlbumService extends Application {

    private static final Logger LOGGER = Logger.getLogger(AlbumService.class);

    @Inject
    AlbumDAO albumDao;
    @Inject
    TrackDAO trackDao;
    @Inject
    GenreDAO genreDAO;
    @Inject
    SupportDAO supportDAO;
    @Inject
    ArtistDAO artistDao;
    @Inject
    AlbumartistDAO albumArtistDao;
    @Inject
    Conf conf;
    @Inject
    UserDAO userDAO;
    @Inject
    UseralbumDAO useralbumDao;
    @Inject
    ArtisttypeDAO artisttypeDAO;
    @Inject
    TrackartistDAO trackartistDAO;

    public AlbumService() {
    }

    /**
     * GET /albums : retrieve all albums
     * 
     * 
     * @return
     */
    @GET
    public List<JsonAlbum> getAllWithParams(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir) {
        List<Album> albums = albumDao.getAlbumsForList(from, limit, orderBy, orderDir);
        LOGGER.info("find " + albums.size() + " albums in the database");
        ArrayList<JsonAlbum> la = new ArrayList<>();
        String artistName;
        Integer artistId;
        for (Album a : albums) {
            if (!a.getAlbumartists().isEmpty()) {
                artistName = a.getAlbumartists().get(0).getArtistBean().getName();
                if (a.getAlbumartists().get(0).getArtistBean().getFirstname() != null) {
                    artistName += " " + a.getAlbumartists().get(0).getArtistBean().getFirstname();
                }
                artistId = a.getAlbumartists().get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            Useralbum mya = useralbumDao.getUseralbum(a.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
            List<Track> tracks = trackDao.getTracksForAlbum(a.getId());
            List<JsonTrack> lt = new ArrayList<>();
            for (Track t : tracks) {
                lt.add(new JsonTrack(t.getId(), a.getId(), t.getTitle(), t.getNumber(), t.getLength(),
                        t.getTrackartists().get(0).getArtistBean().getName(),
                        t.getTrackartists().get(0).getArtistBean().getId()));
            }
            JsonAlbum ja = new JsonAlbum().setId(a.getId()).setTitle(a.getTitle()).setCover(a.getCover())
                    .setReleaseDate(a.getReleasedate()).setCds(a.getCds()).setNbTracks(tracks.size()).setTracks(lt)
                    .setArtist(artistName).setArtistId(artistId);
            if (a.getGenreBean() != null) {
                ja.setGenre(a.getGenreBean().getName()).setGenreId(a.getGenreBean().getId());
            } else {
                ja.setGenre("").setGenreId(null);
            }
            if (a.getSupportBean() != null) {
                ja.setSupport(a.getSupportBean().getName()).setSupportId(a.getSupportBean().getId());
            } else {
                ja.setSupport("").setSupportId(null);
            }
            if (mya != null) {
                ja.setMycollec(true).setRating(mya.getRating()).setSigned(mya.getIssigned());
            } else {
                ja.setMycollec(false).setRating(0).setSigned(false);
            }
            la.add(ja);
        }
        return la;
    }

    /**
     * GET /albums/user : retrieve albums for one user
     * 
     * 
     * @return
     */
    @GET
    @Path(value = "user")
    public List<JsonAlbum> getUserAlbums(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir, @QueryParam("userId") Integer userId) {
        List<JsonAlbum> albums = albumDao.getUserAlbumsForList(from, limit, orderBy, orderDir, userId);
        LOGGER.info("find " + albums.size() + " albums in the database");
        String artistName;
        Integer artistId;
        List<Albumartist> aartists;
        for (JsonAlbum a : albums) {
            a.setMycollec(true);
            aartists = albumDao.getAlbumArtists(a.getId());
            if (!aartists.isEmpty()) {
                artistName = aartists.get(0).getArtistBean().getName();
                if (aartists.get(0).getArtistBean().getFirstname() != null) {
                    artistName += " " + aartists.get(0).getArtistBean().getFirstname();
                }
                artistId = aartists.get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            a.setArtist(artistName);
            a.setArtistId(artistId);
            List<Track> tracks = trackDao.getTracksForAlbum(a.getId());
            List<JsonTrack> lt = new ArrayList<>();
            for (Track t : tracks) {
                lt.add(new JsonTrack(t.getId(), a.getId(), t.getTitle(), t.getNumber(), t.getLength(),
                        t.getTrackartists().get(0).getArtistBean().getName(),
                        t.getTrackartists().get(0).getArtistBean().getId()));
            }
            a.setTracks(lt);
        }
        return albums;
    }

    /**
     * GET /albums/{id} : retrieve one album
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "{id}")
    public JsonAlbum getOne(@Context HttpServletRequest request, @PathParam(value = "id") Integer id) {
        Album a = albumDao.getAlbum(id);
        LOGGER.info("find " + a.getTitle() + " album in the database");
        List<Track> tracks = trackDao.getTracksForAlbum(id);
        LOGGER.info("find " + tracks.size() + " tracks for album : " + id);
        List<JsonTrack> lt = new ArrayList<>();
        for (Track t : tracks) {
            lt.add(new JsonTrack(t.getId(), id, t.getTitle(), t.getNumber(), t.getLength(),
                    t.getTrackartists().get(0).getArtistBean().getName(),
                    t.getTrackartists().get(0).getArtistBean().getId()));
        }
        String artistName = "";
        Integer artistId = 0;
        if (!a.getAlbumartists().isEmpty()) {
            artistName = a.getAlbumartists().get(0).getArtistBean().getName();
            if (a.getAlbumartists().get(0).getArtistBean().getFirstname() != null) {
                artistName = " " + a.getAlbumartists().get(0).getArtistBean().getFirstname();
            }
            artistId = a.getAlbumartists().get(0).getArtistBean().getId();
        }
        Useralbum mya = useralbumDao.getUseralbum(a.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
        JsonAlbum ja = new JsonAlbum().setId(a.getId()).setTitle(a.getTitle()).setCover(a.getCover())
                .setReleaseDate(a.getReleasedate()).setCds(a.getCds()).setNbTracks(tracks.size()).setTracks(lt)
                .setArtist(artistName).setArtistId(artistId);
        if (a.getGenreBean() != null) {
            ja.setGenre(a.getGenreBean().getName()).setGenreId(a.getGenreBean().getId());
        } else {
            ja.setGenre("").setGenreId(null);
        }
        if (a.getSupportBean() != null) {
            ja.setSupport(a.getSupportBean().getName()).setSupportId(a.getSupportBean().getId());
        } else {
            ja.setSupport("").setSupportId(null);
        }
        if (mya != null) {
            ja.setMycollec(true).setRating(mya.getRating()).setSigned(mya.getIssigned());
        } else {
            ja.setMycollec(false).setRating(0).setSigned(false);
        }
        return ja;
    }

    /**
     * POST /albums : create / update one album
     * 
     * @param id
     * @return
     */
    @POST
    @Transactional(rollbackOn = Exception.class)
    public JsonAlbum createUpdateOne(JsonAlbum album) {
        JsonAlbum ja = album;
        if (album.getId() == null) {
            Album a = new Album();
            a.setTitle(album.getTitle());
            a.setCover(album.getCover());
            a.setReleasedate(album.getReleaseDate());
            a.setCds(ja.getCds());
            if (album.getGenreId() != null) {
                a.setGenreBean(genreDAO.getGenre(album.getGenreId()));
            }
            if (album.getSupportId() != null) {
                a.setSupportBean(supportDAO.getSupport(album.getSupportId()));
            }
            albumDao.saveAlbum(a);
            Albumartist aa = new Albumartist();
            AlbumartistPK aaid = new AlbumartistPK();
            aaid.setAlbum(a.getId().intValue());
            Artist albumArtist = artistDao.findArtistByName(ja.getArtist());
            if (ja.getArtistId() != null) {
                albumArtist = artistDao.getArtist(album.getArtistId());
                aaid.setArtist(album.getArtistId().intValue());
                aa.setId(aaid);
                aa.setAlbumBean(a);
                aa.setArtistBean(artistDao.getArtist(album.getArtistId()));
            } else {
                if (albumArtist == null) {
                    albumArtist = new Artist();
                    albumArtist.setName(ja.getArtist());
                    albumArtist.setArtisttype(artisttypeDAO.getArtisttype(1));
                    artistDao.saveArtist(albumArtist);
                }
                aaid.setArtist(albumArtist.getId().intValue());
                aa.setId(aaid);
                aa.setAlbumBean(a);
                aa.setArtistBean(albumArtist);
            }
            albumArtistDao.saveAlbumartist(aa);
            a.addAlbumartist(aa);
            albumDao.updateAlbum(a);
            ja.setId(a.getId());
            for (JsonTrack t : ja.getTracks()) {
                Track track = new Track();
                track.setCd(t.getCd());
                track.setTitle(t.getTitle());
                track.setAlbumBean(a);
                track.setLength(t.getLength());
                track.setNumber(t.getTrackNb());
                trackDao.saveTrack(track);
                Trackartist ta = new Trackartist();
                TrackartistPK taid = new TrackartistPK();
                taid.setTrack(track.getId().intValue());
                if (t.getArtistId() != null) {
                    taid.setArtist(t.getArtistId().intValue());
                    ta.setId(taid);
                    ta.setTrackBean(track);
                    ta.setArtistBean(artistDao.getArtist(album.getArtistId()));
                } else {
                    if (!t.getArtist().isEmpty()) {
                        if (t.getArtist().equalsIgnoreCase(albumArtist.getName())) {
                            taid.setArtist(albumArtist.getId().intValue());
                            ta.setId(taid);
                            ta.setTrackBean(track);
                            ta.setArtistBean(albumArtist);
                        } else {
                            Artist artist = artistDao.findArtistByName(t.getArtist());
                            if (artist == null) {
                                artist = new Artist();
                                artist.setName(t.getArtist());
                                artist.setArtisttype(artisttypeDAO.getArtisttype(1));
                                artistDao.saveArtist(artist);
                            }
                            taid.setArtist(artist.getId().intValue());
                            ta.setId(taid);
                            ta.setTrackBean(track);
                            ta.setArtistBean(artist);
                        }
                    } else {
                        taid.setArtist(albumArtist.getId().intValue());
                        ta.setId(taid);
                        ta.setTrackBean(track);
                        ta.setArtistBean(albumArtist);
                    }
                }
                trackartistDAO.saveTrackartist(ta);
                track.addTrackartist(ta);
                trackDao.updateTrack(track);
            }
        } else {
            Album a = albumDao.getAlbum(album.getId());
            LOGGER.info("find " + a.getTitle() + " album in the database to update");
            a.setTitle(album.getTitle());
            a.setCover(album.getCover());
            a.setReleasedate(album.getReleaseDate());
            if (album.getGenreId() != null) {
                a.setGenreBean(genreDAO.getGenre(album.getGenreId()));
            }
            if (album.getSupportId() != null) {
                a.setSupportBean(supportDAO.getSupport(album.getSupportId()));
            }
            AlbumartistPK aaid = new AlbumartistPK();
            aaid.setAlbum(a.getId().intValue());
            aaid.setArtist(album.getArtistId().intValue());
            if (albumArtistDao.getAlbumartist(aaid) == null) {
                Albumartist aa = new Albumartist();
                aa.setId(aaid);
                aa.setAlbumBean(a);
                aa.setArtistBean(artistDao.getArtist(album.getArtistId()));
                a.addAlbumartist(aa);
                albumArtistDao.saveAlbumartist(aa);
            } else {

            }
            albumDao.updateAlbum(a);
        }
        return ja;
    }

    /**
     * GET /albums/{albumId}/tracks : retrieve all tracks for one album
     * 
     * @return
     */
    @GET
    @Path("/{albumId}/tracks")
    public List<JsonTrack> getAlbumTracks(@PathParam(value = "albumId") Integer albumId) {
        List<Track> tracks = trackDao.getTracksForAlbum(albumId);
        LOGGER.info("find " + tracks.size() + " tracks for album : " + albumId);
        List<JsonTrack> lt = new ArrayList<JsonTrack>();
        for (Track t : tracks) {
            lt.add(new JsonTrack(t.getId(), albumId, t.getTitle(), t.getNumber(), t.getLength(),
                    t.getTrackartists().get(0).getArtistBean().getName(),
                    t.getTrackartists().get(0).getArtistBean().getId()));
        }
        return lt;
    }

    // /**
    // * POST : upload new cover for album
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
    // String path = conf.getAlbumFS() + id + "/";
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
     * POST /removefromcollec : remove album from user's collection
     * 
     * @return
     */
    @POST
    @Path("removefromcollec")
    @Transactional(rollbackOn = Exception.class)
    public Response removeFromCollection(JsonMyAlbum album) {
        Useralbum ua = new Useralbum();
        UseralbumPK uaid = new UseralbumPK();
        uaid.setAlbum(album.getAlbumId().intValue());
        uaid.setUser(album.getUserId().intValue());
        ua.setId(uaid);
        ua.setAlbumBean(albumDao.getAlbum(album.getAlbumId()));
        ua.setUserBean(userDAO.getUser(album.getUserId()));
        ua.setIssigned(album.getSigned());
        ua.setComment(album.getComment());
        ua.setRating(album.getRating());
        useralbumDao.removeUseralbum(ua);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

    /**
     * POST /add/search : search an album from gracenote database
     * 
     * @return
     */
    @POST
    @Path("add/search")
    public Response searchNewAlbum(JsonAddSearch album) {
        String clientID = conf.getGracenoteClientID(); // Put your clientID here.
        String clientTag = conf.getGracenoteClientTag(); // Put your clientTag here.

        return Response.ok(new JsonSimpleResponse(false), MediaType.APPLICATION_JSON).build();
    }

}
