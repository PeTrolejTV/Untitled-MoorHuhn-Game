package com.example.untitledmoorhuhngame;

import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ScoreBoard extends Label {
    private double scoreMultiplier = 1.0;  // Multiplikátor skóre (používa sa na zvýšenie skóre)
    private final DynamicUIManager ui;  // Objekt na správu dynamických UI nastavení
    private final DynamicStyles ds;  // Objekt pre štýly (napr. veľkosti písma)
    private final Group pane;  // Hlavný kontajner, kde bude umiestnený score board
    private final HBox scoreContainer;  // Kontajner pre zobrazenie skóre
    private final Label scoreLabel;  // Label pre text "Score:"
    private final Label scoreValueLabel;  // Label pre zobrazenie aktuálneho skóre
    private ScaleTransition scaleAnimation;  // Animácia pre zväčšovanie skóre
    private RotateTransition rotateAnimation;  // Animácia pre otáčanie skóre
    private Timeline colorAnimation;  // Animácia pre zmenu farby skóre

    private int score = 0;  // Počiatočné skóre

    public ScoreBoard(Game game) {
        this.pane = game.getPane();  // Získanie hlavného panela hry
        this.ui = game.getApp().getUi();  // Získanie UI manažéra pre dynamické prvky
        this.ds = game.getApp().getDs();  // Získanie štýlového manažéra
        this.scoreContainer = new HBox(10);  // Vytvorenie kontajnera pre skóre s medzerami 10 px
        this.scoreLabel = new Label("Score:");  // Label pre text "Score:"
        this.scoreValueLabel = new Label(String.valueOf(score));  // Label pre zobrazenie skóre
        setupScoreBoard();  // Inicializácia skóre panela
    }

    private void setupScoreBoard() {
        scoreValueLabel.setTextFill(Color.WHITE);  // Nastavenie farby písma pre skóre

        // Vytvorenie efektu tieňa pre text skóre
        DropShadow outlineEffect = new DropShadow();
        outlineEffect.setColor(Color.BLACK);  // Nastavenie farby tieňa
        outlineEffect.setRadius(2);  // Nastavenie polomeru tieňa
        outlineEffect.setSpread(1.0);  // Nastavenie šírenia tieňa
        scoreValueLabel.setEffect(outlineEffect);  // Aplikovanie efektu na skóre

        // Dynamické viazanie veľkosti písma pre scoreLabel a scoreValueLabel
        ui.bindFontSize(scoreLabel, ds.h2Size(), ds.Bold());
        ui.bindFontSize(scoreValueLabel, ds.h2Size(), ds.Bold());
        ui.bindToTopLeft(scoreContainer);  // Umiestnenie kontajnera na vrchľavý ľavý roh

        // Pridanie textových labelov do kontajnera a kontajnera na hlavný panel
        scoreContainer.getChildren().addAll(scoreLabel, scoreValueLabel);
        pane.getChildren().add(scoreContainer);
    }

    // Metóda na aktualizáciu skóre
    public void updateScore(int points) {
        score += (int) (points * scoreMultiplier);  // Pridanie bodov s použitím multiplikátora
        scoreValueLabel.setText(String.valueOf(score));  // Aktualizácia textu skóre
        animateScoreValue();  // Spustenie animácie skóre
    }

    // Metóda na animáciu hodnoty skóre (zväčšovanie, otáčanie, zmena farby)
    private void animateScoreValue() {
        removeTimelines();  // Zastavenie predchádzajúcich animácií

        // Animácia zväčšenia textu skóre
        scaleAnimation = new ScaleTransition(Duration.millis(200), scoreValueLabel);
        scaleAnimation.setFromX(1.0);
        scaleAnimation.setFromY(1.0);
        scaleAnimation.setToX(1.5);
        scaleAnimation.setToY(1.5);
        scaleAnimation.setCycleCount(2);  // Zopakovať animáciu dvakrát
        scaleAnimation.setAutoReverse(true);  // Automatické vrátenie do pôvodného stavu
        scaleAnimation.play();  // Spustenie animácie

        // Otáčanie textu skóre pre skóre > 1000
        if (score > 1000) {
            double rotationSpeed = Math.max(100, 500 - (score / 10));  // Nastavenie rýchlosti otáčania
            rotateAnimation = new RotateTransition(Duration.millis(rotationSpeed), scoreValueLabel);
            rotateAnimation.setFromAngle(-5);  // Počiatočný uhol
            rotateAnimation.setToAngle(5);  // Konečný uhol
            rotateAnimation.setCycleCount(RotateTransition.INDEFINITE);  // Neustále otáčanie
            rotateAnimation.setAutoReverse(true);  // Automatické vrátenie otáčania
            rotateAnimation.play();  // Spustenie animácie
        }

        // Zmena farby textu skóre na základe hodnoty skóre
        if (score >= 1000 && score < 5000) {
            scoreValueLabel.setTextFill(Color.GOLD);  // Zlatá farba
        }
        if (score >= 5000 && score < 10000) {
            scoreValueLabel.setTextFill(Color.RED);  // Červená farba
        }
        if (score >= 10000 && score < 15000) {
            scoreValueLabel.setTextFill(Color.DARKRED);  // Temnočervená farba
        }

        // Dynamická zmena farby skóre pri skóre >= 15000
        if (score >= 15000) {
            colorAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0.1), _ -> scoreValueLabel.setTextFill(Color.RED)),
                    new KeyFrame(Duration.seconds(0.2), _ -> scoreValueLabel.setTextFill(Color.ORANGE)),
                    new KeyFrame(Duration.seconds(0.3), _ -> scoreValueLabel.setTextFill(Color.YELLOW)),
                    new KeyFrame(Duration.seconds(0.4), _ -> scoreValueLabel.setTextFill(Color.GREEN)),
                    new KeyFrame(Duration.seconds(0.5), _ -> scoreValueLabel.setTextFill(Color.BLUE)),
                    new KeyFrame(Duration.seconds(0.6), _ -> scoreValueLabel.setTextFill(Color.PURPLE))
            );
            colorAnimation.setCycleCount(Animation.INDEFINITE);  // Neustála zmena farieb
            colorAnimation.play();  // Spustenie animácie
        }
    }

    // Metóda na zastavenie všetkých animácií
    public void removeTimelines() {
        if (scaleAnimation != null) {scaleAnimation.stop(); scaleAnimation = null;}
        if (rotateAnimation != null) {rotateAnimation.stop(); rotateAnimation = null;}
        if (colorAnimation != null) {colorAnimation.stop(); colorAnimation = null;}
    }

    // Získanie aktuálneho skóre
    public int getScore() {
        return score;
    }

    // Získanie aktuálneho multiplikátora skóre
    public double getScoreMultiplier() {
        return scoreMultiplier;
    }

    // Zvýšenie multiplikátora skóre o zadané percento
    public void increaseScoreMultiplier(double percentage) {
        scoreMultiplier += percentage;
    }
}