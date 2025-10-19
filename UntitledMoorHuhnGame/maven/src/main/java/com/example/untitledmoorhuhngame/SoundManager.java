package com.example.untitledmoorhuhngame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.*;

public class SoundManager {

    // Enum pre definovanie typov médií (pozadia a efektov)
    public enum MediaType {
        BACKGROUND,
        EFFECT
    }

    // Mapy pre uloženie ciest k médiám, typov médií a hráčov médií
    private final Map<Integer, String> mediaPaths = new HashMap<>();
    private final Map<Integer, MediaType> mediaTypes = new HashMap<>();
    private final Map<Integer, MediaPlayer> mediaPlayers = new HashMap<>();
    private final List<MediaPlayer> activeEffectPlayers = new ArrayList<>();

    // Počiatočné nastavenie hlasitosti hudby a zvukových efektov
    private double bgMusicVolume = 0.1;
    private double soundEffectVolume = 0.1;
    public boolean isEffectPlaying = false;

    // Konstruktér, ktorý pridáva zvukové súbory
    public SoundManager() {
        addMedia(1, "/sounds/background1.wav", MediaType.BACKGROUND);
        addMedia(2, "/sounds/shotgun_shot.wav", MediaType.EFFECT);
        addMedia(3, "/sounds/shotgun_reload.wav", MediaType.EFFECT);
        addMedia(4, "/sounds/shotgun_shell_insert.wav", MediaType.EFFECT);
        addMedia(5, "/sounds/enemy_shot.wav", MediaType.EFFECT);
        addMedia(6, "/sounds/enemy_spawn.wav", MediaType.EFFECT);
        addMedia(7, "/sounds/warning.wav", MediaType.EFFECT);
        addMedia(8, "/sounds/tick.wav", MediaType.EFFECT);
        addMedia(9, "/sounds/game_over.wav", MediaType.EFFECT);
        addMedia(10, "/sounds/main_menu.wav", MediaType.BACKGROUND);
        addMedia(11, "/sounds/powerup_spawn.wav", MediaType.EFFECT);
        addMedia(12, "/sounds/powerup_shot.wav", MediaType.EFFECT);
        addMedia(13, "/sounds/error.wav", MediaType.EFFECT);
        addMedia(14, "/sounds/main_menu_alt.wav", MediaType.BACKGROUND);
        addMedia(15, "/sounds/end_game.wav", MediaType.BACKGROUND);
    }

    // Metóda pre pridanie mediálnych súborov do mapy
    private void addMedia(int id, String path, MediaType type) {
        mediaPaths.put(id, path);  // Ukladá cestu k médiu
        mediaTypes.put(id, type);  // Ukladá typ média (pozadie alebo efekt)
    }

