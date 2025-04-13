// package com.example;

// import javafx.scene.*;
// import javafx.application.Application;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.fxml.FXMLLoader;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.chart.PieChart.Data;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.Button;
// import javafx.scene.control.ColorPicker;
// import javafx.scene.control.ComboBox;
// import javafx.scene.control.DatePicker;
// import javafx.scene.control.Label;
// import javafx.scene.control.PasswordField;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Font;
// import javafx.stage.Stage;

// import java.io.IOException;
// import java.net.URL;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;
// import java.sql.Timestamp;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.util.List;

// // import io.github.cdimascio.dotenv.Dotenv;

// public class MainAppBackup extends Application {

// public static String appName = "VPIK (Miniprojekt)";
// public Stage primaryStage = null;

// @Override
// public void start(Stage ps) {
// try {
// Database.connect();
// } catch (SQLException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }

// primaryStage = ps;
// rebuild(ps);
// }

// void rebuild() {
// rebuild(primaryStage);
// }

// BorderPane root;

// void rebuild(Stage primaryStage) {
// // Set up the main container with BorderPane layout
// root = new BorderPane();
// // root.setStyle("-fx-background-color: #2E2E2E;"); // Dark background color
// for
// // the main area
// // root.setStyle("-fx-font-family: \"Comic Sans MS\";");

// // Create the sidebar

// // Create the main content area (initial content message)
// // Database.login("val", "val");

// StackPane stack = new StackPane();
// stack.setStyle("-fx-background-color: #3A3A3A;");
// root.setCenter(stack);
// Scene scene = new Scene(root, 600, 1000);

// if (Database.loggedIn()) {
// Database.getOrganizator();
// VBox sidebar = leftSidebar(root); // Pass root to sidebar creation
// root.setLeft(sidebar);

// try {
// stack.getChildren().add(new Label("Select page"));
// // Access the CSS file from the resources folder
// URL cssUrl = getClass().getResource("/styles.css"); // Leading '/' is crucial

// if (cssUrl != null) {
// // Read the CSS file into a String
// String css = new String(Files.readAllBytes(Paths.get(cssUrl.toURI())),
// StandardCharsets.UTF_8);

// // Replace the placeholders with actual values from the database
// css = css.replace("SYSTEM_FONT", "'" + Database.font + "'"); // Assuming the
// font is stored in the
// // DB
// css = css.replace("SYSTEM_COLOR", Database.color); // Assuming the color is
// stored in the DB

// // Clear any previous stylesheets
// scene.getStylesheets().clear();

// // Write the updated CSS into a temporary file
// Path tempCssFile = Files.createTempFile("temp", ".css");
// Files.write(tempCssFile, css.getBytes(StandardCharsets.UTF_8));

// // Add the temporary CSS file as a stylesheet
// scene.getStylesheets().add(tempCssFile.toUri().toString());
// } else {
// // Handle case when the CSS file is not found
// System.out.println("CSS file not found!");
// }

// } catch (Exception e) {
// e.printStackTrace(); // Log any exceptions for debugging
// }
// } else {
// // Initial content (message)
// stack.getChildren().add(pageLogin());
// scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
// }

// // Set the background color as needed
// scene.setFill(Color.BLACK); // Or any other color

// primaryStage.setTitle(appName);
// primaryStage.setScene(scene);
// primaryStage.setResizable(false);
// primaryStage.show();

// // changeContent(pageIzvajalci());
// // debug
// }

// private void mvW(double dx, double dy) {
// // Get the current position of the window
// double currentX = primaryStage.getX();
// double currentY = primaryStage.getY();

// // Move the window relatively by the offsets dx and dy
// primaryStage.setX(currentX + dx); // Move the window by dx pixels
// horizontally
// primaryStage.setY(currentY + dy); // Move the window by dy pixels vertically
// }

// // Label userLabel = null;

// // Function to create the sidebar and handle button actions
// private VBox leftSidebar(BorderPane root) {
// VBox sidebar = new VBox(10);
// sidebar.setAlignment(Pos.TOP_CENTER);
// sidebar.setStyle("-fx-background-color: #1E1E1E; -fx-padding: 20;"); // Dark
// sidebar color

// // Create buttons for the sidebar

// Label userLabel = new Label(
// Database.organizator != null ? Database.organizator.ime : "Log In");
// userLabel.getStyleClass().add("userlabel");
// // Label userLabel = new Label(
// // Database.organizator != null ? "Logged in as \n " +
// Database.organizator.ime
// // : "Log In");

// HBox p = new HBox();
// p.minHeight(200);
// p.getStyleClass().add("container");
// p.setAlignment(Pos.CENTER);
// p.getChildren().add(userLabel);

// Button b1 = newStdButton("Profil");
// Button b2 = newStdButton("Nastavitve");
// Button b3 = newStdButton("Dogodki");
// Button b4 = newStdButton("Prostori");
// Button b5 = newStdButton("Izvajalci");
// Button logOut = newStdButton("LOG OUT");
// logOut.setStyle("-fx-background-color: rgb(218, 70, 70)");

// Region s = new Region();
// VBox.setVgrow(s, Priority.ALWAYS);

// sidebar.setMaxWidth(300);

// // Add buttons to the sidebar
// sidebar.getChildren().addAll(p, b1, b2, b3, b4, b5, s, logOut);

// b1.setOnAction(event -> changeContent(pageOrganizator()));
// b2.setOnAction(event -> changeContent(pageSettings()));
// b3.setOnAction(event -> changeContent(pageDogodki(Database.organizatorId)));
// b4.setOnAction(event -> changeContent(pageProstori()));
// b5.setOnAction(event -> changeContent(pageIzvajalci()));
// // b2.setOnAction(event -> changeContent(root, "Settings Content"));
// // b3.setOnAction(event -> changeContent(root, "Profile Content"));
// logOut.setOnAction(event -> {
// Database.logOut();
// rebuild();
// });

