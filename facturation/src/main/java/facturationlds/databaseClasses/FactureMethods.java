package facturationlds.databaseClasses;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import facturationlds.DataBaseHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;




    //                                                  ***
    // METHOD CLASS containing the FACTURE DATA CLASS, the related methods and the FACTURE-BASED DATABASE methods
    //                                                  ***

public class FactureMethods {

    DataBaseHandler dBhandler;

    public FactureMethods(){
            // gets the CURRENT INSTANCE of the DATABASE HANDLER at CONSTRUCTION
        dBhandler = DataBaseHandler.getInstance();
    }


    //              ***
    //  EXTERNAL FACTURE DATA CLASS
    //              ***
    public class FactureClass {
        private Integer id;
        private Integer sciId;
        private Integer clientId;
        private String factureCode;
        private Boolean definitive;
        private Boolean paid;
        private Date definitiveDate;
        private Float totalHT;
        private Float totalTVA;
        private Float totalTTC;
        private String comment;

            // CONSTRUCTOR
        public FactureClass(Integer id, Integer sciId, Integer clientId, String factureCode,
            Boolean definitive, Boolean paid, Date definitiveDate, Float totalHT, Float totalTVA,
            Float totalTTC, String comment)
        {
            this.id = id;
            this.sciId = sciId;
            this.clientId = clientId;
            this.factureCode = factureCode;
            this.definitive = definitive;
            this.paid = paid;
            this.definitiveDate = definitiveDate;
            this.totalHT = totalHT;
            this.totalTVA = totalTVA;
            this.totalTTC = totalTTC;
            this.comment = comment;
        }
            // GETTERS
        public Integer getId(){return id;}
        public Integer getSciId(){return sciId;}
        public Integer getClientId(){return clientId;}
        public String getFactureCode(){return factureCode;}
        public Boolean getDefinitive(){return definitive;}
        public Boolean getPaid(){return paid;}
        public Date getDefinitiveDate(){return definitiveDate;}
        public Float getTotalHT(){return totalHT;}
        public Float getTotalTVA(){return totalTVA;}
        public Float getTotalTTC(){return totalTTC;}
        public String getComment(){return comment;}

