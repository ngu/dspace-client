package no.ngu.dspace;

import java.util.ArrayList;
import java.util.List;

import no.ngu.dspace.model.Collection;
import no.ngu.dspace.model.Community;
import no.ngu.dspace.model.Item;

/**
 * https://wiki.lyrasis.org/display/DSDOC6x/REST+API
 */
public class DSpaceClient {

    // Constructor
    public DSpaceClient() {
    }

    public List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<Collection>();
        return collections;
    }

    public List<Community> getCommunities() {
        List<Community> communities = new ArrayList<Community>();
        return communities;
    }

    public List<Item> getItemsByCommunitiy(Community community) {
        Item item = new Item();
        return item;
    }

}