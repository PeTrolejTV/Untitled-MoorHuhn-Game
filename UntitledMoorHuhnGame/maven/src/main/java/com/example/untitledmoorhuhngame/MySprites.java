package com.example.untitledmoorhuhngame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MySprites {

    // Mapa pre uchovávanie animácií pre rôzne stavy spritov
    private final Map<String, List<Image>> spriteAnimations;

    // Konstruktér, ktorý inicializuje mapu animácií a načíta sprity
    public MySprites() {
        spriteAnimations = new HashMap<>();
        loadSprites();  // Načítanie spritov pri inicializácii
    }

    // Metóda na načítanie všetkých potrebných animácií
    private void loadSprites() {
        // Načítanie spritov pre rôzne animácie nepriateľov a power-upov
        spriteAnimations.put("enemy_flying_right", List.of(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/right/Untitled_right1.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/right/Untitled_right2.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/right/Untitled_right3.png")).toExternalForm())
        ));
        spriteAnimations.put("enemy_flying_left", List.of(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/left/Untitled_left1.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/left/Untitled_left2.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/left/Untitled_left3.png")).toExternalForm())
        ));
        spriteAnimations.put("enemy_dead", List.of(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/dead/Untitled_dead1.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/enemy/dead/Untitled_dead2.png")).toExternalForm())
        ));

        spriteAnimations.put("powerup_active", List.of(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/powerup/active/Untitled_powerup_active1.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/powerup/active/Untitled_powerup_active2.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/powerup/active/Untitled_powerup_active3.png")).toExternalForm())
        ));
        spriteAnimations.put("powerup_destroyed", List.of(
                new Image(Objects.requireNonNull(getClass().getResource("/assets/powerup/destroyed/Untitled_powerup_destroyed1.png")).toExternalForm()),
                new Image(Objects.requireNonNull(getClass().getResource("/assets/powerup/destroyed/Untitled_powerup_destroyed2.png")).toExternalForm())
        ));
    }

    // Metóda na prehrávanie animácie pre daný kľúč
    public Timeline playAnimation(ImageView imageView, String key, int frameDuration) {
        List<Image> frames = spriteAnimations.get(key);

        // Ak pre daný kľúč neexistujú žiadne obrázky, vyvolá výnimku
        if (frames == null || frames.isEmpty()) {
            throw new IllegalArgumentException("No frames found for key: " + key);
        }

        Timeline timeline = new Timeline();
        final int[] currentIndex = {0};

        // Nastavenie snímok, ktoré sa pravidelne menia pre animáciu
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(frameDuration), _ -> {
                    imageView.setImage(frames.get(currentIndex[0]));  // Nastavenie aktuálneho obrázka na ImageView
                    currentIndex[0] = (currentIndex[0] + 1) % frames.size();  // Posun na ďalšiu snímku (cyklicky)
                })
        );

        // Nastavenie cyklu na neobmedzené prehrávanie animácie
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();  // Spustenie animácie

        return timeline;  // Vrátenie Timeline objektu na kontrolu alebo zastavenie animácie
    }

    // Metóda na zastavenie animácie
    public void stopAnimation(Timeline animation) {
        if (animation != null) {
            animation.stop();  // Zastavenie prehrávania animácie
        }
    }
}