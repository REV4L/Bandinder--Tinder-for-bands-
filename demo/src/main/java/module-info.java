module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.sql;

    opens com.example to javafx.fxml;

    // requires static io.github.cdimascio.dotenv;
    // requires static java.dotenv;
    requires io.github.cdimascio.dotenv.java;
    // requires static dotenv-java;

    // requires static io.github.cdimascio.dotenv;

    exports com.example;
}