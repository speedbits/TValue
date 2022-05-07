package com.reitplace.tvalue;

public class Location4 {
    private String locT1 = null;
    private String locT1Value = null;
    private String locT2 = null;
    private String locT2Value = null;
    private String locT3 = null;
    private String locT3Value = null;
    private String code = null;

    public Location4() {
    }

    public Location4(String code) {
        setCode(code);
    }

    public Location4(String locT1, String locT2, String locT3) {
        this.locT1 = locT1;
        this.locT2 = locT2;
        this.locT3 = locT3;
    }

    public String getLocT1() {
        return locT1;
    }

    public void setLocT1(String locT1) {
        this.locT1 = locT1;
    }

    public String getLocT2() {
        return locT2;
    }

    public void setLocT2(String locT2) {
        this.locT2 = locT2;
    }

    public String getLocT3() {
        return locT3;
    }

    public void setLocT3(String locT3) {
        this.locT3 = locT3;
    }

    public String getCode() {
        StringBuffer buf = new StringBuffer();
        if(this.locT1 != null) buf.append(this.locT1);
        if(this.locT2 != null) buf.append(this.locT2);
        if(this.locT3 != null) buf.append(this.locT3);
        return buf.toString();
    }

    public void setTag(String tag, String value) throws Exception {
        if (this.locT1 == null || "".equals(this.locT1)) {
            this.locT1 = tag;
            this.setLocT1Value(value);
        } else if (this.locT2 == null || "".equals(this.locT2)) {
            this.locT2 = tag;
            this.setLocT2Value(value);
        } else if (this.locT3 == null || "".equals(this.locT3)) {
            this.locT3 = tag;
            this.setLocT3Value(value);
        } else {
            throw new Exception("Number of tags exceeded for the location!");
        }

    }
    public void setCode(String code) {

        this.code = code;
        if (code != null && code.length() > 0) {
            switch (code.length()) {
                case 1:
                    this.locT1 = String.valueOf(code.charAt(0));
                    break;
                case 2:
                    this.locT1 = String.valueOf(code.charAt(0));
                    this.locT2 = String.valueOf(code.charAt(1));
                    break;
                case 3:
                    this.locT1 = String.valueOf(code.charAt(0));
                    this.locT2 = String.valueOf(code.charAt(1));
                    this.locT3 = String.valueOf(code.charAt(2));
                    break;
            }
        }
    }

    public void reset() {
        this.locT1 = null;
        this.locT2 = null;
        this.locT3 = null;
    }

    public String toString () {
        StringBuffer buf = new StringBuffer();
        buf.append("locT1 => ").append(this.locT1).append(", ");
        buf.append("locT2 => ").append(this.locT2).append(", ");
        buf.append("locT3 => ").append(this.locT3);
        return buf.toString();
    }

    public String getLocT1Value() {
        return locT1Value;
    }

    public void setLocT1Value(String locT1Value) {
        this.locT1Value = locT1Value;
    }

    public String getLocT2Value() {
        return locT2Value;
    }

    public void setLocT2Value(String locT2Value) {
        this.locT2Value = locT2Value;
    }

    public String getLocT3Value() {
        return locT3Value;
    }

    public void setLocT3Value(String locT3Value) {
        this.locT3Value = locT3Value;
    }
}
