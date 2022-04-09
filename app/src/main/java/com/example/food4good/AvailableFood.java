package com.example.food4good;


public class AvailableFood {
    String cid;
    int vegMeals;
    int nonVegMeals;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getVegMeals() {
        return vegMeals;
    }

    public void setVegMeals(int vegMeals) {
        this.vegMeals = vegMeals;
    }

    public AvailableFood(String cid, int vegMeals, int nonVegMeals) {
        this.cid = cid;
        this.vegMeals = vegMeals;
        this.nonVegMeals = nonVegMeals;
    }

    public AvailableFood(){

    }

    public int getNonVegMeals() {
        return nonVegMeals;
    }

    public void setNonVegMeals(int nonVegMeals) {
        this.nonVegMeals = nonVegMeals;
    }
}
