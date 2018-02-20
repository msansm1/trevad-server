package bzh.msansm1.trevad.server.rest.admin;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.json.admin.JsonConfParam;
import bzh.msansm1.trevad.server.persistence.dao.ConfigurationDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/admin/conf")
@Api(value = "admin/conf", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationService extends Application {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationService.class);

    @Inject
    ConfigurationDAO configurationDAO;

    public ConfigurationService() {
        // empty constructor
    }

    /**
     * GET /admin/config : retrieve all configuration parameters
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all configuration parameters", notes = "Retreive all configuration parameters", response = JsonConfParam.class, responseContainer = "List")
    public List<JsonConfParam> getAll() {
        return configurationDAO.getJsonConf();
    }

}
