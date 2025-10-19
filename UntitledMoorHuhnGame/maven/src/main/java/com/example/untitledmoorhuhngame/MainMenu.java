package com.example.untitledmoorhuhngame;

import javafx.application.Platform;

import javafx.geometry.Pos;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// Trieda zodpovedná za zobrazenie hlavného menu v hre
public class MainMenu {
    private final Group pane = new Group(); // Hlavný kontajner pre všetky UI elementy
    private final VBox menu = new VBox(); // Vertikálne usporiadanie tlačidiel v menu
    private final DynamicUIManager ui; // Dynamické spravovanie UI prvkov
    private final DynamicStyles ds; // Štýly pre dynamické nastavenie fontov
    private final HelloApplication app; // Aplikácia, ktorá spravuje celkové nastavenie hry
    private final Scene scene; // Scéna, do ktorej sa pridávajú UI prvky
    private final BackgroundManager bg; // Spravovanie pozadia

    // Konstruktor, ktorý nastavuje potrebné objekty a vytvára menu
    public MainMenu(Scene scene, HelloApplication app) {
        this.scene = scene;
        this.app = app;
        this.ui = app.getUi();
        this.ds = app.getDs();
        this.bg = app.getBg();
        decideBackground(); // Rozhodnutie, aké pozadie sa použije
        createTitle(); // Vytvorenie titulku
        createButtons(); // Vytvorenie tlačidiel
    }

    // Vytvorenie tlačidiel pre rôzne funkcie menu
    private void createButtons() {
        menu.setAlignment(Pos.CENTER); // Tlačidlá sú centrované
        menu.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild())); // Dynamické nastavenie vzdialenosti medzi tlačidlami

        // Dynamicky vytvorené tlačidlá s priradenými funkciami
        Button startButton = ui.createDynamicFontButton("Start Game", scene, app::startScreen);
        Button leaderboardButton = ui.createDynamicFontButton("Leaderboard", scene, app::showLeaderboard);
        Button settingsButton = ui.createDynamicFontButton("Settings", scene, app::showSettings);
        Button creditsButton = ui.createDynamicFontButton("Credits", scene, app::showCredits);
        Button quitButton = ui.createDynamicFontButton("Quit", scene, Platform::exit);

        menu.getChildren().addAll(startButton, leaderboardButton, settingsButton, creditsButton, quitButton); // Pridanie tlačidiel do menu
        ui.bindPositionToCenter(menu); // Umiestnenie menu do stredu scény

        pane.getChildren().addAll(menu); // Pridanie menu do hlavného panela
    }

    // Vytvorenie titulku hry s dynamickým nastavením fontu a efektov
    private void createTitle() {
        Label title = new Label("Untitled MoorHuhn Game"); // Názov hry

        // Nastavenie farby textu podľa režimu (genocída alebo normálny režim)
        String color;
        if (app.isGenocide()) {color = "-fx-text-fill: linear-gradient(to bottom, #FF4500, #DC143C);";}
        else {color = "-fx-text-fill: linear-gradient(to bottom, #FFD700, #FF8C00);";}

        ui.bindFontSize(title, ds.h1Size()*1.5, ds.Bold() + color); // Nastavenie veľkosti a štýlu písma
        title.setAlignment(Pos.CENTER); // Text je centrovaný

        // Pridanie tieňa na text (externý a vnútorný)
        DropShadow dropShadow = new DropShadow();
        if (app.isGenocide()) {dropShadow.setColor(Color.rgb(255, 255, 255, 0.7));}
        else dropShadow.setColor(Color.rgb(50, 50, 50, 0.7));
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.2);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        innerShadow.setRadius(5);
        innerShadow.setOffsetX(2);
        innerShadow.setOffsetY(2);

        dropShadow.setInput(innerShadow); // Aplikovanie vnútorného tieňa na vonkajší tieň
        title.setEffect(dropShadow); // Nastavenie efektu na titul

        menu.getChildren().add(title); // Pridanie titulku do menu
    }

    // Rozhodnutie o pozadí na základe režimu hry (genocída alebo nie)
    private void decideBackground(){
        if (app.isGenocide()) setAltBackground(); // Iné pozadie pre genocídny režim
        else setBackground(); // Normálne pozadie
    }

    // Nastavenie bežného pozadia
    private void setBackground() {bg.setBackground(scene, pane, 1);}
    // Nastavenie alternatívneho pozadia pre genocídny režim
    private void setAltBackground() {bg.setBackground(scene, pane, 8);}

    // Getter pre hlavný panel
    public Group getPane() {return pane;}
}