

package io.github.tmgg.kettle.carte.plugin;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.KettleAuthenticationException;
import org.pentaho.di.repository.KettleRepositoryNotFoundException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.www.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetRepObjectsServlet extends BaseHttpServlet implements CartePluginInterface {

    private static Class<?> PKG = GetRepObjectsServlet.class; // i18n


    public static final String CONTEXT_PATH = "/kettle/getRepObjects";

    public static final String XML_TAG = "reps";

    public GetRepObjectsServlet() {
    }

    public GetRepObjectsServlet(TransformationMap transformationMap, JobMap jobMap) {
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
        Repository repository;
        try {
            repository = RepUtils.openRepository(repOption);
        } catch (KettleRepositoryNotFoundException krnfe) {
            // Repository not found.
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            String message = BaseMessages.getString(PKG, "RepoServlet.Error.UnableToFindRepository", repOption);
            out.println(new WebResult(WebResult.STRING_ERROR, message));
            return;
        } catch (KettleException ke) {
            // Authentication Error.
            if (ke.getCause() instanceof ExecutionException) {
                ExecutionException ee = (ExecutionException) ke.getCause();
                if (ee.getCause() instanceof KettleAuthenticationException) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    String message = BaseMessages.getString(PKG, "RepoServlet.Error.Authentication", getContextPath());
                    out.println(new WebResult(WebResult.STRING_ERROR, message));
                    return;
                }
            }

            // Something unexpected occurred.
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String message = BaseMessages.getString(
                    PKG, "RepoServlet.Error.UnexpectedError", Const.CR + Const.getStackTracker(ke));
            out.println(new WebResult(WebResult.STRING_ERROR, message));
            return;
        }


        try {

            RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree();

            List<RepTreeItem> treeList = new ArrayList<>();
            RepUtils.getDirectoryTree(treeList, directory, repository);



            String xml = this.createXml(treeList);

            out.println(xml);
        } catch (KettleException e) {
            throw new ServletException("Unable to get the server status in XML format", e);
        }
    }

    public String createXml(List<RepTreeItem> list) {
        StringBuilder xml = new StringBuilder();

        xml.append(XMLHandler.openTag(XML_TAG)).append(Const.CR);

        for (RepTreeItem item : list) {
            xml.append(XMLHandler.openTag("item")).append(Const.CR);
            xml.append("  ").append(XMLHandler.addTagValue("id", item.getId()));
            xml.append("  ").append(XMLHandler.addTagValue("pid", item.getPid()));
            xml.append("  ").append(XMLHandler.addTagValue("name", item.getName()));
            xml.append("  ").append(XMLHandler.addTagValue("modifiedDate", XMLHandler.date2string(item.getModifiedDate())));
            xml.append(XMLHandler.closeTag("item")).append(Const.CR);

        }


        xml.append(XMLHandler.closeTag(XML_TAG));


        return xml.toString();
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
