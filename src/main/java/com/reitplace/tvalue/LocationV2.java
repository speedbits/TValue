package com.reitplace.tvalue;

public class LocationV2 {
    private String secondary = null;
    private String secondaryValue = null;
    private String primary = null;
    private String primaryValue = null;

    public LocationV2() {
    }

    public LocationV2(String primary, String primaryValue, String secondary, String secondaryValue) {
        this.setPrimary(primary);
        this.setPrimaryValue(primaryValue);
        this.setSecondary(secondary);
        this.setSecondaryValue(secondaryValue);
    }

    public String getCode() {
        StringBuffer buf = new StringBuffer();
        if(this.getPrimary() != null) buf.append(this.getPrimary());
        if(this.getSecondary() != null) buf.append(this.getSecondary());
        return buf.toString();
    }


    public boolean isValid() {
        boolean isValid = false;
        if(this.primary != null && this.getPrimaryValue() != null) isValid = true;
        return isValid;
    }


    public void reset() {
        this.setPrimary(null);
        this.setSecondary(null);
    }

    public String toString () {
        StringBuffer buf = new StringBuffer();
        buf.append("aisle => ").append(this.getPrimary()).append("-").append(this.getPrimaryValue()).append(", ");
        buf.append("section => ").append(this.getSecondary()).append("-").append(this.getSecondaryValue());
        return buf.toString();
    }

    public String toDisplay () {
        StringBuffer buf = new StringBuffer();
        buf.append("Location: Primary");
        if (this.getPrimary() != null) buf.append("(").append(this.getPrimary()).append("-").append(this.getPrimaryValue()).append("), ");
        else buf.append("(None)");
        if (this.getSecondary() != null) {
            buf.append("Secondary(").append(this.getSecondary()).append("-").append(this.getSecondaryValue()).append(") ");
        }

        return buf.toString();
    }

    public String toSearchOutput (Config config) {
        StringBuffer buf = new StringBuffer();

        if (this.getPrimary() != null) {
            buf.append("(").append(config.locationCodeMap.get(getPrimary())).append("-").append(this.getPrimaryValue()).append(")");
        } else buf.append("(None)");

        if (this.getSecondary() != null) {
            buf.append(", (").append(config.locationCodeMap.get(getSecondary())).append("-").append(this.getSecondaryValue()).append(")");
        }

        return buf.toString();
    }

    public String toCompactString () {
        StringBuffer buf = new StringBuffer();
        buf.append("aisle => ").append(this.getPrimary()).append("-").append(this.getPrimaryValue()).append("\n");
        buf.append("section => ").append(this.getSecondary()).append("-").append(this.getSecondaryValue());
        return buf.toString();
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(String secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(String primaryValue) {
        this.primaryValue = primaryValue;
    }
}
