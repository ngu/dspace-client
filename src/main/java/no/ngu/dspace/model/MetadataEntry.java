package no.ngu.dspace.model;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class MetadataEntry {
    private String key;
    private String value;
    private String language;

    public MetadataEntry(){};

    public MetadataEntry(String key, String value) {
        this.key=key;
        this.value = value;
    }
    public MetadataEntry(String key, String value, String language) {
        this.key=key;
        this.value = value;
        this.language = language;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
