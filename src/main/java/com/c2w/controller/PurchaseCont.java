package com.c2w.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.c2w.config.firebaseInit;
import com.c2w.model.Movie;
import com.c2w.model.Purchase;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PurchaseCont {

    @FXML
    private TableView<Purchase> purcshaseTablecol;

    @FXML
    private TableColumn<Purchase, String> emailcol,titlecol,timecol,showingDcol,totalcol;

    

    @FXML
    private Button backbutton;
    private LoginController loginController = new LoginController();
    private ObservableList<Purchase>purchaseList;
    
 
    @FXML
   public void initialize() throws IOException, InterruptedException, ExecutionException {
        purchaseList = FXCollections.observableArrayList();

        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        titlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        timecol.setCellValueFactory(new PropertyValueFactory<>("time"));
        showingDcol.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalcol.setCellValueFactory(new PropertyValueFactory<>("total"));
        // Load movies in a background thread to avoid blocking the UI
        new Thread(() -> {
            try {
                loadPurchases();
                Platform.runLater(() -> purcshaseTablecol.setItems(purchaseList));
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadPurchases() throws InterruptedException, ExecutionException, IOException {
        purchaseList.clear();
        ApiFuture<QuerySnapshot> future = firebaseInit.getDb().collection("AdminPage").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot document : documents) {
            System.out.println("Document ID: " + document.getId());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> purchases = (List<Map<String, Object>>) document.get("purchases");
            if (purchases != null) {
                for (Map<String, Object> purchaseData : purchases) {
                    Purchase purchase = new Purchase();
                    purchase.setEmail((String) purchaseData.get("email"));
                    purchase.setTitle((String) purchaseData.get("title"));
                    purchase.setTime((String) purchaseData.get("time"));
                    purchase.setDate((String) purchaseData.get("date"));
                    purchase.setTotal((String) purchaseData.get("total"));
                    purchase.setDocumentId(document.getId());
                    System.out.println("Loaded purchase: " + purchase.getTitle());
                    Platform.runLater(() -> purchaseList.add(purchase));
                }
            } else {
                System.out.println("No movies found in document: " + document.getId());
            }
        }
    }
    @FXML
    void Handlebackbutton(ActionEvent event) {
        loginController.showAdminScene();
    }

}

