package com.example.untitledmoorhuhngame;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import java.io.InputStream;

public class Gun extends ImageView {

    private int maxAmmo = 4;
    private int reloadTime = 300;

    private int currentAmmo = maxAmmo;
    private boolean isReloading = false;
    private boolean outOfAmmoMessage = false;

    private ImageView[] ammoImages = new ImageView[maxAmmo];
    private final Image ammoImage;
    private final Game game;
    private final DynamicUIManager ui;
    private final DynamicStyles ds;
    private Timeline reloadTimeline;
    private Timeline warningSound;
    private FadeTransition blinkAnimation;
    private final Scene scene;
    private final Group pane;
    private Label outOfAmmoText;
    private final SoundManager sm;

    public Gun(Game game) {
        this.scene = game.getScene();
        this.pane = game.getPane();
        this.game = game;
        this.ui = game.getApp().getUi();
        this.ds = game.getApp().getDs();
        this.sm = game.getApp().getSoundManager();

        // Load ammo image
        String imagePath = "/assets/UntitledAmmo.png";
        try (InputStream imageStream = getClass().getResourceAsStream(imagePath)) {
            if (imageStream == null) {
                throw new IllegalArgumentException("Ammo image resource not found at: " + imagePath);
            }
            ammoImage = new Image(imageStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load ammo image: " + e.getMessage(), e);
        }

        scene.setCursor(Cursor.CROSSHAIR);

        initializeAmmoImages();
        setMouseActions();
        updateAmmoDisplay();

        scene.widthProperty().addListener((_, _, _) -> updateAmmoDisplay());
        scene.heightProperty().addListener((_, _, _) -> updateAmmoDisplay());
    }

    private void initializeAmmoImages() {
        for (int i = 0; i < maxAmmo; i++) {
            ImageView bullet = new ImageView(ammoImage);
            bullet.setPreserveRatio(true);
            pane.getChildren().add(bullet);
            ammoImages[i] = bullet;
        }
        updateAmmoDisplay();
    }

    private void updateAmmoDisplay() {
        double bulletSize = scene.getHeight() * (ds.h4Size() * 5);
        double spacing = bulletSize / 6;
        double xStart = scene.getWidth() - bulletSize + spacing;
        double yPosition = scene.getHeight() - bulletSize - spacing;

        for (int i = 0; i < maxAmmo; i++) {
            ImageView bullet = ammoImages[i];
            bullet.setFitWidth(bulletSize);
            bullet.setFitHeight(bulletSize);
            bullet.setVisible(i < currentAmmo);

            bullet.setLayoutX(xStart - i * (bulletSize - spacing));
            bullet.setLayoutY(yPosition);
        }

        if (outOfAmmoMessage) showOutOfAmmoMessage();
    }

    private void setMouseActions() {
        scene.setOnMouseClicked(event -> {
            if (game.isGameRunning()) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    shoot();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    reload();
                }
            }
        });

        scene.setOnKeyPressed(e -> {
            if (game.isGameRunning() && e.getCode() == KeyCode.R) reload();
        });
    }

    private void shoot() {
        if (currentAmmo > 0) {
            sm.playEffect(2);
            removeOutOfAmmoText();
            if (isReloading && reloadTimeline != null) {
                reloadTimeline.stop();
                isReloading = false;
            }
            currentAmmo--;
            updateAmmoDisplay();
            outOfAmmoMessage = false;
        }
        if (currentAmmo == 0 && !isReloading) {
            outOfAmmoMessage = true;
            showOutOfAmmoMessage();
        }
    }

    private void reload() {
        if (isReloading || currentAmmo == maxAmmo) return;

        isReloading = true;
        outOfAmmoMessage = false;
        removeOutOfAmmoText();
        sm.playEffect(3);

        reloadTimeline = new Timeline(new KeyFrame(Duration.millis(reloadTime), _ -> {
            if (currentAmmo < maxAmmo) {
                currentAmmo++;
                updateAmmoDisplay();
                sm.playEffect(4);
            }
            if (currentAmmo == maxAmmo) {
                reloadTimeline.stop();
                isReloading = false;
            }
        }));
        reloadTimeline.setCycleCount(maxAmmo - currentAmmo);
        reloadTimeline.play();
    }

    private void showOutOfAmmoMessage() {
        if (outOfAmmoText != null) return;

        outOfAmmoText = new Label("Out of ammo! Reload with R or Right-Click.");
        ui.bindFontSize(outOfAmmoText, ds.h2Size(), ds.Bold() + "-fx-effect: dropshadow(gaussian, yellow, 3, 1, 0, 0);");
        outOfAmmoText.setTextFill(Color.RED);

        ui.bindToBottomRight(outOfAmmoText);
        pane.getChildren().add(outOfAmmoText);

        warningSound = new Timeline(new KeyFrame(Duration.seconds(0.5), _ -> sm.playEffect(7)));
        warningSound.setCycleCount(Timeline.INDEFINITE);
        warningSound.play();

        blinkAnimation = new FadeTransition(Duration.seconds(0.5), outOfAmmoText);
        blinkAnimation.setFromValue(1.0);
        blinkAnimation.setToValue(0.0);
        blinkAnimation.setCycleCount(Timeline.INDEFINITE);
        blinkAnimation.setAutoReverse(true);
        blinkAnimation.play();
    }

    private void removeOutOfAmmoText() {
        if (outOfAmmoText != null) {
            removeTimelines();
            pane.getChildren().remove(outOfAmmoText);
            outOfAmmoText = null;
        }
    }

    public void removeTimelines() {
        if (blinkAnimation != null) {
            blinkAnimation.stop();
            blinkAnimation = null;
        }
        if (warningSound != null) {
            warningSound.stop();
            warningSound = null;
        }
        if (reloadTimeline != null) {
            reloadTimeline.stop();
            reloadTimeline = null;
        }
    }

    public boolean hasAmmo() {
        return currentAmmo > 0;
    }

    public void reloadInstantly() {
        currentAmmo = maxAmmo + 1;
    }

    public void increaseMaxAmmo(int amount) {
        if (maxAmmo < 10) {
            int oldMaxAmmo = maxAmmo;
            maxAmmo += amount;

            ImageView[] newAmmoImages = new ImageView[maxAmmo];

            System.arraycopy(ammoImages, 0, newAmmoImages, 0, oldMaxAmmo);

            for (int i = oldMaxAmmo; i < maxAmmo; i++) {
                ImageView bullet = new ImageView(ammoImage);
                bullet.setPreserveRatio(true);
                newAmmoImages[i] = bullet;
                pane.getChildren().add(bullet);
            }
            ammoImages = newAmmoImages;
        }
    }

    public void increaseReloadSpeed(int seconds) {
        if (reloadTime > 50) {
            reloadTime += seconds;
        }
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getReloadTime() {
        return reloadTime;
    }
}