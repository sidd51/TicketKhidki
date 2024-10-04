package com.c2w.config;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
public class firebaseInit {
    private static Firestore db;
    public static String imageUrl1;

    public static void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/javafx-firebase-firestore-firebase-adminsdk-bjupx-13e1a40718.json");
        FirebaseOptions firestoreOptions = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("AIzaSyC5_z-N1JBaT9lCiSZQ6URye5p1sqQvrr8")  // Correct the URL format here
                .build();

        // Check if the app already exists
        boolean appExists = FirebaseApp.getApps().stream()
                .anyMatch(app -> app.getName().equals("firestoreApp"));

        // Initialize the app only if it doesn't already exist
        if (!appExists) {
            FirebaseApp firestoreApp = FirebaseApp.initializeApp(firestoreOptions, "firestoreApp");
            db = FirestoreClient.getFirestore(firestoreApp);
        } else {
            FirebaseApp firestoreApp = FirebaseApp.getInstance("firestoreApp");
            db = FirestoreClient.getFirestore(firestoreApp);
        }
    }

    public static Firestore getDb() throws IOException {
        if (db == null) {
            initializeFirebase();
        }
        return db;
    }

/*public class firebaseInit {
    private static Firestore db;
    public static String imageUrl1;
    public static void initializeFirebase() throws IOException{
        FileInputStream serviceAccount =new FileInputStream("src/main/resources/javafx-firebase-firestore-firebase-adminsdk-bjupx-13e1a40718.json");
        FirebaseOptions firestoreoptions=new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("AIzaSyC5_z-N1JBaT9lCiSZQ6URye5p1sqQvrr8")
        .build();
      FirebaseApp firestoreApp = FirebaseApp.initializeApp(firestoreoptions,"firestoreApp");    
      db = FirestoreClient.getFirestore(firestoreApp);
        
 }

        public static Firestore getDb() throws IOException {
        if (db == null) {
            initializeFirebase();
        }
        return db;
    }
*/
    public static void createRec(String documentId, String title, String genre, String duration, String showingDate,String imageurl) throws InterruptedException, ExecutionException {
        // Create a new movie map
        Map<String, Object> newMovie = new HashMap<>();
        newMovie.put("title", title);
        newMovie.put("genre", genre);
        newMovie.put("duration", duration);
        newMovie.put("showingDate", showingDate);
        newMovie.put("imageurl",imageurl);
    
        // Get the document reference
        DocumentReference docRef = db.collection("AdminPage").document(documentId);
    
        // Get the existing document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
    
        if (document.exists()) {
            // Document exists, get the current list of movies
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) document.get("movies");
    
            if (movies == null) {
                // Initialize the list if it's null
                movies = new ArrayList<>();
            }
    
            // Add the new movie to the list
            movies.add(newMovie);
    
            // Update the document with the new list of movies
            Map<String, Object> updatedMovies = new HashMap<>();
            updatedMovies.put("movies", movies);
            ApiFuture<WriteResult> updateFuture = docRef.update(updatedMovies);
            System.out.println("Update time: " + updateFuture.get().getUpdateTime());
        } else {
            // Document does not exist, create a new list with the new movie
            List<Map<String, Object>> movies = new ArrayList<>();
            movies.add(newMovie);
    
            // Create the document with the list of movies
            Map<String, Object> movieData = new HashMap<>();
            movieData.put("movies", movies);
            ApiFuture<WriteResult> createFuture = docRef.set(movieData);
            System.out.println("Create time: " + createFuture.get().getUpdateTime());
        }
    }
   
    @SuppressWarnings("unchecked")
    public static void updateRec(String documentId, String title, String genre, String duration, String showingDate,String imageurl) throws InterruptedException, ExecutionException {
        // Get the document reference
        DocumentReference docRef = db.collection("AdminPage").document(documentId);
    
        // Get the existing document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
    
        if (document.exists()) {
            List<Map<String, Object>> movies = (List<Map<String, Object>>) document.get("movies");
    
            if (movies != null) {
                for (Map<String, Object> movie : movies) {
                    if (movie.get("title").equals(title)) {
                        movie.put("genre", genre);
                        movie.put("duration", duration);
                        movie.put("showingDate", showingDate);
                        movie.put("imageurl",imageurl);
                        break;
                    }
                }
    
                // Update the document with the updated list of movies
                Map<String, Object> updatedMovies = new HashMap<>();
                updatedMovies.put("movies", movies);
                ApiFuture<WriteResult> updateFuture = docRef.update(updatedMovies);
                System.out.println("Update time: " + updateFuture.get().getUpdateTime());
            }
        } else {
            System.out.println("Document does not exist!");
        }
    }
  
    public static void deleteRecByTitle(String documentId, String title) throws InterruptedException, ExecutionException {
        DocumentReference docRef = db.collection("AdminPage").document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
    
        if (document.exists()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) document.get("movies");
    
            if (movies != null) {
                boolean removed = movies.removeIf(movie -> movie != null && movie.get("title").equals(title));
                
                if (removed) {
                    System.out.println("Movie removed from the list, updating Firestore document.");
                    ApiFuture<WriteResult> writeResult = docRef.update("movies", movies);
                    System.out.println("Update time : " + writeResult.get().getUpdateTime());
                } else {
                    System.out.println("Movie not found in the list.");
                }
            } else {
                System.out.println("No movies found in the document.");
            }
        } else {
            System.out.println("Document not found.");
        }
    }
    

    public static List<Map<String, Object>> fetchMovies(String documentId) throws InterruptedException, ExecutionException {
        DocumentReference docRef = db.collection("AdminPage").document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) document.get("movies");
            return movies;
        } else {
            System.out.println("Document does not exist!");
            return new ArrayList<>();
        }
    }

    
    public static String time1,time2,time3,time4,time5,time6,date1,date2,date3,date4,date5,date6,movieTitle1,movieTitle2,movieTitle3,movieTitle4,movieTitle5,movieTitle6;

    public static void loadData(String documentId,ImageView imgview1,ImageView imgview2,ImageView imgview3,ImageView imgview4,ImageView imgview5,ImageView imgview6,Label label1,Label label2,Label label3,Label label4,Label label5,Label label6,Label slabel1,Label slabel2,Label slabel3,Label slabel4,Label slabel5,Label slabel6 ) throws InterruptedException, ExecutionException {
        List<Map<String, Object>> movies = fetchMovies("MovieDetails");

        if (movies.size() > 0) {
            String imageUrl1 = (String) movies.get(0).get("imageurl");
            imgview1.setImage(new Image(imageUrl1));

         movieTitle1=(String) movies.get(0).get("title");
            label1.setText(movieTitle1);

            String genre1=(String) movies.get(0).get("genre");
            slabel1.setText(genre1);

           time1=(String) movies.get(0).get("duration");
           date1=(String) movies.get(0).get("showingDate");
        }
        if (movies.size() > 1) {
            String imageUrl2 = (String) movies.get(1).get("imageurl");
            imgview2.setImage(new Image(imageUrl2));
            movieTitle2=(String) movies.get(1).get("title");
            label2.setText(movieTitle2);
            String genre2=(String) movies.get(1).get("genre");
            slabel2.setText(genre2);
            time2=(String) movies.get(1).get("duration");
            date2=(String) movies.get(1).get("showingDate");
        }
        if(movies.size()>2)
        {
            String imageUrl3 = (String) movies.get(2).get("imageurl");
            imgview3.setImage(new Image(imageUrl3));
             movieTitle3=(String) movies.get(2).get("title");
            label3.setText(movieTitle3);
            String genre3=(String) movies.get(2).get("genre");
            slabel3.setText(genre3);
            time3=(String) movies.get(2).get("duration");
            date3=(String) movies.get(2).get("showingDate");
        }
        if(movies.size()>3)
        {
            String imageUrl4 = (String) movies.get(3).get("imageurl");
            imgview4.setImage(new Image(imageUrl4));
           movieTitle4=(String) movies.get(3).get("title");
            label4.setText(movieTitle4);
            String genre4=(String) movies.get(3).get("genre");
            slabel4.setText(genre4);
            time4=(String) movies.get(3).get("duration");
            date4=(String) movies.get(3).get("showingDate");
        }
        if(movies.size()>4)
        {
            String imageUrl5 = (String) movies.get(4).get("imageurl");
            imgview5.setImage(new Image(imageUrl5));
          movieTitle5=(String) movies.get(4).get("title");
            label5.setText(movieTitle5);
            String genre5=(String) movies.get(4).get("genre");
            slabel5.setText(genre5);
            time5=(String) movies.get(4).get("duration");
            date5=(String) movies.get(4).get("showingDate");
        }
        if(movies.size()>5)
        {
            String imageUrl6 = (String) movies.get(5).get("imageurl");
            imgview6.setImage(new Image(imageUrl6));
             movieTitle6=(String) movies.get(5).get("title");
            label6.setText(movieTitle6);
            String genre6=(String) movies.get(5).get("genre");
            slabel6.setText(genre6);
            time6=(String) movies.get(5).get("duration");
            date6=(String) movies.get(5).get("showingDate");
        }
    }
         public static void createPurchaseRec(String documentId, String email, String title, String time, String date,String total) throws InterruptedException, ExecutionException {
            // Create a new movie map
            Map<String, Object> newPurchase = new HashMap<>();
            newPurchase.put("email", email);
            newPurchase.put("title", title);
            newPurchase.put("time", time);
            newPurchase.put("date", date);
            newPurchase.put("total",total);
        
            // Get the document reference
            DocumentReference docRef = db.collection("AdminPage").document(documentId);
        
            // Get the existing document
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
        
            if (document.exists()) {
                // Document exists, get the current list of movies
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> purchases = (List<Map<String, Object>>) document.get("purchases");
        
                if (purchases == null) {
                    // Initialize the list if it's null
                    purchases = new ArrayList<>();
                }
        
                // Add the new movie to the list
                purchases.add(newPurchase);
        
                // Update the document with the new list of movies
                Map<String, Object> updatedPurchases = new HashMap<>();
                updatedPurchases.put("purchases", purchases);
                ApiFuture<WriteResult> updateFuture = docRef.update(updatedPurchases);
                System.out.println("Update time: " + updateFuture.get().getUpdateTime());
            } else {
                // Document does not exist, create a new list with the new movie
                List<Map<String, Object>> purchases = new ArrayList<>();
                purchases.add(newPurchase);
        
                // Create the document with the list of movies
                Map<String, Object> purchaseData = new HashMap<>();
                purchaseData.put("purchases", purchases);
                ApiFuture<WriteResult> createFuture = docRef.set(purchaseData);
                System.out.println("Create time: " + createFuture.get().getUpdateTime());
            }
        }
    }


