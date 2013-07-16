package com.designatum_1393.textspansion;

public class Sub {
    private long id;
    private String pasteText;
    private String subTitle;
    private String privacy;

    public Sub() {}

    public Sub(String subTitle, String pasteText, boolean privacy) {
        this.subTitle = subTitle;
        this.pasteText = pasteText;
        if (privacy)
            this.privacy = "1";
        else
            this.privacy = "0";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPasteText() {
        return pasteText;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPasteText(String pasteText) {
        this.pasteText = pasteText;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return subTitle;
    }
}