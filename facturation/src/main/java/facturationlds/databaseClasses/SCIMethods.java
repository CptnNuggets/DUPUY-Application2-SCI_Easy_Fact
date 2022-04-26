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
    // METHOD CLASS containing the SCI DATA CLASS, the related methods and the SCI-BASED DATABASE methods
    //                                                  ***

public class SCIMethods {
    
    DataBaseHandler dBhandler;

    public SCIMethods(){
        // gets the CURRENT INSTANCE of the DATABASE HANDLER at CONSTRUCTION
    dBhandler = DataBaseHandler.getInstance();
    }


    
    //           ***
    //  EXTERNAL SCI DATA CLASS
    //           ***
    public class SCIClass {
        private Integer id;
        private String sciName;
        private String siret;
        private String streetAndNumber;
        private String streetComplement;
        private String zipCode;
        private String city;
        private String email;
        private String phone;
        private String website;
        private Integer paymentMethod;
        private String iban;
        private String bic;
        private String accountOwner;
        private String penalties;
        private String escompte;
        private String recovery;
        private String rcs;
        private String socialFunds;

            // CONSTRUCTOR
        public SCIClass(Integer id, String sciName, String siret, String streetAndNumber,
            String streetComplement, String zipCode, String city, String email,
            String phone, String website, Integer paymentMethod, String iban,
            String bic, String accountOwner, String penalties, String escompte, String recovery,
            String rcs, String socialFunds){
            this.id = id;
            this.sciName = sciName;
            this.siret = siret;
            this.streetAndNumber = streetAndNumber;
            this.streetComplement = streetComplement;
            this.zipCode = zipCode;
            this.city = city;
            this.email = email;
            this.phone = phone;
            this.website =website;
            this.paymentMethod = paymentMethod;
            this.iban = iban;
            this.bic = bic;
            this.accountOwner = accountOwner;
            this.penalties = penalties;
            this.escompte = escompte;
            this.recovery = recovery;
            this.rcs = rcs;
            this.socialFunds = socialFunds;
        }
            // GETTERS
        public Integer getId(){return id;}
        public String getSciName(){return sciName;}
        public String getSiret(){return siret;}
        public String getStreetAndNumber(){return streetAndNumber;}
        public String getStreetComplement(){return streetComplement;}
        public String getZipCode(){return zipCode;}
        public String getCity(){return city;}
        public String getEmail(){return email;}
        public String getPhone(){return phone;}
        public String getWebsite(){return website;}
        public Integer getPaymentMethod(){return paymentMethod;}
        public String getIban(){return iban;}
        public String getBic(){return bic;}
        public String getAccountOwner(){return accountOwner;}
        public String getPenalties(){return penalties;}
        public String getEscompte(){return escompte;}
        public String getRecovery(){return recovery;}
        public String getRCS(){return rcs;}
        public String getSocialFunds(){return socialFunds;}

            // OVERRIDE OF THE "toString()" METHOD TO ENABLE THE RETRIEVEAL OF A FIELD IN THE LIST DISPLAY
        @Override
        public String toString() {
            return this.sciName;
        }
    }   
   


    //                      ***
    //      METHOD to INSERT a new SCI into the DATABASE
    //                      ***

