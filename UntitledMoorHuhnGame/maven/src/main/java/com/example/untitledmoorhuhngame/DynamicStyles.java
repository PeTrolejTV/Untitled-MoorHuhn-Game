package com.example.untitledmoorhuhngame;

/**
 * Trieda DynamicStyles poskytuje metódy na definovanie veľkostí písma
 * a štýlov pre konzistentný dizajn v celom projekte.
 */
public class DynamicStyles {

    // Veľkosť písma pre nadpisy prvej úrovne (najväčšie nadpisy).
    public double h1Size() {
        return 0.07; // 7 % výšky scény
    }

    // Veľkosť písma pre nadpisy druhej úrovne.
    public double h2Size() {
        return 0.04; // 4 % výšky scény
    }

    // Veľkosť písma pre nadpisy tretej úrovne.
    public double h3Size() {
        return 0.03; // 3 % výšky scény
    }

    // Veľkosť písma pre nadpisy štvrtej úrovne.
    public double h4Size() {
        return 0.02; // 2 % výšky scény
    }

    // Definuje normálny (štandardný) štýl písma.
    public String Normal() {
        return "-fx-font-style: normal;";
    }

    // Definuje tučné (bold) písmo.
    public String Bold() {
        return "-fx-font-weight: bold;";
    }

    // Definuje kurzívne (italic) písmo.
    public String Italic() {
        return "-fx-font-style: italic;";
    }

    // Definuje podčiarknuté písmo.
    public String Underline() {
        return "-fx-underline: true;";
    }
}