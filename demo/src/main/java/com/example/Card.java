package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Card {
    private double startX;
    private double dragStartX;
    private StackPane card;

    public Card() {
        this.card = createCard();
        // startX = card.getX();
        setUpMouseEvents();

        Timeline t = new Timeline(new KeyFrame(Duration.millis(1000 / 60), e -> updateAngle()));
        t.setCycleCount(Animation.INDEFINITE); // loop forever
        t.play();
    }

    public StackPane getCard() {
        return card;
    }

    public StackPane createCard() {
        int w = 300;
        int h = 400;

        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(20);
        r.setArcHeight(20);
        r.setFill(Color.DARKSLATEGRAY);

        VBox vb = new VBox();
        vb.setMaxWidth(w);
        vb.setMaxHeight(h);
        vb.setMinWidth(w);
        vb.setMinHeight(h);
        vb.getChildren().addAll(new Label("Name"), new Label("Instrument"));

        card = new StackPane();
        card.getStyleClass().add("border");
        card.setMaxWidth(w);
        card.setMaxHeight(h);
        card.setMinWidth(w);
        card.setMinHeight(h);

        card.getChildren().addAll(r, vb);

        // card.setcinte

        return card;
    }

    private double getAngle() {
        return (card.getTranslateX()) * -0.1;
    }

    private void updateAngle() {
        card.setRotate(getAngle());
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
        updateAngle();
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
                updateAngle();
            }
        });

        transition.play();
    }
}
