package com.example.food4good;

public class Requester {
    String phoneNumber;
    String name;
    double rating;
    int totalOrders;

    public Requester() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Requester(String phoneNumber, String name, double rating, int totalOrders) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.rating = rating;
        this.totalOrders = totalOrders;
    }
}
