package com.example.untitledmoorhuhngame;

// Enum trieda na definovanie rôznych typov nepriateľov s ich veľkosťou a skóre
public enum EnemyType {
    SMALL(20, 100), // Malý nepriateľ s veľkosťou 20 a skóre 100
    MEDIUM(35, 50), // Stredne veľký nepriateľ s veľkosťou 35 a skóre 50
    LARGE(50, 25),  // Veľký nepriateľ s veľkosťou 50 a skóre 25
    LARGER(75, 10); // Veľmi veľký nepriateľ s veľkosťou 75 a skóre 10

    private final int size; // Veľkosť nepriateľa (používa sa na zobrazenie alebo hernú mechaniku)
    private final int score; // Skóre za porazenie nepriateľa

    // Konstruktor na inicializáciu veľkosti a skóre pre každý typ nepriateľa
    EnemyType(int size, int score) {
        this.size = size;
        this.score = score;
    }

    // Getter metóda pre veľkosť
    public int getSize() {
        return size;
    }

    // Getter metóda pre skóre
    public int getScore() {
        return score;
    }
}