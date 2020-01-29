package no.ngu.dspace.model;

/**
 * https://wiki.lyrasis.org/display/DSDOC5x/REST+API#RESTAPI-Model-Objectdatatypes
 */
public class Checksum {
    private String value;
    private String checkSumAlgorithm;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCheckSumAlgorithm() {
        return checkSumAlgorithm;
    }

    public void setCheckSumAlgorithm(String checkSumAlgorithm) {
        this.checkSumAlgorithm = checkSumAlgorithm;
    }
}
