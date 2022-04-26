package facturationlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



    //                                          ***
    // Class used to CONNECT to the DATABASE, and contains the methods to CREATE the DATABASE TABLES
    //                                          ***

public final class DataBaseHandler {

    private static DataBaseHandler dBhandler = null;

    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

        // CONSTRUCTOR : Creates the connection, and setups the table if they don't exist allready
    public DataBaseHandler() {
        createConnection();
        setupSCITable();
        setupClientTable();
        setupFactureTable();
        setupPrestationTable();
        setupTarifTable();
    }

    

    //                           ***
    // Method that UPDATES a FACTURE by adding to it a PDF FILE as BLOB
    //                           ***
        // the file PATH and the FACTURE ID are passed as argument
        // a BOOLEAN is returned to indicate if the update is successfull
    public boolean insertPDF (String pdfName, Integer factureId){

        try {
            InputStream iStream = new FileInputStream(pdfName);  
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE FACTURE SET pdf_file = ? " 
                    + "WHERE id = " + factureId
                );
            ps.setBinaryStream(1, iStream);
            ps.execute();                                  
            System.out.println("PDF FILE saved in the Database");
            return true;      
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error executing the action !");
            alert.setHeaderText("Error : File not Found");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error executing the action !");
            alert.setHeaderText("Error :" + e.getErrorCode());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }



    //                           ***
    // Method that CREATES a PDF FILE by retrieving a BLOB in the FACTURE table
    //                           ***
        // the file to create PATH and the FACTURE ID are passed as argument
        // a BOOLEAN is returned as TRUE if the BLOB existed and the file was created, FALSE if the blob didnt exist
    public Boolean extractPDF(Integer factureId, String fileName) throws SQLException, IOException{
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(
                    "SELECT pdf_file "
                    + "FROM FACTURE "
                    + "WHERE id = " + factureId
                );
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error executing the action !");
            alert.setHeaderText("Error :" + e.getErrorCode());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            result=null;
        }

        Boolean fileAllreadyExists = false;
            
