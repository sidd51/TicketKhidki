package com.c2w.controller;
import java.io.FileInputStream;
import java.io.IOException;
import com.c2w.config.FirebaseService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class LoginController extends Application {
    public static Stage primaryStage;
    public static Scene userScene;
    public static Scene loginPage;
    private FirebaseService firebaseService;
   
    

    public void showUserScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("UserPage.fxml"));
            Scene userScene = new Scene(root, 1000, 800);
            // String css = getClass().getResource("com/c2w/controller/UserDesign.css").toExternalForm();
            // userScene.getStylesheets().add(css);
    
            Platform.runLater(() -> {
                primaryStage.setScene(userScene);
                primaryStage.setFullScreen(true);
                primaryStage.setFullScreenExitHint("");
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load fxml file");
        }
    }
    
    public void showAdminScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminPage.fxml"));
            Scene adminScene = new Scene(root, 500, 500);
            String css = getClass().getResource("/com/c2w/controller/UserDesign.css").toExternalForm();
            adminScene.getStylesheets().add(css);
    
            Platform.runLater(() -> {
                
                primaryStage.setScene(adminScene);
                 primaryStage.setFullScreen(true);
                primaryStage.setFullScreenExitHint("");
               
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load fxml file");
        }
    }
    public void showPurchasehist(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Purchasehist.fxml"));
            Scene historyScene = new Scene(root, 500, 500);
    
    
            Platform.runLater(() -> {
                
                primaryStage.setScene(historyScene);
                 primaryStage.setFullScreen(true);
                primaryStage.setFullScreenExitHint("");
               
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load fxml file");
        }

    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage =primaryStage;
        try{
            FileInputStream serviceAccount= new FileInputStream("src/main/resources/fx-auth-fb-1b521-firebase.json");
            FirebaseOptions options= new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://fx-auth-fb-1b521-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();
            FirebaseApp.initializeApp(options);
        }catch(IOException e){
            e.printStackTrace();
        }

        Scene loginscene =createLoginScene();
   
        primaryStage.setTitle("TiKet KhidKi");
        primaryStage.getIcons().add(new Image("/com/c2w/images/siddhi.jpg"));
        primaryStage.setWidth(2900);
        primaryStage.setHeight(3000);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(loginscene);
        primaryStage.show();
    } 
    
        private Scene createLoginScene(){
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setLayoutX(620);
        loginBox.setLayoutY(310);
        
        loginBox.setPrefSize(350,400);
        loginBox.setStyle("-fx-background-color:WHITE");
        //loginBox.setStyle("-fx-background-color: rgba(199, 21, 133, 0.9);"); // Dark pink with 90% opacity
        Label signInl=new Label("Sign In");
        Font font1=Font.font("Calibri",FontWeight.BOLD,FontPosture.REGULAR,23);
        signInl.setStyle("-fx-text-fill:#98179c");
        signInl.setFont(font1); 
        signInl.setLayoutX(790);
      
        

       

        Group gp= new Group(loginBox);

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Email");
        usernameField.setPrefWidth(200);
        usernameField.getText();
        
        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");


        Button signInButton = new Button("Sign In");
        signInButton.setStyle("-fx-background-color:#98179c;-fx-background-radius: 5;-fx-background-insets: 10;-fx-text-fill: white;-fx-font-size: 14px;-fx-padding: 10 20 10 20;-fx-border-radius: 5;-fx-border-color: WHITE;-fx-border-width: 2;-fx-font-weight:bold");
        signInButton.setPrefSize(100,55);

       
        Button loginButton = new Button("Log In");
        loginButton.setStyle("-fx-background-color:#98179c;-fx-background-radius: 5;-fx-background-insets: 10;-fx-text-fill: white;-fx-font-size: 14px;-fx-padding: 10 20 10 20;-fx-border-radius: 5;-fx-border-color: WHITE;-fx-border-width: 2;-fx-font-weight:bold");
        loginButton.setPrefSize(100,55);
        // Instantiate FirebaseService
        firebaseService = new FirebaseService(this, usernameField, passwordField);
        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firebaseService.signUp();
            }
        });

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                firebaseService.login();
            }
        });

        // Sign Up and Log In buttons
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(signInButton, loginButton);

        Label lb=new Label("TICKET KHIDKI");
        Font font=Font.font("verdana",FontWeight.BOLD,FontPosture.REGULAR,30);
        lb.setStyle("-fx-text-fill: #98179c;");
        lb.setFont(font);
        lb.setLayoutX(875);
        lb.setLayoutY(250);

        Label lb3=new Label("Peek into the world of cinema with ticket khidki!");
        Font font3=Font.font("arialblack", FontPosture.ITALIC,15);
        lb3.setStyle("-fx-text-fill: BLACK");
        lb3.setFont(font3);
        lb3.setLayoutX(875);
        lb3.setLayoutY(250);

        // Add all elements to the VBox
        loginBox.getChildren().addAll(lb,lb3,signInl,usernameField, passwordField, buttonBox);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(5);
        innerShadow.setOffsetY(5);
        innerShadow.setColor(Color.GRAY);
         loginBox.setEffect(innerShadow);


        // Apply the DropShadow effect to the VBox
        loginBox.setEffect(dropShadow);



        // Create the StackPane to hold the background image and the login box
        StackPane root = new StackPane();

        // Load the background image
         Image backgroundImage = new Image("com/c2w/images/3658604.jpg"); // Adjust the path as needed
         ImageView backgroundImageView = new ImageView(backgroundImage);
         backgroundImageView.setFitWidth(1800);
         backgroundImageView.setFitHeight(1500);
         backgroundImageView.setPreserveRatio(true);
         backgroundImageView.setOpacity(0.9);
        
       
        // Add the background image and login box to the root pane
        root.getChildren().addAll(backgroundImageView,gp);

        // Create the scene
        Scene loginPage = new Scene(root, 1700, 1000);
        LoginController.loginPage=loginPage;
        return loginPage;
    }

        public Scene getScene() {
            
            return loginPage;
        }

        public static void handlelogout() {
            primaryStage.setScene(loginPage);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
        }

        public static Scene getuserScene(){
            return userScene;
        }

}


