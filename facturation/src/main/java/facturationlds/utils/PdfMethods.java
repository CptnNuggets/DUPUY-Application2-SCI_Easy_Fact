package facturationlds.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import facturationlds.DataBaseHandler;
import facturationlds.databaseClasses.ClientMethods;
import facturationlds.databaseClasses.FactureMethods;
import facturationlds.databaseClasses.PrestationMethods;
import facturationlds.databaseClasses.SCIMethods;
import facturationlds.databaseClasses.ClientMethods.ClientClass;
import facturationlds.databaseClasses.FactureMethods.FactureClass;
import facturationlds.databaseClasses.PrestationMethods.TarifForFactureClass;
import facturationlds.databaseClasses.SCIMethods.SCIClass;

public class PdfMethods {

        // VARIABLE used to REMEMBER the Y OFFSET
    private static Integer yOffset;

        // DATA CLASSES 
    private static FactureClass facture;
    private static SCIClass sci;
    private static ClientClass client;



    //                                      ***
    // CLASS USED to FORMAT A LONG STRING in SEVERAL LINES based on the WIDTH wanted
    //                                      ***

    public static class LineWidthHandler {
        
        private Integer width;
        private String text;
        private PDFont font;
        private Integer fontSize;

        public LineWidthHandler(Integer width, String text, PDFont font, Integer fontSize) {
            this.width = width;
            this.text = text;
            this.font = font;
            this.fontSize = fontSize;
        }

        public ArrayList<String> getLines() throws IOException {
            ArrayList<String> result = new ArrayList<String>();

            String[] split = text.split("[ ]+");
            int[] endOfLineTrigger = new int[split.length];
            endOfLineTrigger[0] = split[0].length();
            for ( int i = 1 ; i < split.length ; i++ ) {
                endOfLineTrigger[i] = endOfLineTrigger[i-1] + split[i].length();
            }

            int start = 0;
            int end = 0;
            for ( int i : endOfLineTrigger ) {
                float width = font.getStringWidth(text.substring(start,i)) / 1000 * fontSize;
                if ( start < end && width > this.width ) {
                    result.add(text.substring(start,end));
                    start = end;
                }
                end = i;
            }
            // final chars
            result.add(text.substring(start));
            return result;
        }

        public float getFontHeight() {
            return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        }
        public int getWidth() {
            return width;
        }
        public String getText() {
            return text;
        }
        public PDFont getFont() {
            return font;
        }
        public int getFontSize() {
            return fontSize;
        }
    }



    //                  ***
    // CREATES the PDF_FILE based on a FACTURE_ID
    //                  ***
        // IF the facture is DEFINITIVE and the PDF is allready in the DATABASE, creates the file from the BLOB
        // If the facture is TEMPORARY or the PDF is NOT in the DATABASE, creates if from the OTHER DATA
    public static void generateFacture (Integer factureId) throws IOException, SQLException {

            // local METHOD CLASSES
        SCIMethods sciMethods = new SCIMethods();
        ClientMethods clientMethods = new ClientMethods();
        PrestationMethods prestationMethods = new PrestationMethods();  
        FactureMethods factureMethods = new FactureMethods();
        DataBaseHandler dbHandler = DataBaseHandler.getInstance();    
        
            // get the FACTURE DATA
        facture = factureMethods.getFacture(factureId);
            // get the SCI DATA
        sci = sciMethods.getSCI(facture.getSciId());
            // get the DOC NAME to give to the file
        String docName = getDocName();
            // Boolean used to verify if the file is allready in the DB
        Boolean fileAllreadyInDB = false;

            // IF the FACTURE is DEFINITIVE
        if (facture.getDefinitive().equals(true)){
        // EXTRACTS the PDF FILE from the DATABASE
            // RETURNS FALSE if the file doesn't exist yet
            fileAllreadyInDB = dbHandler.extractPDF(factureId, getFilePath(docName));

            if (fileAllreadyInDB.equals(true)){
                System.out.println("File extracted from database");
            }
        }
        
            // IF the file DOESN'T EXISTS (first DEFINITIVE creation, or TEMPORARY creation), CREATE it
        if (fileAllreadyInDB.equals(false) || facture.getDefinitive().equals(false)){
                // get the CLIENT DATA
            client = clientMethods.getClient(facture.getClientId());
                // get the FACTURE LINES DATA
            ArrayList<TarifForFactureClass> tarifList= prestationMethods.getTarifsForFacture(factureId);

                // CREATES the DOCUMENT
            PDDocument document = new PDDocument();
            PDPage my_page = new PDPage(PDRectangle.A4);
            document.addPage(my_page);

                // CREATES and INSTANCIATES the CONTENT STREAM
            PDPageContentStream contentStream = new PDPageContentStream(document, my_page);

                // ADDS the DIFFERENT BLOCKS
            contentStream = sciAdressBlock(document, my_page, contentStream, sci);
            contentStream = factureInfoBlock(document, my_page, contentStream, facture, client);        
            contentStream = clientAdressBlock(document, my_page, contentStream, client);
            contentStream = prestationsBlock(document, my_page, contentStream, tarifList, facture);
            contentStream = legalThingsBlock(document, my_page, contentStream, sci);
            contentStream = paymentInfoBlock(document, my_page, contentStream, sci);        
            contentStream = footerBlock(document, my_page, contentStream, sci, facture);

                // CLOSES the CONTENT STREAM
            contentStream.close();

                // NAMES and SAVES the DOCUMENT
            
            document.save(getFilePath(docName));
            document.close();
            System.out.println("Document created : " + docName);
                // IF the FACTURE is DEFINITIVE, ADDS the PDF to the DB
            if (facture.getDefinitive().equals(true)){
                dbHandler.insertPDF(getFilePath(docName), factureId);
            }
        }        
    }



