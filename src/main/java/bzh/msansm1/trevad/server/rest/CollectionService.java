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

import bzh.msansm1.trevad.server.json.book.JsonCollection;
import bzh.msansm1.trevad.server.persistence.dao.CollectionDAO;
import bzh.msansm1.trevad.server.persistence.model.Collection;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/collections")
@Api(value = "collections", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CollectionService extends Application {

    private static final Logger LOGGER = Logger.getLogger(CollectionService.class);

    @Inject
    CollectionDAO collectionDao;

    public CollectionService() {
    }

    /**
     * GET /collections : retrieve all collections
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all collections", notes = "Retreive all collections", response = JsonCollection.class, responseContainer = "List")
    public List<JsonCollection> getAll() {
        List<Collection> collections = collectionDao.getCollections();
        LOGGER.info("find " + collections.size() + " collections in the database");
        ArrayList<JsonCollection> ll = new ArrayList<JsonCollection>();
        for (Collection l : collections) {
            ll.add(new JsonCollection(l.getId(), l.getName()));
        }
        return ll;
    }

    /**
     * GET /collections/{id} : retrieve one collection
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one collection", notes = "Retrieve one collection", response = JsonCollection.class)
    public JsonCollection getOne(@PathParam(value = "id") Integer id) {
        Collection l = collectionDao.getCollection(id);
        LOGGER.info("find " + l.getName() + " collection in the database");
        return new JsonCollection(l.getId(), l.getName());
    }

    /**
     * POST /collections : create / update one collection
     * 
     * @param JsonCollection
     *            collection
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one collection", notes = "Create / update one collection", response = JsonCollection.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonCollection createUpdateOne(JsonCollection collection) {
        JsonCollection jcollection = collection;
        if (collection.getId() == null) {
            Collection l = new Collection();
            l.setName(collection.getName());
            collectionDao.saveCollection(l);
            jcollection.setId(l.getId());
        } else {
            if (collection.getName().equalsIgnoreCase(Constants.DELETED)) {
                collectionDao.removeCollection(collectionDao.getCollection(collection.getId()));
            } else {
                Collection l = collectionDao.getCollection(collection.getId());
                l.setName(collection.getName());
                collectionDao.updateCollection(l);
            }
        }
        return jcollection;
    }

}
