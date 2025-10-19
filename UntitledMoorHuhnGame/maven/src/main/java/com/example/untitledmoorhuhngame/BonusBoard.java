package com.example.untitledmoorhuhngame;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BonusBoard extends Label {
    // Trieda zodpovedná za zobrazenie bonusov v hre
    private final Game game; // Odkaz na hru, kde sa nachádza BonusBoard
    private final DynamicUIManager ui; // Pomáha dynamicky spravovať UI
    private final DynamicStyles ds; // Poskytuje štýly pre UI
    private final Gun gun; // Odkaz na zbraň hráča
    private final ScoreBoard scoreBoard; // Odkaz na tabuľku skóre
    private Label multiplierLabel; // Zobrazuje multiplikátor skóre
    private Label ammoLabel; // Zobrazuje aktuálnu hodnotu nábojov
    private Label reloadSpeedLabel; // Zobrazuje rýchlosť nabíjania

    public BonusBoard(Game game) {
        // Inicializácia objektu s odkazom na hru a jej komponenty
        this.game = game;
        this.ui = game.getApp().getUi();
        this.ds = game.getApp().getDs();
        this.gun = game.getGun();
        this.scoreBoard = game.getSb();
        createBonusBoard(); // Vytvorenie a nastavenie komponentov bonusovej tabule
    }

    private void createBonusBoard() {
        // Vytvorí a nastaví štítky s informáciami o bonusoch
        multiplierLabel = new Label(formatMultiplier(scoreBoard.getScoreMultiplier()));
        ammoLabel = new Label(formatAmmo(gun.getMaxAmmo()));
        reloadSpeedLabel = new Label(formatReloadSpeed(gun.getReloadTime()));

        // Nastaví veľkosť písma dynamicky podľa výšky scény
        Font font = Font.font("System", FontWeight.NORMAL, game.getScene().getHeight() * ds.h3Size());
        multiplierLabel.setFont(font);
        ammoLabel.setFont(font);
        reloadSpeedLabel.setFont(font);

        // Nastaví farbu textu na čiernu
        multiplierLabel.setTextFill(Color.BLACK);
        ammoLabel.setTextFill(Color.BLACK);
        reloadSpeedLabel.setTextFill(Color.BLACK);

        // Vytvorí vertikálne usporiadaný kontajner a pridá štítky
        VBox bonusBoardVBox = new VBox();
        bonusBoardVBox.getChildren().addAll(multiplierLabel, ammoLabel, reloadSpeedLabel);

        // Umiestni kontajner do pravého horného rohu obrazovky
        ui.bindToTopRight(bonusBoardVBox);

        // Pridá kontajner na hernú scénu
        game.getPane().getChildren().add(bonusBoardVBox);

        // Pridá poslucháča na zmenu výšky scény a dynamicky upravuje veľkosť písma
        game.getScene().heightProperty().addListener((_, _, newValue) -> {
            Font fontUpdate = Font.font("System", FontWeight.NORMAL, newValue.doubleValue() * ds.h3Size());
            multiplierLabel.setFont(fontUpdate);
            ammoLabel.setFont(fontUpdate);
            reloadSpeedLabel.setFont(fontUpdate);
        });
    }

    private String formatMultiplier(double multiplier) {
        // Formátuje multiplikátor skóre na reťazec (napr. "200% - Multiplier")
        return String.format("%d%% - Multiplier", (int) (multiplier * 100));
    }

    private String formatAmmo(int maxAmmo) {
        // Formátuje počet nábojov na reťazec (napr. "10/10 - Ammo")
        return String.format("%d/10 - Ammo", maxAmmo);
    }

    private String formatReloadSpeed(int reloadTime) {
        // Formátuje čas nabíjania na reťazec (napr. "500ms - Reload Speed")
        return String.format("%dms - Reload Speed", reloadTime);
    }

    public void update() {
        // Aktualizuje hodnoty na bonusovej tabuli (volá sa počas hry)
        multiplierLabel.setText(formatMultiplier(scoreBoard.getScoreMultiplier()));
        ammoLabel.setText(formatAmmo(gun.getMaxAmmo()));
        reloadSpeedLabel.setText(formatReloadSpeed(gun.getReloadTime()));
    }
}