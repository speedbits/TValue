package com.reitplace.tvalue;


public class Location {
    private String section = null;
    private String sectionValue = null;
    private String aisle = null;
    private String aisleValue = null;
    private String code = null;

    public Location() {
    }

    public Location(String code) {
        setCode(code);
    }

    public Location(String section, String sValue, String aisle, String aValue) {
        this.setSection(section);
        this.setSectionValue(sValue);
        this.setAisle(aisle);
        this.setAisleValue(aValue);
    }

    public String getCode() {
        StringBuffer buf = new StringBuffer();
        if(this.getAisle() != null) buf.append(this.getAisle());
        if(this.getSection() != null) buf.append(this.getSection());
        return buf.toString();
    }


    public void setCode(String code) {

        this.code = code;
        if (code != null && code.length() > 0) {
            switch (code.length()) {
                case 1:
                    this.setSection(String.valueOf(code.charAt(0)));
                    break;
                case 2:
                    this.setAisle(String.valueOf(code.charAt(0)));
                    this.setSection(String.valueOf(code.charAt(1)));
                    break;
            }
        }
    }

    public void reset() {
        this.setAisle(null);
        this.setSection(null);
    }

    public String toString () {
        StringBuffer buf = new StringBuffer();
        buf.append("aisle => ").append(this.getAisle()).append("-").append(this.getAisleValue()).append(", ");
        buf.append("section => ").append(this.getSection()).append("-").append(this.getSectionValue());
        return buf.toString();
    }

    public String toCompactString () {
        StringBuffer buf = new StringBuffer();
        buf.append("aisle => ").append(this.getAisle()).append("-").append(this.getAisleValue()).append("\n");
        buf.append("section => ").append(this.getSection()).append("-").append(this.getSectionValue());
        return buf.toString();
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionValue() {
        return sectionValue;
    }

    public void setSectionValue(String sectionValue) {
        this.sectionValue = sectionValue;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getAisleValue() {
        return aisleValue;
    }

    public void setAisleValue(String aisleValue) {
        this.aisleValue = aisleValue;
    }
}
