package com.c2w;
import com.c2w.controller.LoginController;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("Launching app....");
        Application.launch(LoginController.class,args);
    }
}