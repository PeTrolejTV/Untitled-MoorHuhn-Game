package com.example.untitledmoorhuhngame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.util.Duration;

import javafx.scene.Group;
import javafx.scene.Scene;

import java.util.*;

public class Game {
    private final Group pane = new Group();
    private final Random random = new Random();
    private final HelloApplication app;
    private final Scene scene;
    private Timeline t;
    private final String playerName;
    private Gun gun;
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final List<PowerUp> activePowerUps = new ArrayList<>();
    private Enemy enemy;
    private PowerUp powerUp;
    private Timer timer;
    private final Leaderboard leaderboard;
    private ScoreBoard sb;
    protected boolean isGameRunning;
    private final BackgroundManager bg;
    private final MySprites sprites;
    private BonusBoard bb;

    public Game(Scene scene, HelloApplication app, String playerName) {
        this.scene = scene;
        this.app = app;
        this.bg = app.getBg();
        this.playerName = playerName;
        this.leaderboard = new Leaderboard(scene, app);
        this.sprites = app.getMs();
        startGame();
    }

    private void startGame() {
        pane.getChildren().clear();
        setBackground();
        addTimer();
        addGun();
        addScoreBoard();
        addBonusBoard();
        enemySpawn();
        powerUpSpawn();
        isGameRunning = true;
    }

    private void setBackground() {bg.setBackground(scene, pane, 2);}

    private void enemySpawn() {
        int randomDelay = 100 + random.nextInt(2000);
        t = new Timeline(new KeyFrame(Duration.millis(randomDelay), _ -> {
            if (isGameRunning) {
                addEnemy();
                enemySpawn();
            }
        }));
        t.setCycleCount(1);
        t.play();
    }

    private void powerUpSpawn(){
        int randomDelay = 5000 + random.nextInt(10000);
        t = new Timeline(new KeyFrame(Duration.millis(randomDelay), _ -> {
            if (isGameRunning) {
                addPowerUp();
                powerUpSpawn();
            }
        }));
        t.setCycleCount(1);
        t.play();
    }

    private void addEnemy() {
        enemy = new Enemy(pane, this, gun, sprites);
        activeEnemies.add(enemy);
        pane.getChildren().add(enemy);
    }

    private void addPowerUp() {
        powerUp = new PowerUp(pane, this, sprites);
        activePowerUps.add(powerUp);
        pane.getChildren().add(powerUp);
    }

    private void addGun() {
        gun = new Gun(this);
        pane.getChildren().add(gun);
    }

    private void addTimer(){
        timer = new Timer(this);
        pane.getChildren().add(timer);
    }

    private void addScoreBoard() {
        sb = new ScoreBoard(this);
        pane.getChildren().add(sb);
    }

    private void addBonusBoard() {
        bb = new BonusBoard(this);
        pane.getChildren().add(bb);
    }

    private void displayEndScreen() {
        EndScreen endScreen = new EndScreen(this);
        pane.getChildren().add(endScreen.getPane());
    }

    public void endGame() {
        if (t != null) {t.stop(); t = null;}
        leaderboard.saveScoreToLeaderboard(playerName, sb.getScore());
        isGameRunning = false;

        gun.removeTimelines();
        if (enemy != null) enemy.removeAllEnemiesTimeline();
        if (powerUp != null) powerUp.removeAllPowerUpTimeline();
        timer.removeTimelines();
        sb.removeTimelines();

        pane.getChildren().clear();
        displayEndScreen();
    }

    public boolean isGameRunning() {return isGameRunning;}
    public HelloApplication getApp() {return app;}
    public String getPlayerName() {return playerName;}
    public Scene getScene() {return scene;}
    public Leaderboard getLeaderboard() {return leaderboard;}
    public ScoreBoard getSb() {return sb;}
    public List<Enemy> getActiveEnemies() {return activeEnemies;}
    public List<PowerUp> getActivePowerUps() {return activePowerUps;}
    public Gun getGun() {return gun;}
    public Timer getTimer() {return timer;}
    public BonusBoard getBb() {return bb;}
    public Group getPane() {return pane;}

}