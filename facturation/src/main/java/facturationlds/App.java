package facturationlds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class App extends Application {

    private static Scene scene;

    DataBaseHandler dBhandler;

    @Override
    public void start(Stage primaryStage) throws IOException {
        scene = new Scene(loadFXML("main"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("SCI Easy Fact V1.0");
        primaryStage.show();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);

        dBhandler = new DataBaseHandler();

            // for testing and demonstration purposes only
        dBhandler.populateSCITable();
        dBhandler.populateClient();
        dBhandler.populateFacture();
        dBhandler.populatePrestationTable();
        dBhandler.populateTarifTable();
        
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}