package no.ngu.dspace.model;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 * Simplified from:
 * https://github.com/DSpace/DSpace/blob/master/dspace-rest/src/main/java/org/dspace/rest/common/Status.java
 */
public class Status {
    private boolean okay;
    private boolean authenticated;
    private String email;
    private String fullname;
    private String sourceVersion;
    private String apiVersion;

    public boolean getOkay() {
        return okay;
    }
    public boolean isOkay() {return okay;}
    public void setOkay(boolean okay) {
        this.okay = okay;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }
}