        if (result != null) {
            byte buff[] = new byte[1024];

            while (result.next()) {
                Blob pdfAsBlob = result.getBlob("pdf_file");
                    // the PDF FILE EXISTS
                if (pdfAsBlob != null){
                    File newFacture = new File(fileName);
    
                    InputStream iStream = pdfAsBlob.getBinaryStream();
    
                    FileOutputStream fOutputStream = new FileOutputStream(newFacture);
    
                    for (int b = iStream.read(buff); b != -1; b = iStream.read(buff)){
                        fOutputStream.write(buff, 0, b);
                    }     
                    iStream.close();
                    fOutputStream.close();
                    fileAllreadyExists = true;
                }                 
            }
            stmt.close();
            // the PDF FILE need to BE CREATED
        } 
        return fileAllreadyExists;
    }





    //                                          ***
    // Method called in the other classes to GET the INSTANCE of the DBHANDLER started with the App.
    //                                          ***

    public static DataBaseHandler getInstance() {
        if (dBhandler==null){
            dBhandler =new DataBaseHandler();
        }
        return dBhandler;
    }

        

    //        ***
    // CONNECTION to the database
    //        ***

    void createConnection () {
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


       
    //                      ***
    // CREATION of the SCI TABLE if it doesn't exist allready
    //                      ***
        // Contains all data related to the SCI

    void setupSCITable() {    
        String TABLE_NAME = "SCI";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go !");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "(" 
                    + "     id int primary key not null generated always as identity, \n"
                    + "     sci_name varchar(200) not null, \n"
                    + "     street_and_number varchar(200) not null, \n"
                    + "     street_complement varchar(200), \n"
                    + "     zip_code varchar(10) not null, \n"
                    + "     city varchar(200) not null, \n"
                    + "     email varchar(200), \n"
                    + "     phone varchar(200), \n"
                    + "     website varchar(200), \n"
                    + "     iban varchar(200), \n"
                    + "     bic varchar(200), \n"
                    + "     account_owner varchar(200), \n"
                    + "     payment_method int default 0, \n"
                    + "     penalties varchar(200), \n"
                    + "     escompte varchar(200), \n"
                    + "     recovery varchar(200), \n"
                    + "     siret varchar(200) not null, \n"
                    + "     rcs varchar(200) not null, \n"
                    + "     social_funds varchar(200) \n"
                    + " )");
            }
        } catch (SQLException e){
            System.err.println(e.getMessage() + " ---  setupDatabase");
        } finally {
        }
    }

      

    //                      ***
    // CREATION of the CLIENT TABLE if it doesn't exist allready
    //                      ***
        // Contains all data related to the Client
            // First_name & Last_name fields are mutually exclusive with Company_Name & Company_type, depending on the MORAL attribute value
            // a VISIBLE attributes allows preservation in the database after deletion from the interface
            // the CLIENT_CODE attribute is UNIQUE for a GIVEN SCI_ID

    void setupClientTable() {
        String TABLE_NAME = "Client";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go !");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "(" 
                    + "     id int primary key not null generated always as identity, \n"
                    + "     sci_id int not null, \n"
                    + "     client_code varchar(200) not null, \n"
                    + "     moral boolean default true not null, \n"
                    + "     street_and_number varchar(200) not null, \n"
                    + "     street_complement varchar(200), \n"
                    + "     zip_code varchar(50) not null, \n"
                    + "     city varchar(200) not null, \n"
                    + "     street_and_number_fac varchar(200) not null, \n"
                    + "     street_complement_fac varchar(200), \n"
                    + "     zip_code_fac varchar(50) not null, \n"
                    + "     city_fac varchar(200) not null, \n"
                    + "     first_name varchar(200), \n"
                    + "     last_name varchar(200), \n"
                    + "     company_name varchar(200), \n"
                    + "     company_type varchar(200), \n"
                    + "     payment_delay varchar(200), \n"
                    + "     visible boolean default true not null, \n"
                    + "     constraint sci_fk foreign key (sci_id) references SCI(id) on delete no action, \n"
                    + "     constraint unique_cli_sci unique (client_code, sci_id) )");
            }
        } catch (SQLException e){
            System.err.println(e.getMessage() + " ---  setupDatabase");
        } finally {
        }
    }

    
      
    //                      ***
    // CREATION of the FACTURE TABLE if it doesn't exist allready
    //                      ***
        // Contains all data related to the Facture
            // a FACTURE_CODE is UNIQUE for a given SCI_ID (which is the reason for that Foreign Key to exist)
            // a facture with DEFINITIVE set to TRUE can't be modified from the interface, except for the paid attribute
            // the PAID attribute becomes accessible once the facture is set to definitive

    void setupFactureTable() {
        String TABLE_NAME = "Facture";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go !");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "(" 
                    + "     id int primary key not null generated always as identity, \n"
                    + "     sci_id int not null, \n"
                    + "     client_id int not null, \n"
                    + "     facture_code varchar(200) not null, \n"
                    + "     definitive boolean default false not null, \n"
                    + "     paid boolean default false not null, \n"
                    + "     definitive_date date, \n"
                    + "     total_ht real, \n"
                    + "     total_tva real, \n"
                    + "     total_ttc real, \n"
                    + "     comment long varchar, \n"
                    + "     pdf_file blob (200 K), \n"
                    + "     constraint cli_fk foreign key (client_id) references CLIENT(id) on delete no action, \n"
                    + "     constraint facsci_fk foreign key (sci_id) references SCI(id) on delete no action, \n"
                    + "     constraint unique_fac_sci unique (facture_code, sci_id) )");
            }
        } catch (SQLException e){
            System.err.println(e.getMessage() + " ---  setupDatabase");
        } finally {
        }
    }

    
      
    //                      ***
    // CREATION of the PRESTATION TABLE if it doesn't exist allready
    //                      ***
        // Contains all data related to the Prestations
            // a VISIBLE attribute allows preservation of the Tarifs even if a prestation is removed from the interface

    void setupPrestationTable() {    
        String TABLE_NAME = "Prestation";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go !");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "(" 
                    + "     id int primary key not null generated always as identity, \n"
                    + "     visible boolean default true not null, \n"
                    + "     presta_name varchar(200) not null )");
            }
        } catch (SQLException e){
            System.err.println(e.getMessage() + " ---  setupDatabase");
        } finally {
        }
    }


          
    //                      ***
    // CREATION of the TARIF TABLE if it doesn't exist allready
    //                      ***
        // Contains all data related to the Tarifs
            // the CLIENT_ID references CLIENT.ID for tarifs that have PRESTA_DEFAULT set to TRUE
            // the FACTURE_ID references FACTURE.ID for tarifs that have PRESTA_DEFAULT set to FALSE

    void setupTarifTable() {
        String TABLE_NAME = "Tarif";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go !");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "(" 
                    + "     id int primary key not null generated always as identity, \n"
                    + "     presta_id int not null, \n"
                    + "     unit_price_ht real not null, \n"
                    + "     tva_percentage real not null, \n"                    
                    + "     quantity int not null default 1, \n"
                    + "     description long varchar, \n"
                    + "     presta_default boolean default false not null, \n"
                    + "     client_id int, \n"
                    + "     facture_id int, \n"
                    + "     constraint descri_cli_fk foreign key (client_id) references CLIENT(id) on delete no action, \n"                    
                    + "     constraint facture_fk foreign key (facture_id) references FACTURE(id) on delete no action, \n"
                    + "     constraint prestation_fk foreign key (presta_id) references PRESTATION(id) on delete no action )");
            }
        } catch (SQLException e){
            System.err.println(e.getMessage() + " ---  setupDatabase");
        } finally {
        }
    }




    //          ***
    //   DATABASE QUERIES UTILS
    //          ***


        // General method to be called for SELECT queries
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Exception at execQuery:dataHandler" + e.getLocalizedMessage());
            result=null;
        } finally {}
        return result;
    }

        // General method to be called for INSERT or DELETE queries
    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error executing the action !");
            alert.setHeaderText("Error :" + e.getErrorCode());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }finally {}
    }

        // Method to format a string to escape apostrophes
    public String formatedForDB(String strToFormat){
        if (strToFormat == null || strToFormat.isEmpty()){
            return null;
        }
        else {
            try {
                return stmt.enquoteLiteral(strToFormat);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        
    }








    //                          ***
    // METHODS TO POPULATE THE DB FOR TESTING PURPOSES
    //                          ***

    public void populateSCITable(){
        ResultSet result = execQuery("SELECT * FROM SCI");
        Boolean allreadyPopulated = false;
        try {
            while (result.next()){
                if (result.getInt("id") > 0){
                    allreadyPopulated = true;   
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (allreadyPopulated == false){
            String qu = "INSERT INTO SCI ("
                + "  sci_name, street_and_number,   "
                + "  street_complement, zip_code, city,  "
                + "  email, phone, website, iban, bic, account_owner, "
                + "  payment_method, siret, rcs  "
                + ") VALUES ("
                + "  'SCI Heureuse', '2 allée occitane',  "
                + "  '3e buisson à droite', '21000', 'DIJON',  "
                + "  'lheureuse@sci.fr', '03.80.56.18.29',  "
                + "  'https://lheureuse.sci', 'FR56 78F4 TG56 8945', 'HYUJ7G89', 'Jacques O''NEILL',  "
                + "  2, '556008686000023', 'DIJON 4567999876'  "
                + ")";
            execAction(qu);
            System.out.println(qu);

            qu = "INSERT INTO SCI ("
            + "  sci_name, street_and_number,   "
            + "  zip_code, city,  "
            + "  email, phone, website, penalties, escompte, recovery, "
            + "  siret, rcs, social_funds "
            + ") VALUES ("
            + "  'SCI Triste', '185 Boulevard du Malheur',  "
            + "  '21000', 'DIJON',  "
            + "  'latristoune@sci.fr', '03.80.82.84.05',  "
            + "  'https://latristoune.sci', 'Pénalités de retard : 10 %', 'Pas d''escompte en cas de paiment anticipé', 'Indemnité forfaitaire en cas de recouvrement : 50 €', "
            + "  '334765876000023', 'DOLE 5655FGHT6T66', '4 500,00 €'  "
            + ")";
            execAction(qu);
            System.out.println(qu);
        } else {
            System.out.println("SCI Table allready populated");
        }
    }


    public void populateClient(){
        ResultSet result = execQuery("SELECT * FROM CLIENT");
        Boolean allreadyPopulated = false;
        try {
            while (result.next()){
                if (result.getInt("id") > 0){
                    allreadyPopulated = true;   
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (allreadyPopulated == false){
            ResultSet resultsci = execQuery("SELECT * FROM SCI");
            Integer sciId = null;
            try {
                while (resultsci.next()){
                    sciId = resultsci.getInt("id");
                    break;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (sciId != null){
                String qu = "INSERT INTO CLIENT ("
                    + "  sci_id, client_code,   "
                    + "  street_and_number, zip_code, city,  "
                    + "  street_and_number_fac, zip_code_fac, city_fac,  "
                    + "  company_name, company_type, payment_delay  "
                    + ") VALUES ("
                    + sciId + ",  'SARL LABEYRE',  "
                    + "  '72 allée des ennuis', '21000', 'DIJON',  "
                    + "  '72 allée des ennuis', '21000', 'DIJON',  "
                    + "  'LABEYRE ET FILS', 'SARL', 'sous 30 jours'  "
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO CLIENT ("
                    + "  sci_id, client_code,   "
                    + "  street_and_number, zip_code, city,  "
                    + "  street_and_number_fac, zip_code_fac, city_fac,  "
                    + "  company_name, company_type, payment_delay  "
                    + ") VALUES ("
                    + 2 + ",  'SAS PAPA',  "
                    + "  '36 quai des forgerons', '21000', 'DIJON',  "
                    + "  '92 allée des problèmes', '21800', 'CHENOVE',  "
                    + "  'PARAIN ET FILS', 'SAS', 'au premier du trimestre'  "
                    + ")";
                execAction(qu);
                System.out.println(qu);
            } else {
                System.out.println("Populate SCI Table First");
            }
        } else {
            System.out.println("Client Table allready populated");
        }        
    }

    public void populateFacture(){
        ResultSet result = execQuery("SELECT * FROM FACTURE");
        Boolean allreadyPopulated = false;
        try {
            while (result.next()){
                if (result.getInt("id") > 0){
                    allreadyPopulated = true;   
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (allreadyPopulated == false){
            ResultSet resultscli = execQuery("SELECT * FROM CLIENT");
            Integer clientId = null;
            Integer sciId = null;
            try {
                while (resultscli.next()){
                    clientId = resultscli.getInt("id");
                    sciId = resultscli.getInt("sci_id");
                    break;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (clientId != null){
                String qu = "INSERT INTO FACTURE ("
                    + "  sci_id, client_id,   "
                    + "  facture_code, definitive, paid,  "
                    + "  definitive_date, total_ht, total_tva, total_ttc  "
                    + ") VALUES ("
                    + sciId + ", " + clientId +",  "
                    + "  'F2021-10-001', true, true,  "
                    + "  '2021-10-25', 2000, 285, 2285  "
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO FACTURE ("
                    + "  sci_id, client_id,   "
                    + "  facture_code,  "
                    + "  total_ht, total_tva, total_ttc  "
                    + ") VALUES ("
                    + sciId + ", " + clientId +",  "
                    + "  'P2021-001',  "
                    + "  1332, 133.20, 1465.20  "
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO FACTURE ("
                    + "  sci_id, client_id,   "
                    + "  facture_code, definitive,   "
                    + "  definitive_date, total_ht, total_tva, total_ttc  "
                    + ") VALUES ("
                    + 2 + ", " + 2 +",  "
                    + "  'F2021-08-002', true, "
                    + " '2021-08-16', 910, 147, 1057   "
                    + ")";
                execAction(qu);
                System.out.println(qu);
            } else {
                System.out.println("Populate Client Table First");
            }
        } else {
            System.out.println("Facture Table allready populated");
        }        
    }


    public void populatePrestationTable(){
        ResultSet result = execQuery("SELECT * FROM PRESTATION");
        Boolean allreadyPopulated = false;
        try {
            while (result.next()){
                if (result.getInt("id") > 0){
                    allreadyPopulated = true;   
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (allreadyPopulated == false){
            String qu = "INSERT INTO PRESTATION (presta_name) "
                + "VALUES ('Loyer')";
            execAction(qu);
            System.out.println(qu);

            qu = "INSERT INTO PRESTATION (presta_name) "
            + "VALUES ('Charges')";
            execAction(qu);
            System.out.println(qu);
        } else {
            System.out.println("PRESTATION Table allready populated");
        }
    }

    public void populateTarifTable(){
        ResultSet result = execQuery("SELECT * FROM TARIF");
        Boolean allreadyPopulated = false;
        try {
            while (result.next()){
                if (result.getInt("id") > 0){
                    allreadyPopulated = true;   
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (allreadyPopulated == false){
            ResultSet resultsfac = execQuery("SELECT * FROM FACTURE");
            ResultSet resultspresta = execQuery("SELECT * FROM PRESTATION");
            Integer factureId = null;
            Integer prestaId = null;
            try {
                while (resultsfac.next()){
                    factureId = resultsfac.getInt("id");
                    break;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                while (resultspresta.next()){
                    prestaId = resultspresta.getInt("id");
                    break;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (factureId != null && prestaId != null){
                String qu = "INSERT INTO TARIF ("
                    + "  presta_id, unit_price_ht, tva_percentage,  "
                    + "  description, facture_id"
                    + ") VALUES ("
                    + prestaId + ", 500, 12, "
                    + "  'Bâtiment A', " + factureId
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO TARIF ("
                    + "  presta_id, unit_price_ht, tva_percentage, quantity, "
                    + "  description, facture_id"
                    + ") VALUES ("
                    + prestaId + ", 750, 15, 2, "
                    + "  'Bâtiment B, appartements 12 et 14', " + factureId
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO TARIF ("
                    + "  presta_id, unit_price_ht, tva_percentage, quantity, "
                    + "  description, facture_id"
                    + ") VALUES ("
                    + prestaId + ", 666, 10, 2, "
                    + "  'Entrées A et C', " + 2
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO TARIF ("
                    + "  presta_id, unit_price_ht, tva_percentage,  "
                    + "  description, facture_id"
                    + ") VALUES ("
                    + prestaId + ", 700, 15, "
                    + "  'Bâtiment B', " + 3
                    + ")";
                execAction(qu);
                System.out.println(qu);

                qu = "INSERT INTO TARIF ("
                    + "  presta_id, unit_price_ht, tva_percentage, quantity, "
                    + "  description, facture_id"
                    + ") VALUES ("
                    + 2 + ", 70, 20, 3,"
                    + "  'Récupération mois impayés', " + 3
                    + ")";
                execAction(qu);
                System.out.println(qu);
            } else {
                System.out.println("Populate Facture and Table First");
            }
        } else {
            System.out.println("Facture Table allready populated");
        }        
    }

        
}








