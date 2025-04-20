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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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

import javafx.beans.binding.Bindings;

public class Bandinder extends Application {
    private StackPane root;
    private StackPane stack;
    private Scene scene;

    private HBox navbar;

    private StackPane authStack;
    private Pane loginPane, registerPane;

    private Pane swipePage, matchPage, profilePage;
    private Pane currentPage;

    private Stage stage;
    private Thread tickThread;

    @Override
    public void start(Stage primaryStage) {
        try {
            Database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        stage = primaryStage;

        buildApp();
    }

    private void rebuildApp() {
        buildApp();
    }

    private void buildApp() {
        root = new StackPane();
        scene = new Scene(root, 400, 750);
        root.getStyleClass().add("root");

        // scene.setOnKeyPressed(e -> {
        // if (e.getCode() == KeyCode.R) {
        // System.out.println("The 'A' key was pressed");
        // }
        // });
        scene.getAccelerators().put(new KeyCodeCombination(
                KeyCode.R, KeyCombination.CONTROL_ANY), new Runnable() {
                    @Override
                    public void run() {
                        // Database.logOut();
                        // rebuildApp();
                        reloadCss();
                    }
                });
        // scene.getAccelerators().put(new KeyCodeCombination( // spammed temp css into
        // appdata/local/temp
        // KeyCode.R, KeyCombination.CONTROL_ANY, KeyCombination.SHIFT_ANY), new
        // Runnable() {
        // @Override
        // public void run() {
        // // Database.logOut();
        // // rebuildApp();
        // new Thread(() -> {
        // try {
        // while (true) {
        // Thread.sleep(1000);
        // reloadCss();
        // }
        // } catch (Exception e) {
        // }
        // }).start();
        // ;
        // scene.getStylesheets().clear();
        // loadCss();
        // }
        // });
        scene.getAccelerators().put(new KeyCodeCombination(
                KeyCode.T, KeyCombination.CONTROL_ANY), new Runnable() {
                    @Override
                    public void run() {
                        Database.logOut();
                        rebuildApp();
                        // scene.getStylesheets().clear();
                        // loadCss();
                    }
                });

        stack = new StackPane();
        authStack = buildAuthStack();
        swipePage = buildSwipePage();
        matchPage = buildMatchPage();
        profilePage = buildProfilePage();

        stack.getChildren().addAll(authStack, swipePage, matchPage, profilePage);
        authStack.setVisible(true);
        swipePage.setVisible(false);
        matchPage.setVisible(false);
        profilePage.setVisible(false);
        currentPage = swipePage;

        navbar = buildNavbar();
        navbar.setVisible(false); // initially hidden

        navbar.setMaxHeight(60);
        StackPane.setAlignment(navbar, Pos.BOTTOM_CENTER);

        StackPane.setAlignment(navbar, Pos.BOTTOM_CENTER);

        root.getChildren().addAll(stack, navbar);

        loadCss();

        stage.setTitle("Bandinder");
        stage.setScene(scene);
        stage.show();

        if (tickThread != null)
            tickThread.interrupt();
        tickThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000); // wait 100ms

                    javafx.application.Platform.runLater(() -> {
                        tick();
                    });
                }
            } catch (Exception ex) {
            }
        });
        tickThread.start();
    }

    private void tick() {
        // matchPage.getChildren().setAll(buildMatchPage());
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
        login.setPadding(Insets.EMPTY);
        login.getStyleClass().add("page");

        Label title = new Label("🎸 Login");
        title.getStyleClass().add("cname");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button loginBtn = new Button("Log In");
        loginBtn.setOnAction(e -> {
            System.out.print("got" + emailField.getText() + passField.getText());
            Database.loginBand(emailField.getText(), passField.getText(), c -> {
                if (c) {
                    authStack.setVisible(false); // Hide auth UI
                    navbar.setVisible(true);

                    System.out.println("loggedInId:" + Database.bandId);
                    profilePage.getChildren().setAll(buildProfilePage());
                    swipePage.setVisible(true);
                    switchPage(swipePage); // Go to app
                } else {
                    emailField.setStyle("-fx-border-color: red;");
                    passField.setStyle("-fx-border-color: red;");
                }
            });
        });

        Button toRegister = new Button("No account? Register");
        toRegister.setOnAction(e -> slideTo(loginPane, registerPane, authStack, false));

        login.getChildren().addAll(title, emailField, passField, loginBtn, toRegister);

        // new Thread(() -> {
        // try {
        // Thread.sleep(1000); // wait 100ms

        // javafx.application.Platform.runLater(() -> {
        // emailField.setText("qwe");
        // passField.setText("qwe");
        // emailField.setText("qqq");
        // passField.setText("qqq");
        // loginBtn.fire();
        // });
        // } catch (Exception ex) {
        // }
        // }).start();

        return login;
    }

    private Pane buildRegisterPane() {
        VBox reg = new VBox(10);
        reg.setAlignment(Pos.CENTER);
        reg.setPadding(Insets.EMPTY);
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

    private void rebuildMatchPage() {
        matchPage.getChildren().setAll(buildMatchPage());
    }

    private Pane buildMatchPage() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(Insets.EMPTY);

        Label title = new Label("🔥 Matches");
        title.getStyleClass().add("cname");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(Insets.EMPTY);
        scrollPane.getStyleClass().addAll("scroll-pane", "transparent");

        VBox matchList = new VBox(15);
        matchList.setFillWidth(true);
        matchList.setPadding(Insets.EMPTY);
        matchList.setStyle("-fx-background-color: transparent;");
        matchList.setAlignment(Pos.TOP_CENTER);

        scrollPane.setContent(matchList);
        content.getChildren().addAll(title, scrollPane);

        // Run the match fetch in a background thread
        new Thread(() -> {
            List<Band> matches = Database.getConfirmedMatches(Database.bandId);

            javafx.application.Platform.runLater(() -> {
                matchList.getChildren().clear();
                int i = 1;
                for (Band b : matches) {
                    HBox matchItem = buildMatchItem(b);

                    matchItem.setTranslateX(-1000);

                    TranslateTransition transition = new TranslateTransition(Duration.millis(300), matchItem);
                    transition.setFromX(-1000);
                    transition.setToX(0);
                    transition.setDelay(Duration.millis(100 * i));

                    transition.play();

                    matchList.getChildren().add(matchItem);

                    i++;
                }
            });
        }).start();

        return content;
    }

    private HBox buildMatchItem(Band band) {
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(band.name);
        name.getStyleClass().add("userlabel");

        Label bio = new Label(band.bio);
        bio.getStyleClass().add("label");

        infoBox.getChildren().addAll(name, bio);

        HBox item = new HBox(10);
        item.setPadding(new Insets(10));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("group");

        item.getChildren().addAll(infoBox);
        item.setOnMouseClicked(e -> showContactPopup(band));

        return item;
    }

    private void showContactPopup(Band band) {
        // Semi-transparent background overlay
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox popupContent = new VBox(10,
                new Label("Email: " + band.email),
                new Label("Phone: " + band.phone),
                new Button("Close"));
        popupContent.setPadding(new Insets(20));
        popupContent.setAlignment(Pos.CENTER);
        popupContent.getStyleClass().add("page");

        Button closeBtn = (Button) popupContent.getChildren().get(2);
        closeBtn.setOnAction(e -> root.getChildren().remove(overlay));

        overlay.getChildren().add(popupContent);
        StackPane.setAlignment(popupContent, Pos.CENTER);

        // Add overlay to the root stack
        root.getChildren().add(overlay);

        TranslateTransition transition = new TranslateTransition(Duration.millis(30000), popupContent);
        transition.setFromY(-1000);
        transition.setToY(0);

        transition.setOnFinished(e -> {

        });

        transition.play();
    }

    // private HBox buildMatchItem(String title, String subtitle, String contact) {
    // VBox infoBox = new VBox(5);
    // infoBox.setAlignment(Pos.CENTER_LEFT);

    // Label name = new Label(title);
    // name.getStyleClass().add("userlabel");

    // Label instrument = new Label(subtitle);
    // instrument.getStyleClass().add("label");

    // Label email = new Label(contact);
    // email.getStyleClass().add("label");

    // infoBox.getChildren().addAll(name, instrument, email);

    // HBox item = new HBox(10);
    // item.setPadding(new Insets(10));
    // item.setAlignment(Pos.CENTER_LEFT);
    // item.getStyleClass().add("group");

    // item.getChildren().addAll(infoBox);

    // return item;
    // }

    private Pane buildProfilePage() {
        StackPane root = new StackPane();
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("Edit Profile");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        FlowPane flow = new FlowPane(20, 20);
        flow.setAlignment(Pos.CENTER);
        flow.getStyleClass().add("flow-pane");

        List<byte[]> existingImages = Database.loadImages(Database.bandId);
        for (int i = 0; i < 6; i++) {
            int imageIndex = i;
            ImageView imageView = new ImageView(
                    existingImages.get(i) != null
                            ? new Image(new ByteArrayInputStream(existingImages.get(i)))
                            : new Image(getClass().getResourceAsStream("/blank.png")));

            imageView.setFitWidth(100);
            imageView.setFitHeight(140);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Rectangle clip = new Rectangle(100, 140);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);

            StackPane imageFrame = new StackPane(imageView);
            Button removeBtn = new Button("✕");
            removeBtn.getStyleClass().add("button");
            StackPane.setAlignment(removeBtn, Pos.TOP_RIGHT);

            removeBtn.setOnAction(e -> {
                Database.deleteImage(Database.bandId, imageIndex);
                imageView.setImage(new Image(getClass().getResourceAsStream("/blank.png")));
                e.consume();
            });

            imageFrame.getChildren().add(removeBtn);
            imageFrame.setOnMouseClicked(e -> {
                if (e.getTarget() instanceof Button)
                    return;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Profile Image");
                File file = fileChooser.showOpenDialog(root.getScene().getWindow());
                if (file != null) {
                    try {
                        byte[] data = Files.readAllBytes(file.toPath());
                        Database.saveImage(data, Database.bandId, imageIndex);
                        imageView.setImage(new Image(new FileInputStream(file)));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            flow.getChildren().add(imageFrame);
        }

        Band band = Database.band;

        if (band != null) {
            TextField nameField = new TextField(band.name);
            nameField.setPromptText("Band Name");

            TextArea bioField = new TextArea(band.bio);
            bioField.setPromptText("Bio");
            bioField.setWrapText(true);
            bioField.setPrefRowCount(4);

            TextField emailField = new TextField(band.email);
            emailField.setPromptText("Email");

            TextField phoneField = new TextField(band.phone);
            phoneField.setPromptText("Phone");

            ComboBox<Kraj> krajCombo = new ComboBox<>(Database.getKraji());
            krajCombo.setPromptText("Select Location");
            krajCombo.getSelectionModel().select(
                    Database.getKraji().stream().filter(k -> k.id == band.krajId).findFirst().orElse(null));

            FlowPane tagPane = new FlowPane(8, 8);
            tagPane.setAlignment(Pos.CENTER);
            ObservableList<String> currentTags = FXCollections
                    .observableArrayList(Database.getTagsForBand(Database.bandId));
            for (String tag : currentTags) {
                // Label tagLabel = new Label("#" + tag);
                // tagLabel = buildTagLabel();
                // tagLabel.setStyle(
                // "-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5 10 5 10;
                // -fx-background-radius: 15;");
                // tagLabel.setOnMouseClicked(e -> tagPane.getChildren().remove(tagLabel));
                // tagPane.getChildren().add(tagLabel);
                // tagPane.getChildren().add(buildTagLabel(tag, c -> {
                // c.setOnMouseClicked(e -> tagPane.getChildren().remove(c));
                // }));

                Label tagLabel = buildTagLabel(tag, null);

                tagLabel.setOnMouseClicked(ev -> tagPane.getChildren().remove(tagLabel));

                tagPane.getChildren().add(tagLabel);
            }

            TextField tagInput = new TextField();
            tagInput.setPromptText("Add tag...");
            tagInput.setOnAction(e -> {
                String text = tagInput.getText().trim().toLowerCase();
                if (!text.isEmpty()) {
                    // Label tagLabel = new Label("#" + text);
                    Label tagLabel = buildTagLabel(text, null);

                    tagLabel.setOnMouseClicked(ev -> tagPane.getChildren().remove(tagLabel));

                    tagPane.getChildren().add(tagLabel);
                    // tagLabel.setStyle(
                    // "-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5 10 5 10;
                    // -fx-background-radius: 15;");
                    // tagLabel.setOnMouseClicked(ev -> tagPane.getChildren().remove(tagLabel));

                    // tagPane.getChildren().add(tagLabel);
                    tagInput.clear();
                }
            });

            Button saveBtn = new Button("Save Changes");
            saveBtn.setOnAction(e -> {
                Kraj selectedKraj = krajCombo.getValue();
                int krajId = selectedKraj != null ? selectedKraj.id : 0;
                if (true || selectedKraj != null) {
                    List<String> tags = new ArrayList<>();
                    for (Node n : tagPane.getChildren()) {
                        if (n instanceof Label) {
                            String text = ((Label) n).getText();
                            if (text.startsWith("#"))
                                tags.add(text.substring(1));
                        }
                    }
                    Database.updateBandProfileAndTags(
                            Database.bandId,
                            nameField.getText(),
                            bioField.getText(),
                            emailField.getText(),
                            phoneField.getText(),
                            krajId, // selectedKraj.id,
                            tags.toArray(new String[0]));
                }
            });

            Button logoutBtn = new Button("Log Out");
            logoutBtn.setOnAction(e -> {
                Database.logOut();
                rebuildApp();
            });
            // Rectangle r = new Rectangle(0, 100);

            VBox form = new VBox(10, nameField, bioField, emailField, phoneField, krajCombo, tagInput, tagPane,
                    new Rectangle(0, 100), saveBtn, new Rectangle(0, 20), logoutBtn, new Rectangle(0, 100));
            form.setAlignment(Pos.CENTER);

            content.getChildren().addAll(heading, flow, form);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        root.getChildren().add(scrollPane);
        return root;
    }

    private Label buildTagLabel(String tag, Consumer<Label> c) {
        Label lbl = new Label("#" + tag);
        lbl.setStyle("-fx-padding: 5 10; -fx-background-radius: 12;");
        lbl.getStyleClass().add("tagLabel");
        // lbl.setOnMouseClicked(e -> container.getChildren().remove(lbl));
        return lbl;
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
        btnMatch.setOnAction(e -> {
            if (currentPage != matchPage) {
                switchPage(matchPage);
                rebuildMatchPage();
            }
        });
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

    private void reloadCss() {
        scene.getStylesheets().clear();
        loadCss();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
