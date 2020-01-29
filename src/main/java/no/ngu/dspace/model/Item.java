package no.ngu.dspace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class Item {
    private int id;
    private String name;
    private String handle;
    private String type = "item";
    private String link = "";
    private List<String> expand = new ArrayList<String>(Arrays.asList("metadata", "parentCollection", "parentCollectionList", "parentCommunityList", "bitstreams", "all"));
    private String lastModified = "";
    private Collection parentCollection = null;
    private List<Collection> parentCollectionList = null;
    private List<Community> parentCommunityList = null;
    private Bitstream bitstream = null;
    private String archived;
    private String withdrawn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getExpand() {
        return expand;
    }

    public void setExpand(List<String> expand) {
        this.expand = expand;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Collection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(Collection parentCollection) {
        this.parentCollection = parentCollection;
    }

    public List<Collection> getParentCollectionList() {
        return parentCollectionList;
    }

    public void setParentCollectionList(List<Collection> parentCollectionList) {
        this.parentCollectionList = parentCollectionList;
    }

    public List<Community> getParentCommunityList() {
        return parentCommunityList;
    }

    public void setParentCommunityList(List<Community> parentCommunityList) {
        this.parentCommunityList = parentCommunityList;
    }

    public Bitstream getBitstream() {
        return bitstream;
    }

    public void setBitstream(Bitstream bitstream) {
        this.bitstream = bitstream;
    }

    public String getArchived() {
        return archived;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(String withdrawn) {
        this.withdrawn = withdrawn;
    }
}
