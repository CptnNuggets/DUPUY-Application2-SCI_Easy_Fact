package facturationlds.databaseClasses;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.FactureMethods.FactureClass;
import facturationlds.databaseClasses.PrestationMethods.TarifForFactureClass;
import facturationlds.databaseClasses.SCIMethods.SCIClass;




    //                                  ***
    //   STATIC METHODS used to EXPORT CSV FILES containing the data for the ACCOUNTANTS
    //                                  ***

public class ExportMethods {
        // Method classes
    private static FactureMethods factureMethods;
    private static SCIMethods sciMethods;
    private static ClientMethods clientMethods;
    private static PrestationMethods prestationMethods;



    //                   ***
    //   Method called for a GLOBAL EXPORT of ALL DATA
    //                   ***

    public static void exportAll(){
            // INSTANCIATES all METHOD CLASSES
        factureMethods = new FactureMethods();
        sciMethods = new SCIMethods();
        clientMethods = new ClientMethods();
        prestationMethods = new PrestationMethods();

            // gets an ARRAY containing ALL SCIS
        ArrayList<SCIClass> sciList = sciMethods.getAllSCIs();
            // Parses the array
        for (SCIClass sci : sciList){
                // All 3 exports per SCI
            exportPerSCI(sci.getId(), sci.getSciName());
            exportClients(sci.getId(), sci.getSciName());
            exportAccountDetails(sci.getId(), sci.getSciName());
        }
    }



    
    //                   ***
    //   Creates a CSV FILE containing the MONEY GAINED
    //                   ***

    private static void exportPerSCI(Integer sciId, String sciName){

            // Data to be printed in the csv file
        List<String[]> csvData = new ArrayList<>();
            // Header of the csv file
        String[] header = {"Date", "Numéro Facture", "Code Client", "Montant HT", "Montant TVA", "Montant TTC"};
        csvData.add(header);
            // Get a list of factures for the SCI_ID
        ArrayList<FactureClass> factureList = factureMethods.getFactures(sciId);
            // Parses the list
        for (FactureClass facture : factureList){
                // only counts the DEFINITIVE factures
            if (facture.getDefinitive().equals(true)){
                    // Get Client info
                ClientClass client = clientMethods.getClient(facture.getClientId());
                    // Adds the relevant facture and client info to csv data
                String[] line = {
                    facture.getDefinitiveDate().toString(),
                    facture.getFactureCode(),
                    client.getClientCode(),
                    String.format("%.2f", facture.getTotalHT()),
                    String.format("%.2f", facture.getTotalTVA()),
                    String.format("%.2f", facture.getTotalTTC())
                };
                csvData.add(line);
            }
        }
        try {
                // writes data in the csv file
            CSVWriter writer = new CSVWriter(new FileWriter("../Exports/CA_"+ sciName +".csv"),';' , CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(csvData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //                   ***
    //   Creates a CSV FILE containing CLIENT DATA
    //                   ***

    private static void exportClients(Integer sciId, String sciName){
            // Data to be printed in the csv file
        List<String[]> csvData = new ArrayList<>();
            // Header of the csv file
        String[] header = {"Code Client", "Type Client", "Rue", "Code Postal", "Ville"};
        csvData.add(header);
            // Get a list of clients for the SCI_ID
        ArrayList<ClientClass> clientList = clientMethods.getClients(sciId);
            // Parses the list
        for (ClientClass client : clientList){
            Boolean moral = client.getMoral();
            String type = null;
            if (moral.equals(true)){
                type = "Personne morale";
            } else {
                type = "Personne mhysique";
            }
            String[] line = {
                client.getClientCode(),
                type,
                client.getStreetAndNumberFac(),
                client.getZipCodeFac(),
                client.getCityFac()
            };
            csvData.add(line);
        }
        try {
                // Writes the data in the csv file
            CSVWriter writer = new CSVWriter(new FileWriter("../Exports/Clients_"+ sciName +".csv"),';' , CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(csvData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //                   ***
    //   Creates a CSV FILE containing FACTURE LINES
    //                   ***

    private static void exportAccountDetails(Integer sciId, String sciName){
            // Data to be printed in the csv file
        List<String[]> csvData = new ArrayList<>();
            // Header of the csv file
        String[] header = {"Numéro Facture", "Item", "Prix Unitaire", "Quantité", "Taux TVA"};
        csvData.add(header);
            // Get a list of factures for the SCI_ID
        ArrayList<FactureClass> factureList = factureMethods.getFactures(sciId);
            // Parses the list
        for (FactureClass facture : factureList){
                // For definitive factures
            if (facture.getDefinitive().equals(true))
            {
                    // Get the facture lines as an array 
                ArrayList<TarifForFactureClass> tarifList = prestationMethods.getTarifsForFacture(facture.getId());
                    // Parses the array
                for (TarifForFactureClass tarif : tarifList){
                    String[] line = {
                        facture.getFactureCode(),
                        tarif.toString(),
                        String.format("%.2f", tarif.getUnitPrice()),
                        String.valueOf(tarif.getQuantity()),
                        String.format("%.2f", tarif.getTvaPercentage())
                    };
                        // Aff the facture lines to the csv
                    csvData.add(line);
                }
            }
        }
        try {
                // Writes data in the csv
            CSVWriter writer = new CSVWriter(new FileWriter("../Exports/DetailsComptes_"+ sciName +".csv"),';' , CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(csvData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
