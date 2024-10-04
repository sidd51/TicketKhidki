package com.c2w.config;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import com.c2w.controller.LoginController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.application.Platform;




public class FirebaseService {
    private LoginController loginController;
    private TextField emailField;
    private PasswordField passwordField;
    public static String email;

    public FirebaseService(LoginController loginController, TextField emailField, PasswordField passwordField) {
        this.loginController = loginController;
        this.emailField = emailField;
        this.passwordField = passwordField;
    }

    public boolean signUp() {
         email = emailField.getText();
        String password = passwordField.getText();

        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created user: " + userRecord.getUid());
            showAlert("Success", "User created successfully.");
            return true;
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create user: " + e.getMessage());
            return false;
        }
    }

    public boolean login() {
         email = emailField.getText();
        String password = passwordField.getText();

        try {
            String apiKey = "AIzaSyC5_z-N1JBaT9lCiSZQ6URye5p1sqQvrr8"; // Replace with your actual API key
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
            jsonRequest.put("returnSecureToken", true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    System.out.println("Login successful: " + jsonResponse.toString());
                     showAlert("Success", "Login successfully.");
                    String userRole = getUserRole(jsonResponse.getString("idToken"));

                    if ("admin".equals(userRole)) {
                        loginController.showAdminScene();
                    } else {
                        loginController.showUserScene();
                    }
                    return true;
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    JSONObject jsonErrorResponse = new JSONObject(errorResponse.toString());
                    System.out.println("Login failed: " + jsonErrorResponse.toString());
                    showAlert("Invalid Login", "Invalid credentials!!!");
                }
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during login.");
            return false;
        }
    }

    private void showAlert(String title, String message) {
       Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(loginController.getScene().getWindow());
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
      });
    }

    private String getUserRole(String idToken) {
        String adminEmail = "siddhi@gmail.com"; 

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();

            if (adminEmail.equals(email)) {
                return "admin";
            }
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        return "user";
    }
}

/* 
public class FirebaseService {
    public static Scene scene2;
    private TextField emailField;
    private PasswordField passwordField;
    private static LoginController loginController;
    LoginController obj=new LoginController();
    
public FirebaseService(LoginController loginController,TextField emailField,PasswordField passwordField){
    FirebaseService.loginController=loginController;
    this.emailField=emailField;
    this.passwordField =passwordField;
  }


public boolean signUp(){
        String email=emailField.getText();
        String password =passwordField.getText();

    try{

        UserRecord.CreateRequest request =new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisabled(false);

        UserRecord userRecord =FirebaseAuth.getInstance().createUser(request);
        System.out.println("Successfully created user: "+userRecord.getUid());
        showAlert("Success","User created successfully.");
        return true;
    }catch(FirebaseAuthException e){
        e.printStackTrace();
        showAlert("Error","Failed to create user: "+ e.getMessage());
        return false;
    }
}
public boolean login() {
    String email = emailField.getText();
    String password = passwordField.getText();
    try {
        // Firebase API key (replace with your actual API key)
        String apiKey = "AIzaSyC5_z-N1JBaT9lCiSZQ6URye5p1sqQvrr8";
                      
        // Firebase authentication URL
        URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        // Request body for login
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", email);
        jsonRequest.put("password", password);
        jsonRequest.put("returnSecureToken", true);

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // Optionally, parse the response to verify login success
                JSONObject jsonResponse = new JSONObject(response.toString());
                System.out.println("Login successful: " + jsonResponse.toString());
                showAlert("Success","Login successfully.");
                String userRole = getUserRole(jsonResponse.getString("idToken"));
                
                if ("admin".equals(userRole)) {
                    
                    obj.showUserScene();
                } else {
                    obj.showAdminScene();
                }
                return true;
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
                // Optionally, parse the error response to show more details
                JSONObject jsonErrorResponse = new JSONObject(errorResponse.toString());
                System.out.println("Login failed: " + jsonErrorResponse.toString());
                showAlert("Invalid Login", "Invalid credentials!!!");
            }
            return false;
        }

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "An error occurred during login.");
        return false;
    }
}

 private void showAlert(String title, String message) {
      Alert alert=new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle(title);
      alert.setHeaderText (null);
      alert.setContentText(message);
      alert.showAndWait(); 
    }

    private String getUserRole(String idToken) {
        // Here you would use the idToken to get user details from Firebase and determine their role.
        // For the purpose of this example, we'll assume all users are "users" except one specific admin email.
        String adminEmail = "admin@example.com"; // Replace with your admin email
    
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
    
            if (adminEmail.equals(email)) {
                return "admin";
            }
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        return "user";
    }
    
   

    



}
*/