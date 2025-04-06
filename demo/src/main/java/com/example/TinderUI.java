package com.example;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TinderUI extends Application {
    private BorderPane root;
    private StackPane stack;
    private Scene scene;
    private HBox navbar;

    private Pane swipePage, matchPage, profilePage;
    private Pane currentPage;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        stack = new StackPane();
        navbar = buildNavbar();

        scene = new Scene(root, 400, 750);
        root.setCenter(stack);
        root.setBottom(navbar);
        root.getStyleClass().add("root");

        swipePage = buildSwipePage();
        matchPage = buildMatchPage();
        profilePage = buildProfilePage();

        stack.getChildren().addAll(swipePage, matchPage, profilePage);

        swipePage.setVisible(true);
        matchPage.setVisible(false);
        profilePage.setVisible(false);
        currentPage = swipePage;

        loadCss();

        primaryStage.setTitle("Bandinder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane buildSwipePage() {
        StackPane p = new StackPane();
        p.getChildren().add(new Card(scene.getWidth() * 0.9).getCard());
        // p.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null,
        // null)));
        p.getStyleClass().add("bg");
        return p;
    }

    private Pane buildMatchPage() {
        StackPane p = new StackPane();
        // p.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null,
        // null)));
        p.getStyleClass().add("bg");
        return p;
    }

    private Pane buildProfilePage() {
        StackPane p = new StackPane();
        // p.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null,
        // null)));
        p.getStyleClass().add("bg");
        return p;
    }

    private HBox buildNavbar() {
        HBox n = new HBox();
        n.setSpacing(20);
        n.setPadding(new Insets(20));
        n.getStyleClass().add("navbar");

        Button btnA = buildNavbarButton("Swipe");
        Button btnB = buildNavbarButton("Match");
        Button btnC = buildNavbarButton("Profile");

        btnA.setOnAction(e -> switchPage(swipePage));
        btnB.setOnAction(e -> switchPage(matchPage));
        btnC.setOnAction(e -> switchPage(profilePage));

        n.getChildren().addAll(btnA, btnB, btnC);
        return n;
    }

    private Button buildNavbarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(40);
        return button;
    }

    private void switchPage(Pane newPage) {
        if (newPage == currentPage)
            return;

        boolean left = stack.getChildren().indexOf(newPage) > stack.getChildren().indexOf(currentPage);
        slideTo(currentPage, newPage, stack, left);
        currentPage = newPage;
    }

    private void slideTo(Pane from, Pane to, StackPane container, boolean left) {
        double width = container.getWidth();

        to.setTranslateX(left ? width : -width);
        to.setVisible(true);

        TranslateTransition ttFrom = new TranslateTransition(Duration.millis(100), from);
        ttFrom.setToX(left ? -width : width);

        TranslateTransition ttTo = new TranslateTransition(Duration.millis(100), to);
        ttTo.setToX(0);

        ttFrom.setOnFinished(e -> {
            from.setVisible(false);
            from.setTranslateX(0);
        });

        ttFrom.play();
        ttTo.play();
    }

    private void loadCss() {
        try {
            URL cssUrl = getClass().getResource("/styles.css");
            if (cssUrl != null) {
                String css = new String(Files.readAllBytes(Paths.get(cssUrl.toURI())), StandardCharsets.UTF_8);
                scene.getStylesheets().clear();
                Path tempCssFile = Files.createTempFile("temp", ".css");
                Files.write(tempCssFile, css.getBytes(StandardCharsets.UTF_8));
                scene.getStylesheets().add(tempCssFile.toUri().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