// return sidebar;
// }

// // Function to create buttons with a consistent style
// private Button newStdButton(String text) {
// Button button = new Button(text);
// button.getStyleClass().add("button");
// // button.setStyle(
// // "-fx-background-color: #3A3A3A; -fx-text-fill: white; -fx-font-size: 20px;
// // -fx-border-radius: 5;");
// button.setPrefWidth(200);
// button.setMaxHeight(40);

// // button.setStyle("-fx-background-color: #3A3A3A; -fx-text-fill: white;");
// // button.setOnMouseEntered(e -> button.setStyle(
// // "-fx-background-color:rgb(77, 77, 77); -fx-text-fill: white;
// -fx-font-size:
// // 20px; -fx-border-radius: 5;"));
// // button.setOnMouseExited(e -> button.setStyle(
// // "-fx-background-color: #3A3A3A; -fx-text-fill: white; -fx-font-size: 20px;
// // -fx-border-radius: 5;"));
// return button;
// }

// private VBox pageLogin() {
// VBox page = new VBox(10); // Adds spacing between elements
// page.setAlignment(Pos.CENTER);
// page.getStyleClass().add("page");
// // page.setStyle("-fx-background-color:rgb(66, 66, 66); -fx-padding: 20px;");
// //
// // Dark background with padding

// Label emailLabel = new Label("User:");
// // emailLabel.setTextFill(Color.WHITE);
// emailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

// TextField emailField = new TextField();
// emailField.setPromptText("Enter your email");
// emailField.setMaxWidth(300);
// // emailField.setStyle("-fx-background-color:rgb(54, 54, 54);
// // -fx-text-fill:rgb(255, 255, 255);");

// Label passwordLabel = new Label("Password:");
// // passwordLabel.setTextFill(Color.WHITE);
// passwordLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

// PasswordField passwordField = new PasswordField();
// passwordField.setPromptText("Enter your password");
// passwordField.setMaxWidth(300);
// // passwordField.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill:
// // #2c3e50;");

// Button loginButton = newStdButton("Log In");
// loginButton.setStyle("-fx-background-color: #3498db;");

// // Add event handler to the button
// loginButton.setOnAction(event -> {
// // Get the values from the text fields
// String email = emailField.getText();
// String password = passwordField.getText();

// // Validate the inputs (optional)
// if (email.isEmpty() || password.isEmpty()) {
// // Show an alert or handle empty input case
// // System.out.println("Please fill out both fields.");
// return;
// }

// // Execute your query here with email and password
// // Example query logic (this is where you'd perform your logic)
// if (Database.login(email, password)) {
// rebuild();
// // userLabel.setText(Database.organizator.ime);
// }
// });

// page.getChildren().addAll(emailLabel, emailField, passwordLabel,
// passwordField, loginButton);

// return page;
// }

// private ComboBox<Kraj> krajCombobox(int preselectedId) {
// ComboBox<Kraj> krajDropdown = new ComboBox<>();
// krajDropdown.setPromptText("Izberi kraj...");
// krajDropdown.setMaxWidth(300);

// // Fetch kraji using getKraji function
// ObservableList<Kraj> krajiList = FXCollections
// .observableArrayList(Database.getKraji());

// // for (Kraj k : krajiList) {
// // System.out.println("Loaded: " + k);
// // }
// krajDropdown.setItems(krajiList);

// // Set the currently selected Kraj (if available)
// krajiList.stream()
// .filter(k -> k.id == preselectedId)
// .findFirst()
// .ifPresent(krajDropdown::setValue);

// // Handle selection change
// // krajDropdown.setOnAction(e -> {
// // Kraj selectedKraj = krajDropdown.getValue();
// // if (selectedKraj != null) {
// // Database.organizator.krajId = selectedKraj.id;
// // }
// // });

// return krajDropdown;

// }

// private ComboBox<Prostor> prostorComboBox(int preselectedId) {
// ComboBox<Prostor> prostorDropdown = new ComboBox<>();
// prostorDropdown.setPromptText("Izberi prostor...");
// prostorDropdown.setMaxWidth(300);

// // Fetch Prostori using the appropriate method (e.g., Database.getProstori())
// ObservableList<Prostor> prostoriList =
// FXCollections.observableArrayList(Database.getProstori());

// prostorDropdown.setItems(prostoriList);

// // Set the currently selected Prostor (if available)
// prostoriList.stream()
// .filter(p -> p.id == preselectedId)
// .findFirst()
// .ifPresent(prostorDropdown::setValue);

// // Handle selection change
// // prostorDropdown.setOnAction(e -> {
// // Prostor selectedProstor = prostorDropdown.getValue();
// // if (selectedProstor != null) {
// // Database.organizator.prostorId = selectedProstor.id;
// // }
// // });

// return prostorDropdown;
// }

// private ComboBox<Izvajalec> izvajalecComboBox(int preselectedId) {
// ComboBox<Izvajalec> izvajalecDropdown = new ComboBox<>();
// izvajalecDropdown.setPromptText("Izberi izvajalca...");
// izvajalecDropdown.setMaxWidth(300);

// // Fetch Izvajalci using the appropriate method (e.g.,
// Database.getIzvajalci())
// ObservableList<Izvajalec> izvajalciList =
// FXCollections.observableArrayList(Database.getIzvajalci());

// izvajalecDropdown.setItems(izvajalciList);

// // Set the currently selected Izvajalec (if available)
// izvajalciList.stream()
// .filter(i -> i.id == preselectedId)
// .findFirst()
// .ifPresent(izvajalecDropdown::setValue);

