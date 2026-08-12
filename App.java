

//--------LUJAIN HELMI------

package projecttry1;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import projecttry1.Database;

/**
 * Entry point for the JavaFX app. Also holds the "switchScene" helper
 * that any controller can call to move between screens.
 *
 * Once a teammate has their FXML screen ready (e.g. "CustomerDashboard.fxml"),
 * anyone can navigate to it from anywhere with:
 *      App.switchScene("CustomerDashboard");
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Load Milestone 1's dummy data (customers, tables, menu, etc.)
        // so there's something to log in with / look at right away.
        Database.initializeDatabase();

        switchScene("Login");
        primaryStage.setTitle("Restaurant Reservation System");
        primaryStage.show();
    }

    /**
     * Loads <fxmlName>.fxml (must be in the same package/folder as this class)
     * and swaps it in as the current scene.
     */
    public static void switchScene(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlName + ".fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
