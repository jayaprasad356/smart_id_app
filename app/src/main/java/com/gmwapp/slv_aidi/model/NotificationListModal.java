package com.gmwapp.slv_aidi.model;

public class NotificationListModal {
    private String id;
    private String title;
    private String description;
    private String datetime;
    private String link;
    private String image;

    // Constructor, getters, and setters
    public NotificationListModal(
            String id,
            String title,
            String description,
            String datetime,
            String link,
            String image
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

