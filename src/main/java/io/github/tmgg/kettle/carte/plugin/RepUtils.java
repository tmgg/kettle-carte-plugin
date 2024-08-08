package io.github.tmgg.kettle.carte.plugin;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.*;

import java.util.List;

public class RepUtils {

    public static void getDirectoryTree(List<RepTreeItem> containers, RepositoryDirectoryInterface dir, Repository repository) {
        containers.add(newItem(dir));


        // 文件夹
        for (int i = 0; i < dir.getNrSubdirectories(); i++) {
            RepositoryDirectory subdir = dir.getSubdirectory(i);
            getDirectoryTree(containers, subdir, repository);
        }


        // 文件
        List<RepositoryElementMetaInterface> files = RepUtils.loadRepositoryObjects(dir, repository);
        for (RepositoryElementMetaInterface file : files) {
            RepTreeItem item = new RepTreeItem();
            item.setId(file.getObjectId().getId());
            item.setName(file.getName());
            item.setModifiedDate(file.getModifiedDate());
            item.setPid(dir.getObjectId().getId());

            containers.add(item);
        }


    }

    private static RepTreeItem newItem(RepositoryDirectoryInterface dir) {
        RepTreeItem item = new RepTreeItem();
        item.setName(dir.getName());
        item.setId(dir.getObjectId().getId());
        if (dir.getParent() != null) {
            item.setPid(dir.getParent().getObjectId().getId());
        }


        return item;
    }

    public static List<RepositoryElementMetaInterface> loadRepositoryObjects(RepositoryDirectoryInterface dir, Repository rep) {
        if (dir.getRepositoryObjects() == null) {
            try {
                dir.setRepositoryObjects(rep.getJobAndTransformationObjects(dir.getObjectId(), false));
            } catch (KettleException e) {
                e.printStackTrace();
            }
        }


        return dir.getRepositoryObjects();
    }


    public static Repository openRepository(String repositoryName) throws KettleException {
        if (Utils.isEmpty(repositoryName)) {
            return null;
        }

        RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
        repositoriesMeta.readData();
        RepositoryMeta repositoryMeta = repositoriesMeta.findRepository(repositoryName);
        if (repositoryMeta == null) {
            throw new KettleRepositoryNotFoundException("UnableToFindRepository " + repositoryName);
        }
        PluginRegistry registry = PluginRegistry.getInstance();
        Repository repository = registry.loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
        repository.init(repositoryMeta);
        repository.connect(null, null);
        return repository;
    }

    public static void createDir(String directoryPath, Repository rep) throws KettleException {
        RepositoryDirectoryInterface directory = rep.findDirectory(directoryPath);
        if (directory != null) {
            return;
        }
        rep.createRepositoryDirectory(rep.loadRepositoryDirectoryTree(), directoryPath);
    }
}
