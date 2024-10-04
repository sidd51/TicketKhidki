package com.c2w.model;

import java.util.List;

public class Movie {
    private String documentId;
    private String title;
    private String genre;
    private String duration;
    private String showingDate;
    private String imageurl;
    private String id;

    
    private List<Movie> movies;

   

    public Movie() {}

    public Movie(String documentId, String title, String genre, String duration, String showingDate,String imageurl) {
        this.documentId = documentId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.showingDate = showingDate;
    }

    // Getters and setters

    public Movie(String title, String duration, String showingDate) {
        this.title = title;
        this.duration = duration;
        this.showingDate = showingDate;
        
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getShowingDate() {
        return showingDate;
    }

    public void setShowingDate(String showingDate) {
        this.showingDate = showingDate;
    }
    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
