package no.ngu.dspace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class Community {
    private int id;
    private String name;
    private String handle;
    private String type = "community";
    private String link;
    private List<String> expand = new ArrayList<String>(Arrays.asList("parentCommunity", "collections", "subCommunities", "logo", "all"));
    private String logo;
    private String parentCommunity = null;
    private String copyrightText = "";
    private String introductoryText = "";
    private String shortDescription = "";
    private String sidebarText = "";
    private int countItems;
    private List<Community> subcommunities = new ArrayList<Community>();
    private List<Collection> Collections = new ArrayList<Collection>();

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getParentCommunity() {
        return parentCommunity;
    }

    public void setParentCommunity(String parentCommunity) {
        this.parentCommunity = parentCommunity;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }

    public String getIntroductoryText() {
        return introductoryText;
    }

    public void setIntroductoryText(String introductoryText) {
        this.introductoryText = introductoryText;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getSidebarText() {
        return sidebarText;
    }

    public void setSidebarText(String sidebarText) {
        this.sidebarText = sidebarText;
    }

    public int getCountItems() {
        return countItems;
    }

    private void setCountItems(int countItems) {
        this.countItems = countItems;
    }


    public List<Community> getSubcommunities() {
        return subcommunities;
    }

    public void setSubcommunities(List<Community> subcommunities) {
        this.subcommunities = subcommunities;
    }

    public List<Collection> getCollections() {
        return Collections;
    }

    public void setCollections(List<Collection> collections) {
        Collections = collections;
    }
}
