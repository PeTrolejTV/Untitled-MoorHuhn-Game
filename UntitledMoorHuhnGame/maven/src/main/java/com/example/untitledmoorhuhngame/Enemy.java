package com.example.untitledmoorhuhngame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import java.util.Random;

public class Enemy extends ImageView {

    // Rýchlosť pohybu nepriateľa, náhodne určená
    private final int speed = 1 + new Random().nextInt(5);

    private final Group pane; // Skupina obsahujúca nepriateľa
    private final Random random = new Random(); // Pre generovanie náhodných hodnôt
    private final Game game; // Hra, do ktorej patrí tento nepriateľ
    private final Gun gun; // Zbraň hráča, ktorá môže streliť nepriateľa
    private final EnemyType type; // Typ nepriateľa (určuje jeho vlastnosti)
    private Timeline movementTimeline; // Časová os pre pohyb nepriateľa
    private Timeline popupTimeline; // Časová os pre zobrazenie skóre po zásahu
    private Timeline animationTimeline; // Časová os pre animáciu nepriateľa
    private boolean isShot = false; // Určuje, či bol nepriateľ zasiahnutý
    private final boolean randomValue = new Random().nextBoolean(); // Náhodná hodnota určuje smer pohybu
    private final SoundManager sm; // Správca zvukov
    private final ScoreBoard sb; // Tabuľka skóre
    private final MySprites sprites; // Správca animácií

    // Konštruktor, ktorý inicializuje nepriateľa
    public Enemy(Group pane, Game game, Gun gun, MySprites sprites) {
        this.pane = pane;
        this.game = game;
        this.gun = gun;
        this.type = EnemyType.values()[random.nextInt(EnemyType.values().length)];
        this.sm = game.getApp().getSoundManager();
        this.sb = game.getSb();
        this.sprites = sprites;

        setFitWidth(type.getSize()*2); // Nastavenie šírky nepriateľa podľa typu
        setFitHeight(type.getSize()); // Nastavenie výšky nepriateľa podľa typu
        spawn(); // Spustenie spawnovania nepriateľa

        setOnMouseClicked(this::gotShot); // Nastavenie reakcie na kliknutie myšou
        startMovement(); // Začatie pohybu nepriateľa
        decide(); // Rozhodnutie o animácii nepriateľa
    }

    // Funkcia na spawnovanie nepriateľa na náhodnej pozícii
    private void spawn(){
        double randomY = game.getScene().getHeight() / 8 + random.nextDouble() * (game.getScene().getHeight() - getFitHeight() - game.getScene().getHeight() / 4);

        if (!randomValue) {setLayoutX(-getFitWidth() * speed);} // Ak je náhodná hodnota false, nepriateľ prichádza zľava
        else {setLayoutX(game.getScene().getWidth() + getFitWidth() * speed);} // Ak je true, nepriateľ prichádza sprava
        setLayoutY(randomY); // Nastavenie náhodnej výšky pre nepriateľa

        sm.playEffect(6); // Prehrať zvuk pri objavení nepriateľa
    }

    // Funkcia na spustenie pohybu nepriateľa pomocou časovej osy
    private void startMovement() {
        if (movementTimeline == null) {
            movementTimeline = new Timeline(new KeyFrame(Duration.millis(10), _ -> move())); // Pohyb každých 10 ms
            movementTimeline.setCycleCount(Timeline.INDEFINITE); // Nekonečné cyklenie
            movementTimeline.play(); // Spustenie časovej osi
        }
    }

    // Funkcia na vykonanie pohybu nepriateľa
    private void move(){
        if (isShot) {setLayoutY(getLayoutY() + 1);} // Ak bol nepriateľ zasiahnutý, pohybuje sa nadol
        else {
            if (randomValue) {setLayoutX(getLayoutX() - speed);} // Pohyb doľava
            else {setLayoutX(getLayoutX() + speed);} // Pohyb doprava
        }
        removeWhenOutside(); // Odstráni nepriateľa, ak opustí obrazovku
    }

    // Funkcia na odstránenie nepriateľa, ak sa dostane mimo obrazovku
    private void removeWhenOutside() {
        if (getLayoutX() + getFitWidth() * speed < 0 ||
                getLayoutX() > game.getScene().getWidth() + getFitWidth() * speed ||
                getLayoutY() > game.getScene().getHeight()) {
            if (movementTimeline != null) {movementTimeline.stop(); movementTimeline = null;} // Zastavenie pohybovej animácie
            if (animationTimeline != null) {sprites.stopAnimation(animationTimeline); animationTimeline = null;} // Zastavenie animácie

            game.getActiveEnemies().remove(this); // Odstránenie nepriateľa zo zoznamu aktívnych nepriateľov
            Group parentPane = (Group) getParent(); // Získanie nadriadeného panela
            if (parentPane != null) { parentPane.getChildren().remove(this); } // Odstránenie nepriateľa z panela
        }
    }