// // Handle selection change
// // izvajalecDropdown.setOnAction(e -> {
// // Izvajalec selectedIzvajalec = izvajalecDropdown.getValue();
// // if (selectedIzvajalec != null) {
// // Database.organizator.izvajalecId = selectedIzvajalec.id;
// // }
// // });

// return izvajalecDropdown;
// }

// private Node pageSettings() {
// ScrollPane scrollPane = new ScrollPane();

// VBox page = new VBox(10); // Adds spacing between elements
// page.setAlignment(Pos.CENTER);
// page.getStyleClass().add("page");

// // ---- Font Selection ----
// HBox hFont = new HBox(10);
// Label fontLabel = new Label("Font:");

// ComboBox<String> fontComboBox = new ComboBox<>();
// fontComboBox.getItems().addAll("Trebuchet MS", "Comic Sans MS", "Arial",
// "Courier New", "Verdana");

// // Load saved font from the database
// String savedFont = Database.font; // Fetch saved font from DB
// fontComboBox.setValue(savedFont != null ? savedFont : "Trebuchet MS"); //
// Default font if none is saved

// Region regionFont = new Region(); // Used for even spacing
// HBox.setHgrow(regionFont, Priority.ALWAYS);

// hFont.getChildren().addAll(fontLabel, regionFont, fontComboBox);
// // ----

// // ---- Color Picker using ComboBox ----
// HBox hColor = new HBox(10);
// Label colorLabel = new Label("Color:");

// // Create a ComboBox with predefined colors (name and hex value)
// ComboBox<String> colorComboBox = new ComboBox<>();

// // Expanded predefined colors (name and hex value)
// colorComboBox.getItems().addAll(
// "Red (#FF0000)", "Green (#00FF00)", "Blue (#0000FF)",
// "Black (#000000)", "White (#FFFFFF)", "Yellow (#FFFF00)",
// "Orange (#FFA500)", "Purple (#800080)",
// // Desaturated colors
// "Beige (#F5F5DC)", "Light Brown (#A52A2A)", "Pastel Blue (#AEC6CF)",
// "Pastel Green (#77DD77)", "Pastel Pink (#FFD1DC)", "Muted Lavender
// (#E6E6FA)",
// "Muted Coral (#F88379)", "Dusty Rose (#D1A7A1)", "Soft Yellow (#F9E79F)",
// // Darker tones
// "Charcoal (#36454F)", "Dark Slate Gray (#2F4F4F)", "Dark Olive Green
// (#556B2F)",
// "Dark Brown (#3E2723)", "Deep Navy (#003366)", "Dark Slate Blue (#483D8B)",
// // Dark grays
// "Gunmetal Gray (#2A3439)", "Ash Gray (#B2BEB5)", "Charcoal Gray (#36454F)");

// // Set the default value for the ComboBox (this could come from the database)
// String savedColor = Database.color; // Fetch saved color from DB
// if (savedColor != null) {
// // Find the color name that matches the saved color
// colorComboBox.setValue(getColorNameByHex(savedColor));
// } else {
// // Default to black if no color is saved
// colorComboBox.setValue("Black (#000000)");
// }

// Region regionColor = new Region(); // Used for even spacing
// HBox.setHgrow(regionColor, Priority.ALWAYS);

// hColor.getChildren().addAll(colorLabel, regionColor, colorComboBox);
// // ----

// // ---- Save Button ----
// Button saveButton = new Button("Save");
// saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
// saveButton.setOnAction(e -> {
// String selectedFont = fontComboBox.getValue();
// String selectedColor = getColorHexByName(colorComboBox.getValue());

// // Update the settings in the database
// Database.updateSettings(Database.organizatorId, selectedFont, selectedColor);
// System.out.println("Selected Font: " + selectedFont);
// System.out.println("Selected Color: " + selectedColor);

// rebuild();
// changeContent(pageSettings()); // Refresh page after saving
// });

// // Add all fields and button to the page
// hFont.getStyleClass().add("group");
// hColor.getStyleClass().add("group");

// page.getChildren().addAll(hFont, hColor, saveButton);

// scrollPane.setContent(page);
// scrollPane.setFitToWidth(true); // Ensures it resizes properly
// scrollPane.setFitToHeight(true); // Ensures it resizes properly
// scrollPane.setPannable(true); // Allows mouse scrolling

// return scrollPane;
// }

// // Helper method to get color name by hex code
// private String getColorNameByHex(String hex) {
// switch (hex) {
// case "#FF0000":
// return "Red (#FF0000)";
// case "#00FF00":
// return "Green (#00FF00)";
// case "#0000FF":
// return "Blue (#0000FF)";
// case "#000000":
// return "Black (#000000)";
// case "#FFFFFF":
// return "White (#FFFFFF)";
// case "#FFFF00":
// return "Yellow (#FFFF00)";
// case "#FFA500":
// return "Orange (#FFA500)";
// case "#800080":
// return "Purple (#800080)";
// // Desaturated colors
// case "#F5F5DC":
// return "Beige (#F5F5DC)";
// case "#A52A2A":
// return "Light Brown (#A52A2A)";
// case "#AEC6CF":
// return "Pastel Blue (#AEC6CF)";
// case "#77DD77":
// return "Pastel Green (#77DD77)";
// case "#FFD1DC":
// return "Pastel Pink (#FFD1DC)";
// case "#E6E6FA":
// return "Muted Lavender (#E6E6FA)";
// case "#F88379":
// return "Muted Coral (#F88379)";
// case "#D1A7A1":
// return "Dusty Rose (#D1A7A1)";
// case "#F9E79F":
// return "Soft Yellow (#F9E79F)";
// // Darker tones
// case "#36454F":
// return "Charcoal (#36454F)";
// case "#2F4F4F":
// return "Dark Slate Gray (#2F4F4F)";
// case "#556B2F":
// return "Dark Olive Green (#556B2F)";
// case "#3E2723":
// return "Dark Brown (#3E2723)";
// case "#003366":
// return "Deep Navy (#003366)";
// case "#483D8B":
// return "Dark Slate Blue (#483D8B)";
// // Dark grays
// case "#2A3439":
// return "Gunmetal Gray (#2A3439)";
// case "#B2BEB5":
// return "Ash Gray (#B2BEB5)";
// default:
// return "Black (#000000)"; // Default value if no match
// }
// }

