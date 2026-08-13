
//dont use if not needed 

package com.restaurant.restaurantsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import projecttry1.Database;

public class RestaurantSystem extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/checkout.fxml")
        );

        Scene scene = new Scene(root);

        stage.setTitle("Restaurant System - Checkout");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        // Load the dummy backend data
        Database.initializeDatabase();

        // Start JavaFX
        launch(args);
    }
}
