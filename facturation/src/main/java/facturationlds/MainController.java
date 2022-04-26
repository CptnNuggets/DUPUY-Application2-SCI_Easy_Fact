package facturationlds;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import facturationlds.databaseClasses.ExportMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

    // CONTROLLER for the MAIN WINDOW
    // LOADS the OTHER CONTROLLERS when the MENU BUTTONS are pressed
public class MainController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private AnchorPane mainWindowAnchorPane;
    @FXML
    private Button sciButton;
    @FXML
    private Button clientButton;
    @FXML
    private Button factureButton;
    @FXML
    private Button prestationButton;
    @FXML
    private Button exportButton;

    private Button selectedButton;

        // GLOBAL DATABASE HANDLER CLASS
    DataBaseHandler dBhandler;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        selectedButton = null;
        dBhandler = DataBaseHandler.getInstance();
            // Initializes the application with the client interface
        setScrollPane("facture.fxml");
        setActiveButton(factureButton);        
    }


    
    //                     ***
    //      METHODS RELATED TO THE FXML ELEMENTS
    //                     ***

        // Calls the clients interface when the related button is pressed
    @FXML
    void clientsRedirect(ActionEvent event) {
        setScrollPane("client.fxml");
        setActiveButton(clientButton);
    }

        // Calls the scis interface when the related button is pressed
    @FXML
    void sciRedirect(ActionEvent event) {
        setScrollPane("sci.fxml");
        setActiveButton(sciButton);
    }

        // Calls the facture interface when the related button is pressed
    @FXML
    void factureRedirect(ActionEvent event) {
        setScrollPane("facture.fxml");
        setActiveButton(factureButton);
    }

        // Calls the prestation interface when the related button is pressed
    @FXML
    void prestaRedirect(ActionEvent event) {
        setScrollPane("prestation.fxml");
        setActiveButton(prestationButton);
    }

        // Calls the prestation interface when the related button is pressed
    @FXML
    void doExport(ActionEvent event) {
        ExportMethods.exportAll();        
        mainWindowAnchorPane.getChildren().clear();
        setActiveButton(exportButton);
    }

        // function to load the called interface in the main window
    public void setScrollPane(String fxmlToLoad) {
        ScrollPane scPane;
        try {
            scPane = FXMLLoader.load(getClass().getResource(fxmlToLoad));
            mainWindowAnchorPane.getChildren().clear();
            mainWindowAnchorPane.getChildren().add(scPane);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

        // function to set the last button pressed to the css pseudo-class "was selected"
    public void setActiveButton(Button button){
        if (selectedButton != null) {
            selectedButton.getStyleClass().removeAll("was_selected");
        }
        button.getStyleClass().add("was_selected");
        selectedButton = button;
    }


}