// // Helper method to get color hex value by name
// private String getColorHexByName(String colorName) {
// switch (colorName) {
// case "Red (#FF0000)":
// return "#FF0000";
// case "Green (#00FF00)":
// return "#00FF00";
// case "Blue (#0000FF)":
// return "#0000FF";
// case "Black (#000000)":
// return "#000000";
// case "White (#FFFFFF)":
// return "#FFFFFF";
// case "Yellow (#FFFF00)":
// return "#FFFF00";
// case "Orange (#FFA500)":
// return "#FFA500";
// case "Purple (#800080)":
// return "#800080";
// // Desaturated colors
// case "Beige (#F5F5DC)":
// return "#F5F5DC";
// case "Light Brown (#A52A2A)":
// return "#A52A2A";
// case "Pastel Blue (#AEC6CF)":
// return "#AEC6CF";
// case "Pastel Green (#77DD77)":
// return "#77DD77";
// case "Pastel Pink (#FFD1DC)":
// return "#FFD1DC";
// case "Muted Lavender (#E6E6FA)":
// return "#E6E6FA";
// case "Muted Coral (#F88379)":
// return "#F88379";
// case "Dusty Rose (#D1A7A1)":
// return "#D1A7A1";
// case "Soft Yellow (#F9E79F)":
// return "#F9E79F";
// // Darker tones
// case "Charcoal (#36454F)":
// return "#36454F";
// case "Dark Slate Gray (#2F4F4F)":
// return "#2F4F4F";
// case "Dark Olive Green (#556B2F)":
// return "#556B2F";
// case "Dark Brown (#3E2723)":
// return "#3E2723";
// case "Deep Navy (#003366)":
// return "#003366";
// case "Dark Slate Blue (#483D8B)":
// return "#483D8B";
// // Dark grays
// case "Gunmetal Gray (#2A3439)":
// return "#2A3439";
// case "Ash Gray (#B2BEB5)":
// return "#B2BEB5";
// case "Charcoal Gray (#36454F)":
// return "#36454F";
// default:
// return "#000000"; // Default hex value
// }
// }

// private void showMessageBox(AlertType type, String title, String message) {
// Alert alert = new Alert(type); // Create the alert with the specified type
// alert.setTitle(title); // Set the title of the message box
// alert.setHeaderText(null); // Optional: Set the header text (null means no
// header)
// alert.setContentText(message); // Set the content (message) text

// alert.showAndWait(); // Show the alert and wait for the user to close it
// }

// private Node pageOrganizator() {
// ScrollPane scrollPane = new ScrollPane();

// VBox page = new VBox(10); // Adds spacing between elements
// page.setAlignment(Pos.CENTER);
// page.getStyleClass().add("page");

// // ----
// HBox hIme = new HBox(10);
// Label imeLabel = new Label("Ime:");

// TextField imeField = new TextField();
// imeField.setPromptText("Ime...");
// imeField.setMaxWidth(300);
// imeField.setText(Database.organizator.ime);

// Region regionIme = new Region(); // Used for even spacing
// HBox.setHgrow(regionIme, Priority.ALWAYS);

// hIme.getChildren().addAll(imeLabel, regionIme, imeField);
// // ----

// // ----
// HBox hOpis = new HBox(10);
// Label opisLabel = new Label("Opis:");

// TextArea opisField = new TextArea();
// opisField.setPromptText("Opis...");
// opisField.setMaxWidth(3000);
// opisField.setPrefRowCount(5); // Adjusts height
// opisField.setWrapText(true); // Enables text wrapping
// opisField.setText(Database.organizator.opis);

// Region regionOpis = new Region();
// regionOpis.setMinWidth(100);
// // HBox.setHgrow(regionOpis, Priority.min(30, 30));
// HBox.setHgrow(opisField, Priority.ALWAYS);

// hOpis.getChildren().addAll(opisLabel, regionOpis, opisField);
// // ----

// // ----
// HBox hEmail = new HBox(10);
// Label emailLabel = new Label("Email:");

// TextField emailField = new TextField();
// emailField.setPromptText("Email...");
// emailField.setMaxWidth(300);
// emailField.setText(Database.organizator.email);

// Region regionEmail = new Region();
// HBox.setHgrow(regionEmail, Priority.ALWAYS);

// hEmail.getChildren().addAll(emailLabel, regionEmail, emailField);
// // ----

// // ----
// HBox hTelefon = new HBox(10);
// Label telefonLabel = new Label("Telefon:");

// TextField telefonField = new TextField();
// telefonField.setPromptText("Telefon...");
// telefonField.setMaxWidth(300);
// telefonField.setText(Database.organizator.telefon);

// Region regionTelefon = new Region();
// HBox.setHgrow(regionTelefon, Priority.ALWAYS);

// hTelefon.getChildren().addAll(telefonLabel, regionTelefon, telefonField);
// // ----

// // ----
// HBox hNaslov = new HBox(10);
// Label naslovLabel = new Label("Naslov:");

// TextField naslovField = new TextField();
// naslovField.setPromptText("Naslov...");
// naslovField.setMaxWidth(300);
// naslovField.setText(Database.organizator.naslov);

// Region regionNaslov = new Region();
// HBox.setHgrow(regionNaslov, Priority.ALWAYS);

// hNaslov.getChildren().addAll(naslovLabel, regionNaslov, naslovField);
// // ----

