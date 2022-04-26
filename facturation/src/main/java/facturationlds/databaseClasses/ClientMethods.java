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




    //                                                  ***
    // METHOD CLASS containing the CLIENT DATA CLASS, the related methods and the CLIENT-BASED DATABASE methods
    //                                                  ***

public class ClientMethods {

    DataBaseHandler dBhandler;

    public ClientMethods(){
            // gets the CURRENT INSTANCE of the DATABASE HANDLER at CONSTRUCTION
        dBhandler = DataBaseHandler.getInstance();
    }


        
    //            ***
    //  EXTERNAL CLIENT DATA CLASS
    //            ***
    public class ClientClass {
        private Integer id;
        private Integer sciId;
        private String clientCode;
        private Boolean moral;
        private String streetAndNumber;
        private String streetComplement;
        private String zipCode;
        private String city;
        private String streetAndNumberFac;
        private String streetComplementFac;
        private String zipCodeFac;
        private String cityFac;
        private String firstName;
        private String lastName;
        private String companyName;
        private String companyType;
        private String paymentDelay;

            // CONSTRUCTOR
        public ClientClass(Integer id, Integer sciId, String clientCode, Boolean moral,
            String streetAndNumber, String streetComplement, String zipCode, String city,
            String streetAndNumberFac, String streetComplementFac, String zipCodeFac, String cityFac, 
            String firstName, String lastName, String companyName, String companyType, String paymentDelay) {
                this.id = id;
                this.sciId = sciId;
                this.clientCode = clientCode;
                this.moral = moral;
                this.streetAndNumber = streetAndNumber;
                this.streetComplement = streetComplement;
                this.zipCode = zipCode;
                this.city = city;
                this.streetAndNumberFac = streetAndNumberFac;
                this.streetComplementFac = streetComplementFac;
                this.zipCodeFac = zipCodeFac;
                this.cityFac = cityFac;
                this.firstName = firstName;
                this.lastName = lastName;
                this.companyName = companyName;
                this.companyType = companyType;
                this.paymentDelay = paymentDelay;
        }

