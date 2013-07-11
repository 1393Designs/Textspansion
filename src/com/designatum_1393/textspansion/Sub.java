package com.designatum_1393.textspansion;

public class Sub {
    private long id;
    private String abbrText;
    private String fullText;
    private String privacy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAbbrText() {
        return abbrText;
    }

    public String getFullText() {
        return fullText;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setAbbrText(String abbrText) {
        this.abbrText = abbrText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return fullText;
    }
}