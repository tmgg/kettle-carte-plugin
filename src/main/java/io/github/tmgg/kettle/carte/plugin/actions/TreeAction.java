

package io.github.tmgg.kettle.carte.plugin.actions;

import io.github.tmgg.kettle.carte.plugin.RepTreeItem;
import io.github.tmgg.kettle.carte.plugin.RepUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TreeAction {


    public static final String XML_TAG = "reps";


    public String doGet(Repository repository) throws
            KettleException {

        RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree();

        List<RepTreeItem> treeList = new ArrayList<>();
        RepUtils.getDirectoryTree(treeList, directory, repository);


        String xml = this.createXml(treeList);

        return xml;

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




}
