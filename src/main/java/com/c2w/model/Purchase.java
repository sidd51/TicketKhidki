package com.c2w.model;
import java.util.List;
public class Purchase {
    private String documentId;
    private String email;
    private String title;
    private String time;
    private String date;
    private String total;
   

    private List<Purchase> purchases;
    public Purchase() {}
    public Purchase(String documentId, String email, String title, String time, String date,String total) {
        this.documentId = documentId;
        this.email = email;
        this.title = title;
        this.time = time;
        this.date = date;
        this.total=total;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTotal() {
        return total;
    }
    public void setTotal(String total) {
        this.total = total;
    }
    public List<Purchase> getPurchases() {
        return purchases;
    }
    public void setPurchases(List<Purchase> purchasess) {
        this.purchases= purchases;
    }
}
   