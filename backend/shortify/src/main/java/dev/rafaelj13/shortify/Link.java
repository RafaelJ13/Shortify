package dev.rafaelj13.shortify;

import java.util.Date;

public class Link {
    private int id;
    private String original_Link;
    private int clicks;
    private Date created_at;
    private boolean isDeleted;

    public Link(int id, String original_Link, int clicks, Date created_at, boolean isDeleted) {
        this.id = id;
        this.original_Link = original_Link;
        this.clicks = clicks;
        this.created_at = created_at;
        this.isDeleted = isDeleted;
    }
    public Link(Link originalLink) {
        this.id = originalLink.getId();
        this.original_Link = originalLink.getOriginal_Link();
        this.clicks = originalLink.getClicks();
        this.created_at = originalLink.getCreated_at();
        this.isDeleted = originalLink.isDeleted();
    }
    public Link(String original_Link) {
        this.original_Link = original_Link;
        this.clicks = 0;
        this.created_at = new Date();
        this.isDeleted = false;

    }
    public int getId() {
        return id;
    }
    public String getOriginal_Link() {
        return original_Link;
    }
    public int getClicks() {
        return clicks;
    }
    public Date getCreated_at() {
        return created_at;
    }
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public void incrementClicks() {
        this.clicks++;
    }
}