    public Boolean insertSCI(String sciName, String siret, String streetAndNumber,
    String streetComplement, String zipCode, String city, String email,
    String phone, String website, Integer paymentMethod, String iban,
    String bic, String accountOwner, String penalties, String escompte, String recovery,
    String rcs, String socialFunds){

            // Verification that all needed input is present
        if ( sciName == null || siret == null || streetAndNumber == null || zipCode == null
            || city == null || paymentMethod == null || rcs == null
        ) {
            alertMissingField();
            return false;
        }else if (sciName.isEmpty() || siret.isEmpty() || streetAndNumber.isEmpty() || zipCode.isEmpty() ||
            city.isEmpty() || ( !paymentMethod.equals(0) && !paymentMethod.equals(1) && !paymentMethod.equals(2) ) || rcs.isEmpty()){
            alertMissingField();
            return false;
        } else {

                // Formatting to escape single quotes in the query
            String dBsciName = dBhandler.formatedForDB(sciName);
            String dBsiret = dBhandler.formatedForDB(siret); 
            String dBstreetAndNumber = dBhandler.formatedForDB(streetAndNumber);
            String dBstreetComplement = dBhandler.formatedForDB(streetComplement); 
            String dBzipCode = dBhandler.formatedForDB(zipCode); 
            String dBcity = dBhandler.formatedForDB(city);
            String dBemail = dBhandler.formatedForDB(email);
            String dBphone = dBhandler.formatedForDB(phone); 
            String dBwebsite = dBhandler.formatedForDB(website); 
            String dBiban = dBhandler.formatedForDB(iban);
            String dBbic = dBhandler.formatedForDB(bic); 
            String dBaccountOwner = dBhandler.formatedForDB(accountOwner); 
            String dBpenalties = dBhandler.formatedForDB(penalties); 
            String dBescompte = dBhandler.formatedForDB(escompte); 
            String dBrecovery = dBhandler.formatedForDB(recovery);
            String dBRCS = dBhandler.formatedForDB(rcs);
            String dBSocialFunds = dBhandler.formatedForDB(socialFunds);


                // Query creation
            String qu = "INSERT INTO SCI (sci_name, street_and_number, street_complement, "
            + " zip_code, city, email, phone, website, iban, bic, account_owner, "
            + " payment_method, penalties, escompte, recovery, siret, rcs, social_funds) VALUES ("
            + "" + dBsciName + ", "
            + "" + dBstreetAndNumber + ", "
            + "" + dBstreetComplement + ", "
            + "" + dBzipCode + ", "
            + "" + dBcity + ", "
            + "" + dBemail + ", "
            + "" + dBphone + ", "
            + "" + dBwebsite + ", "
            + "" + dBiban + ", "
            + "" + dBbic + ", "
            + "" + dBaccountOwner + ", "
            + "" + paymentMethod + ", "
            + "" + dBpenalties + ", "
            + "" + dBescompte + ", "
            + "" + dBrecovery+ ", "
            + "" + dBsiret + ", "
            + "" + dBRCS + ", "
            + "" + dBSocialFunds + " )";
            System.out.println(qu);

            // Query execution and verification
            return (execActionWithAlerts(qu, "creation"));
        }
    }



    //            ***
    //   METHOD TO UPDATE A SCI
    //            ***

    public Boolean updateSCI(Integer sciId, String sciName, String siret, String streetAndNumber,
    String streetComplement, String zipCode, String city, String email,
    String phone, String website, Integer paymentMethod, String iban,
    String bic, String accountOwner, String penalties, String escompte, String recovery,
    String rcs, String socialFunds){

            // Verification that all needed input is present
        if ( sciName == null || siret == null || streetAndNumber == null || zipCode == null
            || city == null || paymentMethod == null || rcs == null
        ) {
            alertMissingField();
            return false;
        }else if (sciName.isEmpty() || siret.isEmpty() || streetAndNumber.isEmpty() || zipCode.isEmpty() ||
            city.isEmpty() || ( !paymentMethod.equals(0) && !paymentMethod.equals(1) && !paymentMethod.equals(2) ) || rcs.isEmpty()){
            alertMissingField();
            return false;
        } else if (sciId == null) {
            return false;
        } else {
        
                // Formatting to escape single quotes in the query
            String dBsciName = dBhandler.formatedForDB(sciName);
            String dBsiret = dBhandler.formatedForDB(siret); 
            String dBstreetAndNumber = dBhandler.formatedForDB(streetAndNumber);
            String dBstreetComplement = dBhandler.formatedForDB(streetComplement); 
            String dBzipCode = dBhandler.formatedForDB(zipCode); 
            String dBcity = dBhandler.formatedForDB(city);
            String dBemail = dBhandler.formatedForDB(email);
            String dBphone = dBhandler.formatedForDB(phone); 
            String dBwebsite = dBhandler.formatedForDB(website); 
            String dBiban = dBhandler.formatedForDB(iban);
            String dBbic = dBhandler.formatedForDB(bic); 
            String dBaccountOwner = dBhandler.formatedForDB(accountOwner); 
            String dBpenalties = dBhandler.formatedForDB(penalties); 
            String dBescompte = dBhandler.formatedForDB(escompte); 
            String dBrecovery = dBhandler.formatedForDB(recovery);
            String dBRCS = dBhandler.formatedForDB(rcs);
            String dBSocialFunds = dBhandler.formatedForDB(socialFunds);

                // SQL query for the update
            String qu = "UPDATE SCI SET " 
            + "sci_name = " + dBsciName + ", "
            + "street_and_number = " + dBstreetAndNumber + ", "
            + "street_complement = " + dBstreetComplement + ", "
            + "zip_code = " + dBzipCode + ", "
            + "city = " + dBcity + ", "
            + "email = " + dBemail + ", "
            + "phone = " + dBphone + ", "
            + "website = " + dBwebsite + ", "
            + "iban = " + dBiban + ", "
            + "bic = " + dBbic + ", "
            + "account_owner = " + dBaccountOwner + ", "
            + "payment_method = " + paymentMethod + ", "
            + "penalties = " + dBpenalties + ", "
            + "escompte = " + dBescompte + ", "
            + "recovery = " + dBrecovery+ ", "
            + "siret = " + dBsiret + ", "
            + "rcs = " + dBRCS + ", "
            + "social_funds = " + dBSocialFunds + " "
            + "WHERE id = " + sciId + " ";
            System.out.println(qu);

                // Execution and verification
            return (execActionWithAlerts(qu, "update"));
        }
    }



