package com.example.untitledmoorhuhngame;

import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class BackgroundManager {

    // Mapy pre ukladanie načítaných obrázkov a ich ImageView (zobrazení)
    private final Map<Integer, Image> backgroundImages = new HashMap<>();
    private final Map<Integer, ImageView> backgroundImageViews = new HashMap<>();

    // Poslucháče zmien rozmerov scény (šírka a výška)
    private ChangeListener<? super Number> widthListener;
    private ChangeListener<? super Number> heightListener;

    // Nastavenie pozadia pre danú scénu a skupinu na základe ID
    public void setBackground(Scene scene, Group pane, int id) {
        // Vymaže aktuálne pozadie (ak existuje)
        clearBackground(scene, pane);

        // Načíta alebo získa pozadie podľa ID
        Image bgImage = backgroundImages.computeIfAbsent(id, this::loadBackground);
        if (bgImage == null) {
            // Ak sa nepodarí načítať obrázok, vypíše chybu a skončí
            System.err.println("Failed to load background with ID " + id);
            return;
        }

        // Načíta alebo vytvorí ImageView pre dané ID pozadia
        ImageView bgImageView = backgroundImageViews.computeIfAbsent(id, _ -> {
            ImageView imageView = new ImageView(bgImage);
            imageView.setPreserveRatio(false); // Obrázok sa roztiahne na celý priestor
            return imageView;
        });

        // Nastaví rozmer ImageView podľa aktuálnej veľkosti scény
        bgImageView.setFitWidth(scene.getWidth());
        bgImageView.setFitHeight(scene.getHeight());

        // Odstráni predchádzajúce poslucháče zmien rozmerov
        removeListeners(scene);

        // Pridá nové poslucháče na aktualizáciu šírky a výšky ImageView pri zmene veľkosti scény
        widthListener = (_, _, newValue) -> bgImageView.setFitWidth(newValue.doubleValue());
        heightListener = (_, _, newValue) -> bgImageView.setFitHeight(newValue.doubleValue());
        scene.widthProperty().addListener(widthListener);
        scene.heightProperty().addListener(heightListener);

        // Pridá ImageView ako prvý element do skupiny (zabezpečí, že je na pozadí)
        pane.getChildren().addFirst(bgImageView);
    }

    // Vyčistenie pozadia zo scény a skupiny
    public void clearBackground(Scene scene, Group pane) {
        // Odstráni poslucháče zmien rozmerov
        removeListeners(scene);
        // Odstráni všetky ImageView zodpovedajúce uloženým pozadiam zo skupiny
        pane.getChildren().removeIf(node -> node instanceof ImageView && backgroundImageViews.containsValue(node));
        backgroundImageViews.clear(); // Vyčistí mapu ImageView
    }

    // Načíta obrázok pozadia na základe ID (vráti null, ak ID neexistuje alebo cesta je chybná)
    private Image loadBackground(int id) {
        // Výber cesty k obrázku podľa ID pomocou switch
        String path = switch (id) {
            case 1 -> "/assets/backgrounds/UntitledMainMenu.jpg";
            case 2 -> "/assets/backgrounds/UntitledGame.jpg";
            case 3 -> "/assets/backgrounds/UntitledNameInput.jpg";
            case 4 -> "/assets/backgrounds/UntitledLeaderboard.jpg";
            case 5 -> "/assets/backgrounds/UntitledSettings.jpg";
            case 6 -> "/assets/backgrounds/UntitledCredits.jpg";
            case 7 -> "/assets/backgrounds/UntitledEndGame.jpg";
            case 8 -> "/assets/backgrounds/UntitledMainMenuAlt.jpg";
            default -> {
                System.err.println("Unknown background ID: " + id);
                yield null; // Ak ID neexistuje, vráti null
            }
        };

        if (path == null) return null; // Ak neexistuje cesta, ukončí

        // Pokúsi sa načítať zdrojový súbor podľa cesty
        try {
            var resource = getClass().getResourceAsStream(path);
            if (resource == null) {
                // Vypíše chybu, ak zdroj neexistuje
                System.err.println("Resource not found: " + path);
                return null;
            }
            // Načíta a vráti obrázok
            return new Image(resource);
        } catch (Exception e) {
            System.err.println("Failed to load image at: " + path + ", error: " + e.getMessage());
            return null;
        }
    }

    // Odstráni poslucháče zmien rozmerov zo scény
    private void removeListeners(Scene scene) {
        if (widthListener != null) {
            scene.widthProperty().removeListener(widthListener);
            widthListener = null;
        }
        if (heightListener != null) {
            scene.heightProperty().removeListener(heightListener);
            heightListener = null;
        }
    }
}