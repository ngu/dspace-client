package no.ngu.dspace.model;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class ResourcePolicy {
    private int id;
    private String action;
    private String epersonId;
    private String groupId;
    private String resourceId;
    private String resourceType;
    private String rpDescription;
    private String rpName;
    private String rpType;
    private String startDate;
    private String endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEpersonId() {
        return epersonId;
    }

    public void setEpersonId(String epersonId) {
        this.epersonId = epersonId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getRpDescription() {
        return rpDescription;
    }

    public void setRpDescription(String rpDescription) {
        this.rpDescription = rpDescription;
    }

    public String getRpName() {
        return rpName;
    }

    public void setRpName(String rpName) {
        this.rpName = rpName;
    }

    public String getRpType() {
        return rpType;
    }

    public void setRpType(String rpType) {
        this.rpType = rpType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
