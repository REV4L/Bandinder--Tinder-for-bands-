package com.example;

import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;

import java.io.ByteArrayInputStream;
import java.util.List;

public class Card {
    private double dragStartX;
    private StackPane card;
    private Label name;
    private Label bio;
    private Label genre;
    private Label swipeIndicator;
    private ImageView imageView;
    private HBox progressBar;
    private List<Image> images;
    private int currentImageIndex = 0;

    public Card(Region r) {
        this.card = createCard(r);
        setUpMouseEvents();

        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000 / 60), e -> tick()));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();

        appearAnim();

        new Thread(() -> {
            try {

                while (true) {
                    Thread.sleep(100); // wait 100ms

                    if (!Database.loggedIn())
                        continue;

                    javafx.application.Platform.runLater(() -> {
                        nextSuggestion();
                    });

                    break;

                }
            } catch (Exception ex) {
            }
        }).start();
    }

    public StackPane getCard() {
        return card;
    }

    public void nextSuggestion() {
        new Thread(() -> {
            card.setTranslateY(1000);
            int nextId = Database.getBestBandMatch(Database.bandId, 1);
            System.out.println(nextId);
            if (nextId >= 0) {
                Band band = Database.getBandInfo(nextId);
                List<Image> imgs = Database.getBandImages(nextId);
                javafx.application.Platform.runLater(() -> {
                    setBand(band);
                    setImages(imgs);
                });
            }

            appearAnim();
        }).start();
    }

    private void setBand(Band band) {
        if (band == null)
            return;
        name.setText(band.name);
        bio.setText(band.bio);
        genre.setText(band.email);
    }

    public StackPane createCard(Region bindTo) {
        double padding = 10;

        imageView = new ImageView();
        imageView.setPreserveRatio(false); // Fill the entire area
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Bind imageView size to card size
        DoubleBinding cardWidth = bindTo.widthProperty().multiply(0.9);
        DoubleBinding cardHeight = cardWidth.multiply(4.0 / 3.0);
        imageView.fitWidthProperty().bind(cardWidth);
        imageView.fitHeightProperty().bind(cardHeight);

        Rectangle r = new Rectangle();
        r.setArcWidth(20);
        r.setArcHeight(20);
        r.widthProperty().bind(cardWidth);
        r.heightProperty().bind(cardHeight);

        StackPane imageHolder = new StackPane(imageView);
        imageHolder.setStyle("-fx-background-color: #181818; -fx-background-radius: 20;");
        imageHolder.setClip(r);

        name = new Label("Name");
        bio = new Label("Instrument");
        genre = new Label("Genre");
        swipeIndicator = new Label();
        swipeIndicator.setStyle(
                "-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: white; -fx-opacity: 0; -fx-font-family: 'Comic Sans MS';");

        name.setStyle("-fx-font-size: 28; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");
        bio.setStyle("-fx-font-size: 22; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");
        genre.setStyle("-fx-font-size: 22; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");

        VBox vb = new VBox(5, name, bio, genre);
        vb.setAlignment(Pos.BOTTOM_LEFT);
        vb.setPadding(new Insets(padding));

        progressBar = new HBox(5);
        progressBar.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setAlignment(progressBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(progressBar, new Insets(0, 0, 10, 0));

        card = new StackPane(imageHolder, vb, swipeIndicator, progressBar);
        card.getStyleClass().add("card");
        card.setStyle(
                "-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 30, 0.2, 0, 0);");

        card.maxWidthProperty().bind(cardWidth);
        card.minWidthProperty().bind(cardWidth);
        card.prefWidthProperty().bind(cardWidth);
        card.maxHeightProperty().bind(cardHeight);
        card.minHeightProperty().bind(cardHeight);
        card.prefHeightProperty().bind(cardHeight);

        vb.maxWidthProperty().bind(cardWidth.subtract(padding * 2));
        vb.prefWidthProperty().bind(cardWidth.subtract(padding * 2));

        card.setOnMouseClicked(e -> {
            if (images == null || images.size() <= 1)
                return;
            if (e.getX() > card.getWidth() / 2)
                nextImage();
            else
                previousImage();
        });

        return card;
    }

    private void setImages(List<Image> imgs) {
        this.images = imgs;
        this.currentImageIndex = 0;
        updateImage();
    }

    private void updateImage() {
        if (images == null || images.isEmpty())
            return;
        imageView.setImage(images.get(currentImageIndex));
        progressBar.getChildren().clear();
        for (int i = 0; i < images.size(); i++) {
            Circle dot = new Circle(5);
            dot.setFill(i == currentImageIndex ? Color.WHITE : Color.GRAY);
            progressBar.getChildren().add(dot);
        }
    }

    private void nextImage() {
        if (images == null || images.size() <= 1)
            return;
        currentImageIndex = (currentImageIndex + 1) % images.size();
        updateImage();
    }

    private void previousImage() {
        if (images == null || images.size() <= 1)
            return;
        currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
        updateImage();
    }

    private double getAngle() {
        return card.getTranslateX() * -0.1;
    }

    private void tick() {
        card.setRotate(getAngle());
        double x = card.getTranslateX();
        double opacity = Math.max(Math.abs(x) / 150.0, 0.01);

        double radius = 10;
        double spread = 100;

        if (x > 0) {
            swipeIndicator.setText("YES");
            swipeIndicator.setStyle(
                    "-fx-text-fill: rgba(93, 255, 107,1); -fx-font-size: 48; -fx-font-weight: bold; -fx-opacity: "
                            + opacity + "; -fx-font-family: 'Comic Sans MS';");
            StackPane.setAlignment(swipeIndicator, Pos.TOP_LEFT);
            StackPane.setMargin(swipeIndicator, new Insets(20, 0, 0, 30));
            card.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(one-pass-box, rgba(93, 255, 107,"
                    + opacity + "), " + radius + ", " + spread + ", 0, 0);");
        } else if (x < 0) {
            swipeIndicator.setText("NO");
            swipeIndicator.setStyle(
                    "-fx-text-fill: rgba(255, 67, 67, 1); -fx-font-size: 48; -fx-font-weight: bold; -fx-opacity: "
                            + opacity + "; -fx-font-family: 'Comic Sans MS';");
            StackPane.setAlignment(swipeIndicator, Pos.TOP_RIGHT);
            StackPane.setMargin(swipeIndicator, new Insets(20, 30, 0, 0));
            card.setStyle("-fx-background-color: transparent; -fx-effect: dropshadow(one-pass-box, rgba(255, 67, 67,"
                    + opacity + "), " + radius + ", " + spread + ", 0, 0);");
        } else {
            swipeIndicator.setStyle("-fx-opacity: 0;");
            card.setStyle(
                    "-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 30, 0.2, 0, 0);");
        }
    }

    private void setUpMouseEvents() {
        card.setOnMousePressed(this::pressed);
        card.setOnMouseDragged(this::dragged);
        card.setOnMouseReleased(this::released);
    }

    private void pressed(MouseEvent event) {
        dragStartX = event.getSceneX();
    }

    private void dragged(MouseEvent event) {
        card.setTranslateX(event.getSceneX() - dragStartX);
    }

    private void released(MouseEvent event) {
        handleSwipe(card);
        tick();
    }

    private void handleSwipe(Node card) {
        double threshold = 150;

        if (card.getTranslateX() > threshold) {
            animateCard(card, 2000);
        } else if (card.getTranslateX() < -threshold) {
            animateCard(card, -2000);
        } else {
            animateCard(card, 0);
        }
    }

    private void animateCard(Node card, double targetX) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), card);
        transition.setToX(targetX);

        transition.setOnFinished(e -> {
            if (Math.abs(targetX) > 300) {
                card.setTranslateX(0);
                tick();
                // appearAnim();
                nextSuggestion();
            }
        });

        transition.play();
    }

    private void appearAnim() {
        card.setTranslateY(1000);
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), card);
        transition.setToY(0);
        transition.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), card);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        RotateTransition rotate = new RotateTransition(Duration.millis(300), card);
        rotate.setFromAngle(10);
        rotate.setToAngle(0);
        rotate.setInterpolator(Interpolator.EASE_BOTH);

        transition.play();
        scale.play();
        rotate.play();
    }
}
