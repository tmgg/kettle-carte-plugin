

package io.github.tmgg.kettle.carte.plugin;

import io.github.tmgg.kettle.carte.plugin.actions.ContentAction;
import io.github.tmgg.kettle.carte.plugin.actions.DeleteAction;
import io.github.tmgg.kettle.carte.plugin.actions.TreeAction;
import io.github.tmgg.kettle.carte.plugin.actions.UploadAction;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.www.*;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class RepServlet extends BaseHttpServlet implements CartePluginInterface {

    public static final String CONTEXT_PATH = "/kettle/plugin-repository-object";


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String contextPath = request.getContextPath();
        if (isJettyMode() && !contextPath.startsWith(CONTEXT_PATH)) {
            return;
        }


        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/xml");
        response.setCharacterEncoding(Const.XML_ENCODING);

        PrintWriter out = response.getWriter();
        out.print(XMLHandler.getXMLHeader(Const.XML_ENCODING));

        String repOption = request.getParameter("rep");


        try {
            Repository repository = RepUtils.openRepository(repOption);
            if (repository == null) {
                out.println(new WebResult(WebResult.STRING_ERROR, "repository [" + repOption + "] not found"));
                return;
            }

            String action = request.getRequestURI().replace(contextPath, "").replace("/", "");

            switch (action) {
                case "delete":
                    new DeleteAction().doGet(repository, request.getParameter("id"));
                    out.println(WebResult.OK);
                    break;
                case "upload":
                    final String xml = IOUtils.toString(request.getInputStream());
                    new UploadAction().doGet(repository, xml);
                    out.println(WebResult.OK);
                    break;
                case "tree":
                    String treeXml = new TreeAction().doGet(repository);
                    out.println(treeXml);
                    break;
                case "content":
                    String contentXml = new ContentAction().doGet(repository, request.getParameter("id"));
                    out.println(contentXml);
                    break;
            }
            repository.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            out.println(new WebResult(WebResult.STRING_ERROR, e.getMessage()));
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
