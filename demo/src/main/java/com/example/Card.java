// --- Card.java ---
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;

public class Card {
    private double dragStartX;
    private StackPane card;
    private Label name;
    private Label instrument;
    private Label genre;
    private Label swipeIndicator;

    public Card(Region r) {
        this.card = createCard(r);
        setUpMouseEvents();

        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000 / 60), e -> tick()));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();

        appearAnim();
    }

    public StackPane getCard() {
        return card;
    }

    public StackPane createCard(Region bindTo) {
        double padding = 10;

        ImageView image = new ImageView(new Image("https://via.placeholder.com/400x533"));
        image.setPreserveRatio(false);
        image.setSmooth(true);
        image.setCache(true);

        Rectangle r = new Rectangle();
        r.setArcWidth(20);
        r.setArcHeight(20);

        StackPane imageHolder = new StackPane(image);
        imageHolder.setStyle("-fx-background-color: #181818; -fx-background-radius: 20;");
        imageHolder.setClip(r);

        name = new Label("Name");
        instrument = new Label("Instrument");
        genre = new Label("Genre");
        swipeIndicator = new Label();
        swipeIndicator.setStyle(
                "-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: white; -fx-opacity: 0; -fx-font-family: 'Comic Sans MS';");

        name.setStyle("-fx-font-size: 28; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");
        instrument.setStyle("-fx-font-size: 22; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");
        genre.setStyle("-fx-font-size: 22; -fx-text-fill: white; -fx-font-family: 'Trebuchet MS';");

        VBox vb = new VBox(5, name, instrument, genre);
        vb.setAlignment(Pos.BOTTOM_LEFT);
        vb.setPadding(new Insets(padding));

        card = new StackPane(imageHolder, vb, swipeIndicator);
        card.getStyleClass().add("card");
        card.setStyle(
                "-fx-background-color: transparent; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 30, 0.2, 0, 0);");

        // BIND WIDTH TO PARENT WIDTH * 0.9
        DoubleBinding maxCardWidthByWidth = bindTo.widthProperty().multiply(0.9);
        DoubleBinding maxCardWidthByHeight = bindTo.heightProperty().subtract(150).multiply(3.0 / 4.0);

        DoubleBinding cardWidth = Bindings.createDoubleBinding(
                () -> Math.min(maxCardWidthByWidth.get(), maxCardWidthByHeight.get()),
                maxCardWidthByWidth, maxCardWidthByHeight);

        DoubleBinding cardHeight = cardWidth.multiply(4.0 / 3.0);

        card.maxWidthProperty().bind(cardWidth);
        card.minWidthProperty().bind(cardWidth);
        card.prefWidthProperty().bind(cardWidth);

        card.maxHeightProperty().bind(cardHeight);
        card.minHeightProperty().bind(cardHeight);
        card.prefHeightProperty().bind(cardHeight);

        r.widthProperty().bind(cardWidth);
        r.heightProperty().bind(cardHeight);

        DoubleBinding vbWidth = cardWidth.subtract(padding * 2);
        DoubleBinding vbHeight = cardHeight.subtract(padding * 2);

        vb.maxWidthProperty().bind(vbWidth);
        vb.minWidthProperty().bind(vbWidth);
        vb.prefWidthProperty().bind(vbWidth);

        vb.maxHeightProperty().bind(vbHeight);
        vb.minHeightProperty().bind(vbHeight);
        vb.prefHeightProperty().bind(vbHeight);

        return card;
    }

    private double getAngle() {
        return (card.getTranslateX()) * -0.1;
    }

    private void tick() {
        card.setRotate(getAngle());
        double x = card.getTranslateX();

        double opacity = Math.abs(x) / 150.0;
        if (opacity < 0.01)
            opacity = 0.01;

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
                appearAnim();
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