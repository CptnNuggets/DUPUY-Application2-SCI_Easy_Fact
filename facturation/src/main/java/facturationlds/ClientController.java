package facturationlds;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import facturationlds.databaseClasses.ClientMethods;
import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.SCIMethods.SCIClass;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;

public class ClientController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private ScrollPane clientScrollPane;
    @FXML
    private JFXComboBox<SCIClass> sciChoiceComboBox;
    @FXML
    private Tab newClientTab;
    @FXML
    private JFXTextField clientCodeTF;
    @FXML
    private JFXComboBox<String> moralComboBox;
    @FXML
    private JFXTextField streetAndNumberTF;
    @FXML
    private JFXTextField streetComplementTF;
    @FXML
    private JFXTextField zipCodeTF;
    @FXML
    private JFXTextField cityTF;
    @FXML
    private Label identityFirstLabel;
    @FXML
    private Label identitySecondLabel;
    @FXML
    private JFXTextField identityFirstTF;
    @FXML
    private JFXTextField identitySecondTF;
    @FXML
    private JFXTextField paymentDelayTF;
    @FXML
    private JFXButton saveButton;
    @FXML
    private Tab updateClientTab;
    @FXML
    private JFXComboBox<ClientClass> clientChoiceComboBox;
    @FXML
    private JFXTextField updateClientCodeTF;
    @FXML
    private JFXTextField updateStreetAndNumberTF;
    @FXML
    private JFXTextField updateStreetComplementTF;
    @FXML
    private JFXTextField updateZipCodeTF;
    @FXML
    private JFXTextField updateCityTF;
    @FXML
    private Label updateIdentityFirstLabel;
    @FXML
    private Label updateIdentitySecondLabel;
    @FXML
    private JFXTextField updateIdentityFirstTF;
    @FXML
    private JFXTextField updateIdentitySecondTF;
    @FXML
    private Label displayMoralLabel;
    @FXML
    private JFXTextField updatePaymentDelayTF;
    @FXML
    private JFXButton updateButton;
    @FXML
    private JFXButton deleteClientButton;

        // METHOD CLASSES
    private ClientMethods clientMethods;
    private SCIMethods sciMethods;

        // Used to reference SCI et CLIENT classes
    private Integer sciId;
    private Integer id;

        // REFERENCES to display for the MORAL COMBO BOX
    private final String clientIsCompany = "Personne morale";
    private final String clientIsPerson = "Personne physique";
    

    
    //                          ***
    // METHOD called when the CONTROLLER is INSTANCIATED
    //                          ***

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
            // INSTANCIATING the METHOD CLASSES
        sciMethods = new SCIMethods();
        clientMethods = new ClientMethods();
            // setting the CLIENT to NULL
        id = null;
            // Initializes the MORAL COMBO BOX DISPLAY
        moralComboBox.getItems().add(clientIsCompany);
        moralComboBox.getItems().add(clientIsPerson);
            // INITIALIZING the COMBO BOX allowing to CHOOSE A SCI
        getSCIList();
        
        
        // adding LISTENERS to the TEXTFIELDS to limit the imput
            // STRING with 30 max char
        clientCodeTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        clientCodeTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        streetAndNumberTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        streetAndNumberTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        streetComplementTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        streetComplementTF.setText(oldValue);
                    }
                }
            }
        });
            // 5 numeric digits
        zipCodeTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,5}")){
                        zipCodeTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 40 max char
        cityTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,40}")){
                        cityTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        identityFirstTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        identityFirstTF.setText(oldValue);
                    }
                }
            }
        });            
            // STRING with 50 max char
        identitySecondTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        identitySecondTF.setText(oldValue);
                    }
                }
            }
        });            
            // STRING with 30 max char
        paymentDelayTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        paymentDelayTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 30 max char
        updateClientCodeTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        updateClientCodeTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        updateStreetAndNumberTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateStreetAndNumberTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        updateStreetComplementTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateStreetComplementTF.setText(oldValue);
                    }
                }
            }
        });
            // 5 numeric digits
        updateZipCodeTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,5}")){
                        updateZipCodeTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 40 max char
        updateCityTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,40}")){
                        updateCityTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        updateIdentityFirstTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateIdentityFirstTF.setText(oldValue);
                    }
                }
            }
        });            
            // STRING with 50 max char
        updateIdentitySecondTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateIdentitySecondTF.setText(oldValue);
                    }
                }
            }
        });            
            // STRING with 30 max char
        updatePaymentDelayTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        updatePaymentDelayTF.setText(oldValue);
                    }
                }
            }
        });

    }



    //                  ***
    //      METHODS RELATED TO THE FXML ELEMENTS
    //                  ***


    
    //                          ***
    // Method called when a SCI is SELECTED from SCI COMBO BOX
    //                          ***

    @FXML
    void handleSelectSCI(ActionEvent event) {
        SCIClass selectedSCI = sciChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedSCI != null){
            this.sciId = selectedSCI.getId();
        }
            // INITIALIZES the CLIENT COMBO BOX
        initializeClientList();
    }



    //                              ***
    // Method called when a CLIENT is SELECTED from CLIENT COMBO BOX
    //                              ***

    @FXML
    void handleSelectClient(ActionEvent event) {
        ClientClass selectedClient = clientChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedClient != null){
                // MEMORIZES the CLIENT ID
            this.id = selectedClient.getId();
                // DISPLAYS in the MODIFICATION FIELDS the CLIENT INFO
            this.updateClientCodeTF.setText(selectedClient.getClientCode());
                // When MORAL is TRUE
            if (selectedClient.getMoral().equals(true)){
                this.displayMoralLabel.setText("Personne morale");
                this.updateIdentityFirstLabel.setText("Type de société (*)");
                this.updateIdentitySecondLabel.setText("Raison sociale (*)");
                this.updateIdentityFirstTF.setText(selectedClient.getCompanyType());
                this.updateIdentitySecondTF.setText(selectedClient.getCompanyName());
                // When MORAL is FALSE
            } else {
                this.displayMoralLabel.setText("Personne physique");
                this.updateIdentityFirstLabel.setText("Prénom (*)");
                this.updateIdentitySecondLabel.setText("Nom (*)");
                this.updateIdentityFirstTF.setText(selectedClient.getFirstName());
                this.updateIdentitySecondTF.setText(selectedClient.getLastName());
            }
            this.updateStreetAndNumberTF.setText(selectedClient.getStreetAndNumber());
            this.updateStreetComplementTF.setText(selectedClient.getStreetComplement());
            this.updateZipCodeTF.setText(selectedClient.getZipCode());
            this.updateCityTF.setText(selectedClient.getCity());
            this.updatePaymentDelayTF.setText(selectedClient.getPaymentDelay());
        }
    }



    //                      ***
    // METHOD called when the CREATION BUTTON is pressed
    //                      ***

    @FXML
    void handleSaveClient(ActionEvent event) {
        if (sciId != null){
                // Declaration of variables
            Boolean moral = null;
            String firstName = null;
            String lastName = null;
            String companyName = null;
            String companyType = null;
                // Fills the relevant values depending on the type of client
            if (moralComboBox.getValue().equals(clientIsCompany)){
                moral = true;
                companyName = identitySecondTF.getText();
                companyType = identityFirstTF.getText();
            } else if (moralComboBox.getValue().equals(clientIsPerson)){
                moral = false;
                firstName = identityFirstTF.getText();
                lastName = identitySecondTF.getText();
            } else {
                clientMethods.alertMissingField(true);
            }
            String clientCode = clientCodeTF.getText();
            String streetAndNumber = streetAndNumberTF.getText();
            String streetComplement = streetComplementTF.getText();
            String zipCode = zipCodeTF.getText();
            String city = cityTF.getText();
            String paymentDelay = paymentDelayTF.getText();

                // ADDS the CLIENT in the database
            Boolean verification = clientMethods.insertClient(sciId, clientCode, moral, streetAndNumber, streetComplement, zipCode, city, streetAndNumber, streetComplement, zipCode, city, firstName, lastName, companyName, companyType, paymentDelay);
            if (verification.equals(true)){
                    // RESETS the CLIENT LIST and EMPTIES the DATA FIELDS
                initializeClientList();
                resetCreationFields();
            }
        }
        else {
            clientMethods.alertMissingField(false);
        }
    }


    
    //                      ***
    // METHOD called when the CREATION BUTTON is pressed
    //                      ***

    @FXML
    void handleUpdateClient(ActionEvent event) {
            // Declaration of variables
        Boolean moral = null;
        String firstName = null;
        String lastName = null;
        String companyName = null;
        String companyType = null;
            // Fills the relevant values depending on the type of client
        if (displayMoralLabel.getText().equals("Personne morale")){
            moral = true;
            companyName = updateIdentitySecondTF.getText();
            companyType = updateIdentityFirstTF.getText();
        } else if (displayMoralLabel.getText().equals("Personne physique")){
            moral = false;
            firstName = updateIdentityFirstTF.getText();
            lastName = updateIdentitySecondTF.getText();
        } else {
            clientMethods.alertMissingField(true);
        }
        String clientCode = updateClientCodeTF.getText();
        String streetAndNumber = updateStreetAndNumberTF.getText();
        String streetComplement = updateStreetComplementTF.getText();
        String zipCode = updateZipCodeTF.getText();
        String city = updateCityTF.getText();
        String paymentDelay = updatePaymentDelayTF.getText();
            // UPDATES the CLIENT
        Boolean verification = clientMethods.updateClient(id, sciId, clientCode, moral, streetAndNumber, streetComplement, zipCode, city, streetAndNumber, streetComplement, zipCode, city, firstName, lastName, companyName, companyType, paymentDelay);
        if (verification.equals(true)){
                // RESETS the CLIENT LIST and EMPTIES the DATA FIELDS
            initializeClientList();
        }
    }



    //                      ***
    // METHOD called when a CLIENT TYPE is SELECTED from the COMBO BOX
    //                      ***

    @FXML
    void handleSelectClientType(ActionEvent event) {
            // sets the IDENTITY FIELDS to NULL
        identityFirstTF.setText(null);
        identitySecondTF.setText(null);
        if (moralComboBox.getValue() == null){
            identityFirstLabel.setText(null);
            identitySecondLabel.setText(null);
        } else {
                // IF the CLIENT is to be MORAL
            if (moralComboBox.getValue().equals(clientIsCompany)){
                identityFirstLabel.setText("Type de société (*)");
                identitySecondLabel.setText("Raison sociale (*)");
                // If the CLIENT is to be a PERSON
            } else if (moralComboBox.getValue().equals(clientIsPerson)){
                identityFirstLabel.setText("Prénom (*)");
                identitySecondLabel.setText("Nom (*)");
            }else {
                identityFirstLabel.setText(null);
                identitySecondLabel.setText(null);
            }
        } 
    }




    //                      ***
    // METHOD called when a CLIENT is DELETED
    //                      ***

    @FXML
    void handleDeleteClient(ActionEvent event) {
        clientMethods.deleteClient(id);
        initializeClientList();
    }



    //                              ***
    //      METHODS related to REFRESHING and INITLIALIZING the INTERFACE
    //                              ***

        // Implements the LIST of SCI in the COMBO BOX
    private void getSCIList() {
        ArrayList sciList = sciMethods.getAllSCIs();
        ObservableList sciComboBoxList = FXCollections.observableArrayList(sciList);
        sciChoiceComboBox.setItems(sciComboBoxList);
    }

        // Implements the LIST of CLIENTS in the COMBO BOX and sets the UPDATE CLIENT DATA FIELDS to NULL
    private void initializeClientList(){
        if (sciId != null){
            ArrayList clienList = clientMethods.getClients(sciId);
            ObservableList clientComboBoxList = FXCollections.observableArrayList(clienList);
            clientChoiceComboBox.setItems(clientComboBoxList);
            updateClientCodeTF.setText(null);
            displayMoralLabel.setText(null);
            updateStreetAndNumberTF.setText(null);
            updateStreetComplementTF.setText(null);
            updateZipCodeTF.setText(null);
            updateCityTF.setText(null);
            updateIdentityFirstLabel.setText(null);
            updateIdentityFirstTF.setText(null);
            updateIdentitySecondLabel.setText(null);
            updateIdentitySecondTF.setText(null);
            updatePaymentDelayTF.setText(null);
            this.id = null;
        }
    }

        // set the CREATE CLIENT DATA FIELDS to NULL
    private void resetCreationFields(){
        clientCodeTF.setText(null);
        moralComboBox.setValue(null);
        streetAndNumberTF.setText(null);
        streetComplementTF.setText(null);
        zipCodeTF.setText(null);
        cityTF.setText(null);
        identityFirstTF.setText(null);
        identitySecondTF.setText(null);
        identityFirstLabel.setText(null);
        identitySecondLabel.setText(null);
        paymentDelayTF.setText(null);
    }


}
