

package io.github.tmgg.kettle.carte.plugin.actions;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;

public class ContentAction {


    public String doGet(Repository repository, String id) throws
            KettleException {
        StringObjectId oid = new StringObjectId(id);

        if (id.endsWith(".kjb")) {
            JobMeta meta = repository.loadJob(oid, null);
            return meta.getXML();
        }

        if (id.endsWith(".ktr")) {
            TransMeta meta = repository.loadTransformation(oid, null);
            return meta.getXML();
        }


        throw new IllegalArgumentException("not support " + id);
    }


}
