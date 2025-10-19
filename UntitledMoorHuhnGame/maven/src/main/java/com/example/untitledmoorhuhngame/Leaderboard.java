package com.example.untitledmoorhuhngame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private final Group pane = new Group();
    private final DynamicUIManager ui;
    private final DynamicStyles ds;
    private final Scene scene;
    private final HelloApplication app;
    private final BackgroundManager bg;

    // Tries to locate the true leaderboard.txt in src/main/resources (not target)
    private static final Path LEADERBOARD_PATH = locateLeaderboard();

    public Leaderboard(Scene scene, HelloApplication app) {
        this.scene = scene;
        this.app = app;
        this.ui = app.getUi();
        this.ds = app.getDs();
        this.bg = app.getBg();
        setBackground();
        initialize();
    }

    private static Path locateLeaderboard() {
        // Start from current working dir (could be target/classes or elsewhere)
        Path cwd = Paths.get("").toAbsolutePath();

        // Go up until we find "src/main/resources/leaderboard.txt"
        for (int i = 0; i < 6; i++) {
            Path possible = cwd.resolve(Paths.get("src", "main", "resources", "leaderboard.txt"));
            if (Files.exists(possible)) {
                return possible.normalize();
            }
            cwd = cwd.getParent(); // go one directory up
            if (cwd == null) break;
        }

        // If not found, fallback to direct relative path (may be empty first run)
        return Paths.get("src", "main", "resources", "leaderboard.txt").normalize();
    }

    private void setBackground() {
        bg.setBackground(scene, pane, 4);
    }

    private void initialize() {
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(ui.getElementOffSetChild() * 1000);
        mainLayout.prefHeightProperty().bind(scene.heightProperty());
        mainLayout.setPadding(new Insets(0, 0, ui.getElementOffSetParent() * 1000, 0));
        ui.bindPositionToCenter(mainLayout);

        Label title = new Label("Leaderboard");
        ui.bindFontSize(title, ds.h1Size(), ds.Bold());

        VBox leaderboardLayout = new VBox();
        leaderboardLayout.setAlignment(Pos.CENTER_LEFT);
        leaderboardLayout.setPadding(new Insets(ui.getElementOffSetChild() * 1000));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: rgba(255, 255, 255, 0.7);");
        scrollPane.setContent(leaderboardLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        List<String> leaderboard = loadLeaderboard();
        updateLeaderboard(leaderboard, leaderboardLayout);

        Button mainMenuButton = ui.createDynamicFontButton("Back to Main Menu", scene, app::showMainMenu);

        mainLayout.getChildren().addAll(title, scrollPane, mainMenuButton);
        pane.getChildren().add(mainLayout);
    }

    private void updateLeaderboard(List<String> leaderboard, VBox leaderboardLayout) {
        leaderboardLayout.getChildren().clear();
        for (int i = 0; i < leaderboard.size(); i++) {
            String entry = leaderboard.get(i);
            String[] parts = entry.split(" - ");
            String position = (i + 1) + ". " + parts[0] + " - " + parts[1];

            Label scoreEntry = new Label(position);
            scoreEntry.setAlignment(Pos.CENTER_LEFT);
            ui.bindFontSize(scoreEntry, ds.h4Size(), ds.Normal());
            leaderboardLayout.getChildren().add(scoreEntry);
        }
    }

    private List<String> loadLeaderboard() {
        List<String> scores = new ArrayList<>();

        try {
            try (BufferedReader reader = Files.newBufferedReader(LEADERBOARD_PATH)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) scores.add(line);
                }
            }

            scores.sort((a, b) -> {
                try {
                    return Integer.compare(
                            Integer.parseInt(b.split(" - ")[1].trim()),
                            Integer.parseInt(a.split(" - ")[1].trim())
                    );
                } catch (Exception e) {
                    return 0;
                }
            });
        } catch (IOException e) {
            System.err.println("Error accessing leaderboard: " + e.getMessage());
        }

        return scores;
    }

    public void saveScoreToLeaderboard(String playerName, int score) {
        List<String> scores = loadLeaderboard();
        boolean nameExists = false;

        for (int i = 0; i < scores.size(); i++) {
            String[] parts = scores.get(i).split(" - ");
            if (parts[0].equals(playerName)) {
                nameExists = true;
                int existingScore = Integer.parseInt(parts[1].trim());
                if (score > existingScore) {
                    scores.set(i, playerName + " - " + score);
                }
                break;
            }
        }

        if (!nameExists) scores.add(playerName + " - " + score);

        scores.sort((a, b) -> {
            try {
                return Integer.compare(
                        Integer.parseInt(b.split(" - ")[1].trim()),
                        Integer.parseInt(a.split(" - ")[1].trim())
                );
            } catch (Exception e) {
                return 0;
            }
        });

        try (BufferedWriter writer = Files.newBufferedWriter(LEADERBOARD_PATH)) {
            for (String line : scores) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing leaderboard: " + e.getMessage());
        }
    }

    public List<String> getLeaderboard() {
        return loadLeaderboard();
    }

    public Group getPane() {
        return pane;
    }
}
