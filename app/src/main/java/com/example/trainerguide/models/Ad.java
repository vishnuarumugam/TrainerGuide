package com.example.trainerguide.models;

import java.util.Date;

public class Ad {

    private String adId;
    private String image;
    private String redirectTo;
    private Double amount;
    private String emailAddress;
    private String url;
    private Date createDate;
    private Date postedDate;
    private Date expiryDate;
    private String isExpired;

    public Ad(String adId, String image, String redirectTo, Double amount, String emailAddress, String url, Date createDate, Date postedDate, Date expiryDate, String isExpired) {
        this.adId = adId;
        this.image = image;
        this.redirectTo = redirectTo;
        this.amount = amount;
        this.emailAddress = emailAddress;
        this.url = url;
        this.createDate = createDate;
        this.postedDate = postedDate;
        this.expiryDate = expiryDate;
        this.isExpired = isExpired;
    }

    public Ad() {
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
