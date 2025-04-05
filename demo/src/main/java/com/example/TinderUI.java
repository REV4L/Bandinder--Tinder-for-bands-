package com.example;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TinderUI extends Application {
    private double startX;
    BorderPane root;
    StackPane stack;
    Scene scene;
    HBox navbar;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        stack = new StackPane();
        navbar = buildNavbar();

        scene = new Scene(root, 400, 600);

        root.setCenter(stack);
        root.setBottom(navbar);
        root.getStyleClass().add("root");
        stack.getChildren().add(new Card().getCard());

        loadCss();

        primaryStage.setTitle("Bandinder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadCss() {
        URL cssUrl = getClass().getResource("/styles.css"); // Leading '/' is crucial

        try {
            if (cssUrl != null) {
                String css = new String(Files.readAllBytes(Paths.get(cssUrl.toURI())), StandardCharsets.UTF_8);

                // css = css.replace("SYSTEM_FONT", "'" + Database.font + "'"); // Assuming the
                // css = css.replace("SYSTEM_COLOR", Database.color); // Assuming the color is

                scene.getStylesheets().clear();

                Path tempCssFile = Files.createTempFile("temp", ".css");
                Files.write(tempCssFile, css.getBytes(StandardCharsets.UTF_8));

                scene.getStylesheets().add(tempCssFile.toUri().toString());
            }
        } catch (Exception e) {

        }
    }

    public HBox buildNavbar() {
        HBox n = new HBox();

        return n;
    }

    public Button buildNavbarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");

        button.setPrefWidth(200);
        button.setMaxHeight(40);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}