    //                                  ***
    // METHOD that queries the DATABASE to return an ARRAY of all the SCI DATA CLASSES 
    //                                  ***

    public ArrayList getAllSCIs(){
            // Array to return
        ArrayList<SCIClass> sciList = new ArrayList<SCIClass>();
            // query
        String qu = "SELECT * FROM SCI ORDER BY sci_name";
            // result set to parse
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                SCIClass sci = new SCIClass(
                    result.getInt("id"),
                    result.getString("sci_name"),
                    result.getString("siret"),
                    result.getString("street_and_number"),
                    result.getString("street_complement"),
                    result.getString("zip_code"),
                    result.getString("city"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getString("website"),
                    result.getInt("payment_method"),
                    result.getString("iban"),
                    result.getString("bic"),
                    result.getString("account_owner"),
                    result.getString("penalties"),
                    result.getString("escompte"),
                    result.getString("recovery"),
                    result.getString("rcs"),
                    result.getString("social_funds")
                );                
                sciList.add(sci);
            }
            return sciList;
        } catch (SQLException e){
            Logger.getLogger(SCIMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }



    //                                  ***
    // Method that queries the DATABASE to return a SCI DATA CLASS based on its ID 
    //                                  ***

    public SCIClass getSCI(Integer sciId){
        SCIClass sci = null;
        String qu = "SELECT * FROM SCI WHERE id = "+sciId;
        ResultSet result = dBhandler.execQuery(qu);
        try {
            while (result.next()){
                sci = new SCIClass(
                    result.getInt("id"),
                    result.getString("sci_name"),
                    result.getString("siret"),
                    result.getString("street_and_number"),
                    result.getString("street_complement"),
                    result.getString("zip_code"),
                    result.getString("city"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getString("website"),
                    result.getInt("payment_method"),
                    result.getString("iban"),
                    result.getString("bic"),
                    result.getString("account_owner"),
                    result.getString("penalties"),
                    result.getString("escompte"),
                    result.getString("recovery"),
                    result.getString("rcs"),
                    result.getString("social_funds")
                );                
            }
            return sci;
        } catch (SQLException e){
            Logger.getLogger(SCIMethods.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }




    //                      ***
    //                  UTILITIES
    //                      ***

        // Alert method, related to missing fields (true) or missing sciId (false)
    public void alertMissingField(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Informations manquantes");
        alert.setContentText("Entrez une information dans chaque champ");
        alert.showAndWait();
    }


    // Execution of query and related alerts method
        // Returns TRUE if the query was executed
        // Returns FALSE if it wasnt
    public boolean execActionWithAlerts(String qu, String type){
        if (dBhandler.execAction(qu)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            if (type == "creation"){
                alert.setContentText("SCI créée avec succès !");
            }
            if (type == "update"){
                alert.setContentText("SCI mise à jour avec succès !");
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

        // Function called to return the correct input for payment method
            // 2 : Check AND wire accepted
            // 1 : Wire ONLY
            // 0 : Check ONLY
            // NULL : BOTH FALSE ; CANT BE ENTERED IN THE DATABASE
    public Integer generatePaymentMethodInt(Boolean check, Boolean wire){
        if ( check.equals(true) && wire.equals(true) ) { 
            return 2;
        } else if ( check.equals(false) && wire.equals(true) ){
            return 1;
        } else if ( check.equals(true) && wire.equals(false) ){
            return 0;
        } else {
            return null;
        }
    }
            
}





