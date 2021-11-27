package com.project.cst2335.Models;

/**
 * Photo Model class
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class PhotoModel {
    public int id;
    public String tinyUrl;
    public String originalUrl;
    public String largeUrl;
    public String large2xUrl;
    public String mediumUrl;

    // setters and getters

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets tiny url.
     *
     * @return the tiny url
     */
    public String getTinyUrl() {
        return tinyUrl;
    }

    /**
     * Sets tiny url.
     *
     * @param tinyUrl the tiny url
     */
    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    /**
     * Gets original url.
     *
     * @return the original url
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * Sets original url.
     *
     * @param originalUrl the original url
     */
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
