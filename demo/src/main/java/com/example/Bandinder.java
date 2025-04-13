package com.example;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Bandinder extends Application {
    private BorderPane root;
    private StackPane stack;
    private Scene scene;

    private HBox navbar;

    private StackPane authStack;
    private Pane loginPane, registerPane;

    private Pane swipePage, matchPage, profilePage;
    private Pane currentPage;

    @Override
    public void start(Stage primaryStage) {
        try {
            Database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        root = new BorderPane();
        stack = new StackPane();
        navbar = buildNavbar();

        scene = new Scene(root, 400, 750);
        root.setCenter(stack);
        root.setBottom(navbar);
        root.getStyleClass().add("root");

        authStack = buildAuthStack();
        swipePage = buildSwipePage();
        matchPage = buildMatchPage();
        profilePage = buildProfilePage();

        stack.getChildren().addAll(authStack, swipePage, matchPage, profilePage);

        authStack.setVisible(true);
        navbar.setVisible(false);

        swipePage.setVisible(false);
        matchPage.setVisible(false);
        profilePage.setVisible(false);
        currentPage = swipePage;

        loadCss();

        primaryStage.setTitle("Bandinder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private StackPane buildAuthStack() {
        loginPane = buildLoginPane();
        registerPane = buildRegisterPane();
        authStack = new StackPane(loginPane, registerPane);
        registerPane.setVisible(false);
        return authStack;
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

    private Pane buildLoginPane() {
        VBox login = new VBox(10);
        login.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(30));
        login.getStyleClass().add("page");

        Label title = new Label("🎸 Login");
        title.getStyleClass().add("cname");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button loginBtn = new Button("Log In");
        loginBtn.setOnAction(e -> {
            Database.loginBand(emailField.getText(), passField.getText(), c -> {
                if (c) {
                    authStack.setVisible(false); // Hide auth UI
                    navbar.setVisible(true);
                    switchPage(swipePage); // Go to app

                    profilePage.getChildren().setAll(buildProfilePage());
                } else {
                    emailField.setStyle("-fx-border-color: red;");
                    passField.setStyle("-fx-border-color: red;");
                }
            });
        });

        Button toRegister = new Button("No account? Register");
        toRegister.setOnAction(e -> slideTo(loginPane, registerPane, authStack, false));

        login.getChildren().addAll(title, emailField, passField, loginBtn, toRegister);
        return login;
    }

    private Pane buildRegisterPane() {
        VBox reg = new VBox(10);
        reg.setAlignment(Pos.CENTER);
        reg.setPadding(new Insets(30));
        reg.getStyleClass().add("page");

        Label title = new Label("🎶 Register");
        title.getStyleClass().add("cname");

        TextField nameField = new TextField();
        nameField.setPromptText("Band Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button regBtn = new Button("Create Account");
        regBtn.setOnAction(e -> {
            boolean ok = Database.registerBand(nameField.getText(), emailField.getText(), passField.getText());
            if (ok)
                slideTo(registerPane, loginPane, authStack, true);
            else
                emailField.setStyle("-fx-border-color: red;");
        });

        Button backToLogin = new Button("← Back to login");
        backToLogin.setOnAction(e -> slideTo(registerPane, loginPane, authStack, true));

        reg.getChildren().addAll(title, nameField, emailField, passField, regBtn, backToLogin);
        return reg;
    }

    private Pane buildMatchPage() {
        VBox content = new VBox(10); // less spacing between title and scroll
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(Insets.EMPTY); // remove all outside padding
        content.getStyleClass().add("page");

        Label title = new Label("🔥 Matches");
        title.getStyleClass().add("cname");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(Insets.EMPTY); // no scroll padding
        scrollPane.getStyleClass().addAll("scroll-pane", "transparent");

        VBox matchList = new VBox(15);
        matchList.setFillWidth(true);
        matchList.setPadding(Insets.EMPTY); // no VBox padding
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
        StackPane root = new StackPane();
        root.getStyleClass().addAll("bg", "page");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("Edit Profile");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        FlowPane flow = new FlowPane();
        flow.setHgap(20);
        flow.setVgap(20);
        flow.setAlignment(Pos.CENTER);
        flow.getStyleClass().add("flow-pane");

        List<byte[]> existingImages = Database.loadImages(Database.bandId);

        for (int i = 0; i < 6; i++) {
            int imageIndex = i;

            ImageView imageView;
            if (existingImages.get(i) != null) {
                imageView = new ImageView(new Image(new ByteArrayInputStream(existingImages.get(i))));
            } else {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
            }

            imageView.setFitWidth(100);
            imageView.setFitHeight(140);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Rectangle clip = new Rectangle(100, 140);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);

            StackPane imageFrame = new StackPane();
            imageFrame.getStyleClass().add("image-frame");

            Button removeBtn = new Button("✕");
            removeBtn.getStyleClass().add("button");
            removeBtn.getStyleClass().remove("remove-button");
            removeBtn.setOnAction(e -> {
                Database.deleteImage(Database.bandId, imageIndex);
                imageView.setImage(new Image(getClass().getResourceAsStream("/logo.png")));
                e.consume();
            });

            StackPane.setAlignment(removeBtn, Pos.TOP_RIGHT);
            imageFrame.getChildren().addAll(imageView, removeBtn);

            imageFrame.setOnMouseClicked(e -> {
                if (e.getTarget() instanceof Button)
                    return;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Profile Image");
                File selectedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
                if (selectedFile != null) {
                    try {
                        byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
                        Database.saveImage(imageBytes, Database.bandId, imageIndex);
                        imageView.setImage(new Image(new FileInputStream(selectedFile)));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            flow.getChildren().add(imageFrame);
        }

        content.getChildren().addAll(heading, flow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        root.getChildren().add(scrollPane);
        return root;
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
