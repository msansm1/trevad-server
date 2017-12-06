package bzh.msansm1.trevad.server.rest;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import bzh.msansm1.trevad.server.json.auth.JsonAuth;
import bzh.msansm1.trevad.server.json.auth.JsonLogin;
import bzh.msansm1.trevad.server.persistence.dao.UserDAO;
import bzh.msansm1.trevad.server.persistence.model.User;
import bzh.msansm1.trevad.server.utils.Crypt;

/**
 * Auth service (/services/auth)
 * 
 * @author ebarona
 * 
 */
@ApplicationScoped
@ApplicationPath("/services")
@Path(value = "/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthService extends Application {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class);

    @Inject
    UserDAO userDao;

    public AuthService() {
    }

    /**
     * POST /login : login for user
     * 
     * @return
     */
    @POST
    @Path("/login")
    @Transactional(rollbackOn = Exception.class)
    public Response loginUser(@Context HttpServletRequest request, JsonLogin jlogin) {
        User u = userDao.getUserByLogin(jlogin.getLogin());
        if (u != null) {
            LOGGER.info("Login to connect : " + jlogin.getLogin());
            if (Crypt.crypt(jlogin.getLogin(), jlogin.getPassword()).equals(u.getPassword())) {
                String token = UUID.randomUUID().toString();
                u.setToken(token);
                userDao.updateUser(u);
                return Response.ok(new JsonAuth(u.getId(), u.getLogin(), u.getEmail(), null, null, null, token),
                        MediaType.APPLICATION_JSON).build();
            } else {
                LOGGER.info("!!!  Wrong password");
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Wrong Login or Password").build();
    }

    /**
     * POST /login/mobile : login for user
     * 
     * @return
     */
    @POST
    @Path("/login/mobile")
    @Transactional(rollbackOn = Exception.class)
    public Response loginMobileUser(@Context HttpServletRequest request, JsonLogin jlogin) {
        User u = userDao.getUserByLogin(jlogin.getLogin());
        if (u != null) {
            LOGGER.info("Login to connect : " + jlogin.getLogin());
            if (Crypt.crypt(jlogin.getLogin(), jlogin.getPassword()).equals(u.getPassword())) {
                String token = UUID.randomUUID().toString();
                u.setMobileToken(token);
                userDao.updateUser(u);
                return Response.ok(new JsonAuth(u.getId(), u.getLogin(), u.getEmail(), null, null, null, token),
                        MediaType.APPLICATION_JSON).build();
            } else {
                LOGGER.info("!!!  Wrong password");
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Wrong Login or Password").build();
    }

    /**
     * POST /logout : logout for user
     * 
     * @return
     */
    @POST
    @Path("/logout")
    public String logoutUser(JsonAuth user) {
        User u = userDao.getUser(user.getId().intValue());
        u.setToken(null);
        userDao.updateUser(u);
        return "ok";
    }

    /**
     * POST /logout/mobile : logout for user
     * 
     * @return
     */
    @POST
    @Path("/logout/mobile")
    public String logoutMobileUser(JsonAuth user) {
        User u = userDao.getUser(user.getId().intValue());
        u.setMobileToken(null);
        userDao.updateUser(u);
        return "ok";
    }

}