    // Funkcia reagujúca na zásah nepriateľa
    private void gotShot(MouseEvent event) {
        if (game.isGameRunning()) { // Ak je hra v chode
            if (event.getButton() == MouseButton.PRIMARY && !isShot && gun.hasAmmo()) { // Ak je stlačené hlavné tlačidlo a zbraň má náboje
                isShot = true; // Označí nepriateľa za zasiahnutého

                sm.playEffect(5); // Prehrať zvuk pri zásahu
                int scoreGained = (int) (type.getScore() * speed * sb.getScoreMultiplier()); // Výpočet skóre za zničenie nepriateľa
                sb.updateScore(scoreGained); // Aktualizácia skóre
                popScore(event.getSceneX(), event.getSceneY(), scoreGained); // Zobrazenie skóre pri zásahu

                decide(); // Rozhodnutie o animácii nepriateľa po zásahu
            }
        }
    }

    // Funkcia na zobrazenie skóre, ktoré sa objaví pri zásahu nepriateľa
    private void popScore(double x, double y, int score) {
        String scoreStr = "+" + score; // Formátovanie skóre ako reťazec

        Text scoreText = new Text(scoreStr); // Vytvorenie textového objektu pre skóre
        scoreText.setFill(Color.YELLOW); // Nastavenie farby textu na žltú
        scoreText.setFont(new Font("Arial Bold", 30)); // Nastavenie fontu a veľkosti

        Text outlineText1 = new Text(scoreStr); // Vytvorenie textu pre obrys skóre
        outlineText1.setFill(Color.BLACK); // Nastavenie farby obrysu na čiernu
        outlineText1.setFont(scoreText.getFont()); // Použitie rovnakého fontu pre obrys

        Text outlineText2 = new Text(scoreStr); // Druhý obrysový text
        outlineText2.setFill(Color.BLACK); // Nastavenie farby obrysu na čiernu
        outlineText2.setFont(scoreText.getFont()); // Použitie rovnakého fontu

        double screenWidth = game.getScene().getWidth(); // Získanie šírky obrazovky

        if (x + scoreText.getLayoutBounds().getWidth() > screenWidth - 50) {
            x = screenWidth - 50 - scoreText.getLayoutBounds().getWidth(); // Ak je text príliš široký, posuň ho tak, aby sa zmestil na obrazovku
        }

        scoreText.setX(x); // Nastavenie pozície textu
        scoreText.setY(y);

        outlineText1.setX(x + 1); // Nastavenie pozície prvého obrysu
        outlineText1.setY(y + 1);

        outlineText2.setX(x - 1); // Nastavenie pozície druhého obrysu
        outlineText2.setY(y - 1);

        DropShadow dropShadow = new DropShadow(); // Vytvorenie efektu tieňa
        dropShadow.setRadius(3.0); // Nastavenie polomeru tieňa
        dropShadow.setOffsetX(2); // Nastavenie horizontálneho posunu tieňa
        dropShadow.setOffsetY(2); // Nastavenie vertikálneho posunu tieňa
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.6)); // Nastavenie farby tieňa
        scoreText.setEffect(dropShadow); // Aplikovanie tieňa na text

        pane.getChildren().addAll(outlineText1, outlineText2, scoreText); // Pridanie textu a obrysov do panela

        popupTimeline = new Timeline( // Časová os pre odstránenie skóre po 1 sekunde
                new KeyFrame(Duration.seconds(1), _ -> pane.getChildren().removeAll(outlineText1, outlineText2, scoreText))
        );
        popupTimeline.setCycleCount(1); // Jeden cyklus
        popupTimeline.play(); // Spustenie časovej osi
    }

    // Funkcia na rozhodovanie o animácii nepriateľa podľa jeho stavu (živý, mŕtvy)
    private void decide() {
        if (isShot) {
            playAnimation("enemy_dead"); // Ak je nepriateľ mŕtvy, prehrá sa animácia smrti
            setFitWidth(type.getSize()); // Nastavenie veľkosti na typ
            setFitHeight(type.getSize());
        }
        else if (randomValue) {playAnimation("enemy_flying_left");} // Ak nepriateľ letí doľava, prehrá sa príslušná animácia
        else {playAnimation("enemy_flying_right");} // Ak nepriateľ letí doprava, prehrá sa príslušná animácia
    }

    // Funkcia na prehratie animácie nepriateľa
    private void playAnimation(String animationKey) {
        if (animationTimeline != null) {sprites.stopAnimation(animationTimeline);} // Zastavenie predchádzajúcej animácie
        animationTimeline = sprites.playAnimation(this, animationKey, 250); // Prehratie novej animácie s rýchlosťou 250 ms
    }

    // Funkcia na odstránenie všetkých časových osí pre nepriateľa
    private void removeTimelines() {
        if (movementTimeline != null) {movementTimeline.stop(); movementTimeline = null;} // Zastavenie pohybovej animácie
        if (popupTimeline != null) {popupTimeline.stop(); popupTimeline = null;} // Zastavenie animácie skóre
        if (animationTimeline != null) {sprites.stopAnimation(animationTimeline); animationTimeline = null;} // Zastavenie animácie
    }

    // Funkcia na odstránenie všetkých nepriateľov z obrazovky a zastavenie ich časových osí
    public void removeAllEnemiesTimeline() {
        for (Enemy enemy : game.getActiveEnemies()) {
            enemy.removeTimelines(); // Odstránenie časových osí pre každý aktívny nepriateľ
        }
    }
}