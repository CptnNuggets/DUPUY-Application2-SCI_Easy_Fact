package facturationlds;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import facturationlds.databaseClasses.ClientMethods;
import facturationlds.databaseClasses.FactureMethods;
import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.FactureMethods.FactureTableLine;
import facturationlds.databaseClasses.SCIMethods.SCIClass;
import facturationlds.utils.PdfMethods;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class FactureController implements Initializable{

        // ELEMENTS from the FXML file
    @FXML
    private ScrollPane factureScrollPane;
    @FXML
    private JFXComboBox<SCIClass> sciChoiceComboBox;
    @FXML
    private JFXComboBox<ClientClass> clientChoiceComboBox;
    @FXML
    private JFXButton generateButton;
    @FXML
    private JFXTreeTableView<LineToDisplay> factureTable;
    @FXML
    private JFXTextField filterTableTF;

        // METHOD CLASSES
    private SCIMethods sciMethods;
    private ClientMethods clientMethods;
    private FactureMethods factureMethods;
   
        // Used to reference the SCI and CLIENT selected in the COMBO BOXES
    private Integer sciId;
    private Integer clientId;
        // ARRAYLIST containing the FACTURE TABLE LINES (Factures AND Prestations)
    private ArrayList<FactureTableLine> arrayOfFactures;
    
        // SCENE used in the NEW WINDOW, needed to ACCESS the EDIT FACTURE CONTROLLER
    private static Scene factureHandlingScene;



    //              ***
    // COLUMNS off the TREETABLEVIEW
    //              ***

    JFXTreeTableColumn<LineToDisplay,String>firstColumn;
    JFXTreeTableColumn<LineToDisplay,String>secondColumn;
    JFXTreeTableColumn<LineToDisplay,String>htColumn;
    JFXTreeTableColumn<LineToDisplay,String>tvaColumn;
    JFXTreeTableColumn<LineToDisplay,String>ttcColumn;
    JFXTreeTableColumn<LineToDisplay,String>editColumn;
    JFXTreeTableColumn<LineToDisplay,String>pdfColumn;
    JFXTreeTableColumn<LineToDisplay,String>deleteColumn;



    //                          ***
    // CLASS formated to pass the infos to DISPLAY in the TREETABLEVIEW
    //                          ***

    class LineToDisplay extends RecursiveTreeObject<LineToDisplay> {
        StringProperty firstCol;
        StringProperty secondCol;
        StringProperty totalHT;
        StringProperty totalTVA;
        StringProperty totalTTC;
        IntegerProperty factureId;
        Boolean definitive;
        Boolean paid;

        public LineToDisplay(String firstCol, String secondCol, 
            Float totalHT, Float totalTVA, Float totalTTC, Integer factureId,
            Boolean definitive, Boolean paid
        )
        {
            this.firstCol = new SimpleStringProperty(firstCol);
            this.secondCol = new SimpleStringProperty(secondCol);
            this.totalHT = new SimpleStringProperty(String.format("%.2f", totalHT));
            this.totalTVA = new SimpleStringProperty(String.format("%.2f", totalTVA));
            this.totalTTC = new SimpleStringProperty(String.format("%.2f", totalTTC));
            if (factureId == null){
                this.factureId = new SimpleIntegerProperty(0);
            } else {
                this.factureId = new SimpleIntegerProperty(factureId);
            }
            this.definitive = definitive;
            this.paid = paid;            
        }

        public Integer getFactureId(){return factureId.get();}
        public Boolean getDefinitive(){return definitive;}
        public Boolean getPaid(){return paid;}

    }

    

    //                          ***
    // METHOD called when the CONTROLLER is INSTANCIATED
    //                          ***

    @Override
    public void initialize(URL url, ResourceBundle arg1) {

            // INSTANCIATING the METHOD CLASSES
        sciMethods = new SCIMethods();
        clientMethods = new ClientMethods();
        factureMethods = new FactureMethods();

            // Creates the ARRAYLIST that will contains the FACTURE and PRESTATION data to display
        arrayOfFactures = new ArrayList<FactureTableLine>();

            // INITIALIZING the COMBO BOX allowing SCI choices
        getSCIList();
        
     
            // PARAMETERS to BUILD the COLUMNS of the TREETABLEVIEW

        firstColumn = new JFXTreeTableColumn<>("Code ");
        firstColumn.setPrefWidth(150);
        firstColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().firstCol;
            }
        });

        secondColumn = new JFXTreeTableColumn<>("Client/détail ");
        secondColumn.setPrefWidth(150);
        secondColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().secondCol;
            }
        });

        htColumn = new JFXTreeTableColumn<>("HT");
        htColumn.setPrefWidth(100);
        htColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().totalHT;
            }
        });

        tvaColumn = new JFXTreeTableColumn<>("TVA");
        tvaColumn.setPrefWidth(100);
        tvaColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().totalTVA;
            }
        });

        ttcColumn = new JFXTreeTableColumn<>("TTC");
        ttcColumn.setPrefWidth(100);
        ttcColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineToDisplay, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call (TreeTableColumn.CellDataFeatures<LineToDisplay,String> param) {
                return param.getValue().getValue().totalTTC;
            }
        });

        editColumn = new JFXTreeTableColumn<>("");
        editColumn.setPrefWidth(200);
        editColumn.setCellFactory(new Callback<TreeTableColumn<LineToDisplay, String>, TreeTableCell<LineToDisplay, String>>(){
            
                // CALL to build the CELL of a column containing a BUTTON
            
            @Override
            public TreeTableCell<LineToDisplay, String> call(TreeTableColumn<LineToDisplay, String> param) {
                final TreeTableCell<LineToDisplay, String> cell = new TreeTableCell<LineToDisplay, String>(){
                    final JFXButton btn = new JFXButton();
                    
                    //     /!\   Needs to be INSIDE the UPDATE method do call GETTREETABLEROW    /!\
                    //                      AND confirm that it is NOT EMPTY
                    //     /!\                           ***                                     /!\
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            if (getTreeTableRow().getTreeItem() != null){
                                LineToDisplay currentLine = getTreeTableRow().getTreeItem().getValue();
                                Integer factureId = currentLine.getFactureId();
                                System.out.println("Trouvé : "+factureId);
                                if (!factureId.equals(0)){
                                    // LINE WITH A FACTURE
                                    if (currentLine.getPaid().equals(true)){
                                        // FACTURE ALREAY PAID
                                        setGraphic(null);
                                        setText("Payée");
                                    } else if (currentLine.getDefinitive().equals(true)){
                                        // DEFINITIVE FACTURE BUT NOT YET PAID
                                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                                        btn.setText("Valider paiement");
                                        btn.setOnAction(event -> {
                                            handleValidatePaymentButton(factureId);
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    } else {
                                        // TEMPORARY FACTURE
                                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                                        btn.setText("Modifier");
                                        btn.setOnAction(event -> {
                                            try {
                                                handleEditFactureButton(factureId);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    }
                                } else {
                                    setGraphic(null);
                                    setText(null);
                                }
                            } else {
                                System.out.println("FactureID=0 ?");
                                setGraphic(null);
                                setText(null);
                            }                            
                        }
                    }
                };
                return cell;
            }
        });        

        pdfColumn = new JFXTreeTableColumn<>("");
        pdfColumn.setPrefWidth(100);
        pdfColumn.setCellFactory(new Callback<TreeTableColumn<LineToDisplay, String>, TreeTableCell<LineToDisplay, String>>(){
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
                                Integer factureId = currentLine.getFactureId();
                                if (!factureId.equals(0)){
                                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                                        btn.setText("PDF");
                                        btn.setOnAction(event -> {
                                            try {
                                                handlePDFButton(factureId);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
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
                                Integer factureId = currentLine.getFactureId();
                                System.out.println("Trouvé : "+factureId);
                                if (!factureId.equals(0)){
                                    // LINE WITH A TEMPORARY FACTURE
                                    if (currentLine.getDefinitive().equals(false)){
                                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                                        btn.setText("Supprimer");
                                        btn.setOnAction(event -> {
                                            handleDeleteFactureButton(factureId);
                                        });
                                        setGraphic(btn);
                                        setText(null);
                                    } else  {
                                        setGraphic(null);
                                        setText(null);
                                    }
                                } else {
                                    setGraphic(null);
                                    setText(null);
                                }
                            } else {
                                System.out.println("FactureID=0 ?");
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

    

    //                        ***
    // METHOD called when the CREATE FACTURE button is pressed
    //                        ***

    @FXML
    void handleGenerate(ActionEvent event) throws IOException {
        if (sciId != null && clientId != null){

                // New window, stage and  scene
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("editFacture.fxml"));
            factureHandlingScene = new Scene(fxmlLoader.load());
            Stage factureHandlingStage = new Stage();
            factureHandlingStage.setScene(factureHandlingScene);
            factureHandlingStage.setTitle("Paramétrage d'une facture");
            factureHandlingStage.initModality(Modality.APPLICATION_MODAL);
    
                // gets the CONTROLLER of the NEW WINDOW
            EditFactureController editFactureController = fxmlLoader.getController();
    
                // Initialization method SPECIFIC to a NEW FACTURE
            editFactureController.initData(sciId, clientId, this);
    
                // Display of the new Window
            factureHandlingStage.show();
            factureHandlingStage.centerOnScreen();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Informations manquantes");
            alert.setContentText("Renseignez la SCI et le Client pour lesquels créer la facture");
            alert.showAndWait();
            return;
        }
    }

    

    //                              ***
    // Method called when a CLIENT is SELECTED from CLIENT COMBO BOX
    //                              ***

    @FXML
    void handleSelectClient(ActionEvent event) {
        ClientClass selectedClient = clientChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedClient != null){
                // the CLIENT_ID is MEMORIZED
            this.clientId = selectedClient.getId();
        }
    }



    //                              ***
    // Method called when a SCI is SELECTED from CLIENT COMBO BOX
    //                              ***

    @FXML
    void handleSelectSCI(ActionEvent event) {
        SCIClass selectedSCI = sciChoiceComboBox.getSelectionModel().getSelectedItem();
        if (selectedSCI != null){
                // the SCI_ID is MEMORIZED
            this.sciId = selectedSCI.getId();
        }
            // Creates the adequate CLIENT LIST in the COMBO BOX
        getClientList();
        displayFactures(sciId);
    }



    //                                  ***
    // METHOD called when the UPDATE FACTURE button from a FACTURE LINE is pressed
    //                                  ***

    private void handleEditFactureButton(Integer factureId) throws IOException{
        if (factureId != null){

                // New window, stage and  scene
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("editFacture.fxml"));
            factureHandlingScene = new Scene(fxmlLoader.load());
            Stage factureHandlingStage = new Stage();
            factureHandlingStage.setScene(factureHandlingScene);
            factureHandlingStage.setTitle("Paramétrage d'une facture");
            factureHandlingStage.initModality(Modality.APPLICATION_MODAL);
    
                // gets the CONTROLLER of the NEW WINDOW
            EditFactureController editFactureController = fxmlLoader.getController();
    
                // Initialization method SPECIFIC for UPDATING a FACTURE
            editFactureController.initData(factureId, this);
    
                // Display of the new Window    
            factureHandlingStage.show();
            factureHandlingStage.centerOnScreen();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Informations manquantes");
            alert.setContentText("Le lien ne renvoie pas à une facture existante");
            alert.showAndWait();
            return;
        }
    }



    //                                  ***
    // METHOD called when the VALIDATE PAYMENT button of a FACTURE LINE is pressed
    //                                  ***

    private void handleValidatePaymentButton(Integer factureId){
            // set the PAID field of a FACTURE to TRUE
        factureMethods.validatePayment(factureId);
            // REFRESHES the FACTURE LINE DISPLAY
        displayFactures(sciId);
    }



    //                                  ***
    // METHOD called when the DELETE FACTURE button of a FACTURE LINE is pressed
    //                                  ***

    private void handleDeleteFactureButton(Integer factureId){
        if (factureMethods.getFacture(factureId).getDefinitive().equals(false)){
            factureMethods.deleteTemporaryFacture(factureId);
        }
    }



    //                                  ***
    // METHOD called when the PDF button of a FACTURE LINE is pressed
    //                                  ***

    private void handlePDFButton(Integer factureId) throws IOException{
            // Creates the PDF FILE of the FACTURE
        try {
            PdfMethods.generateFacture(factureId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //                              ***
    //      METHODS related to REFRESHING and INITLIALIZING the INTERFACE
    //                              ***

        // Set the LIST of the SCIs displayed in the COMBO BOX
    private void getSCIList() {
        ArrayList sciList = sciMethods.getAllSCIs();
        ObservableList sciComboBoxList = FXCollections.observableArrayList(sciList);
        sciChoiceComboBox.setItems(sciComboBoxList);
    }

        // Set the LIST of the CLIENTs displayed in the COMBO BOX
    private void getClientList(){
        if (sciId != null){
            ArrayList clienList = clientMethods.getClients(sciId);
            ObservableList clientComboBoxList = FXCollections.observableArrayList(clienList);
            clientChoiceComboBox.setItems(clientComboBoxList);
        }
    }

        // DISPLAYS the FACTURES and FACTURE LINES in the TREE TABLE VIEW
    public void displayFactures(Integer sciId){
        
            // OBSERVABLE LIST that will contain the FACTURES and FACTURE LINES
        ObservableList<LineToDisplay> linesToDisplay = FXCollections.observableArrayList();

            // DATA ARRAY containing the data to display
        arrayOfFactures = factureMethods.getFactureArrayForFact(sciId);

            // PARSES the DATA ARRAY
        for (FactureTableLine facture : arrayOfFactures){
                // CREATES a DISPLAY LINE
            LineToDisplay factureToDisplay = new LineToDisplay(
                                            facture.getFirstCol(),
                                            facture.getSecondCol(),
                                            facture.getTotalHT(),
                                            facture.getTotalTVA(),
                                            facture.getTotalTTC(),
                                            facture.getFactureId(),
                                            facture.getDefinitive(),
                                            facture.getPaid()
                                            );
                // EXTRACTS the DATA for the related FACTURE LINES
            ArrayList<FactureTableLine> arrayOfLines = facture.getFactLines();
                // PARSES the FACTURE LINE DATA
            for (FactureTableLine line : arrayOfLines){
                    // ADDS LINES TO DISPLAY as CHILDREN to the DISPLAYED FACTURE LINE
                factureToDisplay.getChildren().add(new LineToDisplay(
                                                    line.getFirstCol(),
                                                    line.getSecondCol(),
                                                    line.getTotalHT(),
                                                    line.getTotalTVA(),
                                                    line.getTotalTTC(),
                                                    line.getFactureId(),
                                                    null,
                                                    null
                                                ));
            }
                // ADDS the DISPLAY FACTURE LINE to the OBSERVABLE LIST
            linesToDisplay.add(factureToDisplay);
        }
            // CREATES the ROOT of the TREE TABLE VIEW (root of the OBSERVABLE LIST)
        final TreeItem<LineToDisplay> root = new RecursiveTreeItem<LineToDisplay>(linesToDisplay, RecursiveTreeObject<LineToDisplay>::getChildren);
            
            // SETS the COLUMNS and the ROOT of the TREE TABLE VIEW
        factureTable.getColumns().setAll(firstColumn, secondColumn, htColumn, tvaColumn, ttcColumn, editColumn, pdfColumn, deleteColumn);
        factureTable.setRoot(root);
        factureTable.setShowRoot(false);

            // creates a LISTENER on the SEARCH TEXTFIELD
        filterTableTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newValue) {
                factureTable.setPredicate(new Predicate<TreeItem<LineToDisplay>>() {

                    @Override
                    public boolean test(TreeItem<LineToDisplay> fac) {
                        Boolean flag = fac.getValue().firstCol.getValue().contains(newValue) 
                            || fac.getValue().secondCol.getValue().contains(newValue);
                        return flag;
                    }                    
                });
            }            
        });
    }
}