            // GETTERS
        public Integer getId(){return id;}
        public Integer getSciId(){return sciId;}
        public String getClientCode(){return clientCode;}
        public Boolean getMoral(){return moral;}
        public String getStreetAndNumber(){return streetAndNumber;}
        public String getStreetComplement(){return streetComplement;}
        public String getZipCode(){return zipCode;}
        public String getCity(){return city;}
        public String getStreetAndNumberFac(){return streetAndNumberFac;}
        public String getStreetComplementFac(){return streetComplementFac;}
        public String getZipCodeFac(){return zipCodeFac;}
        public String getCityFac(){return cityFac;}
        public String getFirstName(){return firstName;}
        public String getLastName(){return lastName;}
        public String getCompanyName(){return companyName;}
        public String getCompanyType(){return companyType;}
        public String getPaymentDelay(){return paymentDelay;}

            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return this.clientCode;
        }
    }




    //                      ***
    //  METHOD to INSERT a NEW CLIENT into the DATABASE
    //                      ***

    public Boolean insertClient(Integer sciId, String clientCode, Boolean moral,
        String streetAndNumber, String streetComplement, String zipCode, String city,
        String streetAndNumberFac, String streetComplementFac, String zipCodeFac, String cityFac, 
        String firstName, String lastName, String companyName, String companyType, String paymentDelay){

            
            // Verification that all needed input is present
        if ( clientCode == null || streetAndNumber == null || zipCode == null ||city == null || 
            streetAndNumberFac == null || zipCodeFac == null || cityFac == null || moral == null
        ){
            alertMissingField(true);
            return false;
        } else if (clientCode.isEmpty() || streetAndNumber.isEmpty() || zipCode.isEmpty() || city.isEmpty() ||
            streetAndNumberFac.isEmpty() || zipCodeFac.isEmpty() || cityFac.isEmpty() || 
            ( moral.equals(true) && ( companyType == null || companyName == null ) )  || 
            ( moral.equals(false) && ( firstName == null || lastName == null) )  
        ) {
            alertMissingField(true);
            return false;
        } else if (
            ( moral.equals(true) && ( companyType.isEmpty() || companyName.isEmpty()) )  || 
            ( moral.equals(false) && ( firstName.isEmpty() || lastName.isEmpty()) )
        ){
            alertMissingField(true);
            return false;
        } else if (sciId == null) {
            alertMissingField(false);
            return false;
        } else {



                // Formatting to escape single quotes in the query
            String dBClientCode = dBhandler.formatedForDB(clientCode);
            String dBStreetAndNumber = dBhandler.formatedForDB(streetAndNumber);
            String dBStreetComplement = dBhandler.formatedForDB(streetComplement); 
            String dBZipCode = dBhandler.formatedForDB(zipCode); 
            String dBCity = dBhandler.formatedForDB(city);
            String dBStreetAndNumberFac = dBhandler.formatedForDB(streetAndNumberFac);
            String dBStreetComplementFac = dBhandler.formatedForDB(streetComplementFac); 
            String dBZipCodeFac = dBhandler.formatedForDB(zipCodeFac); 
            String dBCityFac = dBhandler.formatedForDB(cityFac);
            String dBFirstName = dBhandler.formatedForDB(firstName);
            String dBLastName = dBhandler.formatedForDB(lastName);
            String dBCompanyName = dBhandler.formatedForDB(companyName);
            String dBCompanyType = dBhandler.formatedForDB(companyType);
            String dBPaymentDelay = dBhandler.formatedForDB(paymentDelay);

                // Query creation
            String qu = "INSERT INTO Client (sci_id, client_code, moral, "
            + "street_and_number, street_complement, zip_code, city, "
            + "street_and_number_fac, street_complement_fac, zip_code_fac, city_fac, "
            + "first_name, last_name, company_name, company_type, payment_delay"
            + " ) VALUES ("
            + "" + sciId + ", "
            + "" + dBClientCode + ", "
            + "" + moral + ", "
            + "" + dBStreetAndNumber + ", "
            + "" + dBStreetComplement + ", "
            + "" + dBZipCode + ", "
            + "" + dBCity + ", "
            + "" + dBStreetAndNumberFac + ", "
            + "" + dBStreetComplementFac + ", "
            + "" + dBZipCodeFac + ", "
            + "" + dBCityFac + ", "
            + "" + dBFirstName + ", "
            + "" + dBLastName + ", "
            + "" + dBCompanyName + ", "
            + "" + dBCompanyType + ", "
            + "" + dBPaymentDelay + " )";
            System.out.println(qu);

                // Query execution and verification
            return (execActionWithAlerts(qu, "creation"));
        }
    }




    //                      ***
    //  METHOD to UPDATE a CLIENT in the DATABASE
    //                      ***

    public Boolean updateClient(Integer id, Integer sciId, String clientCode, Boolean moral,
    String streetAndNumber, String streetComplement, String zipCode, String city,
    String streetAndNumberFac, String streetComplementFac, String zipCodeFac, String cityFac, 
    String firstName, String lastName, String companyName, String companyType, String paymentDelay){
        
            // Verification that all needed input is present 
        if (clientCode == null || streetAndNumber == null || zipCode == null || city == null ||
            streetAndNumberFac == null ||  zipCodeFac == null || cityFac == null  || moral == null
        ){
            alertMissingField(true);
            return false;
        } else if (clientCode.isEmpty() || streetAndNumber.isEmpty() || zipCode.isEmpty() || city.isEmpty() ||
            streetAndNumberFac.isEmpty() || zipCodeFac.isEmpty() || cityFac.isEmpty() || 
            ( moral.equals(true) && ( companyType == null || companyName == null ) )  || 
            ( moral.equals(false) && ( firstName == null || lastName == null) )  
        ) {
            alertMissingField(true);
            return false;
        } else if (
            ( moral.equals(true) && ( companyType.isEmpty() || companyName.isEmpty()) )  || 
            ( moral.equals(false) && ( firstName.isEmpty() || lastName.isEmpty()) )
        ){
            alertMissingField(true);
            return false;
        } else if (sciId == null || id == null) {
            alertMissingField(false);
            return false;
        } else {

                // Formatting to escape single quotes in the query
            String dBClientCode = dBhandler.formatedForDB(clientCode);
            String dBStreetAndNumber = dBhandler.formatedForDB(streetAndNumber);
            String dBStreetComplement = dBhandler.formatedForDB(streetComplement); 
            String dBZipCode = dBhandler.formatedForDB(zipCode); 
            String dBCity = dBhandler.formatedForDB(city);
            String dBStreetAndNumberFac = dBhandler.formatedForDB(streetAndNumberFac);
            String dBStreetComplementFac = dBhandler.formatedForDB(streetComplementFac); 
            String dBZipCodeFac = dBhandler.formatedForDB(zipCodeFac); 
            String dBCityFac = dBhandler.formatedForDB(cityFac);
            String dBFirstName = dBhandler.formatedForDB(firstName);
            String dBLastName = dBhandler.formatedForDB(lastName);
            String dBCompanyName = dBhandler.formatedForDB(companyName);
            String dBCompanyType = dBhandler.formatedForDB(companyType);
            String dBPaymentDelay = dBhandler.formatedForDB(paymentDelay);

                // Query creation
            String qu = "UPDATE Client SET "
            + "client_code = " + dBClientCode + ", "
            + "moral = " + moral + ", "
            + "street_and_number = " + dBStreetAndNumber + ", "
            + "street_complement = " + dBStreetComplement + ", "
            + "zip_code = " + dBZipCode + ", "
            + "city = " + dBCity + ", "
            + "street_and_number_fac = " + dBStreetAndNumberFac + ", "
            + "street_complement_fac = " + dBStreetComplementFac + ", "
            + "zip_code_fac = " + dBZipCodeFac + ", "
            + "city_fac = " + dBCityFac + ", "
            + "first_name = " + dBFirstName + ", "
            + "last_name = " + dBLastName + ", "
            + "company_name = " + dBCompanyName + ", "
            + "company_type = " + dBCompanyType + ", "
            + "payment_delay = " + dBPaymentDelay + " "
            + "WHERE id = " + id + " ";
            System.out.println(qu);

                // Execution and verification
            return execActionWithAlerts(qu, "update");
        }

    }



    //                      ***
    //  METHOD to "DELETE" a CLIENT from the DATABASE
    //                      ***

    public Boolean deleteClient(Integer id){
        
        if ( id == null) {
            alertMissingField(false);
            return false;
        } else {

                // Query creation
            String qu = "UPDATE Client SET visible = false WHERE id = " + id;
            System.out.println(qu);

                // Execution and verification
            return execActionWithAlerts(qu, "delete");
        }

    }






    //                      ***
    //   METHOD to RETURN an ARRAY of EXISTING CLIENTS
    //                      ***

    public ArrayList getClients(Integer sciId){
            // Array to return
        ArrayList<ClientClass> clientList = new ArrayList<ClientClass>();
            // Query
        String qu = "SELECT * FROM Client WHERE visible = true AND sci_id = "+ sciId + " ORDER BY client_code";
            // Result Set to Parse
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                ClientClass client = new ClientClass(
                    result.getInt("id"), 
                    result.getInt("sci_id"), 
                    result.getString("client_code"), 
                    result.getBoolean("moral"), 
                    result.getString("street_and_number"), 
                    result.getString("street_complement"), 
                    result.getString("zip_code"), 
                    result.getString("city"), 
                    result.getString("street_and_number_fac"), 
                    result.getString("street_complement_fac"), 
                    result.getString("zip_code_fac"), 
                    result.getString("city_fac"), 
                    result.getString("first_name"), 
                    result.getString("last_name"), 
                    result.getString("company_name"), 
                    result.getString("company_type"), 
                    result.getString("payment_delay")
                );
                clientList.add(client);
            }
            return clientList;
        } catch (SQLException e) {
            Logger.getLogger(ClientMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                                  ***
    //  METHOD to RETURN a CLIENT DATA CLASS for the CLIENT_ID passed as parameter
    //                                  ***

    public ClientClass getClient(Integer clientId){
            // CLIENT DATA CLASS to return
        ClientClass client = null;
            // Query
        String qu = "SELECT * FROM Client WHERE id = "+ clientId;
            // Result
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                client = new ClientClass(
                    result.getInt("id"), 
                    result.getInt("sci_id"), 
                    result.getString("client_code"), 
                    result.getBoolean("moral"), 
                    result.getString("street_and_number"), 
                    result.getString("street_complement"), 
                    result.getString("zip_code"), 
                    result.getString("city"), 
                    result.getString("street_and_number_fac"), 
                    result.getString("street_complement_fac"), 
                    result.getString("zip_code_fac"), 
                    result.getString("city_fac"), 
                    result.getString("first_name"), 
                    result.getString("last_name"), 
                    result.getString("company_name"), 
                    result.getString("company_type"), 
                    result.getString("payment_delay")
                );
            }
            return client;
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
            alert.setContentText("Entrez une information dans chaque champ");
        } else {
            alert.setContentText("Sélectionnez la SCI à laquelle rattacher ce client");
        }
        alert.showAndWait();
    }

        // Execution of query and related alerts method
    public Boolean execActionWithAlerts(String qu, String type){
        if (dBhandler.execAction(qu)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            if (type == "creation"){
                alert.setContentText("Client créé avec succès !");
            }
            if (type == "update"){
                alert.setContentText("Client mis à jour avec succès !");
            }   
            if (type == "delete"){
                alert.setContentText("Client supprimé !");
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

