package com.example.untitledmoorhuhngame;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class StartScreen {
    private final Group pane = new Group(); // Hlavný kontajner pre všetky komponenty
    private final DynamicUIManager ui; // UI manažér pre dynamické rozloženie
    private final DynamicStyles ds; // Štýly pre dynamické fonty a veľkosti
    private final HelloApplication app; // Odkaz na aplikáciu
    private Button startButton; // Tlačidlo na začiatok hry
    private TextField nameInput; // Textové pole pre zadanie mena
    private Label errorTitle; // Názov chyby
    private Label invalidNameMessage; // Správa o neplatnom mene
    private final Scene scene; // Scéna pre obrazovku
    private final BackgroundManager bg; // Správca pozadia
    private Rectangle backgroundOverlay; // Prekrytie na zobrazenie chýb

    // Konstruktory, ktorý inicializuje scénu
    public StartScreen(Scene scene, HelloApplication app) {
        this.scene = scene;
        this.app = app;
        this.ui = app.getUi();
        this.ds = app.getDs();
        this.bg = app.getBg();
        setBackground(); // Nastaví pozadie
        initialize(); // Inicializuje obrazovku
    }

    // Nastavenie pozadia
    private void setBackground() {
        bg.setBackground(scene, pane, 3); // Nastaví pozadie pomocou BackgroundManager
    }

    // Inicializácia prvkov na obrazovke
    private void initialize() {
        Label title = createTitle(); // Vytvorí titulok obrazovky

        VBox startScreen = new VBox(); // Vertikálne rozloženie pre prvky na obrazovke
        startScreen.setAlignment(Pos.CENTER); // Vycentrovanie prvkov
        startScreen.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild())); // Dynamické rozstupy

        nameInput = new TextField(); // Textové pole pre meno
        nameInput.setPromptText("Enter your username"); // Nastavenie predvoleného textu
        ui.bindControls(nameInput); // Dynamické nastavenie veľkosti a pozície

        // Ak je stlačený ENTER, spustí hru
        nameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleStartGame(nameInput, errorTitle, invalidNameMessage);
            }
        });

        // Poslucháč na zmenu textu v poli
        nameInput.textProperty().addListener((_, _, _) -> {
            errorTitle.setVisible(false); // Skryje chybovú hlášku
            invalidNameMessage.setText(""); // Vymaže správu o neplatnom mene
            backgroundOverlay.setOpacity(0); // Skryje prekrytie
            errorTitle.getParent().setVisible(false); // Skryje rodičovský kontajner chybovej hlášky
        });

        // Vytvorí a nastaví box s chybami
        VBox errorBox = createErrorBox(scene);

        // Tlačidlo na začiatok hry
        startButton = ui.createDynamicFontButton("Start Game", scene, this::handleEvent);

        startScreen.getChildren().addAll(title, nameInput, startButton, errorBox); // Pridá všetky prvky do obrazovky
        ui.bindPositionToCenter(startScreen); // Vycentruje obrazovku

        pane.getChildren().addAll(startScreen); // Pridá obrazovku do hlavného kontajnera
    }

    // Nastavenie akcie na kliknutie tlačidla "Start Game"
    private void handleEvent() {
        startButton.setOnMouseClicked(event -> {
            event.consume();
            handleStartGame(nameInput, errorTitle, invalidNameMessage);
        });
    }

    // Vytvorí titulok obrazovky
    private Label createTitle() {
        Label title = new Label("Enter your username");
        title.setTextFill(Color.BLACK); // Nastaví farbu textu
        ui.bindFontSize(title, ds.h1Size(), ds.Bold()); // Dynamická veľkosť a štýl textu
        title.setAlignment(Pos.CENTER); // Vycentruje titulok
        return title;
    }

    // Vytvorí box na zobrazenie chýb
    private VBox createErrorBox(Scene scene) {
        VBox errorBox = new VBox(); // Vertikálne rozloženie pre chyby
        errorBox.setAlignment(Pos.CENTER); // Vycentrovanie chýb
        errorBox.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild())); // Dynamické rozstupy
        errorBox.setVisible(false); // Skryje box s chybami predtým

        // Prekrytie pre chybové hlášky
        backgroundOverlay = new Rectangle();
        backgroundOverlay.setFill(Color.WHITE); // Nastaví biele pozadie
        backgroundOverlay.setOpacity(0); // Skryje prekrytie
        backgroundOverlay.setArcWidth(10); // Zaoblené rohy
        backgroundOverlay.setArcHeight(10);
        backgroundOverlay.setStroke(Color.BLACK); // Okraj
        backgroundOverlay.setStrokeWidth(2);

        // Poslucháč na zmenu veľkosti boxu
        errorBox.layoutBoundsProperty().addListener((_, _, newBounds) -> {
            backgroundOverlay.setWidth(newBounds.getWidth() + 20);
            backgroundOverlay.setHeight(newBounds.getHeight() + 20);
            backgroundOverlay.setX(newBounds.getMinX() - 10);
            backgroundOverlay.setY(newBounds.getMinY() - 10);
        });

        errorTitle = new Label("Error"); // Názov chyby
        errorTitle.setTextFill(Color.RED); // Nastavenie farby textu na červenú
        ui.bindFontSize(errorTitle, ds.h2Size(), ds.Bold()); // Dynamická veľkosť textu

        invalidNameMessage = new Label(); // Správa o neplatnom mene
        invalidNameMessage.setTextFill(Color.RED); // Farba textu
        ui.bindFontSize(invalidNameMessage, ds.h3Size(), ds.Normal()); // Dynamická veľkosť textu

        errorBox.getChildren().addAll(errorTitle, invalidNameMessage); // Pridá chyby do boxu

        Group errorGroup = new Group(backgroundOverlay, errorBox); // Skupina pre chybové hlášky
        VBox container = new VBox(errorGroup); // Kontajner pre chyby
        container.setAlignment(Pos.TOP_CENTER); // Vycentrovanie
        container.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild())); // Dynamické rozstupy

        return container;
    }

    // Spustenie hry po zadaní mena
    private void handleStartGame(TextField nameInput, Label errorTitle, Label invalidNameMessage) {
        String name = nameInput.getText().trim();
        String validationMessage = isValidName(name); // Overenie mena

        if (validationMessage == null) { // Ak je meno platné
            app.startGame(name); // Spustí hru
            errorTitle.getParent().setVisible(false); // Skryje chybovú hlášku
        } else { // Ak meno nie je platné
            app.getSoundManager().playEffect(13); // Prehranie zvukového efektu
            backgroundOverlay.setOpacity(0.7); // Zobrazí prekrytie s chybovou hláškou
            errorTitle.setVisible(true); // Zobrazí názov chyby
            invalidNameMessage.setText(validationMessage); // Zobrazí správu o neplatnom mene
            errorTitle.getParent().setVisible(true); // Zobrazí rodičovský kontajner
        }
    }

    // Overenie platnosti mena
    private String isValidName(String name) {
        if (name.isEmpty()) {return "Name is empty.";} // Prázdne meno
        else if (name.length() > 20) {return "Name is longer than 20 characters.";} // Meno je príliš dlhé
        return null; // Meno je platné
    }

    public Group getPane() {
        return pane; // Vráti hlavný kontajner obrazovky
    }
}