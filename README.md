# Pyramid Runner

A 2D tile-based platformer game written in Java using Swing.

## About the Game

You control a treasure hunter exploring a pyramid. Navigate through platforms by running and jumping, and reach the golden **EXIT** door to complete the level.

The level is built from a tile grid — brown tiles are solid walls and platforms, darker tiles are open space. The player spawns in the bottom-left and must make their way to the exit door in the upper-right area.

## Controls

| Key | Action |
|-----|--------|
| `A` / `←` | Move left |
| `D` / `→` | Move right |
| `W` / `↑` / `Space` | Jump |

## How to Run

### Requirements
- Java Development Kit (JDK) 17 or higher

### Command Line

```bash
cd src
javac Main.java pyramidrunner/**/*.java
java Main
```

### IntelliJ IDEA / Eclipse

1. Open the project root folder in your IDE
2. Mark the `src` folder as **Sources Root**
3. Run `Main.java`

## How to Run Tests

### Requirements
- JUnit 5 (`junit-platform-console-standalone-5.x.jar`)

```bash
cd src
javac -cp .;path/to/junit.jar ../test/pyramidrunner/GameTest.java pyramidrunner/**/*.java
java -jar path/to/junit.jar --class-path . --select-class pyramidrunner.GameTest
```

In IntelliJ — right-click `test/pyramidrunner/GameTest.java` → **Run 'GameTest'**

## Project Structure

```
src/
├── Main.java                        # Entry point
├── GamePanel.java                   # Game loop (60 FPS), update & render
└── pyramidrunner/
    ├── entity/
    │   ├── Entity.java              # Abstract base class for all game objects
    │   ├── Player.java              # Player movement, gravity, collision
    │   └── Door.java                # Exit door — triggers level complete
    ├── input/
    │   └── KeyHandler.java          # Keyboard input
    └── world/
        ├── Tile.java                # Base tile class
        ├── WallTile.java            # Solid tile
        ├── FloorTile.java           # Non-solid tile
        └── TileMap.java             # Builds and renders the level grid
test/
└── pyramidrunner/
    └── GameTest.java                # 4 JUnit 5 tests
```

## Author

Maksym Mohula — AIS ID: 135221
Slovenská Technická Univerzita v Bratislave, FIIT
