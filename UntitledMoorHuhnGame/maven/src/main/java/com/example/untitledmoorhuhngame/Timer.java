package com.example.untitledmoorhuhngame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Timer extends Label {
    // Počiatočný čas (3 minúty)
    private final int startingTime = 180;
    // Aktuálny čas, ktorý sa bude odpočítavať
    private int currentTime = startingTime;
    // Timeline pre odpočítavanie času
    private Timeline timerTimeline;
    // Timeline pre blikajúci efekt
    private Timeline blinkTimeline;
    // Rôzne pomocné objekty (manažéri a štýly)
    private final SoundManager sm;
    private final DynamicUIManager ui;
    private final DynamicStyles ds;
    private final Game game;
    // Popisovač pre zobrazenie času
    private Label timerLabel;

    // Konstruktor, ktorý nastaví hru a inicializuje časovač
    public Timer(Game game) {
        this.game = game;
        this.ui = game.getApp().getUi();
        this.ds = game.getApp().getDs();
        this.sm = game.getApp().getSoundManager();
        startTimer(); // Spustí časovač
    }

    // Funkcia na spustenie časovača
    public void startTimer() {
        // Inicializujeme Label pre zobrazenie času
        timerLabel = new Label(formatTime(currentTime));
        timerLabel.setFont(Font.font("System", FontWeight.NORMAL, game.getScene().getHeight() * ds.h1Size()));
        timerLabel.setTextFill(Color.BLACK);
        // Pripájame label na horný stred obrazovky
        ui.bindToTopCenter(timerLabel);

        // Pridávame listener na zmenu veľkosti scény, aby sme aktualizovali font podľa veľkosti okna
        game.getScene().heightProperty().addListener((_, _, newValue) -> {
            FontWeight weight = currentTime <= 10 ? FontWeight.BOLD : FontWeight.NORMAL;
            timerLabel.setFont(Font.font("System", weight, newValue.doubleValue() * ds.h1Size()));
        });

        // Pridáme label do hlavného panela
        game.getPane().getChildren().add(timerLabel);

        // Nastavenie timeline pre blikajúci efekt (viditeľnosť)
        blinkTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), _ -> timerLabel.setVisible(false)),
                new KeyFrame(Duration.seconds(1), _ -> timerLabel.setVisible(true))
        );
        blinkTimeline.setCycleCount(Animation.INDEFINITE); // Opakuje sa neustále

        // Nastavenie timeline pre odpočítavanie času
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            currentTime--; // Znížime aktuálny čas
            timerLabel.setText(formatTime(currentTime)); // Aktualizujeme zobrazenie času

            // Ak je čas menší alebo rovný 10, spustíme blikajúci efekt
            if (currentTime <= 10 && currentTime > 0) {
                startBlinkingEffect();
            }

            // Ak čas dosiahne nulu, zastavíme časovač a ukončíme hru
            if (currentTime <= 0) {
                removeTimelines(); // Zastavíme obe timelines
                game.endGame(); // Ukončíme hru
            }
        }));
        timerTimeline.setCycleCount(Animation.INDEFINITE); // Opakuje sa neustále
        timerTimeline.play(); // Spustíme odpočítavanie
    }

    // Formátovanie času do "hh:mm:ss" alebo "mm:ss" formátu
    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else if (seconds > 10) {
            return String.format("%02d", seconds);
        } else {
            return String.format("%01d", seconds);
        }
    }

    // Funkcia na zastavenie všetkých timelines
    public void removeTimelines() {
        if (timerTimeline != null) {
            timerTimeline.stop();
            timerTimeline = null;
        }
        if (blinkTimeline != null) {
            blinkTimeline.stop();
            blinkTimeline = null;
        }
    }

    // Funkcia na pridanie času k aktuálnemu času
    public void addTime(int seconds) {
        currentTime += seconds;

        // Ak je čas väčší ako 10, zastavíme blikajúci efekt
        if (currentTime > 10) {
            stopBlinkingEffect();
        }

        // Aktualizujeme zobrazenie času
        timerLabel.setText(formatTime(currentTime));
    }

    // Spustenie blikajúceho efektu
    private void startBlinkingEffect() {
        timerLabel.setTextFill(Color.RED); // Nastavíme červenú farbu
        timerLabel.setFont(Font.font("System", FontWeight.BOLD, game.getScene().getHeight() * ds.h1Size()));

        // Pridáme žltý obrys pre efekt
        DropShadow outline = new DropShadow();
        outline.setColor(Color.YELLOW);
        outline.setOffsetX(0);
        outline.setOffsetY(0);
        outline.setRadius(5.0);
        timerLabel.setEffect(outline);

        sm.playEffect(8); // Pustíme zvukový efekt

        // Ak blink timeline nie je spustený, spustíme ho
        if (blinkTimeline.getStatus() != Animation.Status.RUNNING) {
            blinkTimeline.play();
        }
    }

    // Zastavenie blikajúceho efektu
    private void stopBlinkingEffect() {
        // Ak je blikajúci efekt aktívny, zastavíme ho
        if (blinkTimeline != null && blinkTimeline.getStatus() == Animation.Status.RUNNING) {
            blinkTimeline.stop();
        }

        // Obnovíme pôvodné nastavenia pre timer label
        timerLabel.setTextFill(Color.BLACK);
        timerLabel.setFont(Font.font("System", FontWeight.NORMAL, game.getScene().getHeight() * ds.h1Size()));
        timerLabel.setEffect(null); // Zrušíme efekt
        timerLabel.setVisible(true); // Urobíme label opäť viditeľným
    }
}