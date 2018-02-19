package bzh.msansm1.trevad.server.rest.admin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.db.DatabaseMigrationService;
import bzh.msansm1.trevad.server.json.JsonSimpleResponse;

/**
 * Setup service (/api/V1/setupApp)
 * 
 * @author ebarona
 * 
 */
@ApplicationScoped
@Path(value = "/setupApp")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SetupService extends Application {

    private static final Logger LOGGER = Logger.getLogger(SetupService.class);

    @Inject
    DatabaseMigrationService dbMigrationService;

    public SetupService() {
    }

    /**
     * GET /initdb
     * 
     * @return
     */
    @GET
    @Path("/initdb")
    public JsonSimpleResponse userStats(@Context HttpServletRequest request) {
        dbMigrationService.performDatabaseMigration();
        return new JsonSimpleResponse(true);
    }

}
