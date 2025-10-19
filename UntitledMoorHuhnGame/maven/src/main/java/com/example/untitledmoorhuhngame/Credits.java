package com.example.untitledmoorhuhngame;

import javafx.geometry.Pos;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Credits {
    // Trieda na zobrazenie obrazovky s kreditmi hry

    private final Group pane = new Group(); // Skupina, ktorá obsahuje všetky UI prvky na tejto obrazovke
    private final HelloApplication app; // Odkaz na hlavné aplikáciu
    private final Scene scene; // Scéna, na ktorej sa zobrazuje Credits
    private final DynamicUIManager ui; // Pomocník na dynamické spravovanie UI
    private final DynamicStyles ds; // Štýly pre UI
    private final BackgroundManager bg; // Správca pozadia

    public Credits(Scene scene, HelloApplication app) {
        // Inicializuje triedu Credits s referenciami na scénu a aplikáciu
        this.scene = scene;
        this.app = app;
        this.ui = app.getUi();
        this.ds = app.getDs();
        this.bg = app.getBg();
        setBackground(); // Nastavenie pozadia
        initialize(); // Inicializácia ostatných prvkov
    }

    private void setBackground() {
        // Nastaví pozadie pomocou BackgroundManageru
        bg.setBackground(scene, pane, 6);
    }

    private void initialize() {
        // Inicializuje všetky UI prvky na obrazovke Credits
        VBox creditsBox = createCreditsBox(); // Vytvorí box so všetkými kreditmi

        Label titleLabel = new Label("Credits"); // Nadpis
        ui.bindFontSize(titleLabel, ds.h1Size(), ds.Bold()); // Dynamické prispôsobenie veľkosti písma
        ui.bindToTopCenter(titleLabel); // Zarovná na vrch stredu obrazovky

        VBox paddedContainer = new VBox(creditsBox); // Zabalenie creditsBox do kontajnera s odsadením
        paddedContainer.setPadding(new javafx.geometry.Insets(20)); // Nastavenie okrajov
        paddedContainer.setAlignment(Pos.CENTER); // Zarovnanie na stred

        Rectangle backgroundOverlay = createBackgroundOverlay(paddedContainer); // Vytvorí biely polopriehľadný podklad

        Button backButton = ui.createDynamicFontButton("Back to Main Menu", scene, app::showMainMenu); // Tlačidlo na návrat do menu
        ui.bindToBottomCenter(backButton); // Zarovná tlačidlo na spodok stredu obrazovky

        // Pridá všetky prvky na obrazovku
        pane.getChildren().addAll(backgroundOverlay, paddedContainer, titleLabel, backButton);

        ui.bindPositionToCenter(paddedContainer); // Zarovná paddedContainer do stredu
    }

    private Rectangle createBackgroundOverlay(VBox container) {
        // Vytvorí polopriehľadný biely obdlžník za obsahom
        Rectangle backgroundOverlay = new Rectangle();
        backgroundOverlay.setFill(Color.WHITE);
        backgroundOverlay.setOpacity(0.7); // Nastaví priehľadnosť

        // Prepojenie veľkosti a pozície obdlžníka s kontajnerom
        backgroundOverlay.widthProperty().bind(container.widthProperty());
        backgroundOverlay.heightProperty().bind(container.heightProperty());
        backgroundOverlay.layoutXProperty().bind(container.layoutXProperty());
        backgroundOverlay.layoutYProperty().bind(container.layoutYProperty());

        return backgroundOverlay;
    }

    private VBox createCreditsBox() {
        // Vytvorí box, ktorý obsahuje všetky informácie o kreditoch
        VBox creditsBox = new VBox();
        creditsBox.setAlignment(Pos.CENTER); // Zarovná všetky deti na stred
        creditsBox.setSpacing(ui.getElementOffSetChild()); // Nastaví medzery medzi položkami

        // Pridá jednotlivé role a mená do creditsBox
        creditsBox.getChildren().addAll(
                createRoleAndName("Programmer", "Peter Majerik", ""),
                createRoleAndName("Music", "Main Menu", "https://www.youtube.com/watch?v=g5j-6edFJKc"),
                createRoleAndName("", "Main Menu Alt", "https://www.youtube.com/watch?v=BntrNnlKydE"),
                createRoleAndName("", "End Game", "https://www.youtube.com/watch?v=e0h09RpeMSY"),
                createRoleAndName("", "Game", "https://www.youtube.com/watch?v=7oCPuvwFeI0"),
                createRoleAndName("Graphics", "Peter Majerik", ""),
                createRoleAndName("Sound Effects", "Enemy Spawn & No Ammo Warning", "https://www.youtube.com/watch?v=1yxN0FXE0ME"),
                createRoleAndName("", "Enemy Shot", "https://www.youtube.com/watch?v=xEvJmI7uJ-g"),
                createRoleAndName("", "Gun Shell Insert", "https://www.youtube.com/watch?v=AGVz9zoWNrQ"),
                createRoleAndName("", "Gun Reload and Shoot", "https://www.youtube.com/watch?v=Yt0_CsnJqqA"),
                createRoleAndName("", "Clock Ticking", "https://www.youtube.com/watch?v=8VUgLhAvN0U"),
                createRoleAndName("", "Game Over", "https://www.youtube.com/watch?v=s5B188EFlvE"),
                createRoleAndName("", "PowerUp Spawn", "https://www.youtube.com/watch?v=UNZjvty4v2A"),
                createRoleAndName("", "PowerUp Shot", "https://www.youtube.com/watch?v=4XnhAfRyrGI"),
                createRoleAndName("", "Error", "https://www.youtube.com/watch?v=EeA_Y0FSv5Q"),
                createRoleAndName("Special Thanks", "Me & Chat", "")
        );

        return creditsBox;
    }

    private VBox createRoleAndName(String role, String name, String content) {
        // Vytvorí box s rolou, menom a voliteľným obsahom (napr. odkazom)
        VBox roleAndNameBox = new VBox();
        roleAndNameBox.setAlignment(Pos.CENTER_LEFT); // Zarovná na ľavý stred
        roleAndNameBox.setSpacing(5); // Nastaví medzeru medzi prvkami

        if (!role.isEmpty()) {
            // Pridá názov role, ak existuje
            Label roleLabel = createText(role, ds.h3Size(), ds.Bold() + ds.Underline());
            roleAndNameBox.getChildren().add(roleLabel);
        }

        // Pridá meno a obsah
        Label nameLabel = createText(name, ds.h4Size(), ds.Bold() + ds.Italic());
        Label contentLabel = new Label(content);
        ui.bindFontSize(contentLabel, ds.h4Size(), ds.Underline() + "-fx-text-fill: blue;");

        if (!content.isEmpty()) {
            // Nastaví odkaz, ak existuje obsah
            contentLabel.setCursor(javafx.scene.Cursor.HAND);
            contentLabel.setOnMouseClicked(_ -> app.getHostServices().showDocument(content));
        }

        HBox nameAndContentBox = new HBox(10, nameLabel, contentLabel); // Spojí meno a obsah do jedného riadku
        nameAndContentBox.setAlignment(Pos.CENTER_LEFT);

        roleAndNameBox.getChildren().add(nameAndContentBox);

        return roleAndNameBox;
    }

    private Label createText(String text, double fontSize, String fontWeight) {
        // Vytvorí Label so zadaným textom, veľkosťou a štýlom písma
        Label label = new Label(text);
        ui.bindFontSize(label, fontSize, fontWeight);
        return label;
    }

    public Group getPane() {
        // Vráti celú skupinu UI prvkov
        return pane;
    }
}
