package dev.rafaelj13.shortify.dto;

public class LinkRequest {
    private String url;

    public LinkRequest() {
    }

    public LinkRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
