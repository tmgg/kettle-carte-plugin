

package io.github.tmgg.kettle.carte.plugin.actions;

import io.github.tmgg.kettle.carte.plugin.RepUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class UploadAction {


    public void doGet(Repository repository, String xml) throws
            KettleException {

        Document doc = XMLHandler.loadXMLString(xml);


        Node jobNode = XMLHandler.getSubNode(doc, JobMeta.XML_TAG);
        Node transNode = XMLHandler.getSubNode(doc, TransMeta.XML_TAG);
        Node node = jobNode != null ? jobNode : transNode;
        if (node == null) {
            throw new IllegalArgumentException("file content not valid");
        }

        String directoryPath = XMLHandler.getTagValue(node, "directory");
        if (directoryPath != null) {
            RepUtils.createDir(directoryPath, repository);
        }


        if (jobNode != null) {
            JobMeta meta = new JobMeta(jobNode, repository, null);
            repository.save(meta, null, null);
        }else {
            TransMeta meta = new TransMeta(transNode, repository);
            repository.save(meta, null, null);
        }

    }


}