    //                      ***
    // Creates the DOC NAME based on the FACTURE
    //                      ***

    private static String getDocName(){
        String docName = sci.getSciName() + "_" + facture.getFactureCode() + ".pdf";
        return docName;
    }



    //                      ***
    // Creates the FILE PATH based on the DOC NAME
    //                      ***

    private static String getFilePath(String docName){
        String filePath = "../Factures/" + docName;
        return filePath;
    }




    //                              ***
    // CREATES the SCI ADRESSE BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream sciAdressBlock(PDDocument document, PDPage page, PDPageContentStream contentStream, SCIClass sci) throws IOException
    {

        contentStream.beginText();        
        contentStream.setLeading(14.5f);        
        contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 14);
        contentStream.newLineAtOffset(42, 785);
        contentStream.showText(sci.getSciName());
        contentStream.newLine();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.showText(sci.getStreetAndNumber());
        if (sci.getStreetComplement() != null && !sci.getStreetComplement().isEmpty()){
            contentStream.newLine();
            contentStream.showText(sci.getStreetComplement());
        }
        contentStream.newLine();
        contentStream.showText(sci.getZipCode()+" "+sci.getCity());
        if (sci.getPhone() != null && !sci.getPhone().isEmpty()){
            contentStream.newLine();
            contentStream.showText(sci.getPhone());
        }
        if (sci.getEmail() != null && !sci.getEmail().isEmpty()){
            contentStream.newLine();
            contentStream.showText(sci.getEmail());
        }
        if (sci.getWebsite() != null && !sci.getWebsite().isEmpty()){
            contentStream.newLine();
            contentStream.showText(sci.getWebsite());
        }
        contentStream.endText();
        return contentStream;
    }


    
    //                              ***
    // CREATES the FACTURE INFO BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream factureInfoBlock(PDDocument document, PDPage page, PDPageContentStream contentStream, FactureClass facture, ClientClass client) throws IOException
    {
        contentStream.beginText();
        contentStream.setLeading(14.5f);        
        contentStream.setFont(PDType1Font.TIMES_BOLD, 18);
        contentStream.newLineAtOffset(42, 665);
        contentStream.showText("FACTURE");
        contentStream.endText();

        contentStream.beginText();        
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(42, 650);
        contentStream.showText("Facture n°");
        contentStream.newLine();
        contentStream.showText("Date");
        contentStream.newLine();
        contentStream.showText("Paiement dû");
        contentStream.endText();

        contentStream.beginText();        
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(115, 650);
        contentStream.showText(":");
        contentStream.newLine();
        contentStream.showText(":");
        contentStream.newLine();
        contentStream.showText(":");
        contentStream.endText();
        
            // gets the DEFINITIVE DATE and FORMATS IT for printing
        String dateToPrint;
        if (facture.getDefinitiveDate() != null){
            String date = facture.getDefinitiveDate().toString();
            String delims = "[\\-]+";
            String[] dateTokens = date.split(delims);
            dateToPrint = dateTokens[2]+"/"+dateTokens[1]+"/"+dateTokens[0];
        } else {
            dateToPrint = "- - -";
        }       

        contentStream.beginText();        
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(125, 650);
        contentStream.showText(facture.getFactureCode());
        contentStream.newLine();
        contentStream.showText(dateToPrint);
        if (client.getPaymentDelay() != null && !client.getPaymentDelay().isEmpty()){
            contentStream.newLine();
            contentStream.showText(client.getPaymentDelay());
        }        
        contentStream.endText();

            // for a TEMPORARY FACTURE, adds A TEMPORARY MENTION
        if (!facture.getDefinitive().equals(true)){
            contentStream.beginText();   
            contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 20);

            Matrix matrix = Matrix.getRotateInstance(Math.toRadians(45), 225, 650);
            
            contentStream.setTextMatrix(matrix);
            contentStream.newLineAtOffset(0, 0);
            contentStream.showText("PROVISOIRE");
            contentStream.endText();
        }

        return contentStream;
    }



    //                              ***
    // CREATES the CLIENT ADRESS BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream clientAdressBlock(PDDocument document, PDPage page, PDPageContentStream contentStream, ClientClass client) throws IOException
    {
        contentStream.beginText();
        contentStream.setLeading(14.5f);        
        contentStream.setFont(PDType1Font.TIMES_BOLD, 12);
        contentStream.newLineAtOffset(350, 665);
        contentStream.showText("ADRESSE DE FACTURATION");
        contentStream.endText();

        contentStream.beginText();        
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(350, 650);
        if (client.getMoral().equals(true)){
            contentStream.showText(client.getCompanyType() + " " + client.getCompanyName());
        } else {
            contentStream.showText(client.getLastName() + " " + client.getFirstName());
        }
        contentStream.newLine();
        contentStream.showText(client.getStreetAndNumberFac());
        if (client.getStreetComplementFac() != null && !client.getStreetComplementFac().isEmpty()){
            contentStream.newLine();
            contentStream.showText(client.getStreetComplementFac());
        }
        contentStream.newLine();
        contentStream.showText(client.getZipCodeFac() + " " + client.getCityFac());        
        contentStream.endText();

        return contentStream;
    }



    //                              ***
    // CREATES the FACTURE LINES BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream prestationsBlock(PDDocument document, PDPage page, PDPageContentStream contentStream, ArrayList<TarifForFactureClass> tarifList, FactureClass facture) throws IOException
    {
            // HEADER DECORATION
        contentStream.setNonStrokingColor(100, 35, 0, 100);
        contentStream.addRect(42, 570, 511, 30);
        contentStream.fill();
        contentStream.setNonStrokingColor(0, 0, 0, 255);
        contentStream.moveTo(42, 600);
        contentStream.lineTo(553, 600);
        contentStream.stroke();
        contentStream.moveTo(42, 570);
        contentStream.lineTo(553, 570);
        contentStream.stroke();

            // WHITE
        contentStream.setNonStrokingColor(0, 0, 0, 0);

            // HEADER CONTENT
        contentStream.beginText();
        contentStream.setLeading(14.5f);        
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);    
        contentStream.newLineAtOffset(50, 582);
        contentStream.showText("DESCRIPTION");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
        contentStream.newLineAtOffset(260, 590);
        contentStream.showText("PRIX UNI-");
        contentStream.newLine();
        contentStream.showText("TAIRE HT");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
        contentStream.newLineAtOffset(320, 582);
        contentStream.showText("QUANTITE");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
        contentStream.newLineAtOffset(380, 590);
        contentStream.showText("MONTANT");
        contentStream.newLine();
        contentStream.showText("HT");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
        contentStream.newLineAtOffset(440, 590);
        contentStream.showText("MONTANT");
        contentStream.newLine();
        contentStream.showText("TVA");
        contentStream.endText();


        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
        contentStream.newLineAtOffset(500, 590);
        contentStream.showText("MONTANT");
        contentStream.newLine();
        contentStream.showText("TTC");
        contentStream.endText();

            // BLACK
        contentStream.setNonStrokingColor(0, 0, 0, 255);

            // SETS the Y OFFSET
        yOffset = 550;

            // PARSES the ARRAYLIST containing the FACTURE LINES
        for (TarifForFactureClass tarif : tarifList){

                // CALCULATES the DATA TO DISPLAY
            Float ht = tarif.getUnitPrice() * tarif.getQuantity();
            Float tva = ht * tarif.getTvaPercentage()/100;
            Float ttc = ht + tva;

                // PRINTS the DATA at the CORRECT OFFSET
            contentStream.beginText();
            contentStream.setLeading(14.5f);        
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);    
            contentStream.newLineAtOffset(50, yOffset);
            contentStream.showText(tarif.toString());
            contentStream.endText();
    
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
            contentStream.newLineAtOffset(260, yOffset);
            contentStream.showText(String.format("%.2f", tarif.getUnitPrice()));
            contentStream.endText();
    
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
            contentStream.newLineAtOffset(320, yOffset);
            contentStream.showText(String.valueOf(tarif.getQuantity()));
            contentStream.endText();
    
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
            contentStream.newLineAtOffset(380, yOffset);
            contentStream.showText(String. format("%.2f", ht));
            contentStream.endText();
    
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
            contentStream.newLineAtOffset(440, yOffset);
            contentStream.showText(String. format("%.2f", tva));
            contentStream.endText();
    
    
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 9);  
            contentStream.newLineAtOffset(500, yOffset);
            contentStream.showText(String. format("%.2f", ttc));
            contentStream.endText();

                // NEW OFFSET
            yOffset = yOffset - 30;
        }

            // OFFSET ALIGNEMENT after the array
        yOffset = yOffset + 10;

            // SEPARATING STROKE
        contentStream.moveTo(42, yOffset);
        contentStream.lineTo(553, yOffset);
        contentStream.stroke();

            // OFFSET AFTER THE STROKE
        yOffset = yOffset - 20;        

            // FACTURE COMMENT BLOCK
        if (facture.getComment() != null && !facture.getComment().isEmpty()){
            String comment = facture.getComment();
            LineWidthHandler lineWidthHandler = new PdfMethods.LineWidthHandler(225, comment, PDType1Font.TIMES_ROMAN, 10);
            ArrayList<String> commentAsList = lineWidthHandler.getLines();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);  
            contentStream.newLineAtOffset(50, yOffset);

            for (String commentLine : commentAsList) {
                contentStream.showText(commentLine);
                contentStream.newLine();
            }

            contentStream.endText();
        }

            // FINAL SUMS BLOCK
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);  
        contentStream.newLineAtOffset(300, yOffset);
        contentStream.showText("TOTAL HT");
        contentStream.newLine();
        contentStream.showText("TOTAL TVA");
        contentStream.newLine();
        contentStream.showText("TOTAL TTC");
        contentStream.newLine();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
        contentStream.showText("SOLDE A PAYER");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);  
        contentStream.newLineAtOffset(420, yOffset);
        contentStream.showText(":");
        contentStream.newLine();
        contentStream.showText(":");
        contentStream.newLine();
        contentStream.showText(":");
        contentStream.newLine();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
        contentStream.showText(":");
        contentStream.endText();

            // Determines the width of each total line
        String lineHT = String. format("%.2f", facture.getTotalHT());
        String lineTVA = String. format("%.2f", facture.getTotalTVA());
        String lineTTC = String. format("%.2f", facture.getTotalTTC());
        Float lineHTWidth = PDType1Font.TIMES_ROMAN.getStringWidth(lineHT)*12/1000;
        Float lineTVAWidth = PDType1Font.TIMES_ROMAN.getStringWidth(lineTVA)*12/1000;
        Float lineTTCWidth = PDType1Font.TIMES_ROMAN.getStringWidth(lineTTC)*12/1000;
        Float lineToPayWidth = PDType1Font.TIMES_BOLD.getStringWidth(lineTTC)*14/1000;

            // adds a new line for each total, using their width to obtain a right alignement
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(500-lineHTWidth, yOffset);
        contentStream.showText(lineHT); 
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(500-lineTVAWidth, yOffset-15);
        contentStream.showText(lineTVA); 
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(500-lineTTCWidth, yOffset-30);
        contentStream.showText(lineTTC); 
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
        contentStream.newLineAtOffset(500-lineToPayWidth, yOffset-45);
        contentStream.showText(lineTTC); 
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
        contentStream.newLineAtOffset(508, yOffset-45);
        contentStream.showText("EUR"); 
        contentStream.endText();

        return contentStream;
    }



    //                              ***
    // CREATES the LEGAL MENTIONS BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream legalThingsBlock (PDDocument document, PDPage page, PDPageContentStream contentStream, SCIClass sci) throws IOException
    {
        yOffset = yOffset - 100;
            // LINES TO DISPLAY
        String[] lines = {sci.getPenalties(), sci.getEscompte(), sci.getRecovery()};
            // PARSES THE LINES
        for (String line : lines){
            if (line != null && !line.isEmpty()){        
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
                    // determines LINE WIDTH to CENTER
                Float lineWidth = PDType1Font.TIMES_ROMAN.getStringWidth(line)/100;
                contentStream.newLineAtOffset(298 - lineWidth/2, yOffset);
                contentStream.showText(line);
                contentStream.endText();
                    // NEW OFFSET
                yOffset = yOffset - 15;
            }            
        }
        return contentStream;
    }



    //                              ***
    // CREATES the PAYMENT INFORMATION BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream paymentInfoBlock(PDDocument document, PDPage page, PDPageContentStream contentStream, SCIClass sci) throws IOException
    {
            // NEW OFFSET
        yOffset = yOffset -30;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);  
        contentStream.newLineAtOffset(42, yOffset);
            // adapts to the PAYMENT METHOD selected for the SCI
        switch (sci.getPaymentMethod()) {
            case 2:
                contentStream.showText("Réglement par chèque à l'adresse ci-dessus");
                contentStream.newLine();
                contentStream.showText("ou par virement bancaire aux coordonnées ci-dessous :");
                contentStream.newLine();
                contentStream.showText("IBAN : " + sci.getIban());
                contentStream.newLine();
                contentStream.showText("Code BIC : " + sci.getBic());
                contentStream.newLine();
                contentStream.showText("Propriétaire du compte : " + sci.getAccountOwner());
                break;
            case 1:
                contentStream.showText("Réglement par virement bancaire aux coordonnées ci-dessous :");   
                contentStream.newLine();
                contentStream.showText("IBAN : " + sci.getIban());
                contentStream.newLine();
                contentStream.showText("Code BIC : " + sci.getBic());
                contentStream.newLine();
                contentStream.showText("Propriétaire du compte : " + sci.getAccountOwner());
                break; 
            case 0:
                contentStream.showText("Réglement par chèque à l'adresse ci-dessus.");
                break;
            default:
                contentStream.showText("Réglement par chèque à l'adresse ci-dessus.");
                break;
        }
        contentStream.endText();
        return contentStream;
    }



    //                              ***
    // CREATES the FOOTER BLOCK and PASSES it to the CONTENT STREAM
    //                              ***

    private static PDPageContentStream footerBlock (PDDocument document, PDPage page, PDPageContentStream contentStream, SCIClass sci, FactureClass facture) throws IOException
    {
        contentStream.moveTo(42, 85);
        contentStream.lineTo(553, 85);
        contentStream.stroke();

            // FIRST LINE : {SOCIAL FUNDS} , RCS
        String line1 = new String();
        if (sci.getSocialFunds() != null && !sci.getSocialFunds().isEmpty()){
            line1 = "SCI au capital social de " + sci.getSocialFunds() + " € - ";
        }
        line1 = line1 + "RCS " + sci.getRCS();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10); 
            // CENTER
        Float line1Width = PDType1Font.TIMES_ROMAN.getStringWidth(line1)/100; 
        contentStream.newLineAtOffset(298 - line1Width/2, 70);
        contentStream.showText(line1);
        contentStream.endText();

            // SECOND LINE : SIRET, FACTURE_CODE
        String line2 = "Numéro Siret : " + sci.getSiret() + " - Facture numéro " + facture.getFactureCode();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);  
            // CENTER
        Float line2Width = PDType1Font.TIMES_ROMAN.getStringWidth(line2)/100;
        contentStream.newLineAtOffset(298 - line2Width/2, 55);
        contentStream.showText(line2);
        contentStream.endText();

        return contentStream;
    }

}
