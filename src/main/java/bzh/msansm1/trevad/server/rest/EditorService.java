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

import bzh.msansm1.trevad.server.json.book.JsonEditor;
import bzh.msansm1.trevad.server.persistence.dao.EditorDAO;
import bzh.msansm1.trevad.server.persistence.model.Editor;
import bzh.msansm1.trevad.server.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@ApplicationScoped
@Path(value = "/editors")
@Api(value = "editors", authorizations = { @Authorization(value = "token", scopes = {}) })
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EditorService extends Application {

    private static final Logger LOGGER = Logger.getLogger(EditorService.class);

    @Inject
    EditorDAO editorDao;

    public EditorService() {
    }

    /**
     * GET /editors : retrieve all editors
     * 
     * @return
     */
    @GET
    @ApiOperation(value = "Retreive all editors", notes = "Retreive all editors", response = JsonEditor.class, responseContainer = "List")
    public List<JsonEditor> getAll() {
        List<Editor> editors = editorDao.getEditors();
        LOGGER.info("find " + editors.size() + " editors in the database");
        ArrayList<JsonEditor> ll = new ArrayList<JsonEditor>();
        for (Editor l : editors) {
            ll.add(new JsonEditor(l.getId(), l.getName()));
        }
        return ll;
    }

    /**
     * GET /editors/{id} : retrieve one editor
     * 
     * @param id
     * @return
     */
    @GET
    @Path(value = "/{id}")
    @ApiOperation(value = "Retrieve one editor", notes = "Retrieve one editor", response = JsonEditor.class)
    public JsonEditor getOne(@PathParam(value = "id") Integer id) {
        Editor l = editorDao.getEditor(id);
        LOGGER.info("find " + l.getName() + " editor in the database");
        return new JsonEditor(l.getId(), l.getName());
    }

    /**
     * POST /editors : create / update one editor
     * 
     * @param JsonEditor
     *            editor
     * @return
     */
    @POST
    @ApiOperation(value = "Create / update one editor", notes = "Create / update one editor", response = JsonEditor.class)
    @Transactional(rollbackOn = Exception.class)
    public JsonEditor createUpdateOne(JsonEditor editor) {
        JsonEditor jeditor = editor;
        if (editor.getId() == null) {
            Editor l = new Editor();
            l.setName(editor.getName());
            editorDao.saveEditor(l);
            jeditor.setId(l.getId());
        } else {
            if (editor.getName().equalsIgnoreCase(Constants.DELETED)) {
                editorDao.removeEditor(editorDao.getEditor(editor.getId()));
            } else {
                Editor l = editorDao.getEditor(editor.getId());
                l.setName(editor.getName());
                editorDao.updateEditor(l);
            }
        }
        return jeditor;
    }

}
