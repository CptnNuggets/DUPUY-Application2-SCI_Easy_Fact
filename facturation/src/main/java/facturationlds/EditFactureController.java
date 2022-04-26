package facturationlds;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import facturationlds.databaseClasses.ClientMethods;
import facturationlds.databaseClasses.FactureMethods;
import facturationlds.databaseClasses.PrestationMethods;
import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.FactureMethods.FactureClass;
import facturationlds.databaseClasses.PrestationMethods.DefaultPrestationTableLine;
import facturationlds.databaseClasses.PrestationMethods.PrestationClass;
import facturationlds.databaseClasses.PrestationMethods.TarifForFactureClass;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class EditFactureController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private JFXComboBox<PrestationClass> prestationChoiceComboBox;
    @FXML
    private JFXTextField descriptionTF;
    @FXML
    private JFXTextField unitaryPriceTF;
    @FXML
    private JFXTextField tvaPercentageTF;
    @FXML
    private JFXButton addPrestationButton;
    @FXML
    private Label sciLabel;
    @FXML
    private Label clientLabel;
    @FXML
    private Label factureLabel;
    @FXML
    private JFXComboBox<DefaultPrestationTableLine> defaultPrestaComboBox;
    @FXML
    private JFXCheckBox definitiveCheckBox;
    @FXML
    private JFXTextField quantityTF;
    @FXML
    private JFXTreeTableView<LineToDisplay> prestationsTreeTableView;
    @FXML
    private JFXTextArea commentTA;
    @FXML
    private JFXButton saveFactureButton;

        // METHOD CLASSES
    private SCIMethods sciMethods;
    private ClientMethods clientMethods;
    private FactureMethods factureMethods;
    private PrestationMethods prestationMethods;

        // DATABASE CLASSES
    private SCIClass sci;
    private ClientClass client;
    private FactureClass factureBeingEdited;
    
        // Used to keep trace that a facture is being turned to DEFINITIVE
    private Boolean definitive;
        // Used for comparison and allowing revert when PREVIEWING THE DEFINITIVE FACTURE CODE
    private String oldTemporaryFactureCode;
        // Used to reference the PRESTATION in a new or edited facture line
    private Integer prestaId;    
    private String prestaName;
    
        // Array of FACTURE LINES being MODIFIED, ADDED or DELETED
    private ArrayList<TarifForFactureClass> tarifArray;
        // List of EXISTING PRESTATIONS
    private ArrayList<PrestationClass> prestaList;
        //  Class used to pass the parameter to the COMBO BOX when EDITING a PRESTATION
    private PrestationClass prestationForEdition;
        // FACTURE LINE used when EDITING a PRESTATION
    private TarifForFactureClass tarif;

        // PARENT CONTROLLER called when the EDITION WINDOW IS EXITED
    FactureController parentFactureController;

    

    //              ***
    // COLUMNS off the TREETABLEVIEW
    //              ***

    JFXTreeTableColumn<LineToDisplay,String>prestaColumn;
    JFXTreeTableColumn<LineToDisplay,String>descriptionColumn;
    JFXTreeTableColumn<LineToDisplay,String>quantityColumn;
    JFXTreeTableColumn<LineToDisplay,String>unitPriceColumn;
    JFXTreeTableColumn<LineToDisplay,String>htColumn;
    JFXTreeTableColumn<LineToDisplay,String>tvaColumn;
    JFXTreeTableColumn<LineToDisplay,String>ttcColumn;
    JFXTreeTableColumn<LineToDisplay,String>editColumn;
    JFXTreeTableColumn<LineToDisplay,String>deleteColumn;



    //                          ***
    // CLASS formated to pass the infos to DISPLAY in the TREETABLEVIEW
    //                          ***

    class LineToDisplay extends RecursiveTreeObject<LineToDisplay> {
        StringProperty prestaCol;
        StringProperty descriptionCol;
        StringProperty quantityCol;
        StringProperty unitPriceCol;
        StringProperty htCol;
        StringProperty tvaCol;
        StringProperty ttcCol;
        Integer idForDisplay;

        public LineToDisplay(String presta, String description, Integer quantity,
        Float unitPrice, Float tvaPercentage, Integer idForDisplay)
        {
            Float ht = unitPrice*quantity;
            Float tva = ht*tvaPercentage/100;
            Float ttc = ht + tva;

            this.prestaCol = new SimpleStringProperty(presta);
            this.descriptionCol = new SimpleStringProperty(description);
            this.quantityCol = new SimpleStringProperty(quantity.toString());
            this.unitPriceCol = new SimpleStringProperty(String.format("%.2f", unitPrice));
            this.htCol = new SimpleStringProperty(String.format("%.2f", ht));
            this.tvaCol = new SimpleStringProperty(String.format("%.2f", tva));
            this.ttcCol = new SimpleStringProperty(String.format("%.2f", ttc));
            this.idForDisplay = idForDisplay; 
        }

        public Integer getIdForDisplay(){return idForDisplay;}

    }



    //                          ***
    // METHOD called when the CONTROLLER is INSTANCIATED
    //                          ***

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

            // INSTANCIATING the METHOD CLASSES
        sciMethods = new SCIMethods();
        clientMethods = new ClientMethods();
        factureMethods = new FactureMethods();
        prestationMethods = new PrestationMethods();
            // INITIALIZING the COMBO BOX allowing prestations choices
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
                        if (!newValue.isEmpty()){
                            if (newValue.matches("[0-9]{1,2}[.,]{1}[0-9]{1,2}")){

                            } else if (!newValue.matches("[0-9]{1,2}")){
                                tvaPercentageTF.setText(oldValue);
                            }
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
            // INT with 4 digits max
        quantityTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,4}")){
                        quantityTF.setText(oldValue);
                    }
                }
            }                
        });
            // STRING with 300 max char
        commentTA.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,300}")){
                        commentTA.setText(oldValue);
                    }
                }
            }
        });
        
            // PARAMETERS to BUILD the COLUMNS of the TREETABLEVIEW

        prestaColumn = new JFXTreeTableColumn<>("Prestation");
        prestaColumn.setPrefWidth(100);
        prestaColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().prestaCol;
            }
        });

        descriptionColumn = new JFXTreeTableColumn<>("Détails");
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().descriptionCol;
            }
        });

        unitPriceColumn = new JFXTreeTableColumn<>("Prix unitaire HT");
        unitPriceColumn.setPrefWidth(80);
        unitPriceColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().unitPriceCol;
            }
        });

        quantityColumn = new JFXTreeTableColumn<>("Quantité");
        quantityColumn.setPrefWidth(80);
        quantityColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().quantityCol;
            }
        });

        htColumn = new JFXTreeTableColumn<>("Total HT");
        htColumn.setPrefWidth(80);
        htColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().htCol;
            }
        });

        tvaColumn = new JFXTreeTableColumn<>("Total TVA");
        tvaColumn.setPrefWidth(80);
        tvaColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().tvaCol;
            }
        });

        ttcColumn = new JFXTreeTableColumn<>("Total TTC");
        ttcColumn.setPrefWidth(80);
        ttcColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().ttcCol;
            }
        });

        editColumn = new JFXTreeTableColumn<>("");
        editColumn.setPrefWidth(100);
        editColumn.setCellFactory(new Callback<TreeTableColumn<LineToDisplay, String>, TreeTableCell<LineToDisplay, String>>(){
            
                // CALL to build the CELL of a column containing a BUTTON

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
                                Integer lineId = currentLine.getIdForDisplay();
                                if (!lineId.equals(0)){
                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
                                    btn.setText("Modifier");
                                    btn.setOnAction(event -> {
                                        handleEditPrestationButton(lineId);
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
                                Integer lineId = currentLine.getIdForDisplay();
                                if (!lineId.equals(0)){
                                    btn.setButtonType(JFXButton.ButtonType.RAISED);
                                    btn.setText("Supprimer");
                                    btn.setOnAction(event -> {
                                        handleDeletePrestationButton(lineId);
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
    }


    
    //                                  ***
    // INITIALIZATION METHOD when the controller is called for A NEW FACTURE
    //                                  ***

    public void initData(Integer sciId, Integer clientId, FactureController parentFactureController){
        
            // passing the controller
        this.parentFactureController = parentFactureController;

            // creates a FACTURE CLASS that will be used to store the information
        factureBeingEdited = factureMethods.new FactureClass(
            null, 
            sciId, 
            clientId, 
            factureMethods.generateFactureCode(sciId, false), 
            false, 
            false, 
            null, 
            (float) 0, 
            (float) 0, 
            (float) 0, 
            null
        );
            // get the SCI and CLIENT CLASSES related to the facture
        sci = sciMethods.getSCI(sciId);
        sciLabel.setText(sci.getSciName());
        client = clientMethods.getClient(clientId);
        clientLabel.setText(client.getClientCode());
        
            // generate the FACTURE CODE
        factureLabel.setText(factureBeingEdited.getFactureCode());

            // NO OLD FACTURE CODE for a new facture
        this.oldTemporaryFactureCode = null;

            // NO FACTURE LINES for a new facture
        tarifArray = new ArrayList<TarifForFactureClass>();        
        displayPrestations();

            // creates the COMBO BOX containing the DEFAULT PRESTATIONS
        getDefaultPrestaList(clientId);
    }



    //                                  ***
    // INITIALIZATION METHOD when the controller is called for AN EXISTING FACTURE
    //                                  ***

    public void initData(Integer factureId, FactureController parentFactureController){
       
            // passing the controller
        this.parentFactureController = parentFactureController;

            // get the FACTURE CLASS based on the FACTURE ID
        factureBeingEdited = factureMethods.getFacture(factureId);
        factureLabel.setText(factureBeingEdited.getFactureCode());
        
            // get the SCI and CLIENT CLASSES
        this.sci = sciMethods.getSCI(factureBeingEdited.getSciId());
        this.sciLabel.setText(sci.getSciName());
        this.client = clientMethods.getClient(factureBeingEdited.getClientId());
        this.clientLabel.setText(client.getClientCode());

            // fills the INFOS and FACTURE LINES with existing data
        this.commentTA.setText(factureBeingEdited.getComment());
        this.oldTemporaryFactureCode = factureBeingEdited.getFactureCode();
        tarifArray = prestationMethods.getTarifsForFacture(factureBeingEdited.getId());
        displayPrestations();
        
            // creates the COMBO BOX containing the DEFAULT PRESTATIONS
        getDefaultPrestaList(factureBeingEdited.getClientId());
    }



    //                          ***
    // LISTENER for the interaction with the DEFINITIVE CHECK BOX
    //                          ***

    @FXML
    void handleDefinitiveChange(ActionEvent event) {
            // affects a FACTURE CODE based on the existing codes in the database and the code ALREADY EXISTING OR NOT for the facture
        if (definitiveCheckBox.isSelected()){
            definitive = true;
            factureBeingEdited.setFactureCode(factureMethods.generateFactureCode(factureBeingEdited.getSciId(), true));
        } else {
            definitive = false;
            if (oldTemporaryFactureCode != null){
                factureBeingEdited.setFactureCode(oldTemporaryFactureCode);
            } else {
                factureBeingEdited.setFactureCode(factureMethods.generateFactureCode(factureBeingEdited.getSciId(), false));
            }
        }
        factureLabel.setText(factureBeingEdited.getFactureCode());
    }



    //                              ***
    // METHOD called when the BUTTON to ADD A PRESTATION is pressed
    //                              ***
    
    @FXML
    void handleAddPrestation(ActionEvent event) throws ParseException {
        
            // FORMATS the content of the TEXTFIELDS to NUMERICAL values
        DecimalFormat df = new DecimalFormat();
        Float unitPrice = df.parse(unitaryPriceTF.getText()).floatValue();
        Float tvaPercentage = df.parse(tvaPercentageTF.getText()).floatValue();
        Integer quantity = df.parse(quantityTF.getText()).intValue();
      

        if (prestaId != null && !prestaName.isEmpty() && unitPrice != null && tvaPercentage != null ){
            
                // gets the NEXT AVAILABLE ID from the TARIF ARRAY ( != database id)
            int i = 1;
            for (TarifForFactureClass tarif : tarifArray){
                if (tarif.getIdForDisplay() >= i){
                    i = tarif.getIdForDisplay() +1;
                }
            }

                // Creates a NEW FACTURE LINE filled with the information of the TEXTFIELDS
            TarifForFactureClass tarif = prestationMethods.new TarifForFactureClass(
                null, 
                prestaId, 
                // factureId,
                factureBeingEdited.getId(),
                prestaName,
                quantity,
                unitPrice, 
                tvaPercentage, 
                descriptionTF.getText(),
                false,
                false,
                false,
                i
            );
    
                // ADDS the line to the ARRAY and REFRESH THE DISPLAY
            tarifArray.add(tarif);
            displayPrestations();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Informations manquantes");
            alert.setContentText("Saisissez toutes les informations obligatoires");
            alert.showAndWait();
            return;
        }
    }



    //                              ***
    // METHOD called when the FACTURE is SAVED, which EXITS the WINDOW
    //                              ***

    @FXML
    void handleSaveFacture(ActionEvent event) {
        Boolean isEmpty = true;
        for (TarifForFactureClass tarif : tarifArray){
            if (tarif.getToDelete().equals(false) || tarif.getExistence().equals(false)){
                isEmpty = false;
            }
        }

        if (isEmpty.equals(true)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Corps de la facture vide");
            alert.setContentText("Saisissez au moins une prestation à facturer.");
            alert.showAndWait();
            return;
        } else {
                // records the DATE
                factureBeingEdited.setDefinitiveDate();

                // sets the DEFINITIVE value to TRUE if the facture is VALIDATED
            if (definitive == null){
                definitive = false;
            }
            if (definitive.equals(true)){
                factureBeingEdited.setToDefinitive();            
            }

                // determines the TOTAL VALUES and ADD THEM to the facture
            calculateFacturePrices();
                // adds the COMMENT field
            factureBeingEdited.setComment(commentTA.getText());

                // EXTRA STEPS if this is a NEW FACTURE
            if (factureBeingEdited.getId() == null){

                        // ADDS the FACTURE to the DATABASE
                factureMethods.insertNewFacture(factureBeingEdited);

                        // MEMORIZE the SCI_ID and FACTURE_CODE from the FACTURE (UNIQUE in the database)
                Integer sciId = factureBeingEdited.getSciId();
                String factureCode = factureBeingEdited.getFactureCode();

                        // GETS the FACTURE that was CREATED
                factureBeingEdited = factureMethods.getFacture(sciId, factureCode);
                System.out.println("ID entré pour cette facture : " + factureBeingEdited.getId());

                        // PASSES the FACTURE_ID to each of the FACTURE LINES
                for (TarifForFactureClass tarif : tarifArray){
                    tarif.setFactureId(factureBeingEdited.getId());
                }

            } else {
                    // UPDATES an EXISTING FACTURE
                factureMethods.updateFacture(factureBeingEdited);    //OTHER ARGS TO BE DETERMINED
            }

                // passes the FACTURE LINES array to a method that SORTS THEM based on their booleans values and ADDS, UPDATE or REMOVE them
            Boolean verification = prestationMethods.addOrAlterPrestations(factureBeingEdited.getId(), tarifArray);

                // executes only if THE LINES WERE SUCCESSFULLY INSERTED
            if (verification.equals(true)){
                    // CLOSES the window
                Node source = (Node) event.getSource();
                Stage currentWindow = (Stage) source.getScene().getWindow();
                currentWindow.close();
                    // have the PARENT CONTROLLER REFRESH the display of factures
                parentFactureController.displayFactures(factureBeingEdited.getSciId());
            }
        }                
    }



    //                                      ***
    // LISTENER that DISPLAYS and STORE the infos related to the SELECTED PRESTATION
    //                                      ***

    @FXML
    void handleSelectPrestation(ActionEvent event) {
        PrestationClass selectedPrestation = prestationChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedPrestation != null){
            this.prestaId = selectedPrestation.getId();
            this.prestaName = selectedPrestation.getPrestaName();
        }
    }



    //                                              ***
    // LISTENER that INSERTS into the DIFFERENT FIELDS the info selected from the list of DEFAULT PRESTATIONS
    //                                              ***

    @FXML
    void handleSelectDefaultPrestation(ActionEvent event) {
        DefaultPrestationTableLine defaultPresta = defaultPrestaComboBox.getSelectionModel().getSelectedItem();
        if (defaultPresta != null){
            PrestationClass prestationToSelect = null;
            for (PrestationClass presta : prestaList){
                if (defaultPresta.getPrestaCol().equals(presta.getPrestaName())){
                    prestationToSelect = presta;
                    break;
                }
            }
            prestationChoiceComboBox.setValue(prestationToSelect);
            descriptionTF.setText(defaultPresta.getDescriptionCol());
            unitaryPriceTF.setText(String.format("%.2f", defaultPresta.getUnitPriceCol()));

                System.out.println(defaultPresta.getTVAPercentageCol());
                System.out.println(String.format("%.2f", defaultPresta.getTVAPercentageCol()));


            tvaPercentageTF.setText(String.format("%.2f", defaultPresta.getTVAPercentageCol()));
        }
    }



    //                                  ***
    // METHOD called when an EDIT BUTTON is selected in the FACTURE LINES TABLEVIEW
    //   in which CREATES A NEW WINDOW in which the FACTURE LINE can be EDITED
    //                                  ***

    public void handleEditPrestationButton(Integer lineId){

            // initializes then FILLS with data the LINE TO EDIT
        tarif = null;
        for (TarifForFactureClass tarifToGet : tarifArray){
            if (tarifToGet.getIdForDisplay().equals(lineId)){
                tarif = tarifToGet;
                break;
            }
        }
        
            // Creation of the GRIDPANE and the LABELS
        GridPane gridpane = new GridPane();
        Label prestaChoicLabel = new Label("Prestation : ");
        gridpane.add(prestaChoicLabel, 0, 0);
        Label descriptionLabel = new Label("Description : ");
        gridpane.add(descriptionLabel, 0, 1);
        Label unitPriceLabel = new Label("Prix unitaire (HT) : ");
        gridpane.add(unitPriceLabel, 0, 2);
        Label tvaPercentageLabel = new Label("Taux de TVA (%) : ");
        gridpane.add(tvaPercentageLabel, 0, 3);
        Label quantityLabel = new Label("Quantité : ");
        gridpane.add(quantityLabel, 0, 4);

            // Creation of the COMBO BOX displaying PRESTATIONS
        ObservableList prestaComboBoxEditList = FXCollections.observableArrayList(prestaList);
        JFXComboBox<PrestationClass> prestaForEditCB = new JFXComboBox<PrestationClass>(prestaComboBoxEditList);
        
            // Sets the CURRENT VALUE to DISPLAY in the PRESTATION COMBO BOX
        for (PrestationClass presta : prestaList){
            if (tarif.getPrestaName().equals(presta.getPrestaName())){
                prestationForEdition = presta;
                break;
            }
        }
        prestaForEditCB.setValue(prestationForEdition);

            // Creation of the TEXTFIELDS
        gridpane.add(prestaForEditCB, 1, 0);
        JFXTextField descriptionEditTF = new JFXTextField(tarif.getDescription());
        gridpane.add(descriptionEditTF, 1, 1);
        JFXTextField unitPriceEditTF = new JFXTextField(String.format("%.2f", tarif.getUnitPrice()));
        gridpane.add(unitPriceEditTF, 1, 2);
        JFXTextField tvaEditTF = new JFXTextField(String.format("%.2f", tarif.getTvaPercentage()));
        gridpane.add(tvaEditTF, 1, 3);
        JFXTextField quantityEditTF = new JFXTextField(String.valueOf(tarif.getQuantity()));
        gridpane.add(quantityEditTF, 1, 4);


        // adding LISTENERS to the TEXTFIELDS to limit the imput
            // FLOAT with TWO digits and TWO decimals max
        tvaEditTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                    // REGEX CONDITION TO SPECIFY THE ADEQUATE FORMAT
                if (newValue != null){
                    if (oldValue != null){
                        if (oldValue.matches("[0-9]{1,2}[.,]{1}[0-9]{1,2}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{1}[0-9]{0,2}")){
                                tvaEditTF.setText(oldValue);
                            }
                        } else if (oldValue.matches("[0-9]{1,2}[.,]{1}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{0,1}[0-9]{0,2}")){
                                tvaEditTF.setText(oldValue);
                            }
                        } else if (oldValue.matches("[0-9]{0,2}")){
                            if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}[.,]{0,1}")){
                                tvaEditTF.setText(oldValue);
                            }
                        }
                    } else {   
                        if (!newValue.isEmpty() && !newValue.matches("[0-9]{1,2}")){
                            tvaEditTF.setText(oldValue);
                        }
                    }    
                }                
            }                
        });
            // FLOAT with TWO decimals max
        unitPriceEditTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]+[.,]?[0-9]{0,2}")){
                        unitPriceEditTF.setText(oldValue);
                    }
                }
            }                
        });
            // STRING with 70 max char
        descriptionEditTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches(".{0,60}")){
                        descriptionEditTF.setText(oldValue);
                    }
                }
            }
        });
            // INT with 4 digits max
        quantityEditTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if (newValue != null){
                    if (!newValue.isEmpty() && !newValue.matches("[0-9]{0,4}")){
                        quantityEditTF.setText(oldValue);
                    }
                }
            }                
        });

            // Creation of the VALIDATION BUTTON
        JFXButton tarifEditButton = new JFXButton("Mettre à jour");

            // Creation of the VBOX and ADDS CHILDREN
        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridpane, tarifEditButton);
        vbox.setSpacing(30);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setMargin(gridpane, new Insets(45));


            // Creation of the SCENE, the STAGE and DISPLAY OF THE WINDOW
        Scene tarifEditScene = new Scene(vbox, 400, 400);
        tarifEditScene.getStylesheets().add(EditFactureController.class.getResource("css/windows.css").toExternalForm());
        Stage tarifEditStage = new Stage();
        tarifEditStage.setScene(tarifEditScene);
        tarifEditStage.setTitle("Modifier une ligne de la facture");
        tarifEditStage.initModality(Modality.APPLICATION_MODAL);
        tarifEditStage.show();
        tarifEditStage.centerOnScreen();

            // LISTENER for the COMBO BOX to modify the PRESTATION
        prestaForEditCB.setOnAction((ActionEvent arg0) -> {
            prestationForEdition = prestaForEditCB.getSelectionModel().getSelectedItem();
        });

            // LISTENER for the use of the VALIDATION BUTTON
        tarifEditButton.setOnAction((ActionEvent buttonPressed) -> {

                // formatter for numeric values
            DecimalFormat df = new DecimalFormat();
            Float editUnitPrice;
            Float editTVAPercentage;
            Integer editQuantity;

                // LOCATES the LINE BEING EDITING in the array
            for (TarifForFactureClass tarifToModify : tarifArray){
                if (tarifToModify.getIdForDisplay().equals(lineId)){
                    try {
                            // UPDATES the numerical values
                        editTVAPercentage = df.parse(tvaEditTF.getText()).floatValue();
                        editUnitPrice = df.parse(unitPriceEditTF.getText()).floatValue();
                        editQuantity = df.parse(quantityEditTF.getText()).intValue();
                        tarifToModify.setQuantity(editQuantity);
                        tarifToModify.setUnitPrice(editUnitPrice);
                        tarifToModify.setTVAPercentage(editTVAPercentage);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                        // UPDATES the rest of the data
                    tarifToModify.setPrestaId(prestationForEdition.getId());
                    tarifToModify.setPrestaName(prestationForEdition.getPrestaName());
                    tarifToModify.setDescription(descriptionEditTF.getText());
                        // IF the line EXISTS in the DATABASE, FLAGS it for UPDATE 
                    if (tarifToModify.getExistence().equals(true)){
                        tarifToModify.setToModify(true);                    
                    }            
                    break;
                }
            }
                // UPDATES the DISPLAY of FACTURE LINES
            displayPrestations();

                // CLOSES the edition WINDOW
            tarifEditStage.close();
        });
    }



    //                              ***
    // METHOD called whent the DELETION BUTTON is pressed in the TABLEVIEW
    //                              ***

    public void handleDeletePrestationButton(Integer lineId){
            // LOCATES the LINE in the ARRAYLIST
        for (TarifForFactureClass tarif : tarifArray){
            if (tarif.getIdForDisplay().equals(lineId)){

                    // IF the LINE EXISTS in the DATABASE, FLAGS it for REMOVAL
                if (tarif.getExistence().equals(true)){
                    tarif.setToDelete(true);

                    // otherwise just REMOVE IT from the ARRAY
                } else {
                    tarifArray.remove(tarif);
                }                
                break;
            }
        }
        displayPrestations();
    }



    //                                      ***
    // METHOD called to REFRESH and DISPLAY the FACTURE LINES in the TABLEVIEW
    //                                      ***

    public void displayPrestations(){

            // INITIALIZES the OBSERVABLE LIST that will be DISPLAYED
        ObservableList<LineToDisplay> linesToDisplay = FXCollections.observableArrayList();

            // FILLS the OBSERVABLE LIST based on the ARRAYLIST
        for (TarifForFactureClass tarif : tarifArray){
            if (tarif.getToDelete().equals(false)){
                LineToDisplay tarifToDisplay = new LineToDisplay(
                    tarif.getPrestaName(), 
                    tarif.getDescription(), 
                    tarif.getQuantity(), 
                    tarif.getUnitPrice(), 
                    tarif.getTvaPercentage(),
                    tarif.getIdForDisplay()
                );
                linesToDisplay.add(tarifToDisplay);
            }            
        }
            // CREATES the ROOT of the TREE TABLE VIEW (root of the OBSERVABLE LIST)
        final TreeItem<LineToDisplay> root = new RecursiveTreeItem<LineToDisplay>(linesToDisplay, RecursiveTreeObject::getChildren);

            // SETS the COLUMNS and the ROOT of the TREE TABLE VIEW
        prestationsTreeTableView.getColumns().setAll(
                                    prestaColumn,
                                    descriptionColumn,
                                    unitPriceColumn,
                                    quantityColumn,
                                    htColumn,
                                    tvaColumn,
                                    ttcColumn,
                                    editColumn,
                                    deleteColumn
                                );
        prestationsTreeTableView.setRoot(root);
        prestationsTreeTableView.setShowRoot(false);

            // RESETS the TEXTFIELDS
        resetPrestationCreationFields();
    }



    //                  ***
    // RESETS the TEXTFIELDS and COMBO BOXES
    //                  ***

    private void resetPrestationCreationFields(){
        prestaId = null;
        prestaName = null;
        defaultPrestaComboBox.setValue(null);
        prestationChoiceComboBox.setValue(null);
        descriptionTF.setText(null);
        unitaryPriceTF.setText(null);
        tvaPercentageTF.setText(null);
        quantityTF.setText("1");
    }



    //                  ***
    // LOADS the PRESTATION LIST in the COMBO BOX
    //                  ***

    private void getPrestaList() {
        prestaList = prestationMethods.getPrestations();
        ObservableList prestaComboBoxList = FXCollections.observableArrayList(prestaList);
        prestationChoiceComboBox.setItems(prestaComboBoxList);
    }


    
    //                  ***
    // LOADS the DEFAULT PRESTATION LIST in the COMBO BOX
    //                  ***
    
    private void getDefaultPrestaList(Integer clientId) {
        ArrayList<DefaultPrestationTableLine> defaultPrestaList = prestationMethods.getDefaultPrestationPerClient(clientId);
        ObservableList defaultPrestaComboBoxList = FXCollections.observableArrayList(defaultPrestaList);
        defaultPrestaComboBox.setItems(defaultPrestaComboBoxList);
    }



    //                                                  ***
    // INSERTS into the FACTURE the TOTAL SUMS calculating them based on the LINES IN THE ARRAYLIST
    //                                                  ***

    private void calculateFacturePrices(){
            // SETS the SUMS to ZERO
        factureBeingEdited.setTotalHT((float) 0); 
        factureBeingEdited.setTotalTVA((float) 0); 
        factureBeingEdited.setTotalTTC((float) 0); 

            // ITERATES over the ARRAYLIST LINES that are NOT FLAGED FOR DELETION and adds them to the SUMS
        for (TarifForFactureClass tarif : tarifArray){
            if (tarif.getToDelete().equals(false)){
                Float ht =  tarif.getUnitPrice()*tarif.getQuantity();
                Float tva = ht*tarif.getTvaPercentage()/100;
                Float ttc = ht + tva;
                factureBeingEdited.setTotalHT(factureBeingEdited.getTotalHT() + ht); 
                factureBeingEdited.setTotalTVA(factureBeingEdited.getTotalTVA() + tva); 
                factureBeingEdited.setTotalTTC(factureBeingEdited.getTotalTTC() + ttc); 
            }            
        }
    }

    

}
