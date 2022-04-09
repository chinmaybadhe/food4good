package com.example.food4good;

public class Contributor {
    String id;
    String name;
    String longitude;
    String latitude;
    String photoUrl;
    String email;
    String type;
    String address;
    int rewards;



    public Contributor(String id, String name, String longitude, String latitude, String photoUrl, String email, String type, String address, int rewards) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photoUrl = photoUrl;
        this.email = email;
        this.type = type;
        this.address = address;
        this.rewards = rewards;
    }

    public Contributor(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRewards(int rewards){
        this.rewards = rewards;
    }
    public int getRewards(){
        return rewards;
    }
}

