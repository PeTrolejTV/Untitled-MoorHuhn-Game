# **Untitled MoorHuhn Game - Game Design Document - EN**

Dive into a frantic arcade world where time is against you! Hunt flying chickens, grab power-ups, and roast your targets with precision. Will you spare a chicken… or let it burn? Fast reflexes, goofy graphics, and chaotic fun await—can you rise to the top of the leaderboard before the clock runs out?

**Author**: Peter Majerik

---

## Introduction

The game celebrates the classic Moorhuhn series — those delightfully absurd bird-shooting games that mixed quick reflexes with ridiculous fun.
This reinterpretation, built with **JavaFX**, keeps the heart of that chaos alive but replaces the rustic countryside look with a more original and goofy visual style — think exaggerated animations, playful shapes, and tongue-in-cheek art direction. It’s a tribute that doesn’t take itself too seriously — just like the original.

Players face the eternal question: **will you spare the chickens, or let them burn for the high score?**

---

## Inspiration

*Untitled MoorHuhn Game* draws its roots from the classic Moorhuhn series, a cult favorite of the late 1990s and early 2000s. These games became famous for their simple yet addictive gameplay, absurd humor, and over-the-top arcade chaos.

<p align="center">
   <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/gallery/moorhuhnog.jpg" alt="Original Moorhuhn Reference" width="60%">
   <br>
   <em>Figure 1 Preview of the Original Moorhuhn</em>
</p>

---

## Gameplay

The gameplay focuses on precision, timing, and resource management.  
Chickens fly across the screen in unpredictable patterns, and the player must shoot them before time runs out.

| <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/gallery/gameplay1.png" width="666"> | <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/gallery/gameplay2.png" width="666"> |
|:--:|:--:|
| *Figure 2 Preview of the Game play* | *Figure 3 Preview of the Game play* |

### Core Mechanics

- Aim and shoot at flying chickens to earn points.  
- Collect temporary **power-ups** that modify your weapon or abilities.  
- Manage your **ammo** and reload efficiently.  
- The **timer** constantly counts down — when it hits zero, the hunt is over.  
- Difficulty increases dynamically based on your performance.

<p align="center">
  <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/gallery/gameplay3.png" alt="Game play screen" width="65%">
      <br>
   <em>Figure 4 Preview of the Game play</em>
</p>

---

## Development Software

- **Java 21 & JavaFX 22.0.1**: Core programming language and framework for the project.
- **Maven**: Build automation and dependency management tool.
- **IntelliJ IDEA & Visual Studio Code**: Primary Integrated Development Environments (IDEs) used for coding, debugging, and testing.
- **Adobe Photoshop**: For creating and editing game graphics.
- **Audacity**: For sound effects and audio editing.
  
---

## Power-Ups

Power-ups appear randomly during gameplay.
Each provides a lasting bonus for the remainder of the game, enhancing your chances of survival and a higher score.

- **Score Multiplier +25%:** Permanently increases all future points by 25%.  
- **Faster Reload:** Reduces reload time for quicker shots.  
- **Instant Reload:** Removes reload delay entirely, allowing continuous fire.  
- **+30 Seconds:** Extends the round timer by 30 seconds.  
- **+1 Max Ammo:** Increases your total ammo capacity (default 4 → maximum 10).  

| <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/maven/src/main/resources/assets/powerup/active/Untitled_powerup_active3.png" width="150"> | <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/maven/src/main/resources/assets/powerup/active/Untitled_powerup_active2.png" width="150"> | <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/maven/src/main/resources/assets/powerup/destroyed/Untitled_powerup_destroyed2.png" width="150"> | <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/maven/src/main/resources/assets/powerup/destroyed/Untitled_powerup_destroyed1.png" width="150"> |
|:--:|:--:|:--:|:--:|
| *Figure 5 Power Up Active* | *Figure 6 Power Up Active* | *Figure 7 Power Up Destroyed* | *Figure 8 Power Up Destroyed* |

---

## Scoring System

Points are awarded based on chicken size, speed, and your sharpshooting skills. Tiny, zippy chickens are worth more than slow, clumsy ones, rewarding quick reflexes and precision.
Combos, streaks, and clever use of power-ups multiply your score, turning frantic chicken-chasing into a strategic (and hilarious) arcade challenge.

All high scores are saved locally and proudly displayed in the **Leaderboard**, letting you compare results and aim for legendary chicken-slaying glory.

---

## Class Design

