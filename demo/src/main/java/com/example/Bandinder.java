package com.example;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Bandinder extends Application {
    private BorderPane root;
    private StackPane stack;
    private Scene scene;
    private HBox navbar;

    private Pane swipePage, matchPage, profilePage;
    private Pane currentPage;

    @Override
    public void start(Stage primaryStage) {
        try {
            Database.connect();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
        p.getChildren().add(new Card(root).getCard());
        // p.getChildren().add(new Card(scene.getWidth() * 0.9).getCard());
        // p.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null,
        // null)));
        p.getStyleClass().add("bg");
        p.getStyleClass().add("page");
        return p;
    }

    private Pane buildMatchPage() {
        VBox content = new VBox(10); // less spacing between title and scroll
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(Insets.EMPTY); // ⛔ remove all outside padding
        content.getStyleClass().add("page");

        Label title = new Label("🔥 Matches");
        title.getStyleClass().add("cname");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(Insets.EMPTY); // ⛔ no scroll padding
        scrollPane.getStyleClass().addAll("scroll-pane", "transparent");

        VBox matchList = new VBox(15);
        matchList.setFillWidth(true);
        matchList.setPadding(Insets.EMPTY); // ⛔ no VBox padding
        matchList.setStyle("-fx-background-color: transparent;");
        matchList.setAlignment(Pos.TOP_CENTER); // align items neatly

        for (int i = 0; i < 50; i++) {
            matchList.getChildren().add(buildMatchItem(
                    "Band #" + (i + 1),
                    "Rock • Guitar",
                    "Contact: band" + (i + 1) + "@music.com"));
        }

        scrollPane.setContent(matchList);
        content.getChildren().addAll(title, scrollPane);

        return content;
    }

    private HBox buildMatchItem(String title, String subtitle, String contact) {
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(title);
        name.getStyleClass().add("userlabel");

        Label instrument = new Label(subtitle);
        instrument.getStyleClass().add("label");

        Label email = new Label(contact);
        email.getStyleClass().add("label");

        infoBox.getChildren().addAll(name, instrument, email);

        HBox item = new HBox(10);
        item.setPadding(new Insets(10));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("group");

        item.getChildren().addAll(infoBox);

        return item;
    }

    private Pane buildProfilePage() {
        StackPane p = new StackPane();
        // p.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null,
        // null)));
        p.getStyleClass().add("bg");
        p.getStyleClass().add("page");
        return p;
    }

    private HBox buildNavbar() {
        HBox n = new HBox();

        n.setMinHeight(90);
        n.setMinWidth(0);
        n.setSpacing(0);
        n.setPadding(new Insets(0, 0, 0, 0));
        n.getStyleClass().add("navbar");

        Button btnSwipe = buildNavbarButton("/swipe.png");
        Button btnMatch = buildNavbarButton("/match.png");
        Button btnProfile = buildNavbarButton("/profile.png");

        btnSwipe.setOnAction(e -> switchPage(swipePage));
        btnMatch.setOnAction(e -> switchPage(matchPage));
        btnProfile.setOnAction(e -> switchPage(profilePage));

        // Create spacers
        Region s1 = new Region();
        Region s2 = new Region();
        Region s3 = new Region();
        Region s4 = new Region();

        // Let spacers grow
        HBox.setHgrow(s1, Priority.ALWAYS);
        HBox.setHgrow(s2, Priority.ALWAYS);
        HBox.setHgrow(s3, Priority.ALWAYS);
        HBox.setHgrow(s4, Priority.ALWAYS);

        n.getChildren().addAll(s1, btnSwipe, s2, btnMatch, s3, btnProfile, s4);
        return n;
    }

    private Button buildNavbarButton(String iconPath) {
        Button button = new Button();
        ImageView icon = new ImageView(getClass().getResource(iconPath).toExternalForm());

        icon.setFitWidth(70);
        icon.setFitHeight(70);

        button.setGraphic(icon);

        HBox.setHgrow(button, Priority.NEVER);
        // button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        button.setMinWidth(0);
        button.getStyleClass().add("navbtn");
        button.getStyleClass().remove("button");

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
