package bzh.msansm1.trevad.server.rest.admin;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.json.admin.user.JsonAdminUser;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.model.User;
import bzh.msansm1.trevad.server.utils.Crypt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/admin/users")
@Api(value = "admin/users", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserService extends Application {

    private static final Logger LOGGER = Logger.getLogger(AdminUserService.class);

    @Inject
    UserDAO userDao;

    public AdminUserService() {
    }

    /**
     * GET /admin/users : retrieve all users
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all users", notes = "Retreive all users", response = JsonAdminUser.class, responseContainer = "List")
    public List<JsonAdminUser> getAll() {
        List<User> users = userDao.getUsers();
        LOGGER.info("find " + users.size() + " users in the database");
        List<JsonAdminUser> lu = new ArrayList<JsonAdminUser>();
        for (User u : users) {
            lu.add(new JsonAdminUser(u.getId(), u.getLogin(), u.getEmail()));
        }
        return lu;
    }

    /**
     * GET /admin/users/{id} : retrieve one user
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one user", notes = "Retrieve one user", response = JsonAdminUser.class)
    public JsonAdminUser getOne(@PathParam(value = "id") Integer id) {
        User u = userDao.getUser(id);
        LOGGER.info("find " + u.getLogin() + " user in the database");
        return new JsonAdminUser(u.getId(), u.getLogin(), u.getEmail());
    }

    /**
     * POST /admin/users : create / update one user
     * 
     * @param JsonUser
     *            user
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one user", notes = "Create / update one user", response = JsonAdminUser.class)
    public JsonAdminUser createUpdateOne(JsonAdminUser user) {
        JsonAdminUser juser = user;
        if (user.getId() == null) {
            User u = new User();
            u.setLogin(user.getLogin());
            u.setEmail(user.getEmail());
            u.setPassword(Crypt.crypt(user.getLogin(), user.getPassword()));
            userDao.saveUser(u);
            juser.setId(u.getId());
        } else {
            User u = userDao.getUser(user.getId());
            u.setLogin(user.getLogin());
            u.setEmail(user.getEmail());
            if (user.getPassword() != null) {
                u.setPassword(Crypt.crypt(user.getLogin(), user.getPassword()));
            }
            userDao.updateUser(u);
        }
        return juser;
    }

}
