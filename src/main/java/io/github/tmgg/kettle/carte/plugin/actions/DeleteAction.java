

package io.github.tmgg.kettle.carte.plugin.actions;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.StringObjectId;

import java.util.List;

public class DeleteAction {


    public void doGet(Repository repository, String id) throws KettleException {
        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }

        StringObjectId objectId = new StringObjectId(id);
        if (id.endsWith(".kjb")) {
            repository.deleteJob(objectId);
            return;
        }
        if (id.endsWith(".ktr")) {
            repository.deleteTransformation(objectId);
            return;
        }
        // 目录
        if (id.endsWith("/")) {
            RepositoryDirectoryInterface dir = repository.findDirectory(objectId);

            List<RepositoryElementMetaInterface> objs = repository.getJobAndTransformationObjects(objectId, false);
            if (objs != null && !objs.isEmpty()) {
                throw new IllegalStateException("deleteRepositoryDirectory error, has children: " + id);
            }

            repository.deleteRepositoryDirectory(dir);
        }

        throw new IllegalArgumentException("not support delete " + id);

    }


}
