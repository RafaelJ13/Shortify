package dev.rafaelj13.shortify.dto;

public class LinkResponse {
    private String shortUrl;

    public LinkResponse() {
    }

    public LinkResponse(String shortUrl, Integer id) {
        this.shortUrl = shortUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

}
