package no.ngu.dspace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class Collection {
    private String uuid;
    private String name;
    private String handle;
    private String type = "collection";
    private String link;
    private List<String> expand = new ArrayList<String>(Arrays.asList("parentCommunityList","parentcommunity","items","license","logo","all"));
    private String logo = null;
    private Community parentCommunity = null;
    private List<Community> parentCommunityList = new ArrayList<Community>();
    private List<Item> items = new ArrayList<Item>();
    private String license = null;
    private String copyrightText = "";
    private String introductoryText = "";
    private String shortDescription = "";
    private String sidebarText = "";
    private int numberItems = getItems().size();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Community getParentCommunity() {
        return parentCommunity;
    }

    public void setParentCommunity(Community parentCommunity) {
        this.parentCommunity = parentCommunity;
    }

    public List<Community> getParentCommunityList() {
        return parentCommunityList;
    }

    public void setParentCommunityList(List<Community> parentCommunityList) {
        this.parentCommunityList = parentCommunityList;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }
}