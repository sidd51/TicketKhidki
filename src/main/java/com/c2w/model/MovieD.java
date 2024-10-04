package com.c2w.model;

public class MovieD {
    private final String title;
    private final String time;
    private final String date;

    
    public MovieD(String title, String time,String date) {
        this.title =title;
        this.time = time;
        this.date =date;
    }
    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    
}