package com.example.untitledmoorhuhngame;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;

// Trieda pre dynamické prispôsobenie UI
public class DynamicUIManager {

    private final Scene scene; // Scéna aplikácie
    private final DynamicStyles ds; // Štýly UI
    private double fontSize; // Veľkosť fontu
    private final double OffSet = 20; // Posun pre pozície

    private final Map<Button, ChangeListener<? super Number>> heightListeners = new HashMap<>();
    private final Map<Button, ChangeListener<Boolean>> focusListeners = new HashMap<>();

    // Konštruktor
    public DynamicUIManager(Scene scene, HelloApplication app) {
        this.scene = scene;
        this.ds = app.getDs();
    }

    // Offset pre rodičovské prvky
    public double getElementOffSetParent() {
        return 0.05;
    }

    // Offset pre detské prvky
    public double getElementOffSetChild() {
        return 0.02;
    }

    // Viaže veľkosť a štýl ovládacieho prvku
    public void bindControls(Control control) {
        control.prefWidthProperty().bind(scene.heightProperty().multiply(0.3));
        control.prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        control.styleProperty().bind(Bindings.concat("-fx-font-size: ", scene.heightProperty().multiply(ds.h4Size()), "px;"));
    }

    // Vytvára tlačidlo s dynamickým fontom
    public Button createDynamicFontButton(String text, Scene scene, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(_ -> action.run());
        removeListeners(button, scene);

        // Inicializácia fontu
        fontSize = scene.getHeight() * ds.h3Size();
        button.setFont(Font.font("System", FontWeight.NORMAL, fontSize));

        // Listener na zmenu zamerania
        ChangeListener<Boolean> focusListener = (_, _, isFocused) -> {
            FontWeight weight = isFocused ? FontWeight.BOLD : FontWeight.NORMAL;
            button.setFont(Font.font("System", weight, fontSize));
        };
        button.focusedProperty().addListener(focusListener);
        focusListeners.put(button, focusListener);

        // Listener na zmenu veľkosti scény
        ChangeListener<? super Number> heightListener = (_, _, newHeight) -> {
            fontSize = newHeight.doubleValue() * ds.h3Size();
            FontWeight weight = button.isFocused() ? FontWeight.BOLD : FontWeight.NORMAL;
            button.setFont(Font.font("System", weight, fontSize));
        };
        scene.heightProperty().addListener(heightListener);
        heightListeners.put(button, heightListener);

        return button;
    }

    // Odstraňuje listenery
    private void removeListeners(Button button, Scene scene) {
        if (focusListeners.containsKey(button)) {
            button.focusedProperty().removeListener(focusListeners.get(button));
            focusListeners.remove(button);
        }

        if (heightListeners.containsKey(button)) {
            scene.heightProperty().removeListener(heightListeners.get(button));
            heightListeners.remove(button);
        }
    }

    // Viaže veľkosť fontu a štýl k uzlu
    public void bindFontSize(Node node, double proportion, String textStyle) {
        node.styleProperty().bind(Bindings.concat(
                "-fx-font-size: ", scene.heightProperty().multiply(proportion).asString(), "px;",
                textStyle));
    }

    // Centruje uzol
    public void bindPositionToCenter(Region node) {
        node.layoutXProperty().bind(scene.widthProperty().subtract(node.widthProperty()).divide(2));
        node.layoutYProperty().bind(scene.heightProperty().subtract(node.heightProperty()).divide(2).add(OffSet));
    }

    // Viaže uzol k vrchu stredu
    public void bindToTopCenter(Region node) {
        node.layoutXProperty().bind(scene.widthProperty().subtract(node.widthProperty()).divide(2));
        node.layoutYProperty().set(OffSet);
    }

    // Viaže uzol k spodku stredu
    public void bindToBottomCenter(Region node) {
        node.layoutXProperty().bind(scene.widthProperty().subtract(node.widthProperty()).divide(2));
        node.layoutYProperty().bind(scene.heightProperty().subtract(node.heightProperty()).subtract(OffSet));
    }

    // Viaže uzol k ľavému hornému rohu
    public void bindToTopLeft(Region node) {
        node.layoutXProperty().set(OffSet);
        node.layoutYProperty().set(OffSet);
    }

    // Viaže uzol k pravému hornému rohu
    public void bindToTopRight(Region node) {
        node.layoutXProperty().bind(scene.widthProperty().subtract(node.widthProperty()).subtract(OffSet));
        node.layoutYProperty().set(OffSet);
    }

    // Viaže uzol k pravému dolnému rohu
    public void bindToBottomRight(Region node) {
        node.layoutXProperty().bind(scene.widthProperty().subtract(node.widthProperty()).subtract(OffSet));
        node.layoutYProperty().bind(scene.heightProperty().subtract(node.heightProperty()).subtract(OffSet));
    }
}