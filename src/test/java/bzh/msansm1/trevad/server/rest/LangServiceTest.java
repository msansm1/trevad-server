package bzh.msansm1.trevad.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import bzh.msansm1.trevad.server.json.JsonLang;
import bzh.msansm1.trevad.server.utils.Constants;
import bzh.msansm1.trevad.server.utils.TestConstants;
import bzh.msansm1.trevad.server.utils.TestUtils;

/**
 * Test class for lang REST service
 * 
 * @author msansm1
 *
 */
@RunWith(Arquillian.class)
// Run the tests of the class as a client
@RunAsClient
public class LangServiceTest {
    private static final Logger LOGGER = Logger.getLogger(LangServiceTest.class);

    private static final String svc_root = "/api/v1/langs";

    // testable = false => it's for testing as a client (we don't test inside
    // the app)
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        // creation of the war for testing
        final WebArchive war = TestUtils.getWarFile("testwar");
        LOGGER.info(war.toString(Formatters.VERBOSE));
        return war;
    }

    /**
     * Test for /services/langs GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(1)
    public void callGetList() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonLang> response = client.target(TestConstants.SERVER_ROOT + svc_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);
        assertFalse("No lang found", response.isEmpty());
    }

    /**
     * Test for /services/langs/{id} GET Test OK
     * 
     * @throws Exception
     */
    @Test
    @InSequence(2)
    public void callGetOne() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_root + "/1")
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(JsonLang.class);
        assertEquals(Integer.valueOf(1), response.getId());
    }

    /**
     * Test for /services/langs POST Test OK creation
     * 
     * @throws Exception
     */
    @Test
    @InSequence(3)
    public void callCreate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLang lang = new JsonLang(null, "testrest");

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals("testrest", response.getName());
    }

    /**
     * Test for /services/langs POST Test OK update
     * 
     * @throws Exception
     */
    @Test
    @InSequence(4)
    public void callUpdate() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);
        JsonLang lang = new JsonLang(1, "msansm1");

        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals("msansm1", response.getName());
    }

    /**
     * Test for /services/langs POST Test OK delete
     * 
     * @throws Exception
     */
    @Test
    @InSequence(5)
    public void callDelete() throws Exception {
        Client client = ClientBuilder.newClient().register(ResteasyJackson2Provider.class);

        @SuppressWarnings("unchecked")
        List<JsonLang> listbefore = client.target(TestConstants.SERVER_ROOT + svc_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        JsonLang lang = new JsonLang(3, Constants.DELETED);
        JsonLang response = client.target(TestConstants.SERVER_ROOT + svc_root).request(MediaType.APPLICATION_JSON)
                .header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .post(Entity.entity(lang, MediaType.APPLICATION_JSON), JsonLang.class);
        assertEquals(Constants.DELETED, response.getName());

        @SuppressWarnings("unchecked")
        List<JsonLang> listafter = client.target(TestConstants.SERVER_ROOT + svc_root)
                .request(MediaType.APPLICATION_JSON).header(Constants.HTTP_HEADER_TOKEN, TestConstants.USER_TOKEN)
                .get(List.class);

        assertEquals(listbefore.size() - 1, listafter.size());
    }

}
