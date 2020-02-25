package no.ngu.dspace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bitstream {
    private String uuid;
    private String name;
    private String type = "bitstream";
    private String link = "";
    private List<String> expand = new ArrayList<String>(Arrays.asList("parent","policies","all"));
    private String description = "";
    private String format;
    private String mimeType;
    private int sizeBytes;
    private Bitstream parentObject = null;
    private String retrieveLink;
    private Checksum checkSum;
    private int sequenceId;
    private String policies = null;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Bitstream getParentObject() {
        return parentObject;
    }

    public void setParentObject(Bitstream parentObject) {
        this.parentObject = parentObject;
    }

    public String getRetrieveLink() {
        return retrieveLink;
    }

    public void setRetrieveLink(String retrieveLink) {
        this.retrieveLink = retrieveLink;
    }

    public Checksum getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(Checksum checkSum) {
        this.checkSum = checkSum;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getPolicies() {
        return policies;
    }

    public void setPolicies(String policies) {
        this.policies = policies;
    }
}
