

package io.github.tmgg.kettle.carte.plugin;

import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.KettleRepositoryNotFoundException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.BaseHttpServlet;
import org.pentaho.di.www.CartePluginInterface;
import org.pentaho.di.www.WebResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UploadRepObjectServlet extends BaseHttpServlet implements CartePluginInterface {


    public static final String CONTEXT_PATH = "/kettle/uploadRepObject";


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

        Repository rep;
        try {
            rep = RepUtils.openRepository(repOption);
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
            final String xml = IOUtils.toString(request.getInputStream());

            Document doc = XMLHandler.loadXMLString(xml);


            Node jobNode = XMLHandler.getSubNode(doc, JobMeta.XML_TAG);
            Node transNode = XMLHandler.getSubNode(doc, TransMeta.XML_TAG);
            Node node = jobNode != null ? jobNode : transNode;
            if (node == null) {
                out.println(new WebResult(WebResult.STRING_ERROR, "file content not valid"));
                return;
            }

            String directoryPath = XMLHandler.getTagValue(node, "directory");
            if (directoryPath != null) {
                RepUtils.createDir(directoryPath, rep);
            }


            RepositoryElementInterface meta = null;
            if (jobNode != null) {
                meta = new JobMeta(jobNode, rep, null);
            } else if (transNode != null) {
                meta = new TransMeta(transNode, rep);
            }


            rep.save(meta, null, null);
            out.println(new WebResult(WebResult.STRING_OK, "save obj " + meta.getName() + " OK"));


        } catch (KettleException e) {
            throw new ServletException("Unable to get the server status in XML format", e);
        }
    }


    public String toString() {
        return getClass().getSimpleName();
    }

    public String getService() {
        return CONTEXT_PATH + " (" + toString() + ")";
    }

    public String getContextPath() {
        return CONTEXT_PATH;
    }


}
