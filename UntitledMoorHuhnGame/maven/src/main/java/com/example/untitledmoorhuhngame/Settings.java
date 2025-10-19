package com.example.untitledmoorhuhngame;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class Settings {
    private final Group pane = new Group();  // Skupina pre všetky UI prvky
    private final Scene scene;               // Scéna, na ktorej sú zobrazené UI prvky
    private final HelloApplication app;      // Referencia na hlavnú aplikáciu
    private final DynamicUIManager ui;       // Správa dynamických UI prvkov
    private final DynamicStyles ds;          // Správa štýlov pre UI
    private final SoundManager sm;           // Správa zvukov
    private final BackgroundManager bg;     // Správa pozadia

    // Konstruktér nastavení, ktorý inicializuje scény a objekty
    public Settings(Scene scene, HelloApplication app) {
        this.scene = scene;
        this.app = app;
        this.ui = app.getUi();
        this.ds = app.getDs();
        this.sm = app.getSoundManager();
        this.bg = app.getBg();
        setBackground();  // Nastaví pozadie scény
        initialize();     // Inicializuje všetky UI prvky a nastavenia
    }

    // Nastavenie pozadia scény
    private void setBackground() { bg.setBackground(scene, pane, 5); }

    // Inicializácia všetkých prvkov v nastaveniach
    private void initialize() {
        Label titleLabel = new Label("Settings");  // Nadpis
        ui.bindFontSize(titleLabel, ds.h1Size(), ds.Bold());  // Nastavenie fontu nadpisu
        ui.bindToTopCenter(titleLabel);  // Umiestnenie nadpisu na stred hore

        // Vytvorenie nastavení pre zobrazenie a zvuk
        VBox displaySettingsBox = createDisplaySettings();
        VBox volumeSettingsBox = createVolumeSettings();

        // Kontajner pre nastavenia
        VBox settingsContainer = new VBox(displaySettingsBox, volumeSettingsBox);
        settingsContainer.setAlignment(Pos.CENTER);  // Umiestnenie na stred
        settingsContainer.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetParent()));  // Dynamické medzery medzi prvkami
        ui.bindPositionToCenter(settingsContainer);  // Umiestnenie kontajnera na stred

        Button backButton = ui.createDynamicFontButton("Back to Main Menu", scene, app::showMainMenu);  // Tlačidlo na návrat do hlavného menu
        ui.bindToBottomCenter(backButton);  // Umiestnenie tlačidla na spodok

        // Pridanie všetkých prvkov do hlavnej skupiny
        pane.getChildren().addAll(titleLabel, settingsContainer, backButton);
    }

    // Vytvorenie nastavení pre režim zobrazenia
    private VBox createDisplaySettings() {
        Label displayModeLabel = new Label("Display Mode:");  // Label pre režim zobrazenia
        ui.bindFontSize(displayModeLabel, ds.h2Size(), ds.Bold());  // Nastavenie fontu labelu

        // ComboBox pre výber režimu zobrazenia
        ComboBox<String> displayModeDropdown = new ComboBox<>();
        displayModeDropdown.getItems().addAll(
                "1920 x 1080 Fullscreen",
                "1920 x 1080 Borderless",
                "1760 x 990 Windowed",
                "1680 x 1050 Windowed",
                "1680 x 900 Windowed",
                "1366 x 768 Windowed",
                "1280 x 1024 Windowed",
                "1280 x 720 Windowed",
                "1128 x 634 Windowed",
                "1024 x 768 Windowed",
                "800 x 600 Windowed",
                "720 x 480 Windowed",
                "640 x 480 Windowed"
        );
        displayModeDropdown.setValue(app.getCurrentResolution());  // Nastaví aktuálne rozlíšenie

        // Po zmene režimu zobrazenia sa aktualizuje scéna
        app.getStage().fullScreenProperty().addListener((_, _, isNowFullScreen) -> {
            if (isNowFullScreen) { displayModeDropdown.setValue("1920 x 1080 Fullscreen"); }
            else { displayModeDropdown.setValue(app.getCurrentResolution()); }
        });

        // Klávesová skratka na prepínanie režimu zobrazenia
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                app.getStage().setFullScreen(!app.getStage().isFullScreen());
                event.consume();
            }
        });

        // Umiestnenie a formátovanie ComboBoxu
        ui.bindFontSize(displayModeDropdown, ds.h4Size(), ds.Normal());
        ui.bindControls(displayModeDropdown);
        displayModeDropdown.setOnAction(_ -> {
            updateDisplayMode(displayModeDropdown.getValue());
            displayModeDropdown.hide();
        });

        VBox displaySettingsBox = new VBox(displayModeLabel, displayModeDropdown);  // Vytvorenie boxu pre nastavenia zobrazenia
        displaySettingsBox.setAlignment(Pos.CENTER);
        displaySettingsBox.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild()));

        return displaySettingsBox;
    }

    // Vytvorenie nastavení pre zvuk
    private VBox createVolumeSettings() {
        Label volumeTitleLabel = new Label("Volume:");  // Nadpis pre zvuk
        ui.bindFontSize(volumeTitleLabel, ds.h2Size(), ds.Bold());

        // Slider pre nastavenie hlasitosti hudby na pozadí
        Slider bgVolumeSlider = new Slider(0, 100, app.getSoundManager().getBgMusicVolume() * 100);
        ui.bindControls(bgVolumeSlider);
        bgVolumeSlider.valueProperty().addListener((_, _, newVal) -> app.getSoundManager().setBgMusicVolume(newVal.doubleValue() / 100));

        Label bgVolumeLabel = new Label("Background");
        ui.bindFontSize(bgVolumeLabel, ds.h4Size(), ds.Normal());

        // Slider pre nastavenie hlasitosti zvukových efektov
        Slider effectsVolumeSlider = new Slider(0, 100, app.getSoundManager().getSoundEffectVolume() * 100);
        ui.bindControls(effectsVolumeSlider);
        effectsVolumeSlider.valueProperty().addListener((_, _, newVal) -> app.getSoundManager().setSoundEffectVolume(newVal.doubleValue() / 100));
        effectsVolumeSlider.valueProperty().addListener((_, _, _) -> {
            if (!sm.isEffectPlaying) { sm.playEffect(2); }  // Ak nehrá efekt, spustí efekt
        });

        Label effectsVolumeLabel = new Label("Effects");
        ui.bindFontSize(effectsVolumeLabel, ds.h4Size(), ds.Normal());

        VBox volumeSettingsBox = new VBox(volumeTitleLabel, bgVolumeLabel, bgVolumeSlider, effectsVolumeLabel, effectsVolumeSlider);  // Vytvorenie boxu pre nastavenia zvuku
        volumeSettingsBox.setAlignment(Pos.CENTER);
        volumeSettingsBox.spacingProperty().bind(scene.heightProperty().multiply(ui.getElementOffSetChild()));

        return volumeSettingsBox;
    }

    // Aktualizácia režimu zobrazenia na základe výberu
    private void updateDisplayMode(String selectedMode) {
        if ("1920 x 1080 Fullscreen".equals(selectedMode)) {
            Platform.runLater(() -> app.setFullscreen(true));
        } else if ("1920 x 1080 Borderless".equals(selectedMode)) {
            app.setFullscreen(false);
            app.getStage().setWidth(1920);
            app.getStage().setHeight(1080);
        } else {
            app.setFullscreen(false);
            String[] parts = selectedMode.split(" x ");
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1].split(" ")[0]);
            app.setResolution(width, height);
        }
        app.centerWindow();  // Umiestnenie okna na stred
    }

    // Získanie hlavného panela s nastaveniami
    public Group getPane() { return pane; }
}