// // ----
// HBox hKraj = new HBox(10);
// Label krajIdLabel = new Label("Kraj:");

// ComboBox<Kraj> krajCB = krajCombobox(Database.organizator.krajId);

// // TextField krajIdField = new TextField();
// // krajIdField.setPromptText("Kraj ID...");
// // krajIdField.setMaxWidth(300);
// // krajIdField.setText(String.valueOf(Database.organizator.krajId));

// Region regionKrajId = new Region();
// HBox.setHgrow(regionKrajId, Priority.ALWAYS);

// hKraj.getChildren().addAll(krajIdLabel, regionKrajId, krajCB);
// // ----

// // ----
// HBox hSettingsId = new HBox(10);
// Label settingsIdLabel = new Label("Settings ID:");

// TextField settingsIdField = new TextField();
// settingsIdField.setPromptText("Settings ID...");
// settingsIdField.setMaxWidth(300);
// settingsIdField.setText(String.valueOf(Database.organizator.settingsId));

// Region regionSettingsId = new Region();
// HBox.setHgrow(regionSettingsId, Priority.ALWAYS);

// hSettingsId.getChildren().addAll(settingsIdLabel, regionSettingsId,
// settingsIdField);
// // ----

// Button saveButton = new Button("Save");
// saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
// saveButton.setOnAction(e -> {

// int krajId = krajCB.getSelectionModel().getSelectedItem().id;

// Database.updateOrganizator(Database.organizatorId,
// imeField.getText(),
// opisField.getText(),
// emailField.getText(),
// telefonField.getText(),
// naslovField.getText(),
// krajId);

// rebuild();

// // Update the database with the new values (you need to create an update
// method
// // in Database)
// // Database.updateOrganizator(Database.organizator);

// // Feedback to the user (optional)
// System.out.println("Profile updated successfully!");
// });

// // Add all fields and button to the page

// hIme.getStyleClass().add("group");
// hOpis.getStyleClass().add("group");
// hEmail.getStyleClass().add("group");
// hTelefon.getStyleClass().add("group");
// hNaslov.getStyleClass().add("group");
// hKraj.getStyleClass().add("group");
// // hSettingsId.getStyleClass().add("group");
// // hStDogodkov.getStyleClass().add("group");
// page.getChildren().addAll(hIme, hOpis, hEmail, hTelefon, hNaslov, hKraj,
// saveButton);

// scrollPane.setContent(page);
// scrollPane.setFitToWidth(true); // Ensures it resizes properly
// scrollPane.setFitToHeight(true); // Ensures it resizes properly
// scrollPane.setPannable(true); // Allows mouse scrolling

// // return scrollPane;
// return scrollPane;
// }

// public Node pageIzvajalci() {
// // List all Izvajalci from the database
// ObservableList<Izvajalec> izvajalciList = Database.getIzvajalci();

// ScrollPane scrollPane = new ScrollPane();

// // VBox to hold the content
// VBox page = new VBox(10);
// page.setPadding(new Insets(10));
// page.getStyleClass().add("page");

// // Create "Add New" button
// Button addNewButton = new Button("Add New");
// addNewButton.setOnAction(event -> {
// // Insert blank izvajalec and get the ID
// int newId = Database.insertIzvajalec("New Izvajalec", "", ""); // Insert
// blank values

// System.out.println(newId);
// // After inserting, go to the edit page for this new ID
// changeContent(pageEditIzvajalec(newId)); // Call the pageEditIzvajalec method
// to navigate
// });

// // Add the button at the top of the page
// page.getChildren().add(addNewButton);

// // Display all izvajalci with their edit buttons
// for (Izvajalec izvajalec : izvajalciList) {
// HBox hbox = new HBox(10);
// hbox.setAlignment(Pos.CENTER);
// hbox.getStyleClass().add("group"); // Add "group" style class to each HBox

// // Create label for the ime
// Label imeLabel = new Label(izvajalec.ime); // Directly accessing public field

// // Create edit button
// Button editButton = new Button("Edit");
// editButton.setOnAction(e -> changeContent(pageEditIzvajalec(izvajalec.id)));
// editButton.setStyle("-fx-background-color: rgb(78, 112, 175)");
// Button deleteButton = new Button("Delete");
// deleteButton.setOnAction(e -> {
// try {
// Database.deleteIzvajalec(izvajalec.id);
// page.getChildren().remove(hbox);
// } catch (Exception ex) {
// showMessageBox(AlertType.ERROR, "Error", "Ta band je povezan z dogodki");
// }
// });
// deleteButton.setStyle("-fx-background-color: rgb(206, 78, 78)");

// // Create Region for spacing between imeLabel and editButton
// Region region = new Region();
// HBox.setHgrow(region, Priority.ALWAYS); // Allow the Region to grow and push
// items apart

// // Add imeLabel, region, and editButton to HBox
// Region r2 = new Region();
// r2.setMinWidth(10);

// hbox.getChildren().addAll(imeLabel, region, editButton, r2, deleteButton);

// // Add HBox to VBox
// page.getChildren().add(hbox);
// }

// scrollPane.setContent(page);
// scrollPane.setFitToWidth(true); // Ensures it resizes properly
// scrollPane.setFitToHeight(true); // Ensures it resizes properly
// scrollPane.setPannable(true); // Allows mouse scrolling

// return scrollPane;
// }

// public Node pageEditIzvajalec(int id) {
// // Get the Izvajalec data by ID
// Izvajalec izvajalec = Database.getIzvajalec(id);

// // VBox to hold the content
// VBox page = new VBox(10);
// page.setPadding(new Insets(10));
// page.setAlignment(Pos.CENTER);
// // page.getStyleClass().add("page");
// // page.setStyle("-fx-background-color: rgb(43, 43, 43);");

