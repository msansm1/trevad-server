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

import bzh.msansm1.trevad.server.json.JsonSupport;
import bzh.msansm1.trevad.server.persistence.dao.SupportDAO;
import bzh.msansm1.trevad.server.persistence.model.Support;
import bzh.msansm1.trevad.server.utils.Constants;

@ApplicationScoped
@Path(value = "/supports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SupportService extends Application {

    private static final Logger LOGGER = Logger.getLogger(SupportService.class);

    @Inject
    SupportDAO supportDao;

    public SupportService() {
    }

    /**
     * GET /supports : retrieve all supports
     * 
     * @return
     */
    @GET
    public List<JsonSupport> getAll() {
        List<Support> supports = supportDao.getSupports();
        LOGGER.info("find " + supports.size() + " supports in the database");
        ArrayList<JsonSupport> ls = new ArrayList<JsonSupport>();
        for (Support s : supports) {
            ls.add(new JsonSupport(s.getId(), s.getName()));
        }
        return ls;
    }

    /**
     * GET /supports/{id} : retrieve one support
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    public JsonSupport getOne(@PathParam(value = "id") Integer id) {
        Support s = supportDao.getSupport(id);
        LOGGER.info("find " + s.getName() + " support in the database");
        return new JsonSupport(s.getId(), s.getName());
    }

    /**
     * POST /supports : create / update one support
     * 
     * @param JsonSupport
     *            support
     * @return
     */
    @POST
    @Transactional(rollbackOn = Exception.class)
    public JsonSupport createUpdateOne(JsonSupport support) {
        JsonSupport jsupport = support;
        if (support.getId() == null) {
            Support s = new Support();
            s.setName(support.getName());
            supportDao.saveSupport(s);
            jsupport.setId(s.getId());
        } else {
            if (support.getName().equalsIgnoreCase(Constants.DELETED)) {
                supportDao.removeSupport(supportDao.getSupport(support.getId()));
            } else {
                Support s = supportDao.getSupport(support.getId());
                s.setName(support.getName());
                supportDao.updateSupport(s);
            }
        }
        return jsupport;
    }

}
