package com.pkmunla.asus.homedashboard.Model;

public class Model {

    String title, description, location, ustad, image, masjid;

    public String getMasjid() {
        return masjid;
    }

    public void setMasjid(String masjid) {
        this.masjid = masjid;
    }

    public Model(){

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUstad() {
        return ustad;
    }

    public void setUstad(String ustad) {
        this.ustad = ustad;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