// // Create fields for editing
// TextField imeField = new TextField(izvajalec.ime); // Directly accessing
// public field
// imeField.setPromptText("Ime");

// TextArea opisField = new TextArea(izvajalec.opis); // Directly accessing
// public field
// opisField.setPromptText("Opis");

// TextField telefonField = new TextField(izvajalec.telefon); // Directly
// accessing public field
// telefonField.setPromptText("Telefon");

// // Save and Back buttons
// Button backButton = new Button("Back");
// backButton.setMinWidth(300);
// backButton.setStyle("-fx-background-color:rgb(83, 83, 83); -fx-text-fill:
// white;");

// Button saveButton = new Button("Save Changes");
// saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
// saveButton.setMinWidth(300);

// // Back button action
// backButton.setOnAction(event -> {
// changeContent(pageIzvajalci()); // Navigate back to the list of izvajalci
// });

// // Save button action
// saveButton.setOnAction(event -> {
// // Get updated data
// String ime = imeField.getText();
// String opis = opisField.getText();
// String telefon = telefonField.getText();

// // Update the izvajalec in the database
// Database.updateIzvajalec(id, ime, opis, telefon);
// });

// // Create HBoxes for each field with labels and fields, adding Regions for
// // spacing
// HBox imeBox = new HBox(10);
// imeBox.setAlignment(Pos.TOP_LEFT);
// imeBox.getStyleClass().add("group");
// Label imeLabel = new Label("Ime: ");
// Region imeRegion = new Region();
// HBox.setHgrow(imeRegion, Priority.ALWAYS);
// imeBox.getChildren().addAll(imeLabel, imeRegion, imeField);

// HBox opisBox = new HBox(10);
// opisBox.setAlignment(Pos.TOP_LEFT);
// opisBox.getStyleClass().add("group");
// Label opisLabel = new Label("Opis: ");
// Region opisRegion = new Region();
// opisRegion.setMinWidth(100);
// HBox.setHgrow(opisField, Priority.ALWAYS);
// opisBox.getChildren().addAll(opisLabel, opisRegion, opisField);

// HBox telefonBox = new HBox(10);
// telefonBox.setAlignment(Pos.TOP_LEFT);
// telefonBox.getStyleClass().add("group");
// Label telefonLabel = new Label("Telefon: ");
// Region telefonRegion = new Region();
// HBox.setHgrow(telefonRegion, Priority.ALWAYS);
// telefonBox.getChildren().addAll(telefonLabel, telefonRegion, telefonField);

// // Add elements to the page
// page.getChildren().addAll(imeBox, opisBox, telefonBox, saveButton,
// backButton);

// return page;
// }

// public Node pageProstori() {
// // List all Prostori from the database
// ObservableList<Prostor> prostoriList = Database.getProstori();

// ScrollPane scrollPane = new ScrollPane();

// // VBox to hold the content
// VBox page = new VBox(10);
// page.setPadding(new Insets(10));
// page.getStyleClass().add("page");

// // Create "Add New" button
// Button addNewButton = new Button("Add New");
// addNewButton.setOnAction(event -> {
// // Insert blank prostor and get the ID
// int newId = Database.insertProstor("New Prostor", "", 0, "Nekje",
// Database.getKraji().get(0).id); // Insert
// // blank
// // values

// System.out.println(newId);
// // After inserting, go to the edit page for this new ID
// changeContent(pageEditProstor(newId)); // Call the pageEditProstor method to
// navigate
// });

// // Add the button at the top of the page
// page.getChildren().add(addNewButton);

// // Display all prostori with their edit and delete buttons
// for (Prostor prostor : prostoriList) {
// HBox hbox = new HBox(10);
// hbox.setAlignment(Pos.CENTER);
// hbox.getStyleClass().add("group"); // Add "group" style class to each HBox

// // Create label for the ime
// Label imeLabel = new Label(prostor.ime); // Directly accessing public field

// // Create edit button
// Button editButton = new Button("Edit");
// editButton.setOnAction(e -> changeContent(pageEditProstor(prostor.id)));
// editButton.setStyle("-fx-background-color: rgb(78, 112, 175)");

// // Create delete button
// Button deleteButton = new Button("Delete");
// deleteButton.setOnAction(e -> {
// // page.getChildren().remove(hbox); // Remove the deleted item from the UI
// try {
// Database.deleteProstor(prostor.id);
// page.getChildren().remove(hbox);
// } catch (Exception ex) {
// showMessageBox(AlertType.ERROR, "Error", "Ta prostor je povezan z dogodki");
// }
// });
// deleteButton.setStyle("-fx-background-color: rgb(206, 78, 78)");

// // Create Region for spacing between imeLabel and editButton
// Region region = new Region();
// HBox.setHgrow(region, Priority.ALWAYS); // Allow the Region to grow and push
// items apart

// // Add imeLabel, region, and editButton to HBox
// Region r2 = new Region();
// r2.setMinWidth(10);

// hbox.getChildren().addAll(imeLabel, region, editButton, r2, deleteButton);

// // Add HBox to VBox
// page.getChildren().add(hbox);
// }

// scrollPane.setContent(page);
// scrollPane.setFitToWidth(true); // Ensures it resizes properly
// scrollPane.setFitToHeight(true); // Ensures it resizes properly
// scrollPane.setPannable(true); // Allows mouse scrolling

// return scrollPane;
// }

// public Node pageEditProstor(int id) {
// // Get the Prostor data by ID
// Prostor prostor = Database.getProstor(id);

// // VBox to hold the content
// VBox page = new VBox(10);
// page.setPadding(new Insets(10));
// page.setAlignment(Pos.CENTER);

// // Create fields for editing
// TextField imeField = new TextField(prostor.ime);
// imeField.setPromptText("Ime");

// TextArea opisField = new TextArea(prostor.opis);
// opisField.setPromptText("Opis");

