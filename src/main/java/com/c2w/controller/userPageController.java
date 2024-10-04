package com.c2w.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.c2w.config.FirebaseService;
import com.c2w.config.firebaseInit;
import com.c2w.model.Movie;
import com.c2w.model.MovieD;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class userPageController {

    @FXML
    private ImageView imgview1, imgview2, imgview3, imgview4, imgview5, imgview6;

    @FXML
    private Label label1, label2, label3, label4, label5, label6;

    @FXML
    private Label slabel1, slabel2, slabel3, slabel4, slabel5, slabel6;

    @FXML
    private Button select1, select2, select3, select4, select5, select6;

    @FXML
    private TableView<MovieD> tablev;

    @FXML
    private TableColumn<MovieD, String> movieTcol, timecol, showingDcol;

    @FXML
    private Spinner<Integer> Spinner1, Spinner2;

    @FXML
    private Label price1, price2, totallabel;

    @FXML
    private Button buybutton, clearbutt, logoutbutt ,recieptbutt;

    @FXML
    LoginController loginController=new LoginController();
    MovieD selectedMovie;
    public int  specialseats=0;
    public int  normalseats=0;
    public String totalstr;

    private String documentID = "MovieDetails"; // Initialize properly
    ObservableList<MovieD> data = FXCollections.observableArrayList();

    public void initialize() throws IOException {
        try {
            firebaseInit.initializeFirebase();
            initializeUserPage();
            addTableViewListener();
            recieptbutt.setDisable(true);
            buybutton.setDisable(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void initializeUserPage() throws InterruptedException, ExecutionException {
        
        firebaseInit.loadData(documentID, imgview1, imgview2, imgview3, imgview4, imgview5, imgview6, label1, label2, label3, label4, label5, label6, slabel1, slabel2, slabel3, slabel4, slabel5, slabel6);
       
    }
   
    
    public void initializePurchase() {
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0);
        Spinner1.setValueFactory(valueFactory1);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        Spinner2.setValueFactory(valueFactory2);

        Spinner1.valueProperty().addListener((obs, oldValue, newValue) -> updatePrices());
        Spinner2.valueProperty().addListener((obs, oldValue, newValue) -> updatePrices());

        updatePrices();
        buybutton.setDisable(false);
    }

    private void updatePrices() {
        int specialclassn = Spinner1.getValue();
        int normalclassn = Spinner2.getValue();

        int result1 = specialclassn * 400;
        int result2 = normalclassn * 200;
       int total = result1 + result2;
         totalstr=total+" Rs";

        price1.setText(result1 + " Rs");
        price2.setText(result2 + " Rs");
        totallabel.setText(total + " Rs");
        
        
    }
    private void initializeSeats(){
         specialseats=30;
         normalseats=50;
    }


    @FXML
    void HandleLogOut(ActionEvent event) {
        LoginController.handlelogout();
    }
   

   
    @FXML
void Handlebuybutton(ActionEvent event) {
   

    int specialclassn = Spinner1.getValue();
    int normalclassn = Spinner2.getValue();
    
    String message;

    if (specialclassn > 0 || normalclassn > 0) {
        if(specialclassn>specialseats || normalclassn>normalseats){
            message ="Sorry!\n.";
        }else{
            message = "Congratulations!\nYour tickets for the movie \"" + selectedMovie.getTitle() + "\" are confirmed.";
            specialseats-=specialclassn;
            normalseats-=normalclassn;
            System.out.println(specialseats +" "+normalseats );
            recieptbutt.setDisable(false); }
        
    } else {
        message = "Please select at least one ticket to purchase.";
    }

    Platform.runLater(() -> {
        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.initModality(Modality.APPLICATION_MODAL);
        alert1.initOwner(buybutton.getScene().getWindow());
        alert1.setTitle("Purchase Confirmation");
        alert1.setHeaderText(message);
        alert1.setContentText("Thank you for choosing Ticket Khidki.");
        Stage alertStage = (Stage) alert1.getDialogPane().getScene().getWindow();
        alertStage.centerOnScreen();
        alert1.showAndWait();
        try {
            firebaseInit.createPurchaseRec("PurchaseDetails",FirebaseService.email, selectedMovie.getTitle(), selectedMovie.getTime(),selectedMovie.getDate(),totalstr);
        } catch (InterruptedException e) {
            // TODO
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    });
}


    
    

    @FXML
    void handleClearbutton(ActionEvent event) {
        Spinner1.getValueFactory().setValue(0);
        Spinner2.getValueFactory().setValue(0);
        totallabel.setText("0 Rs");
        price1.setText("0 Rs");
        price2.setText("0 Rs");

        tablev.getItems().clear();
    }

    @FXML
    void handleSelect1(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle1, firebaseInit.time1, firebaseInit.date1);
            
            
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj);
                tablev.refresh(); 
            });
        }).start();
    }
    

    @FXML
    void handleSelect2(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle2, firebaseInit.time2, firebaseInit.date2);
            
           
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj); 
                tablev.refresh(); 
            });
        }).start();
    }

    @FXML
    void handleSelect3(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle3, firebaseInit.time3, firebaseInit.date3);
            
           
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj); 
                tablev.refresh(); 
            });
        }).start();
    }

    @FXML
    void handleSelect4(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle4, firebaseInit.time4, firebaseInit.date4);
            
           
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj); 
                tablev.refresh(); 
            });
        }).start();
    }

    @FXML
    void handleSelect5(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle5, firebaseInit.time5, firebaseInit.date5);
            
            
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj); // Add item to TableView
                tablev.refresh(); // Refresh TableView (if needed)
            });
        }).start();
    }

    @FXML
    void handleSelect6(ActionEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate some operation delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            MovieD obj = new MovieD(firebaseInit.movieTitle6, firebaseInit.time6, firebaseInit.date6);
            
            
            Platform.runLater(() -> {
                movieTcol.setCellValueFactory(new PropertyValueFactory<>("title"));
                timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
                showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
                tablev.getItems().add(obj); // Add item to TableView
                tablev.refresh(); // Refresh TableView (if needed)
            });
        }).start();
    }

    private void addTableViewListener() {
        tablev.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMovie = newSelection; // Set the selected movie
                initializePurchase();
                initializeSeats();
            }
            });
    }
    
    @FXML
    void handleRecieptbutton() {
      
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt");
        fileChooser.setInitialFileName("TicketKhidkiReceipt.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    
        Stage primaryStage = (Stage) recieptbutt.getScene().getWindow();
        File file = fileChooser.showSaveDialog(primaryStage);
    
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("***************************TICKET KHIDKI***********************************************");
                writer.newLine();
                writer.write("            Peek into the world of cinema with ticket khidki!            ");
                writer.newLine();
                writer.write("Movie: " + selectedMovie.getTitle());
                writer.newLine();
                writer.write("Time: " + selectedMovie.getTime());
                writer.newLine();
                writer.write("Date: " + selectedMovie.getDate());
                writer.newLine();
                writer.write("Special Class Tickets: " + Spinner1.getValue());
                writer.newLine();
                writer.write("Normal Class Tickets: " + Spinner2.getValue());
                writer.newLine();
                writer.write("Total Price: " + totallabel.getText());
                writer.newLine();
                writer.write("Thank you for choosing Ticket Khidki.");
                showAlert("Success", "Receipt saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while saving the Receipt.");
            }
        } else {
            showAlert("Cancelled", "Save operation cancelled.");
        }
    }
    

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(recieptbutt.getScene().getWindow());
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Center the alert
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.centerOnScreen();

            alert.showAndWait();
        });
    }
   
}