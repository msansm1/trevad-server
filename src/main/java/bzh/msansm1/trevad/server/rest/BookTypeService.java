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

import bzh.msansm1.trevad.server.json.book.JsonBooktype;
import bzh.msansm1.trevad.server.persistence.dao.BooktypeDAO;
import bzh.msansm1.trevad.server.persistence.model.Booktype;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/booktypes")
@Api(value = "booktypes", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookTypeService extends Application {

    private static final Logger LOGGER = Logger.getLogger(BookTypeService.class);

    @Inject
    BooktypeDAO booktypeDao;

    public BookTypeService() {
    }

    /**
     * GET /booktypes : retrieve all booktypes
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all booktypes", notes = "Retreive all booktypes", response = JsonBooktype.class, responseContainer = "List")
    public List<JsonBooktype> getAll() {
        List<Booktype> booktypes = booktypeDao.getBooktypes();
        LOGGER.info("find " + booktypes.size() + " booktypes in the database");
        ArrayList<JsonBooktype> ll = new ArrayList<JsonBooktype>();
        for (Booktype l : booktypes) {
            ll.add(new JsonBooktype(l.getId(), l.getName()));
        }
        return ll;
    }

    /**
     * GET /booktypes/{id} : retrieve one booktype
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one booktype", notes = "Retrieve one booktype", response = JsonBooktype.class)
    public JsonBooktype getOne(@PathParam(value = "id") Integer id) {
        Booktype l = booktypeDao.getBooktype(id);
        LOGGER.info("find " + l.getName() + " booktype in the database");
        return new JsonBooktype(l.getId(), l.getName());
    }

    /**
     * POST /booktypes : create / update one booktype
     * 
     * @param JsonBooktype
     *            booktype
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one booktype", notes = "Create / update one booktype", response = JsonBooktype.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonBooktype createUpdateOne(JsonBooktype booktype) {
        JsonBooktype jbooktype = booktype;
        if (booktype.getId() == null) {
            Booktype l = new Booktype();
            l.setName(booktype.getName());
            booktypeDao.saveBooktype(l);
            jbooktype.setId(l.getId());
        } else {
            if (booktype.getName().equalsIgnoreCase(Constants.DELETED)) {
                booktypeDao.removeBooktype(booktypeDao.getBooktype(booktype.getId()));
            } else {
                Booktype l = booktypeDao.getBooktype(booktype.getId());
                l.setName(booktype.getName());
                booktypeDao.updateBooktype(l);
            }
        }
        return jbooktype;
    }

}
