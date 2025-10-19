package com.example.untitledmoorhuhngame;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.Random;

public class PowerUp extends ImageView {
    // Vytvárame potrebné inštancie pre generovanie náhodných hodnôt, animácie a zvuk
    private final Random random = new Random();
    private final Group pane;  // Pane, do ktorého bude pridaný PowerUp
    private final Game game;  // Referencia na aktuálnu hru
    private Timeline movementTimeline;  // Animácia pohybu power-upu
    private Timeline popupTimeline;  // Animácia vyskakovacieho okna
    private Timeline animationTimeline;  // Animácia zobrazenia power-upu
    private boolean isShot = false;  // Označenie, či bol power-up zostrelený
    private final SoundManager sm;  // Manažér zvukov
    private final MySprites sprites;  // Sprity pre animácie
    private final int speed = 3;  // Rýchlosť pohybu power-upu
    private final BonusBoard bb;  // Ovládanie bonusových bodov

    // Konstruktér pre inicializáciu PowerUp
    public PowerUp(Group pane, Game game, MySprites sprites) {
        this.pane = pane;
        this.game = game;
        this.sm = game.getApp().getSoundManager();
        this.sprites = sprites;
        this.bb = game.getBb();

        setPowerUp();  // Nastavenie vzhľadu power-upu
        spawn();  // Spawnovanie power-upu na obrazovke
        setOnMouseClicked(this::gotShot);  // Nastavenie kliknutia na power-up

        startMovement();  // Začiatok pohybu power-upu
    }

    // Začiatok pohybu power-upu pomocou animácie
    private void startMovement() {
        if (movementTimeline == null) {
            movementTimeline = new Timeline(new KeyFrame(Duration.millis(10), _ -> move()));  // Pohyb na vertikálnej osi
            movementTimeline.setCycleCount(Timeline.INDEFINITE);  // Cyklický pohyb
            movementTimeline.play();
        }
    }

    // Nastavenie spritu pre power-up
    private void setPowerUp() {
        playAnimation("powerup_active");  // Spustenie animácie power-upu
        setFitWidth(50);  // Nastavenie šírky
        setFitHeight(50);  // Nastavenie výšky
    }

    // Spawnovanie power-upu na náhodnom mieste
    private void spawn() {
        double startX = random.nextDouble() * (game.getScene().getWidth() - getFitWidth());

        setLayoutX(startX);  // Nastavenie počiatočnej X pozície
        setLayoutY(game.getScene().getHeight() + getFitHeight() * speed);  // Nastavenie počiatočnej Y pozície mimo obrazovky

        sm.playEffect(11);  // Prehrávanie zvuku pri spawnovaní
    }

    // Pohyb power-upu na obrazovke
    private void move() {
        if (!isShot) {
            setLayoutY(getLayoutY() - speed);  // Pohyb hore, pokiaľ nebol zostrelený
        } else {
            setLayoutY(getLayoutY() + 1);  // Pokiaľ bol zostrelený, pohybuje sa nadol
        }
        removeWhenOutside();  // Odstránenie power-upu, ak je mimo obrazovky
    }

    // Odstránenie power-upu, keď sa dostane mimo obrazovky
    private void removeWhenOutside() {
        if (getLayoutX() + getFitWidth() < 0 ||
                getLayoutX() > game.getScene().getWidth() ||
                getLayoutY() < -getFitHeight() ||
                getLayoutY() > game.getScene().getHeight() + getFitHeight() * speed) {
            removeTimelines();  // Zastavenie všetkých animácií

            game.getActivePowerUps().remove(this);  // Odstránenie power-upu zo zoznamu aktívnych power-upov

            Group parentPane = (Group) getParent();
            if (parentPane != null) {
                parentPane.getChildren().remove(this);  // Odstránenie z rodičovského pane
            }
        }
    }

    // Spracovanie, keď hráč klikne na power-up
    private void gotShot(MouseEvent event) {
        if (game.isGameRunning() && event.getButton() == MouseButton.PRIMARY && !isShot && game.getGun().hasAmmo()) {
            isShot = true;

            sm.playEffect(12);  // Prehrávanie zvuku pri zasiahnutí
            playAnimation("powerup_destroyed");  // Prehrávanie animácie zničenia
            applyRandomEffect();  // Aplikovanie náhodného efektu
            bb.update();  // Aktualizácia bonusovej tabuľky
        }
    }

    // Aplikovanie náhodného efektu pri zasiahnutí power-upu
    private void applyRandomEffect() {
        PowerUpEffect effect = PowerUpEffect.getRandomEffect(game);

        if (effect == null) {
            showPopup("No more power-ups available!");  // Zobrazenie správy, ak už nie sú žiadne ďalšie power-upy
            return;
        }

        effect.apply(game);  // Aplikovanie vybraného efektu
        showPopup(effect.getDescription());  // Zobrazenie pop-up správy s popisom efektu
    }

    // Zobrazenie pop-up správy na obrazovke
    private void showPopup(String message) {
        Label popup = new Label(message);
        popup.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        popup.setTextFill(Color.WHITE);
        popup.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 10; -fx-background-radius: 5;");

        double screenWidth = game.getScene().getWidth();

        double popupX;
        if (getLayoutX() + getFitWidth() > screenWidth - 100) {
            popupX = getLayoutX() - popup.getWidth() - 100;  // Ak je pop-up príliš vpravo, presunieme ho naľavo
        } else {
            popupX = getLayoutX() + getFitWidth() / 2 - popup.getWidth() / 2;  // Centrovanie pop-upu
        }

        double popupY = (getLayoutY() < 50)
                ? getLayoutY() + getFitHeight() + 10  // Ak je power-up blízko hornej časti obrazovky, pop-up sa zobrazuje pod ním
                : getLayoutY() - 30;  // Inak nad ním

        popup.setLayoutX(popupX);
        popup.setLayoutY(popupY);
        pane.getChildren().add(popup);

        // Fade animácia pre pop-up, aby zmizol po 2 sekundách
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), popup);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(_ -> pane.getChildren().remove(popup));
        fadeOut.play();

        popupTimeline = new Timeline(new KeyFrame(Duration.seconds(2), _ -> {}));
        popupTimeline.setCycleCount(1);
        popupTimeline.play();
    }

    // Prehrávanie animácie power-upu
    private void playAnimation(String animationKey) {
        if (animationTimeline != null) {
            sprites.stopAnimation(animationTimeline);  // Zastavenie predchádzajúcej animácie
        }
        animationTimeline = sprites.playAnimation(this, animationKey, 500);  // Spustenie novej animácie
    }

    // Odstránenie všetkých animácií, keď je power-up odstránený
    private void removeTimelines() {
        if (movementTimeline != null) {movementTimeline.stop(); movementTimeline = null;}  // Zastavenie pohybovej animácie
        if (popupTimeline != null) {popupTimeline.stop(); popupTimeline = null;}  // Zastavenie animácie pop-upu
        if (animationTimeline != null) {sprites.stopAnimation(animationTimeline); animationTimeline = null;}  // Zastavenie animácie spritu
    }

    // Odstránenie všetkých timeline animácií pre všetky power-upy
    public void removeAllPowerUpTimeline() {
        for (PowerUp powerUp : game.getActivePowerUps()) {
            powerUp.removeTimelines();
        }
    }
}