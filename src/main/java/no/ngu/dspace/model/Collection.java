package no.ngu.dspace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class Collection {
    private int id;
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
    private int numberItems = items.size();
}
