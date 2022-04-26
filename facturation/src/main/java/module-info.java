module facturationlds {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires org.apache.derby.commons;
    requires org.apache.derby.engine;
    requires org.apache.pdfbox;
    requires com.opencsv;

    opens facturationlds to javafx.fxml;
    exports facturationlds;
}
