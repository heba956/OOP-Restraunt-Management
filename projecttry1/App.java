package projecttry1;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application launcher for the Restaurant Management System.
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Initialize sample database records
        Database.initializeDatabase();

        // Start background Order Status TCP Server
        new Thread(() -> {
            Server server = new Server();
            server.start(8080);
        }, "OrderServerThread").start();

        // Launch initial scene
        switchScene("Login");

        primaryStage.setTitle("Restaurant Management & Order System");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    /**
     * Helper to switch current scene dynamically using FXML file name.
     */
    public static void switchScene(String fxmlName) throws IOException {
        if (!fxmlName.toLowerCase().endsWith(".fxml")) {
            fxmlName = fxmlName + ".fxml";
        }

        URL fxmlUrl = App.class.getResource(fxmlName);
        if (fxmlUrl == null) {
            fxmlUrl = App.class.getResource("/projecttry1/" + fxmlName);
        }
        if (fxmlUrl == null) {
            fxmlUrl = App.class.getResource("/" + fxmlName);
        }
        if (fxmlUrl == null) {
            throw new IOException("FXML file '" + fxmlName + "' not found in classpath.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Attach global CSS stylesheet
        URL cssUrl = App.class.getResource("style.css");
        if (cssUrl == null) {
            cssUrl = App.class.getResource("/projecttry1/style.css");
        }
        if (cssUrl == null) {
            cssUrl = App.class.getResource("/style.css");
        }
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
