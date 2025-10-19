package com.example.untitledmoorhuhngame;

import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.io.InputStream;

public class HelloApplication extends Application {
    // Deklarácia premenných
    private Stage stage; // Hlavné okno aplikácie
    private final Group root = new Group(); // Skupina pre GUI prvky
    private int ApWidth = 1280; // Šírka okna
    private int ApHeight = 720; // Výška okna
    private final Scene scene = new Scene(root, ApWidth, ApHeight); // Scéna pre hru
    private boolean isFullScreen = false; // Stav pre full screen mód
    private final SoundManager sm = new SoundManager(); // Správca zvukov
    private final BackgroundManager bg = new BackgroundManager(); // Správca pozadia
    private final DynamicStyles ds = new DynamicStyles(); // Dynamické štýly
    private final DynamicUIManager ui = new DynamicUIManager(scene, this); // Dynamické UI
    private final MySprites ms = new MySprites(); // Správca sprite obrázkov
    private boolean genocide = false; // Malý spooky reskin secret

    @Override
    public void start(Stage primaryStage) {
        // Inicializácia okna a nastavenia
        stage = primaryStage;
        stage.setResizable(false); // Zamedzenie zmene veľkosti okna

        setIcon(); // Nastavenie ikony okna
        setupFullscreenToggle(); // Nastavenie pre prechod do fullscreen režimu
        setFullscreen(true); // Nastavenie na fullscreen pri spustení
        showMainMenu(); // Zobrazenie hlavného menu
    }

    public void setIcon() {
        // Nastavenie ikony okna
        try {
            String iconPath = "/assets/UntitledIcon.png"; // Correct path to the icon
            InputStream iconStream = getClass().getResourceAsStream(iconPath);
            if (iconStream == null) {
                System.err.println("Icon resource not found at: " + iconPath);
                return; // Skip setting icon if not found
            }
            Image icon = new Image(iconStream);
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    public void showMainMenu() {
        // Zobrazenie hlavného menu
        MainMenu mainMenu = new MainMenu(scene, this);
        root.getChildren().clear();
        root.getChildren().add(mainMenu.getPane());

        sm.stopEffect(); // Zastavenie zvukových efektov
        if (genocide) sm.playBackground(14); // Prehrávanie pozadia pre genocídu
        else sm.playBackground(10); // Prehrávanie základného pozadia

        stage.setTitle("Untitled MoorHuhn Game"); // Nastavenie názvu okna
        stage.setScene(scene); // Nastavenie scény
        stage.show(); // Zobrazenie okna
    }

    // Ďalšie metódy na zobrazenie rôznych obrazoviek (startScreen, startGame, showLeaderboard, showSettings, showCredits)
    public void startScreen() {
        StartScreen startScreen = new StartScreen(scene, this);
        root.getChildren().clear();
        root.getChildren().add(startScreen.getPane());

        stage.setScene(scene);
        stage.show();
    }

    public void startGame(String playerName) {
        Game game = new Game(scene, this, playerName);
        root.getChildren().clear();
        root.getChildren().add(game.getPane());

        sm.stopBackground(); // Zastavenie pozadia
        sm.stopEffect(); // Zastavenie efektov
        sm.playBackground(1); // Prehrávanie hry pozadia

        stage.setScene(scene);
        stage.show();
    }

    public void showLeaderboard() {
        Leaderboard leaderboard = new Leaderboard(scene, this);
        root.getChildren().clear();
        root.getChildren().add(leaderboard.getPane());

        stage.setScene(scene);
        stage.show();
    }

    public void showSettings() {
        Settings settings = new Settings(scene, this);
        root.getChildren().clear();
        root.getChildren().add(settings.getPane());

        stage.setScene(scene);
        stage.show();
    }

    public void showCredits() {
        Credits credits = new Credits(scene, this);
        root.getChildren().clear();
        root.getChildren().add(credits.getPane());

        stage.setScene(scene);
        stage.show();
    }

    public String getCurrentResolution() {
        // Vrátenie aktuálneho rozlíšenia podľa režimu
        if (isFullScreen()) { return "1920 x 1080 Fullscreen"; }
        else if (isBorderless()) { return "1920 x 1080 Borderless"; }
        else { return ApWidth + " x " + ApHeight + " Windowed"; }
    }

    public void setResolution(int width, int height) {
        // Nastavenie rozlíšenia
        this.ApWidth = width;
        this.ApHeight = height;
        stage.setWidth(width);
        stage.setHeight(height);
        centerWindow(); // Centrování okna
    }

    public void setFullscreen(boolean fullscreen) {
        // Nastavenie režimu fullscreen
        this.isFullScreen = fullscreen;
        stage.setFullScreen(fullscreen);
        stage.setFullScreenExitHint(""); // Skrytie výstupného nápisu
    }

    private void setupFullscreenToggle() {
        // Nastavenie pre prechod medzi fullscreen a windowed režimom
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                if (stage.isFullScreen()) {
                    setFullscreen(false); // Prechod do okienkového režimu
                    setResolution(ApWidth, ApHeight); // Nastavenie pôvodného rozlíšenia
                } else { setFullscreen(true); } // Prechod do fullscreen režimu
                event.consume(); // Spotrebovanie udalosti
            }
        });
    }

    // Getter metódy pre prístup k objektom
    public boolean isFullScreen() { return isFullScreen; }
    public boolean isBorderless() { return !getStage().isFullScreen() && getStage().getWidth() == 1920 && getStage().getHeight() == 1080; }
    public void centerWindow() { stage.centerOnScreen(); }
    public Stage getStage() { return stage; }
    public SoundManager getSoundManager() { return sm; }
    public BackgroundManager getBg() { return bg; }
    public DynamicUIManager getUi() { return ui; }
    public DynamicStyles getDs() { return ds; }
    public MySprites getMs() { return ms; }
    public boolean isGenocide() { return genocide; }
    public void setGenocide(boolean genocide) { this.genocide = genocide; }

    // Hlavná metóda na spustenie aplikácie
    public static void main(String[] args) { launch(args); }
}