package com.example.standing_alone;

public class Upload {
    private String title;
    private String imageUrl;
    public Upload(){

    }
    public Upload(String title, String imageUrl){
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
