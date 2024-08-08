

package io.github.tmgg.kettle.carte.plugin;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.*;
import org.pentaho.di.www.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DeleteRepObjectServlet extends BaseHttpServlet implements CartePluginInterface {

    private static Class<?> PKG = DeleteRepObjectServlet.class; // i18n


    public static final String CONTEXT_PATH = "/kettle/deleteRepObject";

    public static final String XML_TAG = "reps";

    public DeleteRepObjectServlet() {
    }

    public DeleteRepObjectServlet(TransformationMap transformationMap, JobMap jobMap) {
        super(transformationMap, jobMap);
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (isJettyMode() && !request.getContextPath().startsWith(CONTEXT_PATH)) {
            return;
        }


        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/xml");
        response.setCharacterEncoding(Const.XML_ENCODING);

        PrintWriter out = response.getWriter();
        out.print(XMLHandler.getXMLHeader(Const.XML_ENCODING));


        String repOption = request.getParameter("rep");
        String id = request.getParameter("id");

        Repository repository;
        try {
            repository = RepUtils.openRepository(repOption);
        } catch (KettleRepositoryNotFoundException krnfe) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new WebResult(WebResult.STRING_ERROR, "UnableToFindRepository" + repOption));
            return;
        } catch (KettleException ke) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new WebResult(WebResult.STRING_ERROR, Const.getStackTracker(ke)));
            return;
        }


        try {
            StringObjectId objectId = new StringObjectId(id);
            if(id.endsWith(".kjb")){
                repository.deleteJob(objectId);
                out.println(new WebResult(WebResult.STRING_OK, "deleteJob OK"));
            }else if(id.endsWith(".ktr")) {

                repository.deleteTransformation(objectId);
                out.println(new WebResult(WebResult.STRING_OK, "deleteTransformation OK"));
            }
            else if(id.endsWith("/")){
                RepositoryDirectoryInterface dir = repository.findDirectory(objectId);

                List<RepositoryElementMetaInterface> objs = repository.getJobAndTransformationObjects(objectId, false);
                if(objs != null && !objs.isEmpty()){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(new WebResult(WebResult.STRING_ERROR, "deleteRepositoryDirectory error, has children objects " + id));
                }

                repository.deleteRepositoryDirectory(dir);
                out.println(new WebResult(WebResult.STRING_OK, "deleteRepositoryDirectory OK"));
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new WebResult(WebResult.STRING_ERROR, "not support delete " + id));
            }

        } catch (KettleException e) {
            throw new ServletException("Unable to get the server status in XML format", e);
        }
    }





    public String getContextPath() {
        return CONTEXT_PATH;
    }


    public String getService() {
        return getContextPath() + " (" + toString() + ")";
    }

    public String toString() {
        return getClass().getSimpleName();
    }

}