// TextField kapacitetaField = new
// TextField(String.valueOf(prostor.kapaciteta));
// kapacitetaField.setPromptText("Kapaciteta");

// TextField naslovField = new TextField(prostor.naslov);
// naslovField.setPromptText("Naslov");

// // Create ComboBox for Kraj
// ComboBox<Kraj> krajComboBox = krajCombobox(prostor.krajId); // Pass in the
// existing krajId for pre-selection

// // Save and Back buttons
// Button backButton = new Button("Back");
// backButton.setMinWidth(300);
// backButton.setStyle("-fx-background-color:rgb(83, 83, 83); -fx-text-fill:
// white;");

// Button saveButton = new Button("Save Changes");
// saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
// saveButton.setMinWidth(300);

// // Back button action
// backButton.setOnAction(event -> {
// changeContent(pageProstori()); // Navigate back to the list of prostori
// });

// // Save button action
// saveButton.setOnAction(event -> {
// // Get updated data
// String ime = imeField.getText();
// String opis = opisField.getText();
// int kapaciteta = Integer.parseInt(kapacitetaField.getText());
// String naslov = naslovField.getText();
// int krajId = krajComboBox.getSelectionModel().getSelectedItem().id; // Get
// selected Kraj ID

// // Update the prostor in the database
// Database.updateProstor(id, ime, opis, kapaciteta, naslov, krajId); // Update
// with the selected Kraj ID
// });

// // Create HBoxes for each field with labels and fields, adding Regions for
// // spacing
// HBox imeBox = new HBox(10);
// imeBox.setAlignment(Pos.TOP_LEFT);
// imeBox.getStyleClass().add("group");
// Label imeLabel = new Label("Ime: ");
// Region imeRegion = new Region();
// HBox.setHgrow(imeRegion, Priority.ALWAYS);
// imeBox.getChildren().addAll(imeLabel, imeRegion, imeField);

// HBox opisBox = new HBox(10);
// opisBox.setAlignment(Pos.TOP_LEFT);
// opisBox.getStyleClass().add("group");
// Label opisLabel = new Label("Opis: ");
// Region opisRegion = new Region();
// opisRegion.setMinWidth(100);
// HBox.setHgrow(opisField, Priority.ALWAYS);
// opisBox.getChildren().addAll(opisLabel, opisRegion, opisField);

// HBox kapacitetaBox = new HBox(10);
// kapacitetaBox.setAlignment(Pos.TOP_LEFT);
// kapacitetaBox.getStyleClass().add("group");
// Label kapacitetaLabel = new Label("Kapaciteta: ");
// Region kapacitetaRegion = new Region();
// HBox.setHgrow(kapacitetaRegion, Priority.ALWAYS);
// kapacitetaBox.getChildren().addAll(kapacitetaLabel, kapacitetaRegion,
// kapacitetaField);

// HBox naslovBox = new HBox(10);
// naslovBox.setAlignment(Pos.TOP_LEFT);
// naslovBox.getStyleClass().add("group");
// Label naslovLabel = new Label("Naslov: ");
// Region naslovRegion = new Region();
// HBox.setHgrow(naslovRegion, Priority.ALWAYS);
// naslovBox.getChildren().addAll(naslovLabel, naslovRegion, naslovField);

// HBox krajBox = new HBox(10);
// krajBox.setAlignment(Pos.TOP_LEFT);
// krajBox.getStyleClass().add("group");
// Label krajLabel = new Label("Kraj: ");
// Region krajRegion = new Region();
// HBox.setHgrow(krajRegion, Priority.ALWAYS);
// krajBox.getChildren().addAll(krajLabel, krajRegion, krajComboBox);

// // Add elements to the page
// page.getChildren().addAll(imeBox, opisBox, kapacitetaBox, naslovBox, krajBox,
// saveButton, backButton);

// return page;
// }

// public Node pageDogodki(int organizatorId) {
// // Fetch the list of events using the `getAllDogodki` function from the
// database
// // ObservableList<Dogodek> dogodkiList =
// Database.getAllDogodki(organizatorId);
// ObservableList<Dogodek> dogodkiList =
// FXCollections.observableArrayList(Database.getAllDogodki(organizatorId));

// ScrollPane scrollPane = new ScrollPane();

// // VBox to hold the content
// VBox page = new VBox(10);
// page.setPadding(new Insets(10));
// page.getStyleClass().add("page");

// // Create "Add New" button
// Button addNewButton = new Button("Add New Event");
// addNewButton.setOnAction(event -> {
// // Insert a blank event and get the ID
// int newId = Database.insertDogodek(organizatorId, 1, 1, 0.0f, new
// Timestamp(System.currentTimeMillis()),
// "New Event", ""); // Dummy values for now
// System.out.println(newId);
// // After inserting, go to the edit page for this new ID
// changeContent(pageEditDogodek(newId)); // Call the pageEditDogodek method to
// navigate
// });

// // Add the button at the top of the page
// page.getChildren().add(addNewButton);

// // Display all events with their edit and delete buttons
// for (Dogodek dogodek : dogodkiList) {
// HBox hbox = new HBox(10);
// hbox.setAlignment(Pos.CENTER);
// hbox.getStyleClass().add("group"); // Add "group" style class to each HBox

// // Create label for the event name
// Label imeLabel = new Label(dogodek.ime);

// // Create edit button
// Button editButton = new Button("Edit");
// editButton.setOnAction(e -> changeContent(pageEditDogodek(dogodek.id)));
// editButton.setStyle("-fx-background-color: rgb(78, 112, 175)");

// // Create delete button
// Button deleteButton = new Button("Delete");
// deleteButton.setOnAction(e -> {
// // Database.deleteDogodek(dogodek.id);
// // page.getChildren().remove(hbox); // Remove the deleted item from the UI