    // Vytvorenie MediaPlayer objektu pre prehrávanie médií
    private MediaPlayer createMediaPlayer(String path, boolean loop) {
        try {
            // Načítanie médií z cesty
            Media media = new Media(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            return getMediaPlayer(loop, media);// Vracia MediaPlayer
        } catch (Exception e) {
            System.err.println("Error loading media: " + e.getMessage());  // Chyba pri načítaní médií
            return null;  // Ak sa nepodarí načítať, vráti null
        }
    }

    private MediaPlayer getMediaPlayer(boolean loop, Media media) {
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            // Ak je požiadavka na opakovanie (loop), nastaví sa opakovanie
            if (loop) {
                mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(javafx.util.Duration.ZERO));
            }
            // Nastavenie hlasitosti podľa typu (pozadie alebo efekt)
            mediaPlayer.setVolume(loop ? bgMusicVolume : soundEffectVolume);
        });

        mediaPlayer.setOnError(() -> System.err.println("Error occurred while trying to play media: " + mediaPlayer.getError()));
        return mediaPlayer;
    }

    // Prehrávanie pozadia na základe ID zvuku
    public void playBackground(int soundId) {
        MediaPlayer player = mediaPlayers.get(soundId);  // Získanie existujúceho MediaPlayer pre pozadie

        if (player == null) {
            String path = mediaPaths.get(soundId);  // Získanie cesty k zvuku
            if (path != null && mediaTypes.get(soundId) == MediaType.BACKGROUND) {
                player = createMediaPlayer(path, true);  // Vytvorenie MediaPlayer pre pozadie s opakovaním
                if (player != null) {
                    mediaPlayers.put(soundId, player);  // Uloženie MediaPlayer do mapy
                }
            }
        }

        // Ak je player validný a nie je práve prehrávaný, začne prehrávať
        if (player != null) {
            if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                player.setVolume(bgMusicVolume);  // Nastavenie hlasitosti pozadia
                player.play();  // Prehrávanie zvuku
            }
        } else {System.err.println("Background music with ID " + soundId + " not found or invalid.");}
    }

    // Zastavenie pozadia a uvoľnenie zdrojov
    public void stopBackground() {
        for (Map.Entry<Integer, MediaPlayer> entry : mediaPlayers.entrySet()) {
            int soundId = entry.getKey();
            if (mediaTypes.get(soundId) == MediaType.BACKGROUND) {
                MediaPlayer player = entry.getValue();
                if (player != null) {
                    player.stop();  // Zastavenie prehrávača
                    player.dispose();  // Uvoľnenie zdrojov
                    entry.setValue(null);  // Vymazanie MediaPlayer z mapy
                }
            }
        }
    }

    // Prehrávanie zvukového efektu na základe ID zvuku
    public void playEffect(int soundId) {
        String path = mediaPaths.get(soundId);  // Získanie cesty k zvuku
        if (path != null && mediaTypes.get(soundId) == MediaType.EFFECT) {
            MediaPlayer player = createMediaPlayer(path, false);  // Vytvorenie MediaPlayer pre efekt bez opakovania
            if (player != null) {
                player.setVolume(soundEffectVolume);  // Nastavenie hlasitosti efektu
                activeEffectPlayers.add(player);  // Pridanie do zoznamu aktívnych efektov
                isEffectPlaying = true;

                // Po skončení efektu uvoľníme zdroje
                player.setOnEndOfMedia(() -> {
                    player.dispose();
                    activeEffectPlayers.remove(player);  // Odstránenie prehrávača zo zoznamu
                    isEffectPlaying = !activeEffectPlayers.isEmpty();  // Ak je ešte nejaký efekt, nastavíme na true
                });

                player.setOnStopped(() -> isEffectPlaying = !activeEffectPlayers.isEmpty());  // Ak sa efekt zastaví, zistíme, či sú aktívne ďalšie
                player.play();  // Prehrávanie efektu
            }
        } else {System.err.println("Sound effect with ID " + soundId + " not found or invalid.");}
    }

    // Zastavenie všetkých zvukových efektov
    public void stopEffect() {
        for (MediaPlayer player : new ArrayList<>(activeEffectPlayers)) {
            player.stop();  // Zastavenie efektu
            player.dispose();  // Uvoľnenie zdrojov
            activeEffectPlayers.remove(player);  // Odstránenie prehrávača zo zoznamu
        }
        isEffectPlaying = false;  // Nastavenie, že žiadny efekt nehrá
    }

    // Nastavenie hlasitosti hudby na pozadí
    public void setBgMusicVolume(double volume) {
        this.bgMusicVolume = volume;

        // Upravenie hlasitosti pre všetky prehrávače pozadia
        for (Map.Entry<Integer, MediaPlayer> entry : mediaPlayers.entrySet()) {
            int soundId = entry.getKey();
            if (mediaTypes.get(soundId) == MediaType.BACKGROUND) {
                MediaPlayer player = entry.getValue();
                if (player != null) {player.setVolume(volume);}
            }
        }
    }

    // Nastavenie hlasitosti zvukových efektov
    public void setSoundEffectVolume(double volume) {
        this.soundEffectVolume = volume;
        for (MediaPlayer player : activeEffectPlayers) {player.setVolume(volume);}  // Nastavenie hlasitosti pre všetky aktívne efekty
    }

    // Získanie aktuálnej hlasitosti hudby na pozadí
    public double getBgMusicVolume() {return bgMusicVolume;}

    // Získanie aktuálnej hlasitosti zvukových efektov
    public double getSoundEffectVolume() {return soundEffectVolume;}
}