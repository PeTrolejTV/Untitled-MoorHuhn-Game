package com.example.untitledmoorhuhngame;

import javafx.application.Platform;

import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

// Trieda zodpovedná za zobrazenie obrazovky konca hry
public class EndScreen {
    private final Group pane = new Group(); // Koreňový kontajner
    private final DynamicUIManager ui; // Manažér UI
    private final DynamicStyles ds; // Štýly
    private final SoundManager sm; // Manažér zvukov
    private final Scene scene; // Aktuálna scéna
    private final HelloApplication app; // Hlavná aplikácia
    private final VBox endMenu; // Hlavné menu na konci hry
    private final List<String> leaderboard; // Zoznam top skóre
    private final String playerName; // Meno hráča
    private Button restartButton; // Tlačidlo na reštart hry
    private final ScoreBoard sb; // Tabuľka skóre
    private final BackgroundManager bg; // Manažér pozadia
    private final Game game; // Hra

    // Konštruktor obrazovky konca hry
    public EndScreen(Game game) {
        this.game = game;
        this.scene = game.getScene();
        this.sb = game.getSb();
        this.app = game.getApp();
        this.ui = game.getApp().getUi();
        this.ds = game.getApp().getDs();
        this.sm = game.getApp().getSoundManager();
        this.bg = game.getApp().getBg();
        this.endMenu = new VBox(ui.getElementOffSetChild());
        this.leaderboard = game.getLeaderboard().getLeaderboard();
        this.playerName = game.getPlayerName();
        scene.setCursor(Cursor.DEFAULT);

        sounds(); // Nastavenie zvukov
        setBackground(); // Nastavenie pozadia
        setupEndMenu(); // Inicializácia menu
        decide(); // Logika pre rozhodovanie

        // Pridanie prekrytia a menu do koreňového kontajnera
        Rectangle backgroundOverlay = createBackgroundOverlay();
        pane.getChildren().addAll(backgroundOverlay, endMenu);
    }

    // Nastavuje pozadie
    private void setBackground() {
        bg.setBackground(scene, pane, 7);
    }

    // Nastavuje zvuky
    private void sounds() {
        sm.stopBackground();
        sm.stopEffect();
        sm.playEffect(9);
        sm.playBackground(15);
    }

    // Inicializácia a konfigurácia menu
    private void setupEndMenu() {
        endMenu.setAlignment(Pos.CENTER);
        ui.bindPositionToCenter(endMenu);

        addTitle(); // Pridanie titulku
        addSeparator(); // Pridanie oddeľovača
        addPlayerDetails(); // Detaily o hráčovi
        addSeparator();
        addLeaderboard(); // Zobrazenie rebríčka
        addSeparator();
        addButtons(); // Pridanie tlačidiel
    }

    // Pridáva titulok
    private void addTitle() {
        Label title = createLabel("Game Over", ds.h1Size(), ds.Bold(), Color.BLACK);
        endMenu.getChildren().add(title);
    }

    // Pridáva oddeľovač
    private void addSeparator() {
        Label separator = createLabel("────────────────", ds.h3Size(), ds.Normal(), Color.BLACK);
        endMenu.getChildren().add(separator);
    }

    // Pridáva detaily o hráčovi
    private void addPlayerDetails() {
        endMenu.getChildren().add(createLabel("Player", ds.h2Size(), ds.Bold(), Color.BLACK));
        endMenu.getChildren().add(createLabel(playerName, ds.h3Size(), ds.Normal(), Color.BLACK));
        endMenu.getChildren().add(createLabel("Current Score", ds.h3Size(), ds.Bold(), Color.BLACK));
        Label currentScoreText = createLabel(String.valueOf(sb.getScore()), ds.h3Size(), ds.Bold(), Color.RED);
        endMenu.getChildren().add(currentScoreText);
    }

    // Pridáva rebríček top hráčov
    private void addLeaderboard() {
        endMenu.getChildren().add(createLabel("Leaderboard Top 5", ds.h2Size(), ds.Bold(), Color.BLACK));
        for (int i = 0; i < 5; i++) {
            endMenu.getChildren().add(createLeaderboardEntryLabel(i));
        }
    }

    // Pridáva tlačidlá do menu
    private void addButtons() {
        HBox buttonBox = new HBox(ui.getElementOffSetParent() * 100);
        buttonBox.setAlignment(Pos.CENTER);

        restartButton = ui.createDynamicFontButton("Restart", scene, this::handleEvent);
        Button mainMenuButton = ui.createDynamicFontButton("Main Menu", scene, this::handleMenu);
        Button quitButton = ui.createDynamicFontButton("Quit", scene, Platform::exit);

        buttonBox.getChildren().addAll(restartButton, mainMenuButton, quitButton);
        endMenu.getChildren().add(buttonBox);
    }

    // Akcia pre návrat do hlavného menu
    private void handleMenu() {
        sm.stopBackground();
        app.showMainMenu();
    }

    // Akcia pre reštart hry
    private void handleEvent() {
        restartButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            app.startGame(playerName);
        });
        restartButton.setOnAction(_ -> app.startGame(playerName));
    }

    // Vytvára prekrytie pozadia
    private Rectangle createBackgroundOverlay() {
        Rectangle backgroundOverlay = new Rectangle();
        backgroundOverlay.setFill(Color.WHITE);
        backgroundOverlay.setOpacity(0.5);
        backgroundOverlay.widthProperty().bind(scene.widthProperty());
        backgroundOverlay.heightProperty().bind(scene.heightProperty());
        return backgroundOverlay;
    }

    // Vytvára label s textom a štýlom
    private Label createLabel(String text, double fontSize, String fontStyle, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        ui.bindFontSize(label, fontSize, fontStyle);
        return label;
    }

    // Vytvára položku v rebríčku
    private Label createLeaderboardEntryLabel(int index) {
        if (index >= leaderboard.size()) return new Label();

        String entry = leaderboard.get(index);
        String[] parts = entry.split(" - ");
        String position = (index + 1) + ". " + parts[0];

        Label scoreEntry = new Label(position + " - " + parts[1]);
        boolean isCurrentPlayer = parts[0].equals(playerName);
        String fontStyle = isCurrentPlayer ? ds.Bold() : ds.Normal();
        scoreEntry.setTextFill(isCurrentPlayer ? Color.RED : Color.BLACK);
        ui.bindFontSize(scoreEntry, ds.h3Size(), fontStyle);
        return scoreEntry;
    }

    // Nastavuje hodnotu pre "genocide" podľa skóre
    public void decide() {
        game.getApp().setGenocide(sb.getScore() > 15000);
    }

    // Vracia koreňový kontajner
    public Group getPane() {
        return pane;
    }
}