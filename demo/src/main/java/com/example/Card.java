package com.example;

import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    private Node swipeActionIndicator;

    public Card(Region r) {
        this.card = createCard(r);
        // startX = card.getX();
        setUpMouseEvents();

        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000 / 60), e -> tick()));
        t.setCycleCount(Animation.INDEFINITE); // loop forever
        t.play();

        appearAnim();

    }

    public StackPane getCard() {
        return card;
    }

    public StackPane createCard(double w, int alalla) {
        // int w = 300;
        // int h = 400;
        int h = (int) (w * 4.0 / 3.0);

        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(20);
        r.setArcHeight(20);

        r.getStyleClass().add("card");

        int padding = 40;
        VBox vb = new VBox();
        vb.setMaxWidth(w - padding);
        vb.setMaxHeight(h - padding);
        vb.setMinWidth(w - padding);
        vb.setMinHeight(h - padding);

        name = new Label("Name");
        instrument = new Label("Instrument");

        swipeActionIndicator = new Label("X");

        name.getStyleClass().add("cname");
        instrument.getStyleClass().add("cname");

        vb.getChildren().addAll(name, instrument);

        card = new StackPane();
        // card.getStyleClass().add("border");
        card.setMaxWidth(w);
        card.setMaxHeight(h);
        card.setMinWidth(w);
        card.setMinHeight(h);

        card.getChildren().addAll(r, vb);

        return card;
    }

    public StackPane createCard(Region bindTo) {
        Rectangle r = new Rectangle();
        r.setArcWidth(20);
        r.setArcHeight(20);
        r.getStyleClass().add("card");

        double padding = 10;

        VBox vb = new VBox();
        vb.setPadding(new Insets(padding));
        vb.setAlignment(Pos.TOP_LEFT);
        vb.setFillWidth(true);

        name = new Label("Name");
        instrument = new Label("Instrument");
        swipeActionIndicator = new Label("X");

        name.getStyleClass().add("cname");
        instrument.getStyleClass().add("cname");

        vb.getChildren().addAll(name, instrument);

        card = new StackPane();
        card.getChildren().addAll(r, vb);
        card.setStyle("-fx-background-color: transparent;");

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

        var opacity = Math.abs(card.getTranslateX() * 1f / 100);

        // swipeActionIndicator.setOpacity(Math.abs(card.getTranslateX() * 1f / 300));

        double radius = 3;
        double spread = 100;

        if (card.getTranslateX() < 0)
            // swipeActionIndicator.setStyle("-fx-text-fill: rgb(255, 67, 67);");
            card.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(255, 67, 67," + opacity + "), " + radius + ", "
                    + spread + ", 0, 0);");
        else
            card.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(93, 255, 107," + opacity + "), " + radius + ", "
                    + spread + ", 0, 0);");

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
        double a = (event.getSceneX() - dragStartX) * 0.1;
        card.setTranslateX(event.getSceneX() - dragStartX); // Update card's X position while dragging
        // card.setRotate(getAngle());
    }

    private void released(MouseEvent event) {
        handleSwipe(card); // When mouse is released, handle the swipe behavior
        tick();
    }

    private void handleSwipe(Node card) {
        double threshold = 150; // Define swipe threshold

        if (card.getTranslateX() > threshold) { // If the card is moved far enough to the right
            animateCard(card, 2000); // Swipe right animation
        } else if (card.getTranslateX() < -threshold) { // If the card is moved far enough to the left
            animateCard(card, -2000); // Swipe left animation
        } else {
            animateCard(card, 0); // If not far enough, return card to center
        }
    }

    private void animateCard(Node card, double targetX) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), card);
        transition.setToX(targetX); // Target position for the swipe

        transition.setOnFinished(e -> {
            // If card was swiped out of bounds (left or right), reset position
            if (Math.abs(targetX) > 300) {
                card.setTranslateX(0); // Reset card position to center
                tick();
                appearAnim();
            }
        });

        transition.play();
    }

    private void appearAnim() {
        card.setTranslateY(1000);
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), card);
        transition.setToY(0); // Target position for the swipe
        transition.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition transition1 = new ScaleTransition(Duration.millis(300), card);
        transition1.setFromY(0); // Target position for the swipe
        transition1.setFromX(0); // Target position for the swipe
        transition1.setToY(1); // Target position for the swipe
        transition1.setToX(1); // Target position for the swipe
        transition1.setInterpolator(Interpolator.EASE_BOTH);

        RotateTransition t3 = new RotateTransition(Duration.millis(300), card);
        t3.setFromAngle(10);
        t3.setToAngle(0);
        t3.setInterpolator(Interpolator.EASE_BOTH);

        transition.play();
        transition1.play();
        t3.play();
    }

}
