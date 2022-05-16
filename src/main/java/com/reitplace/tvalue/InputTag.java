package com.reitplace.tvalue;

public class InputTag {
    private int tagType = Constants.TagTypeInvalid;
    private Object inputObject = null;

    public InputTag() {

    }
    public InputTag(int tagType, Object inputObject) {
        this.tagType = tagType;
        this.inputObject = inputObject;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public Object getInputObject() {
        return inputObject;
    }

    public void setInputObject(Object inputObject) {
        this.inputObject = inputObject;
    }

    @Override
    public String toString() {
        String str = ""+ tagType;
        if(inputObject != null) str = str + " -> " +inputObject.toString();
        return str;
    }
}
