package com.neshijain.myapplication;

/**
 * Class movie for each card view
 */
//

public class Movie {

    private String Title;
    private int Thumbnail ;

    public Movie() { }

    public Movie(String title, String category, String description, int thumbnail) {
        Title = title;
        Thumbnail = thumbnail;
    }
    public String getTitle() {
        return Title;
    }
    public int getThumbnail() {
        return Thumbnail;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
