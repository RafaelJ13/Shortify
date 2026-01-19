package dev.rafaelj13.shortify.dto;

public class LinkResponse {
    private String shortUrl;
    private Integer id;

    public LinkResponse() {
    }

    public LinkResponse(String shortUrl, Integer id) {
        this.shortUrl = shortUrl;
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
