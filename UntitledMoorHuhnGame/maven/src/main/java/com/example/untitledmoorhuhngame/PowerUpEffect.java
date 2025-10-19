package com.example.untitledmoorhuhngame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum PowerUpEffect {
    // Definícia jednotlivých efektov power-upov s popisom
    INSTANT_RELOAD("Instant Reload!") {
        @Override
        public void apply(Game game) {
            // Aplikovanie efektu: okamžité načítanie zbrane
            game.getGun().reloadInstantly();
        }
    },
    INCREASE_MAX_AMMO("Max Ammo +") {
        @Override
        public void apply(Game game) {
            // Aplikovanie efektu: zvýšenie maximálneho počtu nábojov
            game.getGun().increaseMaxAmmo(1);
        }
    },
    INCREASE_RELOAD_SPEED("Reload Speed +") {
        @Override
        public void apply(Game game) {
            // Aplikovanie efektu: zvýšenie rýchlosti nabíjania zbrane
            game.getGun().increaseReloadSpeed(-10);
        }
    },
    INCREASE_SCORE_MULTIPLIER("Multiplier +"+(int)(getNumber()*100)+"%") {
        @Override
        public void apply(Game game) {
            // Aplikovanie efektu: zvýšenie multiplikátora skóre
            game.getSb().increaseScoreMultiplier(getNumber());
        }
    },
    ADD_TIME("+"+getSecs()+"sec") {
        @Override
        public void apply(Game game) {
            // Aplikovanie efektu: pridaný čas do herného času
            game.getTimer().addTime(getSecs());
        }
    };

    // Premenné pre základné hodnoty efektov
    private static final double number = 0.25;  // Hodnota pre multiplikátor skóre
    private static final int secs = 30;  // Počet sekúnd, ktoré sa pridajú pri "ADD_TIME" efekte

    private static double getNumber() {return number;}  // Získanie hodnoty pre multiplikátor skóre
    private static int getSecs() {return secs;}  // Získanie hodnoty pre počet pridaných sekúnd

    private final String description;  // Popis efektu power-upu

    // Konstruktér pre nastavenie popisu efektu
    PowerUpEffect(String description) {
        this.description = description;
    }

    // Získanie popisu efektu
    public String getDescription() {
        return description;
    }

    // Abstraktná metóda, ktorá sa implementuje pre každý konkrétny efekt
    public abstract void apply(Game game);

    // Metóda na získanie náhodného efektu power-upu, ktorý ešte nie je dosiahnutý
    public static PowerUpEffect getRandomEffect(Game game) {
        PowerUpEffect[] effects = values();  // Získanie všetkých efektov
        List<PowerUpEffect> availableEffects = new ArrayList<>();  // Zoznam dostupných efektov

        // Pre každý efekt skontrolujeme, či nie je maximálne nastavený
        for (PowerUpEffect effect : effects) {
            if (!effect.isMaxed(game)) {
                availableEffects.add(effect);  // Pridáme efekt do zoznamu, ak nie je maximálny
            }
        }

        // Ak nie sú dostupné žiadne efekty, vrátime null
        if (availableEffects.isEmpty()) {
            return null;
        }

        // Vrátime náhodne vybraný efekt
        Random random = new Random();
        return availableEffects.get(random.nextInt(availableEffects.size()));
    }

    // Metóda na kontrolu, či je efekt maximálne dosiahnutý (napríklad maximálny počet nábojov)
    public boolean isMaxed(Game game) {
        return switch (this) {
            case INCREASE_MAX_AMMO -> game.getGun().getMaxAmmo() >= 10;  // Ak je maximálny počet nábojov 10, efekt je maxovaný
            case INCREASE_RELOAD_SPEED -> game.getGun().getReloadTime() <= 50;  // Ak je rýchlosť nabíjania menej alebo rovná 50, efekt je maxovaný
            default -> false;  // Pre ostatné efekty nie je definované, že by mali byť maxované
        };
    }
}