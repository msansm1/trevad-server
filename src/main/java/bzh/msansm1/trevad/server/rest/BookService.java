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
import bzh.msansm1.trevad.server.json.book.JsonBook;
import bzh.msansm1.trevad.server.json.book.JsonMyBook;
import bzh.msansm1.trevad.server.persistence.dao.BookDAO;
import bzh.msansm1.trevad.server.persistence.dao.BooktypeDAO;
import bzh.msansm1.trevad.server.persistence.dao.CollectionDAO;
import bzh.msansm1.trevad.server.persistence.dao.EditorDAO;
import bzh.msansm1.trevad.server.persistence.dao.LangDAO;
import bzh.msansm1.trevad.server.persistence.dao.StorygenreDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.dao.UserbookDAO;
import bzh.msansm1.trevad.server.persistence.model.Book;
import bzh.msansm1.trevad.server.persistence.model.Bookartist;
import bzh.msansm1.trevad.server.persistence.model.Collection;
import bzh.msansm1.trevad.server.persistence.model.Userbook;
import bzh.msansm1.trevad.server.persistence.model.UserbookPK;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/books")
@Api(value = "books", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookService extends Application {

    private static final Logger LOGGER = Logger.getLogger(BookService.class);

    @Inject
    BookDAO bookDao;
    @Inject
    BooktypeDAO booktypeDAO;
    @Inject
    StorygenreDAO storygenreDAO;
    @Inject
    EditorDAO editorDAO;
    @Inject
    CollectionDAO collectionDAO;
    @Inject
    LangDAO langDAO;
    @Inject
    Conf conf;
    @Inject
    UserbookDAO userbookDAO;
    @Inject
    UserDAO userDAO;

    public BookService() {
    }

    /**
     * GET /books : retrieve all books
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all books", notes = "Retreive all books", response = JsonBook.class,
            responseContainer = "List")
    public List<JsonBook> getAllWithParams(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir) {
        List<Book> books = bookDao.getBooksForList(from, limit, orderBy, orderDir);
        LOGGER.info("find " + books.size() + " books in the database");
        ArrayList<JsonBook> lb = new ArrayList<>();
        String artistName;
        Integer artistId;
        for (Book b : books) {
            if (!b.getBookartists().isEmpty()) {
                artistName = b.getBookartists().get(0).getArtistBean().getName() + " "
                        + b.getBookartists().get(0).getArtistBean().getFirstname();
                artistId = b.getBookartists().get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            Userbook myb = userbookDAO.getUserbook(b.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
            JsonBook jb = new JsonBook().setId(b.getId()).setTitle(b.getTitle()).setCover(b.getCover())
                    .setPublicationDate(b.getPublicationdate()).setAuthor(artistName).setAuthorId(artistId)
                    .setDescription(b.getDescription()).setSeries(b.getSeries()).setIsSerieDone(b.getIsseriedone())
                    .setBookNb(b.getBooknb());
            if (b.getEditorBean() != null) {
                jb.setEditor(b.getEditorBean().getName()).setEditorId(b.getEditorBean().getId());
            } else {
                jb.setEditor("").setEditorId(null);
            }
            if (b.getCollectionBean() != null) {
                jb.setCollection(b.getCollectionBean().getName()).setCollectionId(b.getCollectionBean().getId());
            } else {
                jb.setCollection("").setCollectionId(null);
            }
            if (b.getStorygenre() != null) {
                jb.setGenre(b.getStorygenre().getName()).setGenreId(b.getStorygenre().getId());
            } else {
                jb.setGenre("").setGenreId(null);
            }
            if (b.getBooktype() != null) {
                jb.setType(b.getBooktype().getName()).setTypeId(b.getBooktype().getId());
            } else {
                jb.setType("").setTypeId(null);
            }
            if (b.getLangBean() != null) {
                jb.setLang(b.getLangBean().getName()).setLangId(b.getLangBean().getId());
            } else {
                jb.setLang("").setLangId(null);
            }
            if (myb != null) {
                jb.setMycollec(true).setRating(myb.getRating()).setSigned(myb.getIssigned());
            } else {
                jb.setMycollec(false).setRating(0).setSigned(false);
            }
            lb.add(jb);
        }
        return lb;
    }

    /**
     * GET /books/user : retrieve books for one user
     * 
     * @return
     */
    @GET
    @Path(value = "user")
    @ApiOperation(value = "Retreive all books for one user", notes = "Retreive all books for one user",
            response = JsonBook.class, responseContainer = "List")
    public List<JsonBook> getUserBooks(@Context HttpServletRequest request, @QueryParam("from") int from,
            @QueryParam("limit") int limit, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderDir") String orderDir, @QueryParam("userId") Integer userId) {
        List<JsonBook> books = bookDao.getUserBooksForList(from, limit, orderBy, orderDir, userId);
        LOGGER.info("find " + books.size() + " books in the database");
        String artistName = "";
        Integer artistId = 0;
        List<Bookartist> bartists = null;
        for (JsonBook b : books) {
            bartists = bookDao.getBookArtists(b.getId());
            if (!bartists.isEmpty()) {
                artistName = bartists.get(0).getArtistBean().getName() + " "
                        + bartists.get(0).getArtistBean().getFirstname();
                artistId = bartists.get(0).getArtistBean().getId();
            } else {
                artistName = "";
                artistId = 0;
            }
            if (bookDao.getBook(b.getId()).getCollectionBean() != null) {
                Collection c = bookDao.getBook(b.getId()).getCollectionBean();
                b.setCollection(c.getName());
                b.setCollectionId(c.getId());
            }
            b.setAuthor(artistName);
            b.setAuthorId(artistId);
        }
        return books;
    }

    /**
     * GET /books/{id} : retrieve one book
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "{id}")
    @ApiOperation(value = "Retreive one book", notes = "Retreive one book", response = JsonBook.class)
    public JsonBook getOne(@Context HttpServletRequest request, @PathParam(value = "id") Integer id) {
        Book b = bookDao.getBook(id);
        LOGGER.info("find " + b.getTitle() + " in the database");
        String artistName = "";
        Integer artistId = 0;
        if (!b.getBookartists().isEmpty()) {
            artistName = b.getBookartists().get(0).getArtistBean().getName() + " "
                    + b.getBookartists().get(0).getArtistBean().getFirstname();
            artistId = b.getBookartists().get(0).getArtistBean().getId();
        }
        Userbook myb = userbookDAO.getUserbook(b.getId(), request.getHeader(Constants.HTTP_HEADER_TOKEN));
        JsonBook jb = new JsonBook().setId(b.getId()).setTitle(b.getTitle()).setCover(b.getCover())
                .setPublicationDate(b.getPublicationdate()).setAuthor(artistName).setAuthorId(artistId)
                .setDescription(b.getDescription()).setSeries(b.getSeries()).setIsSerieDone(b.getIsseriedone())
                .setBookNb(b.getBooknb());
        if (b.getEditorBean() != null) {
            jb.setEditor(b.getEditorBean().getName()).setEditorId(b.getEditorBean().getId());
        } else {
            jb.setEditor("").setEditorId(null);
        }
        if (b.getCollectionBean() != null) {
            jb.setCollection(b.getCollectionBean().getName()).setCollectionId(b.getCollectionBean().getId());
        } else {
            jb.setCollection("").setCollectionId(null);
        }
        if (b.getStorygenre() != null) {
            jb.setGenre(b.getStorygenre().getName()).setGenreId(b.getStorygenre().getId());
        } else {
            jb.setGenre("").setGenreId(null);
        }
        if (b.getBooktype() != null) {
            jb.setType(b.getBooktype().getName()).setTypeId(b.getBooktype().getId());
        } else {
            jb.setType("").setTypeId(null);
        }
        if (b.getLangBean() != null) {
            jb.setLang(b.getLangBean().getName()).setLangId(b.getLangBean().getId());
        } else {
            jb.setLang("").setLangId(null);
        }
        if (myb != null) {
            jb.setMycollec(true).setRating(myb.getRating()).setSigned(myb.getIssigned());
        } else {
            jb.setMycollec(false).setRating(0).setSigned(false);
        }
        return jb;
    }

    /**
     * POST /books : create / update one book
     * 
     * @param id
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one book", notes = "Create / update one book", response = JsonBook.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonBook createUpdateOne(JsonBook jb) {
        JsonBook jbook = jb;
        if (jb.getId() == null) {
            Book b = new Book();
            b.setTitle(jb.getTitle());
            b.setBooknb(jb.getBookNb());
            b.setDescription(jb.getDescription());
            b.setCover(jb.getCover());
            b.setIsseriedone(jb.getIsSerieDone());
            b.setSeries(jb.getSeries());
            b.setPublicationdate(jb.getPublicationDate());
            if (jb.getEditorId() != null) {
                b.setEditorBean(editorDAO.getEditor(jb.getEditorId()));
            }
            if (jb.getCollectionId() != null) {
                b.setCollectionBean(collectionDAO.getCollection(jb.getCollectionId()));
            }
            if (jb.getLangId() != null) {
                b.setLangBean(langDAO.getLang(jb.getLangId()));
            }
            if (jb.getTypeId() != null) {
                b.setBooktype(booktypeDAO.getBooktype(jb.getTypeId()));
            }
            if (jb.getGenreId() != null) {
                b.setStorygenre(storygenreDAO.getStorygenre(jb.getGenreId()));
            }
            bookDao.saveBook(b);
            jbook.setId(b.getId());
        } else {
            Book b = bookDao.getBook(jb.getId());
            b.setTitle(jb.getTitle());
            b.setBooknb(jb.getBookNb());
            b.setDescription(jb.getDescription());
            b.setCover(jb.getCover());
            b.setIsseriedone(jb.getIsSerieDone());
            b.setSeries(jb.getSeries());
            b.setPublicationdate(jb.getPublicationDate());
            if (jb.getEditorId() != null) {
                b.setEditorBean(editorDAO.getEditor(jb.getEditorId()));
            }
            if (jb.getCollectionId() != null) {
                b.setCollectionBean(collectionDAO.getCollection(jb.getCollectionId()));
            }
            if (jb.getLangId() != null) {
                b.setLangBean(langDAO.getLang(jb.getLangId()));
            }
            if (jb.getTypeId() != null) {
                b.setBooktype(booktypeDAO.getBooktype(jb.getTypeId()));
            }
            if (jb.getGenreId() != null) {
                b.setStorygenre(storygenreDAO.getStorygenre(jb.getGenreId()));
            }
            bookDao.updateBook(b);
        }
        return jbook;
    }

    // /**
    // * POST : upload new cover for book
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
    // String path = conf.getBookFS() + id + "/";
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
     * POST /addtocollec : add book to user's collection
     * 
     * @return
     */
    @POST
    @Path("addtocollec")
    @ApiOperation(value = "Add book to user's collection", notes = "Add book to user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response addToCollection(JsonMyBook book) {
        Userbook ub = new Userbook();
        UserbookPK ubid = new UserbookPK();
        ubid.setBook(book.getBookId().intValue());
        ubid.setUser(book.getUserId().intValue());
        ub.setId(ubid);
        ub.setBookBean(bookDao.getBook(book.getBookId()));
        ub.setUserBean(userDAO.getUser(book.getUserId()));
        ub.setIssigned(book.getSigned());
        ub.setComment(book.getComment());
        ub.setRating(book.getRating());
        userbookDAO.saveUserbook(ub);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

    /**
     * POST /removefromcollec : remove book from user's collection
     * 
     * @return
     */
    @POST
    @Path("removefromcollec")
    @ApiOperation(value = "Remove book from user's collection", notes = "Remove book from user's collection",
            response = Response.class)
    @Transactional(rollbackOn = Exception.class)
    public Response removeFromCollection(JsonMyBook book) {
        Userbook ub = new Userbook();
        UserbookPK ubid = new UserbookPK();
        ubid.setBook(book.getBookId().intValue());
        ubid.setUser(book.getUserId().intValue());
        ub.setId(ubid);
        ub.setBookBean(bookDao.getBook(book.getBookId()));
        ub.setUserBean(userDAO.getUser(book.getUserId()));
        ub.setIssigned(book.getSigned());
        ub.setComment(book.getComment());
        ub.setRating(book.getRating());
        userbookDAO.removeUserbook(ub);
        return Response.ok(new JsonSimpleResponse(true), MediaType.APPLICATION_JSON).build();
    }

}
