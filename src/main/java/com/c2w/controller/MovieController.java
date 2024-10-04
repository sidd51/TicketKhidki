package com.c2w.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.c2w.config.firebaseInit;
import com.c2w.model.Movie;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MovieController {
    @FXML
    private StackPane spane;

    @FXML
    private BorderPane addMovies_form;

    @FXML
    private TextField movieTitleField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField durationField;

    @FXML
    private TextField showingDateField;

    @FXML
    private TableView<Movie> movieTableView;

    @FXML
    private TableColumn<Movie, String> col_movieTable;

    @FXML
    private TableColumn<Movie, String> col_genre;

    @FXML
    private TableColumn<Movie, String> col_dur;

    @FXML
    private TableColumn<Movie, String> col_sTime;

   
    @FXML
    private Button PurchaseHist;

    private ObservableList<Movie> movieList;


    @FXML
    private ImageView movieImageView;
    private LoginController loginController = new LoginController();
    
    public void initialize() throws IOException, InterruptedException, ExecutionException {
        movieList = FXCollections.observableArrayList();
    
        col_movieTable.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        col_dur.setCellValueFactory(new PropertyValueFactory<>("duration"));
        col_sTime.setCellValueFactory(new PropertyValueFactory<>("showingDate"));
    
        // Load movies in a background thread to avoid blocking the UI
        new Thread(() -> {
            try {
                loadMovies();
                Platform.runLater(() -> movieTableView.setItems(movieList));
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    
       
       
    }
    
    private void loadMovies() throws InterruptedException, ExecutionException, IOException {
        movieList.clear();
        ApiFuture<QuerySnapshot> future = firebaseInit.getDb().collection("AdminPage").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot document : documents) {
            System.out.println("Document ID: " + document.getId());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) document.get("movies");
            if (movies != null) {
                for (Map<String, Object> movieData : movies) {
                    Movie movie = new Movie();
                    movie.setTitle((String) movieData.get("title"));
                    movie.setGenre((String) movieData.get("genre"));
                    movie.setDuration((String) movieData.get("duration"));
                    movie.setShowingDate((String) movieData.get("showingDate"));
                    movie.setDocumentId(document.getId());
                    System.out.println("Loaded movie: " + movie.getTitle());
                    Platform.runLater(() -> movieList.add(movie));
                }
            } else {
                System.out.println("No movies found in document: " + document.getId());
            }
        }
    }
    
   
    
    
   
String imgurl = null;
@FXML
void handleImportButton() throws InterruptedException, ExecutionException {
    FileChooser open = new FileChooser();
    open.setTitle("Open Image File");
    open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

    Stage stage = (Stage) addMovies_form.getScene().getWindow();

    File file = open.showOpenDialog(stage);
    if (file != null) {
        imgurl = file.toURI().toString(); // Assign the selected image file URI to imgurl
        System.out.println("Imported Image URL: " + imgurl);
        Image image = new Image(imgurl);
        movieImageView.setImage(image);
    } else {
        System.out.println("No file selected.");
    }
}

@FXML
void handleAddButton() {
    String documentId = "MovieDetails";
    String title = movieTitleField.getText();
    String genre = genreField.getText();
    String duration = durationField.getText();
    String showingDate = showingDateField.getText();
    String imageurl = imgurl; // Use the imgurl assigned in handleImportButton

   
    // Add movie in a background thread
    new Thread(() -> {
        try {
            firebaseInit.createRec(documentId, title, genre, duration, showingDate, imageurl);
            Movie newMovie = new Movie(documentId, title, genre, duration, showingDate, imageurl);
            Platform.runLater(() -> {
                movieList.add(newMovie);
                movieTableView.setItems(movieList);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }).start();
}

@FXML
void handleUpdateButton() {
    Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
     if (selectedMovie != null) {
         // Only update the image URL if a new image was imported
         if (imgurl != null) {
            selectedMovie.setImageurl(imgurl);
            
        }
        // Load selected movie data into text fields if they are empty
        if (movieTitleField.getText().isEmpty()) {
            movieTitleField.setText(selectedMovie.getTitle());
        }
        if (genreField.getText().isEmpty()) {
            genreField.setText(selectedMovie.getGenre());
        }
        if (durationField.getText().isEmpty()) {
            durationField.setText(selectedMovie.getDuration());
        }
        if (showingDateField.getText().isEmpty()) {
            showingDateField.setText(selectedMovie.getShowingDate());
        }
        if(movieImageView.getUserData()==null){
            Image image2=new Image(imgurl);
            movieImageView.setImage(image2);
        }
       

        String documentId = selectedMovie.getDocumentId();
        String title = movieTitleField.getText();
        String genre = genreField.getText();
        String duration = durationField.getText();
        String showingDate = showingDateField.getText();
        String imageurl = selectedMovie.getImageurl(); // Get the current image URL from the selected movie

   
        new Thread(() -> {
            try {
                // Call the updated updateRec method with the movieId
                firebaseInit.updateRec(documentId, title, genre, duration, showingDate, imageurl);

                // Update the local movie list
                Platform.runLater(() -> {
                    selectedMovie.setTitle(title);
                    selectedMovie.setGenre(genre);
                    selectedMovie.setDuration(duration);
                    selectedMovie.setShowingDate(showingDate);
                    selectedMovie.setImageurl(imageurl);
                    movieTableView.refresh();
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }
}




    @FXML
    void handleClearButton() {
        movieTitleField.clear();
        genreField.clear();
        durationField.clear();
        showingDateField.clear();
        movieImageView.setImage(null);
    }
    @FXML
    void handleDeleteButton() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            String movieTitle = selectedMovie.getTitle();  // Get the title of the selected movie
            new Thread(() -> {
                try {
                    firebaseInit.deleteRecByTitle("MovieDetails", movieTitle);  // Pass the title for deletion
                    Platform.runLater(() -> movieList.remove(selectedMovie));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    

    




  

   


    @FXML
    void handleLogOutButton() {
       LoginController.handlelogout();
    }
    @FXML
    void handlePurchaseHist()
    {
        loginController.showPurchasehist();
    }
}
