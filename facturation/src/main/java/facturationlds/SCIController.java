package facturationlds;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.SCIMethods.SCIClass;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;


public class SCIController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private ScrollPane sciScrollPane;
    @FXML
    private JFXComboBox<SCIClass> sciChoiceComboBox;
    @FXML
    private JFXTextField updateSCINameTF;
    @FXML
    private JFXTextField updateSiretTF;
    @FXML
    private JFXTextField updateStreetAndNumberTF;
    @FXML
    private JFXTextField updateStreetComplementTF;
    @FXML
    private JFXTextField updateZipCodeTF;
    @FXML
    private JFXTextField updateCityTF;
    @FXML
    private JFXTextField updateEmailTF;
    @FXML
    private JFXTextField updatePhoneTF;
    @FXML
    private JFXTextField updateWebsiteTF;
    @FXML
    private JFXCheckBox updateWirePayCB;
    @FXML
    private JFXCheckBox updateCheckPayCB;
    @FXML
    private JFXTextField updateIbanTF;
    @FXML
    private JFXTextField updateBicTF;
    @FXML
    private JFXTextField updateAccountOwnerTF;
    @FXML
    private JFXTextField updatePenaltiesTF;
    @FXML
    private JFXTextField updateEscompteTF;
    @FXML
    private JFXTextField updateRecoveryTF;
    @FXML
    private JFXTextField updateRCSTF;
    @FXML
    private JFXTextField updateSocialFundsTF;
    @FXML
    private JFXButton updateSCIButton;
    @FXML
    private JFXTextField sciNameTF;
    @FXML
    private JFXTextField siretTF;
    @FXML
    private JFXTextField streetAndNumberTF;
    @FXML
    private JFXTextField streetComplementTF;
    @FXML
    private JFXTextField zipCodeTF;
    @FXML
    private JFXTextField cityTF;
    @FXML
    private JFXTextField emailTF;
    @FXML
    private JFXTextField phoneTF;
    @FXML
    private JFXTextField websiteTF;
    @FXML
    private JFXCheckBox wirePayCB;
    @FXML
    private JFXCheckBox checkPayCB;
    @FXML
    private JFXTextField ibanTF;
    @FXML
    private JFXTextField bicTF;
    @FXML
    private JFXTextField accountOwnerTF;
    @FXML
    private JFXTextField penaltiesTF;
    @FXML
    private JFXTextField escompteTF;
    @FXML
    private JFXTextField recoveryTF;
    @FXML
    private JFXTextField rcsTF;
    @FXML
    private JFXTextField socialFundsTF;
    @FXML
    private JFXButton insertSCIButton;

        // METHOD CLASS
    private SCIMethods sciMethods;

        // Used to keep track of the edited SCI
    private Integer sciId;



    //                          ***
    // METHOD called when the CONTROLLER is INSTANCIATED
    //                          ***

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
            // INSTANCIATED the METHOD CLASS
        sciMethods = new SCIMethods();
            // FILLS the SCI COMBO BOX
        initializeSCIList();

        // Adding LISTENERS to the TEXTFIELDS to LIMIT INPUT
            // STRING with 50 max char
        updateSCINameTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateSCINameTF.setText(oldValue);
                    }
                }
            }
        });
            // 14 numeric digits
        updateSiretTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,14}")){
                        updateSiretTF.setText(oldValue);
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
            // STRING with 40 max char
        updateEmailTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,40}")){
                        updateEmailTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 20 max char
        updatePhoneTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,20}")){
                        updatePhoneTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 60 max char
        updateWebsiteTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,60}")){
                        updateWebsiteTF.setText(oldValue);
                    }
                }
            }
        });
            // CAPITALS and NUMBERS with 34 max char
        updateIbanTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[A-Z0-9 ]{0,34}")){
                        updateIbanTF.setText(oldValue);
                    }
                }
            }
        });
            // CAPITALS and NUMBERS with 14 max char
        updateBicTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[A-Z0-9]{0,14}")){
                        updateBicTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        updateAccountOwnerTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        updateAccountOwnerTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        updatePenaltiesTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        updatePenaltiesTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        updateEscompteTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        updateEscompteTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        updateRecoveryTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        updateRecoveryTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 30 max char
        updateRCSTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        updateRCSTF.setText(oldValue);
                    }
                }
            }
        });
                // FLOAT with TWO decimals max
            updateSocialFundsTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]+[.,]?[0-9]{0,2}")){
                        updateSocialFundsTF.setText(oldValue);
                    }
                }
            }                
        });
            // STRING with 50 max char
        sciNameTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        sciNameTF.setText(oldValue);
                    }
                }
            }
        });
            // 14 numeric digits
        siretTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,14}")){
                        siretTF.setText(oldValue);
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
            // STRING with 40 max char
        emailTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,40}")){
                        emailTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 20 max char
        phoneTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,20}")){
                        phoneTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 60 max char
        websiteTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,60}")){
                        websiteTF.setText(oldValue);
                    }
                }
            }
        });
            // CAPITALS and NUMBERS with 34 max char
        ibanTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[A-Z0-9 ]{0,34}")){
                        ibanTF.setText(oldValue);
                    }
                }
            }
        });
            // CAPITALS and NUMBERS with 14 max char
        bicTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[A-Z0-9]{0,14}")){
                        bicTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 50 max char
        accountOwnerTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,50}")){
                        accountOwnerTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        penaltiesTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        penaltiesTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        escompteTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        escompteTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 100 max char
        recoveryTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,100}")){
                        recoveryTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 30 max char
        rcsTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        rcsTF.setText(oldValue);
                    }
                }
            }
        });
                // FLOAT with TWO decimals max
        socialFundsTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]+[.,]?[0-9]{0,2}")){
                        socialFundsTF.setText(oldValue);
                    }
                }
            }                
        });


    }



    //                        ***
    // METHOD called when the BUTTON to ADD A SCI is pressed
    //                        ***

    @FXML
    void handleInsertSCI(ActionEvent event) {
            // get the DATA from the TEXTFIELDS
        String sciName =  sciNameTF.getText();
        String siret = siretTF.getText();
        String streetAndNumber = streetAndNumberTF.getText();
        String streetComplement = streetComplementTF.getText();
        String zipCode = zipCodeTF.getText();
        String city = cityTF.getText();
        String email = emailTF.getText();
        String phone = phoneTF.getText();
        String website = websiteTF.getText();
        String iban = ibanTF.getText();
        String bic = bicTF.getText();
        String accountOwner = accountOwnerTF.getText();
        String penalties = penaltiesTF.getText();
        String escompte = escompteTF.getText();
        String recovery = recoveryTF.getText();
        String rcs = rcsTF.getText();
        String socialFunds = socialFundsTF.getText();

            // determines the INTEGER to pass to PAYMENT METHOD depending on the related CHECKBOXES state
        Integer paymentMethodInt = sciMethods.generatePaymentMethodInt(checkPayCB.isSelected(), wirePayCB.isSelected());

            // INSERTS the SCI in the DATABASE
        Boolean verification = sciMethods.insertSCI(sciName, siret, streetAndNumber, streetComplement, zipCode, city, email, phone, website, paymentMethodInt, iban, bic, accountOwner, penalties, escompte, recovery, rcs, socialFunds);
        if (verification.equals(true)){
                // REFRESHES the SCI COMBO BOX
            initializeSCIList();
                // RESETS the DATA FIELDS
            resetCreationFields();
        }        
    }



    //                        ***
    // METHOD called when a SCI is SELECTED from the COMBO BOX
    //                        ***

    @FXML
    void handleSCISelection(ActionEvent event) {
            // get the SCI DATA from the DB
        SCIClass selectedSCI = sciChoiceComboBox.getSelectionModel().getSelectedItem();
            // PASSES that data to the UPDATE TEXTFIELDS
        if (selectedSCI != null){
            updateSCINameTF.setText(selectedSCI.getSciName());
            updateSiretTF.setText(selectedSCI.getSiret());
            updateStreetAndNumberTF.setText(selectedSCI.getStreetAndNumber());
            updateStreetComplementTF.setText(selectedSCI.getStreetComplement());
            updateZipCodeTF.setText(selectedSCI.getZipCode());
            updateCityTF.setText(selectedSCI.getCity());
            updateEmailTF.setText(selectedSCI.getEmail());
            updatePhoneTF.setText(selectedSCI.getPhone());
            updateWebsiteTF.setText(selectedSCI.getWebsite());
            System.out.println(selectedSCI.getPaymentMethod());
            switch (selectedSCI.getPaymentMethod()) {
                case 2:
                    updateWirePayCB.setSelected(true);
                    updateCheckPayCB.setSelected(true);
                    break;   
                case 1:
                    updateWirePayCB.setSelected(true);
                    updateCheckPayCB.setSelected(false);
                    break;   
                case 0:
                    updateWirePayCB.setSelected(false);
                    updateCheckPayCB.setSelected(true);
                    break;          
                default:
                    updateWirePayCB.setSelected(false);
                    updateCheckPayCB.setSelected(true);
                    break;
            }
            updateIbanTF.setText(selectedSCI.getIban());
            updateBicTF.setText(selectedSCI.getBic());
            updateAccountOwnerTF.setText(selectedSCI.getAccountOwner());
            updatePenaltiesTF.setText(selectedSCI.getPenalties());
            updateEscompteTF.setText(selectedSCI.getEscompte());
            updateRecoveryTF.setText(selectedSCI.getRecovery());
            updateRCSTF.setText(selectedSCI.getRCS());
            updateSocialFundsTF.setText(selectedSCI.getSocialFunds());            
            this.sciId = selectedSCI.getId();
        }
    }



    //                        ***
    // METHOD called when the UPDATE SCI BUTTON is pressed
    //                        ***

    @FXML
    void handleUpdateSCI(ActionEvent event) {
            // GETS the DATA from the TEXTFIELDS
        String sciName =  updateSCINameTF.getText();
        String siret = updateSiretTF.getText();
        String streetAndNumber = updateStreetAndNumberTF.getText();
        String streetComplement = updateStreetComplementTF.getText();
        String zipCode = updateZipCodeTF.getText();
        String city = updateCityTF.getText();
        String email = updateEmailTF.getText();
        String phone = updatePhoneTF.getText();
        String website = updateWebsiteTF.getText();
        String iban = updateIbanTF.getText();
        String bic = updateBicTF.getText();
        String accountOwner = updateAccountOwnerTF.getText();
        String penalties = updatePenaltiesTF.getText();
        String escompte = updateEscompteTF.getText();
        String recovery = updateRecoveryTF.getText();
        String rcs = updateRCSTF.getText();
        String socialFunds = updateSocialFundsTF.getText();

            // determines the INTEGER to pass to PAYMENT METHOD depending on the related CHECKBOXES state
        Integer paymentMethodInt = sciMethods.generatePaymentMethodInt(updateCheckPayCB.isSelected(), updateWirePayCB.isSelected());
            // UPDATES the SCI in the DATABASE
        Boolean verification = sciMethods.updateSCI(sciId, sciName, siret, streetAndNumber, streetComplement, zipCode, city, email, phone, website, paymentMethodInt, iban, bic, accountOwner, penalties, escompte, recovery, rcs, socialFunds);
        if (verification.equals(true)){
                // REFRESHES the SCI COMBO BOX
            initializeSCIList();
        }
    }



    //                              ***
    //      METHODS related to REFRESHING and INITLIALIZING the INTERFACE
    //                              ***

        // Set the list of the scis and resets the fields of the update section
    private void initializeSCIList() {
        ArrayList sciList = sciMethods.getAllSCIs();
        ObservableList sciComboBoxList = FXCollections.observableArrayList(sciList);
        sciChoiceComboBox.setItems(sciComboBoxList);
        updateSCINameTF.setText(null);
        updateSiretTF.setText(null);
        updateStreetAndNumberTF.setText(null);
        updateStreetComplementTF.setText(null);
        updateZipCodeTF.setText(null);
        updateCityTF.setText(null);
        updateEmailTF.setText(null);
        updatePhoneTF.setText(null);
        updateWebsiteTF.setText(null);
        updateWirePayCB.setSelected(false);
        updateCheckPayCB.setSelected(false);
        updateIbanTF.setText(null);
        updateBicTF.setText(null);
        updateAccountOwnerTF.setText(null);
        updatePenaltiesTF.setText(null);
        updateEscompteTF.setText(null);
        updateRecoveryTF.setText(null);
        updateRCSTF.setText(null);
        updateSocialFundsTF.setText(null);

        this.sciId = null;
    }

        // reset the creation fields
    private void resetCreationFields(){
        sciNameTF.setText(null);
        siretTF.setText(null);
        streetAndNumberTF.setText(null);
        streetComplementTF.setText(null);
        zipCodeTF.setText(null);
        cityTF.setText(null);
        emailTF.setText(null);
        phoneTF.setText(null);
        websiteTF.setText(null);
        wirePayCB.setSelected(false);
        checkPayCB.setSelected(false);
        ibanTF.setText(null);
        bicTF.setText(null);
        accountOwnerTF.setText(null);
        penaltiesTF.setText(null);
        escompteTF.setText(null);
        recoveryTF.setText(null);
        rcsTF.setText(null);
        socialFundsTF.setText(null);
    }

}
