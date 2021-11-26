package com.project.cst2335.Models;

public class PhotoModel {
    public int id;
    public String tinyUrl;
    public String originalUrl;
    public String largeUrl;
    public String large2xUrl;
    public String mediumUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public String getLarge2xUrl() {
        return large2xUrl;
    }

    public void setLarge2xUrl(String large2xUrl) {
        this.large2xUrl = large2xUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        mediumUrl = mediumUrl;
    }
}
