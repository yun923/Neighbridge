package com.example.standing_alone;

import android.net.Uri;

public class WriteInfo {
    private String title;
    private String contents;
    private String hashtag;
   // private String publisher;

    public WriteInfo(String title, String contents/*, String publisher*/, String hashtag) {
        this.title = title;
        this.contents = contents;
        //this.publisher = publisher;
        this.hashtag = hashtag;
    }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title;}
    public String getContents() { return this.contents; }
    public void setContents(String contents) { this.contents = contents;}
    /*public String getPublisher() { return this.publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher;}*/
    public String getHashtag() { return this.hashtag; }
    public void setHashtag(String hashtag) { this.hashtag = hashtag;}

}
