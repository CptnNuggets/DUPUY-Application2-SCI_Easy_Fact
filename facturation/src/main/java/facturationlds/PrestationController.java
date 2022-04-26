package facturationlds;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import facturationlds.databaseClasses.ClientMethods;
import facturationlds.databaseClasses.PrestationMethods;
import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.PrestationMethods.DefaultPrestationTableLine;
import facturationlds.databaseClasses.PrestationMethods.PrestationClass;
import facturationlds.databaseClasses.SCIMethods.SCIClass;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class PrestationController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private JFXComboBox<SCIClass> sciChoiceComboBox;
    @FXML
    private JFXComboBox<ClientClass> clientChoiceComboBox;
    @FXML
    private JFXComboBox<PrestationClass> prestationChoiceComboBox;
    @FXML
    private JFXTextField descriptionTF;
    @FXML
    private JFXTextField unitaryPriceTF;
    @FXML
    private JFXTextField tvaPercentageTF;
    @FXML
    private JFXButton defaultPrestationButton;
    @FXML
    private JFXTextField generalPrestationTF;
    @FXML
    private JFXButton savePrestationButton;
    @FXML
    private JFXTreeTableView<LineToDisplay> prestationsPerClientTreeTableView;
    @FXML
    private JFXTreeTableView<GeneralPrestaDisplay> generalPrestationsTable;

        // METHOD CLASSES
    ClientMethods clientMethods;
    SCIMethods sciMethods;
    PrestationMethods prestaMethods;

        // Used to reference SCI, CLIENT and PRESTATION classes
    private Integer clientId;
    private Integer sciId;
    private Integer prestaId;



    //              ***
    // COLUMNS off the TREETABLEVIEWS
    //              ***
        // Default Presta TableView
    JFXTreeTableColumn<LineToDisplay,String>prestaColumn;
    JFXTreeTableColumn<LineToDisplay,String>descriptionColumn;
    JFXTreeTableColumn<LineToDisplay,String>unitPriceColumn;
    JFXTreeTableColumn<LineToDisplay,String>tvaPercentageColumn;
    JFXTreeTableColumn<LineToDisplay,String>deleteColumn;
        // General Presta TableView    
    JFXTreeTableColumn<GeneralPrestaDisplay,String>generalPrestaColumn;
    JFXTreeTableColumn<GeneralPrestaDisplay,String>deleteGeneralPrestaColumn;

    

    //                          ***
    // CLASS formated to pass the infos to DISPLAY in the DEFAULT PRESTATION TREETABLEVIEW
    //                          ***

    class LineToDisplay extends RecursiveTreeObject<LineToDisplay> {
        StringProperty prestaCol;
        StringProperty descriptionCol;
        StringProperty unitPriceCol;
        StringProperty tvaPercentageCol;
        Integer defaultPrestaId;

        public LineToDisplay(String presta, String description, 
        Float unitPrice, Float tvaPercentage, Integer defaultPrestaId)
        {
            this.prestaCol = new SimpleStringProperty(presta);
            this.descriptionCol = new SimpleStringProperty(description);
            this.unitPriceCol = new SimpleStringProperty(String.format("%.2f", unitPrice));
            this.tvaPercentageCol = new SimpleStringProperty(tvaPercentage.toString());
            this.defaultPrestaId = defaultPrestaId;
        }

        public Integer getId(){return defaultPrestaId;}

    }



    //                          ***
    // CLASS formated to pass the infos to DISPLAY in the GENERAL PRESTATION TREETABLEVIEW
    //                          ***

    class GeneralPrestaDisplay extends RecursiveTreeObject<GeneralPrestaDisplay> {
        StringProperty generalPrestaCol;
        Integer prestaId;

        public GeneralPrestaDisplay(String presta, Integer prestaId)
        {
            this.generalPrestaCol = new SimpleStringProperty(presta);
            this.prestaId = prestaId;
        }

        public Integer getPrestaId(){return prestaId;}
    }



    //                          ***
    // METHOD called when the CONTROLLER is INSTANCIATED
    //                          ***

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
            // INSTANCIATING the METHOD CLASSES
        clientMethods = new ClientMethods();
        sciMethods = new SCIMethods();
        prestaMethods = new PrestationMethods();
        
            // INITIALIZES the VARIABLES
        sciId = null;
        clientId = null;
        prestaId = null;

            // FILLS the SCI COMBO BOX
        getSCIList();
            // FILLS the PRESTATION COMBO BOX
        getPrestaList();
        
        // adding LISTENERS to the TEXTFIELDS to limit the imput
            // FLOAT with TWO digits and TWO decimals max
        tvaPercentageTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                    // REGEX CONDITION TO SPECIFY THE ADEQUATE FORMAT
                if (newValue != null){
                    if (oldValue != null){
                        if (oldValue.matches("[0-9]{1,2}[.,]{1}[0-9]{1,2}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{1}[0-9]{0,2}")){
                                tvaPercentageTF.setText(oldValue);
                            }
                        } else if (oldValue.matches("[0-9]{1,2}[.,]{1}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{0,1}[0-9]{0,2}")){
                                tvaPercentageTF.setText(oldValue);
                            }
                        } else if (oldValue.matches("[0-9]{0,2}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{0,1}")){
                                tvaPercentageTF.setText(oldValue);
                            }
                        }
                    } else {   
                        if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}")){
                            tvaPercentageTF.setText(oldValue);
                        }
                    }    
                }                
            }                
        });
                // FLOAT with TWO decimals max
        unitaryPriceTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]+[.,]?[0-9]{0,2}")){
                        unitaryPriceTF.setText(oldValue);
                    }
                }
            }                
        });
            // STRING with 70 max char
        descriptionTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,60}")){
                        descriptionTF.setText(oldValue);
                    }
                }
            }
        });
            // STRING with 30 max char
        generalPrestationTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,30}")){
                        generalPrestationTF.setText(oldValue);
                    }
                }
            }
        });


            // PARAMETERS to BUILD the COLUMNS of the TREETABLEVIEW
            
        prestaColumn = new JFXTreeTableColumn<>("Prestation");
        prestaColumn.setPrefWidth(150);
        prestaColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().prestaCol;
            }
        });

        descriptionColumn = new JFXTreeTableColumn<>("Description");
        descriptionColumn.setPrefWidth(300);
        descriptionColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().descriptionCol;
            }
        });

        unitPriceColumn = new JFXTreeTableColumn<>("Prix unitaire HT");
        unitPriceColumn.setPrefWidth(150);
        unitPriceColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().unitPriceCol;
            }
        });

        tvaPercentageColumn = new JFXTreeTableColumn<>("Taux de TVA (%)");
        tvaPercentageColumn.setPrefWidth(150);
        tvaPercentageColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().tvaPercentageCol;
            }
        });
        deleteColumn = new JFXTreeTableColumn<>("");
        deleteColumn.setPrefWidth(100);
        deleteColumn.setCellFactory(new Callback<TreeTableColumn<LineToDisplay, String>, TreeTableCell<LineToDisplay, String>>(){
            @Override
            public TreeTableCell<LineToDisplay, String> call(TreeTableColumn<LineToDisplay, String> param) {
                final TreeTableCell<LineToDisplay, String> cell = new TreeTableCell<LineToDisplay, String>(){
                    final JFXButton btn = new JFXButton();
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTreeTableRow().getTreeItem() != null){
                                LineToDisplay currentLine = getTreeTableRow().getTreeItem().getValue();
                                Integer lineId = currentLine.getId();
                                if (!lineId.equals(0)){
                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
                                    btn.setText("Supprimer");
                                    btn.setOnAction(event -> {
                                        handleDeleteDefaultPrestationButton(lineId);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                } else {
                                    setGraphic(null);
                                    setText(null);
                                }
                            } else {
                                setGraphic(null);
                                setText(null);
                            }
                            
                        }
                    }
                };
                return cell;
            }
        });

        generalPrestaColumn = new JFXTreeTableColumn<>("Prestation");
        generalPrestaColumn.setPrefWidth(350);
        generalPrestaColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<GeneralPrestaDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<GeneralPrestaDisplay,String> param) {
                return param.getValue().getValue().generalPrestaCol;
            }
        });

        deleteGeneralPrestaColumn = new JFXTreeTableColumn<>("");
        deleteGeneralPrestaColumn.setPrefWidth(100);
        deleteGeneralPrestaColumn.setCellFactory(new Callback<TreeTableColumn<GeneralPrestaDisplay, String>, TreeTableCell<GeneralPrestaDisplay, String>>(){
            @Override
            public TreeTableCell<GeneralPrestaDisplay, String> call(TreeTableColumn<GeneralPrestaDisplay, String> param) {
                final TreeTableCell<GeneralPrestaDisplay, String> cell = new TreeTableCell<GeneralPrestaDisplay, String>(){
                    final JFXButton btn = new JFXButton();                    
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTreeTableRow().getTreeItem() != null){
                                GeneralPrestaDisplay currentLine = getTreeTableRow().getTreeItem().getValue();
                                Integer prestaId = currentLine.getPrestaId();
                                if (prestaId != null){
                                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                                        btn.setText("Supprimer");
                                        btn.setOnAction(event -> {
                                            handleDeleteGeneralPrestationButton(prestaId);
                                        });
                                        setGraphic(btn);
                                        setText(null);        
                                } else {
                                    setGraphic(null);
                                    setText(null);
                                }
                            } else {
                                setGraphic(null);
                                setText(null);
                            }                            
                        }
                    }
                };
                return cell;
            }
        });
       
            // FILLS the PRESTATION LISTVIEW
        displayGeneralPrestations();
    }

    

    //                                      ***
    //      METHOD called when the DEFAULT PRESTATION CREATION BUTTON is pressed
    //                                      ***

    @FXML
    void handleDefaultPrestationCreation(ActionEvent event) throws ParseException {
        if ( clientId != null && prestaId != null){

                // FORMATS the content of the TEXTFIELDS to NUMERICAL values
            DecimalFormat df = new DecimalFormat();
            Float unitPrice = df.parse(unitaryPriceTF.getText()).floatValue() ;
            Float tvaPercentage = df.parse(tvaPercentageTF.getText()).floatValue();
                // stores the DESCRIPTION
            String description = descriptionTF.getText();
                // CRATES a DEFAULT TARIF in the DATABASE
            Boolean verification = prestaMethods.insertDefaultTarif(prestaId, unitPrice, tvaPercentage, description, clientId);
            if (verification.equals(true)){
                    // REFRESHES the DISPLAYS and RESETS the DATA FIELDS
                resetDescriptionCreationFields();
                getPrestaList();
                displayDefaultPrestations(clientId);
            }
        }
    }



    //                                  ***
    //      METHOD called when the SAVE GENERAL PRESTATION BUTTON is pressed
    //                                  ***

    @FXML
    void handleSavePrestation(ActionEvent event) {
            // CREATES the PRESTATION in the DATABASE
        String prestaName = generalPrestationTF.getText();
        Boolean verification = prestaMethods.insertPrestation(prestaName);
        if (verification.equals(true)){
                // RESETS the DATA FIELD
            resetGeneralCreationField();
        }
            // REFRESHES the INTERFACE
        getPrestaList();
        displayGeneralPrestations();
    }

    

    //                                  ***
    //      METHOD called when  a CLIENT is SELECTED from the COMBO BOX
    //                                  ***

    @FXML
    void handleSelectClient(ActionEvent event) {
        ClientClass selectedClient = clientChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedClient != null){
            this.clientId = selectedClient.getId();
        }
            // DISPLAYS the CLIENT DEFAULT PRESTATIONS
        displayDefaultPrestations(clientId);
    }



    //                                  ***
    //      METHOD called when a PRESTATION is SELECTED from the COMBO BOX
    //                                  ***

    @FXML
    void handleSelectPrestation(ActionEvent event) {
        PrestationClass selectedPrestation = prestationChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedPrestation != null){
            this.prestaId = selectedPrestation.getId();
        }
    }



    //                             ***
    //      METHOD called when a SCI is SELECTED from the COMBO BOX
    //                             ***

    @FXML
    void handleSelectSCI(ActionEvent event) {
        SCIClass selectedSCI = sciChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedSCI != null){
            this.sciId = selectedSCI.getId();
        }

            // FILLS the CLIENT COMBO BOX with relevant clients
        initializeClientList();
            // RESETS the DEFAULT PRESTATION DISPLAY
        displayDefaultPrestations(null);
    }



    
    //                                      ***
    // METHOD called when the DELETION BUTTON is pressed in the GENERAL PRESTATION TABLEVIEW
    //                                      ***

    public void handleDeleteGeneralPrestationButton(Integer prestaId){
        // calls a method to make the prestation not visible
        prestaMethods.hidePrestation(prestaId);

        displayGeneralPrestations();
        displayDefaultPrestations(clientId);
        getPrestaList();
    }



    //                              ***
    // METHOD called when the DELETION BUTTON is pressed in the DEFAULT PRESTATION TABLEVIEW
    //                              ***

    public void handleDeleteDefaultPrestationButton(Integer lineId){
        prestaMethods.deleteDefaultPrestation(lineId);
        displayDefaultPrestations(clientId);
    }



    //                              ***
    //      METHODS related to REFRESHING and INITLIALIZING the INTERFACE
    //                              ***

        // Set the list of the PRESTATIONS in the COMBO BOX
    private void getPrestaList() {
        ArrayList prestaList = prestaMethods.getPrestations();
        ObservableList prestaComboBoxList = FXCollections.observableArrayList(prestaList);
        prestationChoiceComboBox.setItems(prestaComboBoxList);
    }

        // Set the list of the SCIs in the COMBO BOX
    private void getSCIList() {
        ArrayList sciList = sciMethods.getAllSCIs();
        ObservableList sciComboBoxList = FXCollections.observableArrayList(sciList);
        sciChoiceComboBox.setItems(sciComboBoxList);
    }

        // set the list of the CLIENTS in the COMBO BOX
    private void initializeClientList(){
        if (sciId != null){
            ArrayList clienList = clientMethods.getClients(sciId);
            ObservableList clientComboBoxList = FXCollections.observableArrayList(clienList);
            clientChoiceComboBox.setItems(clientComboBoxList);
                // RESETS the MEMORIZED CLIENT
            this.clientId = null;
        }
    }

        // resets the general prestation creation field
    private void resetGeneralCreationField(){
        generalPrestationTF.setText(null);
    }

        // resets the default prestation creation fields
    private void resetDescriptionCreationFields(){
        descriptionTF.setText(null);
        unitaryPriceTF.setText(null);
        tvaPercentageTF.setText(null);
    }

        // Fills the DISPLAY DEFAULT PRESTATIONS TABLEVIEW
    private void displayDefaultPrestations(Integer clientId){
        
            // OBSERVABLE LIST containing the LINES TO DISPLAY
        ObservableList<LineToDisplay> linesToDisplay = FXCollections.observableArrayList();

        if (clientId != null){
                // gets the DATA from the DATABASE
            ArrayList<DefaultPrestationTableLine> arrayOfDefaultPrestations = prestaMethods.getDefaultPrestationPerClient(clientId);
                // PARSES the DATA ARRAY
            for (DefaultPrestationTableLine defaultPresta : arrayOfDefaultPrestations){
                    // CREATES a new LINE TO DISPLAY
                LineToDisplay defaultPrestaToDisplay = new LineToDisplay(
                                                    defaultPresta.getPrestaCol(),
                                                    defaultPresta.getDescriptionCol(),
                                                    defaultPresta.getUnitPriceCol(),
                                                    defaultPresta.getTVAPercentageCol(),
                                                    defaultPresta.getTarifId()
                                                );
                    // ADDS it to the OBSERVABLE LIST
                linesToDisplay.add(defaultPrestaToDisplay);
            }
        } 
            // CREATES the ROOT of the TREE TABLE VIEW (root of the OBSERVABLE LIST)
        final TreeItem<LineToDisplay> root = new RecursiveTreeItem<LineToDisplay>(linesToDisplay, RecursiveTreeObject::getChildren);
            
            // SETS the COLUMNS and the ROOT of the TREE TABLE VIEW
        prestationsPerClientTreeTableView.getColumns().setAll(prestaColumn, descriptionColumn, unitPriceColumn, tvaPercentageColumn, deleteColumn);
        prestationsPerClientTreeTableView.setRoot(root);
        prestationsPerClientTreeTableView.setShowRoot(false);
    }

        // DISPLAYS the GENERAL PRESTATIONS in the LISTVIEW
    private void displayGeneralPrestations(){
            
        ArrayList<PrestationClass> prestaList = prestaMethods.getPrestations();
            // TABLEVIEW
        ObservableList<GeneralPrestaDisplay> generalPrestaList = FXCollections.observableArrayList();
            // PARSES the DATA ARRAY
        for (PrestationClass generalPresta : prestaList){
            // CREATES a new LINE TO DISPLAY
            GeneralPrestaDisplay generalPrestaToDisplay = new GeneralPrestaDisplay(generalPresta.getPrestaName(), generalPresta.getId());
            generalPrestaList.add(generalPrestaToDisplay);
        }
            // CREATES the ROOT of the TREE TABLE VIEW (root of the OBSERVABLE LIST)
        final TreeItem<GeneralPrestaDisplay> root = new RecursiveTreeItem<GeneralPrestaDisplay>(generalPrestaList, RecursiveTreeObject::getChildren);
          
            // SETS the COLUMNS and the ROOT of the TREE TABLE VIEW
        generalPrestationsTable.getColumns().setAll(generalPrestaColumn, deleteGeneralPrestaColumn);
        generalPrestationsTable.setRoot(root);
        generalPrestationsTable.setShowRoot(false);
    }
}