            // SETTERS
        public void setFactureCode(String factureCode){this.factureCode = factureCode;}
        public void setToDefinitive(){this.definitive = true;}
        public void setToPaid(){this.paid = true;}
        public void setDefinitiveDate(){
            LocalDate date = LocalDate.now();
            this.definitiveDate = Date.valueOf(date);            
        }
        public void setTotalHT(Float totalHT){this.totalHT = totalHT;}
        public void setTotalTVA(Float totalTVA){this.totalTVA = totalTVA;}
        public void setTotalTTC(Float totalTTC){this.totalTTC = totalTTC;}
        public void setComment(String comment){this.comment = comment;}

            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return this.factureCode;
        }

    }

   

    //                                  ***
    //  EXTERNAL CLASS formated to contain FACTURES and FACTURE LINES for DISPLAY
    //                                  ***
    public class FactureTableLine {
        String firstCol;
        String secondCol;
        Float totalHT;
        Float totalTVA;
        Float totalTTC;
        Integer factureId;
        Boolean definitive;
        Boolean paid;
            // ARRAY containing CHILDREN DATA (Facture lines for a facture)
        ArrayList<FactureTableLine> children;

            // CONSTRUCTOR
        public FactureTableLine(String firstCol, String secondCol, 
            Float totalHT, Float totalTVA,Float totalTTC, Integer factureId, 
            Boolean definitive, Boolean paid, ArrayList<FactureTableLine> children)
        {
            this.firstCol = firstCol;
            this.secondCol = secondCol;
            this.totalHT = totalHT;
            this.totalTVA = totalTVA;
            this.totalTTC = totalTTC;
            this.factureId = factureId;
            this.definitive = definitive;
            this.paid = paid;
            this.children = children;
        }
            // GETTERS
        public String getFirstCol(){return firstCol;}
        public String getSecondCol(){return secondCol;}
        public Float getTotalHT(){return totalHT;}
        public Float getTotalTVA(){return totalTVA;}
        public Float getTotalTTC(){return totalTTC;}
        public Integer getFactureId(){return factureId;}
        public Boolean getDefinitive(){return definitive;}
        public Boolean getPaid(){return paid;}
        public ArrayList<FactureTableLine> getFactLines(){return children;}

    }


    
    //                      ***
    //  METHOD to insert a NEW FACTURE into the DATABASE
    //                      ***

    public void insertNewFacture(FactureClass facture)
    {
            // Verification that all needed input is present
        if (facture.getFactureCode() == null || facture.getSciId() == null || facture.getClientId() == null) {
            alertMissingField(false);
        } else if (facture.getFactureCode().isEmpty() ) {
            alertMissingField(false);
        } else if (facture.getTotalHT() == null || facture.getTotalTVA() == null || facture.getTotalTTC() == null) {
            alertMissingField(true);
        } else {
                // Formatting to escape single quotes in the query
            String dBComment = dBhandler.formatedForDB(facture.getComment());

                // Query creation
            String qu = "INSERT INTO Facture (sci_id, client_id, facture_code, definitive, definitive_date, total_ht, total_tva, total_ttc, comment) VALUES ("
            + "" + facture.getSciId()+ ", "
            + "" + facture.getClientId() + ", "
            + "'" + facture.getFactureCode()+ "', "
            + "" + facture.getDefinitive()+ ", "
            + "'" + facture.getDefinitiveDate()+ "', "
            + "" + facture.getTotalHT()+ ", "
            + "" + facture.getTotalTVA()+ ", "
            + "" + facture.getTotalTTC()+ ", "
            + "" + dBComment+ ")";
            System.out.println(qu);

                // Query execution and verification
            execActionWithAlerts(qu, "creation");
        }        
    }



    //                      ***
    //  METHOD to UPDATE a FACTURE in the DATABASE
    //                      ***

    public void updateFacture(FactureClass facture){
        
            // Verification that all needed input is present 
        if (facture.getFactureCode() == null || facture.getSciId() == null || facture.getClientId() == null) {
            alertMissingField(false);
        } else if (facture.getFactureCode().isEmpty()) {
            alertMissingField(false);
        } else if  (facture.getTotalHT() == null || facture.getTotalTVA() == null || facture.getTotalTTC() == null) 
        {
            alertMissingField(true);
        } else {
                // Formatting to escape single quotes in the query
            String dBComment = dBhandler.formatedForDB(facture.getComment());

                // Query creation
            String qu = "UPDATE Facture SET "
                + "facture_code = " + "'" + facture.getFactureCode()+ "', "
                + "definitive = " + facture.getDefinitive()+ ", "
                + "definitive_date = " + "'" + facture.getDefinitiveDate()+ "', "
                + "total_ht = " + facture.getTotalHT()+ ", "
                + "total_tva = " + facture.getTotalTVA()+ ", "
                + "total_ttc = " + facture.getTotalTTC()+ ", "
                + "comment = " + dBComment + " "
                + "WHERE id = " + facture.getId();
            System.out.println(qu);

                // Query execution and verification
            if (facture.getDefinitive().equals(true)){
                execActionWithAlerts(qu, "definitive");
            } else {
                execActionWithAlerts(qu, "update");
            }
        }                
    }




    //                      ***
    //  METHOD to DELETE a TEMPORARY facture from the DATABASE
    //                      ***

    public void deleteTemporaryFacture(Integer factureId)
    {
        if (factureId != null) {
            String tarifQu = "DELETE FROM Tarif WHERE facture_id = " + factureId; 
            System.out.println(tarifQu);
            dBhandler.execAction(tarifQu); 

            String qu = "DELETE FROM facture WHERE definitive = false AND id = " + factureId;
            System.out.println(qu);

                // Query execution and verification
            execActionWithAlerts(qu, "deletion");
        }        
    }




    //                                      ***
    //   METHOD to RETURN an ARRAY of FACTURE LINES as FACTURE_TABLE_LINE CLASS for a given FACTURE_ID
    //                                      ***

    public ArrayList<FactureTableLine> getPrestationArrayForFact(Integer factureId){
            // ARRAY of FACTURE TABLE LINES to return
        ArrayList<FactureTableLine> linesArray = new ArrayList<FactureTableLine>();
            // QUERY
        String qu = "SELECT presta_name, description, quantity, unit_price_ht, tva_percentage "
            +" FROM Tarif T INNER JOIN Prestation P ON P.id = T.presta_id "
            +" WHERE facture_id = "+ factureId + " ORDER BY presta_name";
            // RESULTSET to PARSE
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                    // Adds RELEVANT FACTURE LINE DATA (all columns related to a facture set to NULL)
                Float totalHT = result.getFloat("unit_price_ht")*result.getInt("quantity");
                Float totalTVA = totalHT*result.getFloat("tva_percentage")/100;
                Float totalTTC = totalHT + totalTVA;
                FactureTableLine line = new FactureTableLine(
                    result.getString("presta_name"), 
                    result.getString("description"), 
                    totalHT,
                    totalTVA, 
                    totalTTC,
                    null,
                    null,
                    null, 
                    null
                );
                linesArray.add(line);
            }
            return linesArray;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                                      ***
    //   METHOD to RETURN an ARRAY of FACTURES as FACTURE_TABLE_LINE CLASS for a given SCI_ID
    //                                      ***

    public ArrayList<FactureTableLine> getFactureArrayForFact(Integer sciId){
            // ARRAY of FACTURES as FACTURE TABLE LINES
        ArrayList<FactureTableLine> facturesArray = new ArrayList<FactureTableLine>();
            // QUERY
        String qu = "SELECT F.id AS facture_id, facture_code, client_code, total_ht, total_tva, total_ttc, definitive, paid "
            +" FROM Facture F INNER JOIN Client C ON C.id = F.client_id "
            +" WHERE F.sci_id = "+ sciId + " ORDER BY facture_code DESC";
            // RESULT SET to PARSE
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                    // GETS a FACTURE_TABLE_LINE ARRAY containing the FACTURE LINES of this FACTURE_ID
                    // and passes it to the FACTURE_TABLE_LINE CONSTRUCTOR for that FACTURE
                ArrayList<FactureTableLine> linesArray = getPrestationArrayForFact(result.getInt("facture_id"));
                FactureTableLine facture = new FactureTableLine(
                    result.getString("facture_code"), 
                    result.getString("client_code"), 
                    result.getFloat("total_ht"),
                    result.getFloat("total_tva"),
                    result.getFloat("total_ttc"),
                    result.getInt("facture_id"),
                    result.getBoolean("definitive"),
                    result.getBoolean("paid"),
                    linesArray
                );
                facturesArray.add(facture);    
            }
            return facturesArray;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                                      ***
    //  METHOD to PULL the LAST FACTURE CODE from the DATABASE for a SCI and a TYPE of facture
    //                                      ***

    public String getLastFactureCode(Integer sciId, Boolean definitive){
            // letter which STARTS the searched FACTURE CODE
        String firstletter;
        if (definitive.equals(true)){
            firstletter = "F";
        } else {
            firstletter = "P";
        }
            // String to return
        String lastId=null;
            // Query
        String qu = "SELECT facture_code FROM Facture WHERE sci_id = " 
            + sciId + "AND facture_code LIKE '"
            + firstletter + "%' ORDER BY facture_code DESC fetch first 1 rows only";
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                lastId = result.getString("facture_code");
            }        
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return lastId;
    }



    //                             ***
    //  METHOD to return AN ARRAY of EXISTING FACTURES as FACTURE DATA CLASS
    //                             ***

    public ArrayList getFactures(Integer sciId){
            // ARRAY to return
        ArrayList<FactureClass> factureList = new ArrayList<FactureClass>();
            // Query
        String qu = "SELECT * FROM Facture WHERE sci_id = "+ sciId + "ORDER BY facture_code DESC";
            // Result Set to parse
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                FactureClass facture = new FactureClass(
                    result.getInt("id"), 
                    result.getInt("sci_id"), 
                    result.getInt("client_id"), 
                    result.getString("facture_code"), 
                    result.getBoolean("definitive"),
                    result.getBoolean("paid"),
                    result.getDate("definitive_date"),
                    result.getFloat("total_HT"),
                    result.getFloat("total_TVA"),
                    result.getFloat("total_TTC"),
                    result.getString("comment")
                );                
                factureList.add(facture);              
            }
            return factureList;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                                  ***
    //   METHOD to return a FACTURE DATA CLASS based on a SCI and FACTURE_CODE
    //                                  ***

    public FactureClass getFacture(Integer sciId, String factureCode){
            // Facture to return
        FactureClass facture = null;
            // Query
        String qu = "SELECT * FROM Facture WHERE sci_id = "+ sciId + " AND facture_code = '" + factureCode + "'";
            // Result parsed, only one possible
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                facture = new FactureClass(
                    result.getInt("id"), 
                    result.getInt("sci_id"), 
                    result.getInt("client_id"), 
                    result.getString("facture_code"), 
                    result.getBoolean("definitive"),
                    result.getBoolean("paid"),
                    result.getDate("definitive_date"),
                    result.getFloat("total_HT"),
                    result.getFloat("total_TVA"),
                    result.getFloat("total_TTC"),
                    result.getString("comment")
                );              
            }
            return facture;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                           ***
    //  METHOD to return a FACTURE DATA CLASS based on its FACTURE_ID
    //                           ***

    public FactureClass getFacture(Integer factureId){
            // Facture returned
        FactureClass facture = null;
            // Query
        String qu = "SELECT * FROM Facture WHERE id = "+ factureId;
            // Result parsed, only one possible
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()) {
                facture = new FactureClass(
                    result.getInt("id"), 
                    result.getInt("sci_id"), 
                    result.getInt("client_id"), 
                    result.getString("facture_code"), 
                    result.getBoolean("definitive"),
                    result.getBoolean("paid"),
                    result.getDate("definitive_date"),
                    result.getFloat("total_HT"),
                    result.getFloat("total_TVA"),
                    result.getFloat("total_TTC"),
                    result.getString("comment")
                );              
            }
            return facture;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }


    //                      ***
    //  METHOD to change the PAID status of a facture to TRUE
    //                      ***
    public void validatePayment(Integer factureId){
            // Query creation
        String qu = "UPDATE Facture SET paid = true WHERE id = " + factureId;
        System.out.println(qu);
            // Query execution and verification
        execActionWithAlerts(qu, "update");   
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
            alert.setContentText("Entrez une information dans chaque champ");
        } else {
            alert.setContentText("Référence externe manquante");
        }
        alert.showAndWait();
        return;
    }


        // Execution of query and related alerts method
    public void execActionWithAlerts(String qu, String type){
        if (dBhandler.execAction(qu)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            if (type == "creation"){
                alert.setContentText("Facture créée avec succès !");
            }
            if (type == "update"){
                alert.setContentText("Facture mise à jour avec succès !");
            }
            if (type == "definitive"){
                alert.setContentText("Facture définitive sauvegardée");
            }   
            if (type == "deletion"){
                alert.setContentText("Facture supprimée");
            }                        
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Echec de la requête");
            alert.showAndWait();
        }
    }


    
    //              ***
    // GENERATION OF A FACTURE CODE
    //              ***
        // Arguments : sciId ; definitive
        // Queries the database to extract the last factureId for that sciId, getting :
            // a temporary (PYYYY-00X) string if the facture is temporary (definitive = false)
            // a definitive (FYYYY-MM-00X) string if the facture is definitive
    public String generateFactureCode(Integer sciId, Boolean definitive){
        String lastId = getLastFactureCode(sciId, definitive);

            // Creation of a timestamp of current date YYYY-MM
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String currentTimeStamp = date.format(formatter);
        String currentDelims = "[\\-]+";
        String[] currentTokens = currentTimeStamp.split(currentDelims);
            // Tokens for current date
        Integer currentYear = Integer.valueOf(currentTokens[0]);
        Integer currentMonth = Integer.valueOf(currentTokens[1]);

            // DEFINITIVE FACTURE (FYYYY-MM-XXX)
        if (definitive.equals(true)){   
                // A code was found 
            if (lastId != null){
                String delims = "[F\\-]+";
                String[] tokens = lastId.split(delims);   
                    // Tokens for last date  
                Integer lastYear = Integer.valueOf(tokens[1]);
                Integer lastMonth = Integer.valueOf(tokens[2]);
                Integer lastNumber = Integer.valueOf(tokens[3]);
    
                System.out.println("ID Found : F"+lastYear+"-"+lastMonth+"-"+lastNumber);
    
                    // Last code is for the same year
                if (currentYear.equals(lastYear)){
                        // Increments the number to print by one
                    Integer currentNumber = (int)lastNumber+1;    
                        // formats it depending on the number of digits
                    if ((int)currentNumber <10){
                        return "F"+currentTimeStamp+"-00"+currentNumber;
                    } else if ((int)currentNumber <100) {
                        return "F"+currentTimeStamp+"-0"+currentNumber;
                    } else {
                        return "F"+currentTimeStamp+"-"+currentNumber;
                    }
                }   
                    // Last code is from an anterior year
                else {
                        // Starts at one
                    return "F"+currentTimeStamp+"-001";
                }
            } 
                // First definitive facture for that sci
            else {
                System.out.println("no Definitive ID found");
                return "F"+currentTimeStamp+"-001";
            }
        } 
            // TEMPORARY FACTURE (PYYYY-XXX)
        else {
                // A code is found
            if (lastId != null){
                String delims = "[P\\-]+";
                String[] tokens = lastId.split(delims);  
                    // Tokens for last year
                Integer lastYear = Integer.valueOf(tokens[1]);
                Integer lastNumber = Integer.valueOf(tokens[2]); 
                
                System.out.println("ID Found : P"+lastYear+"-"+lastNumber);
                    // Same as current year
                if (currentYear.equals(lastYear)){
                    Integer currentNumber = (int)lastNumber+1;
                    if ((int)currentNumber <10){
                        return "P"+currentYear+"-00"+currentNumber;
                    } else if ((int)currentNumber <100) {
                        return "P"+currentYear+"-0"+currentNumber;
                    } else {
                        return "P"+currentYear+"-"+currentNumber;
                    }
                } 
                    // first one for current year
                else {
                    return "P"+currentYear+"-001";
                }
                // no code for that sci
            } else {
                System.out.println("no temporary ID found");
                return "P"+currentYear+"-001";
            }
        }
    } 
        

    
}

