# 🎮 Ball Sort Puzzle 

## 1. Game Overview
Ball Sort Puzzle is a simple logic-based “board-style” game where the player’s goal is to sort colored balls into separate tubes so that each tube contains balls of only one color.  
The game encourages logical thinking, planning, and step-by-step problem solving.

This project was developed using **LibGDX**, **Scene2D**, and the **Ashley ECS framework**, following the course requirements.

---

## 🧠 2. Game Dynamics
The game consists of several tubes (containers), each containing a stack of colored balls in a random order.  
The player interacts with the tubes to reorganize the balls by following simple sorting rules.

**Goal:**  
Arrange all balls so that each tube holds only one color.

**Game loop dynamics:**
- The player selects a tube as the source.
- Then selects another tube as the target.
- If the move is valid, the top ball (or several consecutive balls of the same color) is transferred to the target tube.
- The puzzle is solved when all tubes contain uniform colors or are empty.

The game includes multiple levels, increasing in difficulty by adding more tubes and more ball colors.

---

## ⚙️ 3. Game Mechanics

### Valid Move Conditions
A ball can be moved from one tube to another if:
1. The source tube is not empty.
2. The target tube is not full.
3. The top ball in the target tube matches the color of the ball being moved, **or** the target tube is completely empty.

### Actions
- **Select Tube:** Player taps a tube to select it.
- **Transfer Ball:** If the next tapped tube satisfies the rules, the ball moves with a short animation.
- **Reset Level:** Restarts the puzzle to its initial configuration.
- **Undo Move (optional):** Reverts the most recent action.
- **Next Level:** Moves to the next puzzle after successful completion.

### Win Condition
A level is solved when:
- All non-empty tubes contain balls of **only one color**, and
- No tube contains mixed colors.

---

## 🧩 4. Game Elements

### 🧪 Tubes
- Transparent vertical containers.
- Each has a fixed capacity (e.g., 4 balls).
- May start empty or partially filled depending on the level.

### 🔵 Balls
- Colored circular sprites.
- Multiple colors (red, blue, yellow, green, purple, etc.).
- Stored in each tube as a stack structure.

### 🎯 Levels
- Each level defines a starting configuration of tubes and ball colors.
- Higher levels introduce more tubes or more complex arrangements.

### 💻 UI Components
- Reset button
- Undo button
- Sound toggle
- Level number label
- Next level screen / success animation

---