// try {
// Database.deleteDogodek(dogodek.id);
// page.getChildren().remove(hbox);
// } catch (Exception ex) {
// showMessageBox(AlertType.ERROR, "Error", "Napaka");
// ex.printStackTrace();
// }
// });
// deleteButton.setStyle("-fx-background-color: rgb(206, 78, 78)");

// // Create Region for spacing between imeLabel and editButton
// Region region = new Region();
// HBox.setHgrow(region, Priority.ALWAYS); // Allow the Region to grow and push
// items apart

// // Add imeLabel, region, and editButton to HBox
// Region r2 = new Region();
// r2.setMinWidth(10);

// hbox.getChildren().addAll(imeLabel, region, editButton, r2, deleteButton);

// // Add HBox to VBox
// page.getChildren().add(hbox);
// }

// scrollPane.setContent(page);
// scrollPane.setFitToWidth(true); // Ensures it resizes properly
// scrollPane.setFitToHeight(true); // Ensures it resizes properly
// scrollPane.setPannable(true); // Allows mouse scrolling

// return scrollPane;
// }

// public Node pageEditDogodek(int id) {
// // Fetch the event data from the database
// Dogodek dogodek = Database.getDogodek(id);

// // VBox container for the page
// VBox page = new VBox(20);
// page.setPadding(new Insets(20));
// page.setAlignment(Pos.CENTER);

// // Fields and Labels
// TextField imeField = new TextField(dogodek.ime);
// // TextField opisField = new TextField(dogodek.opis);

// TextArea opisField = new TextArea();
// opisField.setPromptText("Opis...");
// opisField.setMaxWidth(3000);
// opisField.setPrefRowCount(5); // Adjusts height
// opisField.setWrapText(true); // Enables text wrapping
// opisField.setText(dogodek.opis);

// TextField cenaField = new TextField(String.valueOf(dogodek.cena_vstopnice));
// DatePicker casField = new
// DatePicker(dogodek.cas.toLocalDateTime().toLocalDate());

// // Hour and minute combo boxes
// ComboBox<String> hourComboBox = new ComboBox<>();
// hourComboBox.getItems().addAll(
// "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
// "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
// hourComboBox.setValue(String.format("%02d",
// dogodek.cas.toLocalDateTime().getHour()));

// ComboBox<String> minuteComboBox = new ComboBox<>();
// minuteComboBox.getItems().addAll(
// "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
// minuteComboBox.setValue(String.format("%02d",
// dogodek.cas.toLocalDateTime().getMinute()));

// // ComboBoxes for Prostor and Izvajalec
// ComboBox<Prostor> prostorComboBox = prostorComboBox(dogodek.prostor_id);
// ComboBox<Izvajalec> izvajalecComboBox =
// izvajalecComboBox(dogodek.izvajalec_id);

// // Save and Back buttons
// Button backButton = new Button("Back");
// backButton.setMinWidth(300);
// backButton.setStyle("-fx-background-color:rgb(83, 83, 83); -fx-text-fill:
// white;");

// Button saveButton = new Button("Save Changes");
// saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
// saveButton.setMinWidth(300);

// // Back button action
// backButton.setOnAction(event -> {
// changeContent(pageDogodki(Database.organizatorId)); // Navigate back
// });

// // Save button action
// saveButton.setOnAction(event -> {
// String ime = imeField.getText();
// String opis = opisField.getText();
// float cena = Float.parseFloat(cenaField.getText());
// LocalDateTime dateTime = casField.getValue().atTime(
// Integer.parseInt(hourComboBox.getValue()),
// Integer.parseInt(minuteComboBox.getValue()));
// Timestamp cas = Timestamp.valueOf(dateTime);
// int prostorId = prostorComboBox.getSelectionModel().getSelectedItem().id;
// int izvajalecId = izvajalecComboBox.getSelectionModel().getSelectedItem().id;

// Database.updateDogodek(id, dogodek.organizator_id, prostorId, izvajalecId,
// cena, cas, ime, opis);
// });

// // Layout for each field in HBox
// HBox imeBox = createHBox("Ime: ", imeField);
// HBox opisBox = createHBox("Opis: ", opisField);
// HBox cenaBox = createHBox("Cena: ", cenaField);
// HBox casBox = createHBox("Datum: ", casField);
// HBox timeBox = createHBox("Izberi uro: ", hourComboBox, minuteComboBox);
// HBox prostorBox = createHBox("Izberi prostor: ", prostorComboBox);
// HBox izvajalecBox = createHBox("Izberi izvajalca: ", izvajalecComboBox);

// // Add elements to the page
// page.getChildren().addAll(imeBox, opisBox, cenaBox, casBox, timeBox,
// prostorBox, izvajalecBox, saveButton,
// backButton);

// return page;
// }

// private HBox createHBox(String labelText, Node... elements) {
// HBox hbox = new HBox(10);
// hbox.setAlignment(Pos.TOP_LEFT);
// Label label = new Label(labelText);
// Region spacer = new Region();
// HBox.setHgrow(spacer, Priority.ALWAYS);
// hbox.getChildren().add(label);
// hbox.getChildren().add(spacer);
// hbox.getChildren().addAll(elements);
// return hbox;
// }

// private void changeContent(Node page) {
// Database.getOrganizator(Database.organizatorId);

// root.setStyle("-fx-font-family: '" + Database.font + "'" +
// "-fx-background-color: " + Database.color + ";");

// StackPane contentArea = (StackPane) root.getCenter(); // Access the content
// area from the root pane
// contentArea.getChildren().clear(); // Clear the existing content
// // page.setStyle("-fx-font-family: '" + Database.font + "'"
// // -fx-background-color: " + Database.color + ";");
// contentArea.getChildren().add(page); // Add new content
// }

// public static void main(String[] args) {
// launch(args);
// }
// }