package io.github.tmgg.kettle.carte.plugin;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepTreeItem {
    String id;
    String pid;
    String name;




    // more info
    Date modifiedDate;



    List<RepTreeItem> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public List<RepTreeItem> getChildren() {
        return children;
    }

    public void setChildren(List<RepTreeItem> children) {
        this.children = children;
    }







    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return "RepoTreeItem{" +
               "id='" + id + '\'' +
               ", pid='" + pid + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