- **HelloApplication**: Main JavaFX entry point that initializes and launches the game environment.
- **Game & Timer**: Core gameplay loop and time management system controlling overall game flow.
- **DynamicUIManager & DynamicStyles**: Handle responsive UI layout, scaling, and consistent styling across all menus.
- **MainMenu, StartScreen & EndScreen**: Interactive screens for navigation, game start, and results display.
- **Settings**: Manages display modes (Fullscreen, Borderless, Windowed) and in-game volume adjustments.
- **Leaderboard & ScoreBoard**: Handle player score tracking, saving, loading, and presentation.
- **SoundManager**: Controls looping background music, sound effects, and volume balancing.
- **Enemy, EnemyType & Gun**: Manage enemy behavior, types, and shooting system with dynamic hit detection.
- **PowerUp & PowerUpEffect**: Introduce special bonuses and visual feedback enhancing gameplay variety.
- **BackgroundManager & MySprites**: Handle background layers, sprite loading, and animation assets.
- **BonusBoard & Credits**: Display bonus scores and developer credits with dynamic, styled presentation.

---

## Art and Visuals

All of the visuals in Untitled MoorHuhn Game were hand-painted by me using a mouse, giving the game a truly personal and quirky touch. The style is intentionally goofy and arcade-like, almost like something a kindergarten class might proudly display — exaggerated shapes, silly proportions, and bright, cheerful colors.

Animations are over-the-top, with chickens flapping wildly, smoke trails puffing, and comical burn effects, all designed to make the chaos fun, chaotic, and visually entertaining. Every frame celebrates the lighthearted, absurd spirit of the game.

<p align="center">
  <img src="https://github.com/PeTrolejTV/Untitled-MoorHuhn-Game/blob/main/UntitledMoorHuhnGame/gallery/MainMenuPreview.png" alt="Main Menu Screen" width="65%">
      <br>
   <em>Figure 9 Preview of the Main Menu</em>
</p>

---

## Music and Sound Design

The audio in *Untitled MoorHuhn Game* is all about **chaos, energy, and absurd fun**! Music dynamically reacts to your play — get close to the timer running out, and the tension (and ridiculousness) ramps up.
Sound effects cover every zany moment, from gunfire to chicken screams and comedic mishaps.

### Music

- **Main Menu Theme:** [Listen here](https://www.youtube.com/watch?v=g5j-6edFJKc)  
- **Main Menu Alt:** [Listen here](https://www.youtube.com/watch?v=BntrNnlKydE)  
- **End Game Music:** [Listen here](https://www.youtube.com/watch?v=e0h09RpeMSY)  
- **In-Game Music:** [Listen here](https://www.youtube.com/watch?v=7oCPuvwFeI0)  

### Sound Effects

- Enemy Spawn & No Ammo Warning — [Watch/listen](https://www.youtube.com/watch?v=1yxN0FXE0ME)  
- Enemy Shot — [Watch/listen](https://www.youtube.com/watch?v=xEvJmI7uJ-g)  
- Gun Shell Insert — [Watch/listen](https://www.youtube.com/watch?v=AGVz9zoWNrQ)  
- Gun Reload & Shoot — [Watch/listen](https://www.youtube.com/watch?v=Yt0_CsnJqqA)  
- Clock Ticking — [Watch/listen](https://www.youtube.com/watch?v=8VUgLhAvN0U)  
- Game Over — [Watch/listen](https://www.youtube.com/watch?v=s5B188EFlvE)  
- PowerUp Spawn — [Watch/listen](https://www.youtube.com/watch?v=UNZjvty4v2A)  
- PowerUp Shot — [Watch/listen](https://www.youtube.com/watch?v=4XnhAfRyrGI)  
- Error / Oops Sounds — [Watch/listen](https://www.youtube.com/watch?v=EeA_Y0FSv5Q)  

> Arcade chaos meets chicken carnage! Expect your ears to get as wild as your trigger finger.

---

## Controls

| Action            | Key / Input             |
|-------------------|-------------------------|
| Shoot             | Left Mouse Button       |
| Reload            | Right Mouse Button or R |
| Toggle Fullscreen | F11                     |

---

## Conclusion

Untitled MoorHuhn Game reinvents the beloved chaos of the classic Moorhuhn series with a modern, playful spin. 
Developed entirely in JavaFX, it swaps the rustic farm backdrop for an original, exaggerated art style full of quirky animations and humorous visuals. 
The game thrives on fast-paced action, challenging players’ reflexes while rewarding daring shots and clever use of power-ups. 
Lighthearted, chaotic, and unapologetically silly, it honors the spirit of the originals while carving out its own space as a fun, frantic arcade experience.

And somewhere, a chicken is plotting its revenge — watch your back!

---

## 🎮 How to Play

1. **Download the project**
   - Click the green **"Code" → "Download ZIP"**, then extract it anywhere on your PC

2. **Open the folder**
   - Go to: `Untitled-MoorHuhn-Game-main\UntitledMoorHuhnGame\maven`

3. **Run the game**
   - To **build an EXE** → double-click `build_exe.bat`
   - Wait some time till everything downloads and builds the game
   - Press any key in the Command Prompt or just close it
   - To **Play** the game → open the newly created folder `target` and double-click on `UntitledMoorHuhnGame.exe`
