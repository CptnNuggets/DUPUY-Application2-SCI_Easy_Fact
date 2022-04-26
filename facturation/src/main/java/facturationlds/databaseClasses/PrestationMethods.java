package facturationlds.databaseClasses;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import facturationlds.DataBaseHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;




    //                                                          ***
    // METHOD CLASS containing the PRESTATION and TARIF DATA CLASS, the related methods and the PRESTATION or TARIF-BASED DATABASE methods
    //                                                          ***

public class PrestationMethods {

    DataBaseHandler dBhandler;

    public PrestationMethods(){
            // gets the CURRENT INSTANCE of the DATABASE HANDLER at CONSTRUCTION
        dBhandler = DataBaseHandler.getInstance();
    }

    //                   ***
    //     EXTERNAL PRESTATION DATA CLASS
    //                   ***
    public class PrestationClass {
        private Integer id;
        private String prestaName;
            // CONSTRUCTOR
        public PrestationClass(Integer id, String prestaName){
            this.id = id;
            this.prestaName = prestaName;
        }
            // GETTERS
        public Integer getId(){return id;}
        public String getPrestaName(){return prestaName;}
            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return this.prestaName;
        }
    }


    //                      ***
    //  EXTERNAL DATA CLASS formated for FACTURES DISPLAY
    //                      ***
    public class TarifForFactureClass {
        private Integer id;
        private Integer prestaId;
        private Integer factureId;
        private String prestaName;
        private Integer quantity;
        private Float unitPrice;
        private Float tvaPercentage;
        private String description;
        private Boolean existsInDB;
        private Boolean toBeModified;
        private Boolean toBeDeleted;
        private Integer idForDisplay;

            // CONSTRUCTOR
        public TarifForFactureClass(Integer id, Integer prestaId, Integer factureId, String prestaName,
            Integer quantity, Float unitPrice, Float tvaPercentage, String description, 
            Boolean existsInDB, Boolean toBeModified, Boolean toBeDeleted, Integer idForDisplay)
        {
            this.id = id;
            this.prestaId = prestaId;
            this.factureId = factureId;
            this.prestaName = prestaName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.tvaPercentage = tvaPercentage;
            this.description = description;
            this.existsInDB = existsInDB;
            this.toBeModified = toBeModified;
            this.toBeDeleted = toBeDeleted;
            this.idForDisplay = idForDisplay;
        }
            // GETTERS
        public Integer getId(){return id;}
        public Integer getPrestaId(){return prestaId;}
        public Integer getFactureId(){return factureId;}
        public String getPrestaName(){return prestaName;}
        public Integer getQuantity(){return quantity;}
        public Float getUnitPrice(){return unitPrice;}
        public Float getTvaPercentage(){return tvaPercentage;}
        public String getDescription(){return description;}
        public Boolean getExistence(){return existsInDB;}
        public Boolean getToModify(){return toBeModified;}
        public Boolean getToDelete(){return toBeDeleted;}
        public Integer getIdForDisplay(){return idForDisplay;}
            // SETTERS
        public void setPrestaId(Integer prestaId){this.prestaId = prestaId;}
        public void setFactureId(Integer factureId){this.factureId = factureId;}
        public void setPrestaName(String prestaName){this.prestaName = prestaName;}
        public void setQuantity(Integer quantity){this.quantity = quantity;}
        public void setUnitPrice(Float unitPrice){this.unitPrice = unitPrice;}
        public void setTVAPercentage(Float tvaPercentage){this.tvaPercentage = tvaPercentage;}
        public void setDescription(String description){this.description = description;} 
        public void setToModify(Boolean toBeModified){this.toBeModified = toBeModified;}
        public void setToDelete(Boolean toBeDeleted){this.toBeDeleted = toBeDeleted;}
        public void setIdForDisplay(Integer idForDisplay){this.idForDisplay = idForDisplay;}

            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return (this.prestaName + " : " + this.description);
        }
    }



    //                              ***
    //  EXTERNAL DATA CLASS formated for DEFAULT PRESTATIONS DISPLAY
    //                              ***
    public class DefaultPrestationTableLine {
        String prestaCol;
        String descriptionCol;
        Float unitPriceCol;
        Float tvaPercentageCol;
        Integer tarifId;
            // CONSTRUCTOR
        public DefaultPrestationTableLine(String presta, String description, Float unitPrice, Float tvaPercentage, Integer tarifId){
            this.prestaCol = presta;
            this.descriptionCol = description;
            this.unitPriceCol = unitPrice;
            this.tvaPercentageCol = tvaPercentage;
            this.tarifId = tarifId;
        }
            // GETTERS
        public String getPrestaCol(){return prestaCol;}
        public String getDescriptionCol(){return descriptionCol;}
        public Float getUnitPriceCol(){return unitPriceCol;}
        public Float getTVAPercentageCol(){return tvaPercentageCol;}
        public Integer getTarifId(){return tarifId;}

            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return prestaCol + " : "+descriptionCol;
        }
    }



    //                      ***
    //  METHOD to insert a NEW PRESTATION into the DATABASE
    //                      ***

    public Boolean insertPrestation(String prestaName){
            // validation of the field
        if (prestaName == null){
            alertMissingField(true);
            return false;
        } else if (prestaName.isEmpty()){
            alertMissingField(true);
            return false;
        } else {
                // Escape the single quotes
            String dBPrestaName = dBhandler.formatedForDB(prestaName);
                // query
            String qu = "INSERT INTO Prestation (presta_name) VALUES (" + dBPrestaName +")";
            System.out.println(qu);
                // Execution verification
            return (execActionWithAlerts(qu, "prestation"));
        }
    }



    //                              ***
    //  METHOD to "DELETE" (hide) a GENERAL PRESTATION from the DATABASE
    //                              ***

    public Boolean hidePrestation(Integer prestaId){
        if (prestaId == null){
            alertMissingField(true);
            return false;
        } else {
            // query
            String qu = "UPDATE Prestation SET visible = false WHERE id = " + prestaId;
            System.out.println(qu);
                // Execution verification
            return (execActionWithAlerts(qu, "delete prestation"));
        }
    }


    //                              ***
    //  METHOD to DELETE a DEFAULT PRESTATION from the DATABASE
    //                              ***

    public Boolean deleteDefaultPrestation(Integer defaultPrestaId){
        if (defaultPrestaId == null){
            alertMissingField(true);
            return false;
        } else {
            // query
            String qu = "DELETE FROM Tarif WHERE presta_default = true AND id = " + defaultPrestaId;
            System.out.println(qu);
                // Execution verification
            return (execActionWithAlerts(qu, "delete description"));
        }
    }



    //                      ***
    //  METHOD to insert a NEW DEFAULT TARIF into the DATABASE
    //                      ***

    public Boolean insertDefaultTarif(
        Integer prestaId, Float unitPrice, Float tvaPercentage, String description, Integer clientId
    ){
            // Verification that all fields are present
        if (prestaId.equals(null) || unitPrice.equals(null) || tvaPercentage.equals(null) || clientId.equals(null)){
            alertMissingField(false);
            return false;
        } else {
                // Format to escape single quotes
            String dBDescription = dBhandler.formatedForDB(description);
                // Query setting the PRESTA_DEFAULT value to TRUE
            String qu = "INSERT INTO Tarif (presta_id, unit_price_ht, tva_percentage, description, presta_default, client_id) "
                + "VALUES (" + prestaId + ", " 
                + " " + unitPrice + ", " 
                + " " + tvaPercentage + ", " 
                + " " + dBDescription + ", " 
                + " true, " 
                + " " + clientId + ")";
            System.out.println(qu);
                // Execution and validation
            return (execActionWithAlerts(qu, "description"));
        }
    }



    //                                  ***
    // METHOD to return an ARRAY of existing TARIF FOR DISPLAY based on a FACTURE_ID
    //                                  ***

    public ArrayList<TarifForFactureClass> getTarifsForFacture(Integer factureId){
            // Array to return
        ArrayList<TarifForFactureClass> tarifList = new ArrayList<TarifForFactureClass>();
            // Query, selects only prestations where PRESTA_DEFAULT is FALSE and the FACTURE_ID is present
        String qu = "SELECT T.id AS tarif_id, presta_id, unit_price_ht, tva_percentage, quantity, "            
            + "description, facture_id, presta_name FROM Tarif T INNER JOIN Prestation P "
            + "ON T.presta_id = P.id WHERE presta_default = false AND facture_id = " +  factureId 
            + " ORDER BY presta_name";
            // Result set to parse
        ResultSet result = dBhandler.execQuery(qu);
            // Integer stored as "array_id" for the display class
        int i = 1;
        try {
            while (result.next()){
                TarifForFactureClass tarif = new TarifForFactureClass(
                    result.getInt("tarif_id"),
                    result.getInt("presta_id"), 
                    result.getInt("facture_id"), 
                    result.getString("presta_name"), 
                    result.getInt("quantity"), 
                    result.getFloat("unit_price_ht") , 
                    result.getFloat("tva_percentage"), 
                    result.getString("description"),  
                    true,
                    false, 
                    false, 
                    i
                );
                    // increments the "array_id"
                i = i+1;
                tarifList.add(tarif);
            }
            return tarifList;
        } catch (SQLException e){
            Logger.getLogger(SCIMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                          ***
    // METHOD called when SAVING / UPDATING a FACTURE to either
    //  CREATE, ALTER or DELETE a TARIF related to that FACTURE
    //                          ***

    public Boolean addOrAlterPrestations(Integer factureId, ArrayList<TarifForFactureClass> tarifArray){
            // Verification of the information passed
        if (factureId == null || tarifArray == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Prolème lors d'une insertion");
            alert.setContentText("Aucunes données à insérer");
            return false;
        } else if (tarifArray.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Prolème lors d'une insertion");
            alert.setContentText("Aucunes données à insérer");
            return false;
        } else {
                // Boolean returned as validation
            Boolean verification = true;
                // Parses the array passed to the function
            for (TarifForFactureClass tarif : tarifArray){    
                if (tarif.existsInDB.equals(true) && tarif.toBeModified.equals(false) && tarif.toBeDeleted.equals(false)){
                    // NO NEED TO MAKE A QUERY : Prestation allready exists and is not to be altered
                } else {
                        // Formats the description to escape apastrophes
                    String dBDescription = dBhandler.formatedForDB(tarif.getDescription());
                        // Query that will be created 
                    String qu;
                    if (tarif.getExistence().equals(false)){
                            // Query to INSERT a NEW PRESTATION
                        qu = "INSERT INTO TARIF (presta_id, unit_price_ht, tva_percentage, quantity, "
                        + "description, facture_id) VALUES ( "
                        + "" + tarif.getPrestaId() + ", "
                        + "" + tarif.getUnitPrice() + ", "
                        + "" + tarif.getTvaPercentage() + ", "
                        + "" + tarif.getQuantity() + ", "
                        + "" + dBDescription + ", "
                        + "" + tarif.getFactureId() + " )";
    
                    } else if (tarif.toBeDeleted.equals(true)){
                            // Query to DELETE a PRESTATION
                        qu = "DELETE FROM TARIF WHERE id = " + tarif.getId(); 
                    } else {
                            // Query to UPDATE a PRESTATION
                        qu = "UPDATE TARIF SET " 
                        + "presta_id = " + tarif.getPrestaId() + ", "
                        + "unit_price_ht = " + tarif.getUnitPrice() + ", "
                        + "tva_percentage = " + tarif.getTvaPercentage() + ", "
                        + "quantity = " + tarif.getQuantity() + ", "
                        + "description = " + dBDescription + ", "
                        + "facture_id = " + tarif.getFactureId() 
                        + " WHERE id = " + tarif.getId();
                    }
                    System.out.println(qu);
                    if (dBhandler.execAction(qu)){
                        // executes the query
                    } else {
                        verification = false;
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.setTitle("Prolème lors d'une insertion");
                        alert.setContentText("Erreur pour la prestation : " + tarif.toString());
                    }
                }
            }
            return verification;
        }        
    }



    //                             ***
    //  METHOD to return an ARRAY of existing PRESTATION DATA CLASSES that are VISIBLE
    //                             ***
    public ArrayList getPrestations(){
        ArrayList<PrestationClass> prestationList = new ArrayList<PrestationClass>();
        String qu = "SELECT * FROM Prestation WHERE visible = true ORDER BY presta_name";
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                PrestationClass presta = new PrestationClass(result.getInt("id"), result.getString("presta_name"));
                prestationList.add(presta);
                
            }
            return prestationList;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                             ***
    //  METHOD to return an ARRAY of DEFAULT PRESTATION CLASSES per CLIENT
    //                             ***

    public ArrayList<DefaultPrestationTableLine> getDefaultPrestationPerClient(Integer clientId){
            // Array to return
        ArrayList<DefaultPrestationTableLine> defaultPrestationsArray = new ArrayList<DefaultPrestationTableLine>();
            // Query : select only prestations where PRESTA_DEFAULT is TRUE
        String qu = "SELECT T.id AS tarif_id, presta_name, description, unit_price_ht, tva_percentage "
            +" FROM Prestation P INNER JOIN Tarif T ON P.id = T.presta_id "
            +" WHERE T.client_id = "+ clientId + " AND presta_default = true AND P.visible = true ORDER BY presta_name";
            // Result Set to parse
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                DefaultPrestationTableLine defaultPresta = new DefaultPrestationTableLine(
                    result.getString("presta_name"), 
                    result.getString("description"), 
                    result.getFloat("unit_price_ht"),
                    result.getFloat("tva_percentage"),
                    result.getInt("tarif_id")
                );
                defaultPrestationsArray.add(defaultPresta);
            }
            return defaultPrestationsArray;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }


            

    //                      ***
    //                  UTILITIES
    //                      ***

        // Alert method, related to missing fields (true) or missing sciId (false)
    public void alertMissingField(Boolean type){
        Alert alert = new Alert(AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Informations manquantes");
        if (type == true){
            alert.setContentText("Indiquez la nature de la prestation");
        } else {
            alert.setContentText("Entrez un élément dans chaque champ");
        }
        alert.showAndWait();
    }

        // Execution of query and related alerts method
    public Boolean execActionWithAlerts(String qu, String type){
        if (dBhandler.execAction(qu)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            if (type == "prestation"){
                alert.setContentText("Prestation renseignée");
            }
            if (type == "description"){
                alert.setContentText("Prestation par défaut ajoutée");
            } 
            if (type == "delete prestation"){
                alert.setContentText("Prestation supprimée");
            }  
            if (type == "delete description"){
                alert.setContentText("Prestation par défaut supprimée");
            }            
            alert.showAndWait();
            return true;
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Echec de la requête");
            alert.showAndWait();
            return false;
        }
    }
    
}
