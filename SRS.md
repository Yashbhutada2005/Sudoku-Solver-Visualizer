# Software Requirements Specification (SRS)
## For Minimal Sudoku Solver & Algorithm Visualizer

**Document Version:** 1.0.0  
**Author:** Yash Bhutada  
**Date:** September 2026  
**Status:** Approved  
**Standard Compliance:** IEEE Std 830-1998 Format  

---

## Table of Contents
1. [Introduction](#1-introduction)
   - 1.1 Purpose
   - 1.2 Document Conventions
   - 1.3 Intended Audience & Reading Suggestions
   - 1.4 Project Scope
   - 1.5 References
2. [Overall Description](#2-overall-description)
   - 2.1 Product Perspective
   - 2.2 Product Functions
   - 2.3 User Classes and Characteristics
   - 2.4 Operating Environment
   - 2.5 Design and Implementation Constraints
   - 2.6 Assumptions and Dependencies
3. [System Features & Functional Requirements](#3-system-features--functional-requirements)
   - 3.1 Board Initialization & Preset Management
   - 3.2 Recursive Backtracking Engine
   - 3.3 Visual Backtracking Simulation
   - 3.4 Interactive Board Gameplay & Conflict Detection
   - 3.5 Java Source Code Inspector
   - 3.6 Theme Customization & UI Responsiveness
4. [External Interface Requirements](#4-external-interface-requirements)
   - 4.1 User Interfaces
   - 4.2 Hardware Interfaces
   - 4.3 Software Interfaces
   - 4.4 Communications Interfaces
5. [Non-Functional Requirements](#5-non-functional-requirements)
   - 5.1 Performance Requirements
   - 5.2 Usability & Accessibility Requirements
   - 5.3 Reliability & Fault Tolerance
   - 5.4 Portability & Cross-Platform Support
   - 5.5 Security & Data Privacy
6. [System Architecture & Flow Diagrams](#6-system-architecture--flow-diagrams)
   - 6.1 Data Flow Diagram (DFD Level 1)
   - 6.2 Backtracking Solver Finite State Machine
7. [Mathematical & Algorithmic Formulation](#7-mathematical--algorithmic-formulation)

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) establishes the complete functional and non-functional requirements for the **Minimal Sudoku Solver & Algorithm Visualizer**. This project implements a high-performance Sudoku solving system based on recursive depth-first search (DFS) with backtracking, paired with an interactive, modern, and minimalist web interface.

### 1.2 Document Conventions
- **Shall**: Indicates a mandatory requirement.
- **Should**: Indicates a recommended requirement.
- **May**: Indicates an optional feature.
- Requirements are uniquely identified using alphanumeric codes:
  - `FR-x`: Functional Requirement
  - `NFR-x`: Non-Functional Requirement
  - `UI-x`: User Interface Requirement

### 1.3 Intended Audience & Reading Suggestions
- **Developers & Maintainers**: Reference for implementation details, architecture, and extension points.
- **Academic Evaluators**: Review of algorithmic rigor, complexity analysis, and IEEE requirement adherence.
- **End Users**: Overview of functional capabilities, keyboard shortcuts, and visualizer usage.

### 1.4 Project Scope
The software provides:
1. An instant backtracking solver capable of resolving valid 9×9 Sudoku puzzles within sub-15ms execution time.
2. A step-by-step visual backtracking simulator that illustrates state exploration, candidate placement, constraint validation, and rollback operations in real-time.
3. An interactive player interface supporting keyboard and touch keypad inputs, real-time constraint validation, crosshair highlighting, and dark/light modes.
4. A Java command-line solver (`Sudoku.java`) providing terminal execution and verification.

### 1.5 References
- IEEE Std 830-1998: *IEEE Recommended Practice for Software Requirements Specifications*.
- Knuth, Donald E. (2000): *Dancing Links* (Exact cover formulation comparison).
- Norvig, Peter: *Solving Every Sudoku Puzzle* (Constraint propagation and backtracking benchmarks).

---

## 2. Overall Description

### 2.1 Product Perspective
The product is a self-contained, client-side web application and standalone Java console application. The web system runs entirely within client browser runtimes (V8, SpiderMonkey, JavaScriptCore) without backend server dependencies or external databases.

```
+-------------------------------------------------------------+
|                      Client Browser                         |
|  +------------------------+    +--------------------------+ |
|  | Presentation Tier      |    | Application / Logic Tier | |
|  | - index.html           |<-->| - app.js                 | |
|  | - style.css            |    |   * isSafe() validator   | |
|  | - Virtual Keypad       |    |   * Backtracking solver  | |
|  | - Visualizer Controls  |    |   * Animation scheduler  | |
|  +------------------------+    +--------------------------+ |
+-------------------------------------------------------------+
                              |
                     Reference Algorithm
                              v
+-------------------------------------------------------------+
|                      Java Environment                       |
|  - Sudoku.java (Terminal Solver & Algorithm Specification)  |
+-------------------------------------------------------------+
```

### 2.2 Product Functions
- **Puzzle Loading**: Support for built-in difficulty presets (Java Reference Preset, Easy, Medium, Hard) and custom blank grid input.
- **Instant Solution Computation**: Execution of the recursive DFS backtracking algorithm with immediate grid population.
- **Animated Backtracking Exploration**: Dynamic rendering of candidate attempts and backtracks with adjustable speed (1× to 60×) and pause/stop controls.
- **Interactive Gameplay**: Cell selection, numeric entry (1–9), deletion (Backspace/Delete), and arrow key grid traversal.
- **Constraint Violation Highlighting**: Real-time identification of duplicate entries across rows, columns, and 3×3 subgrids.
- **Java Code Inspector**: Interactive modal displaying the underlying Java implementation.

### 2.3 User Classes and Characteristics
- **Students & Learners**: Users seeking to understand recursive backtracking, decision trees, and depth-first search through visual simulation.
- **Casual Sudoku Players**: Users seeking a distraction-free, aesthetically pleasing Sudoku solving and playing utility.
- **Software Engineers / Interviewees**: Users reviewing classic constraint satisfaction problem (CSP) paradigms.

### 2.4 Operating Environment
- **Web Browsers**: Google Chrome (>= 90), Mozilla Firefox (>= 88), Microsoft Edge (>= 90), Apple Safari (>= 14).
- **Display Resolutions**: Fully responsive across mobile viewports (360px width) through 4K displays (3840×2160).
- **Java Runtime (Optional CLI)**: OpenJDK / Oracle JDK 8 or higher.

### 2.5 Design and Implementation Constraints
- **Zero-Dependency Architecture**: No external JavaScript frameworks (React, Vue, Angular) or CSS libraries (Tailwind, Bootstrap). Only vanilla web standards (HTML5, CSS3, ES6+).
- **Execution Sandboxing**: All calculations must run client-side without network requests.
- **Browser Thread Responsiveness**: The visualizer must use asynchronous yielding (`requestAnimationFrame` / `setTimeout`) to prevent blocking the main UI thread during animation.

### 2.6 Assumptions and Dependencies
- JavaScript must be enabled in the user's web browser.
- Modern CSS Grid and Flexbox must be supported by the host browser.

---

## 3. System Features & Functional Requirements

### 3.1 Board Initialization & Preset Management
- **FR-1.1**: The system shall initialize a 9×9 grid upon document load.
- **FR-1.2**: The system shall provide a dropdown menu containing at least five options:
  - `Sudoku.java Preset`: The exact benchmark puzzle from the reference Java class.
  - `Easy`: Puzzle with 36 clues.
  - `Medium`: Puzzle with 28 clues.
  - `Hard`: Puzzle with 22 clues.
  - `Blank Board`: Empty 9×9 grid for arbitrary user puzzle entry.
- **FR-1.3**: When a preset is selected, clue cells shall be designated as immutable and styled with bold contrast.

### 3.2 Recursive Backtracking Engine
- **FR-2.1**: The solver shall implement the `isSafe(grid, row, col, digit)` constraint validator:
  - Check row uniqueness: $\forall j \in [0, 8], \text{grid}[\text{row}][j] \neq \text{digit}$
  - Check column uniqueness: $\forall i \in [0, 8], \text{grid}[i][\text{col}] \neq \text{digit}$
  - Check 3×3 box uniqueness: $\forall i \in [\text{sr}, \text{sr}+2], j \in [\text{sc}, \text{sc}+2], \text{grid}[i][j] \neq \text{digit}$ where $\text{sr} = \lfloor\text{row}/3\rfloor \times 3$, $\text{sc} = \lfloor\text{col}/3\rfloor \times 3$.
- **FR-2.2**: The solver shall evaluate base cases: terminate and return `true` when $\text{row} = 9 \land \text{col} = 0$.
- **FR-2.3**: If the puzzle is solvable, the system shall populate all empty cells and display the execution duration in milliseconds.
- **FR-2.4**: If the puzzle contains contradictory initial inputs, the system shall abort and display a warning banner without modifying the grid.

### 3.3 Visual Backtracking Simulation
- **FR-3.1**: The visualizer shall highlight candidate digit placements in cyan (`.vis-trying`).
- **FR-3.2**: When a candidate dead-ends, the cell shall temporarily highlight in red (`.vis-backtrack`) before resetting to blank.
- **FR-3.3**: The user shall be able to dynamically adjust visualization playback speed via an input slider.
- **FR-3.4**: The system shall provide **Pause**, **Resume**, and **Stop** controls during an active visualization run.
- **FR-3.5**: The system shall maintain live counters for total recursive exploration steps and total backtrack events.

### 3.4 Interactive Board Gameplay & Conflict Detection
- **FR-4.1**: Clicking any cell shall designate it as selected and activate peer highlighting (same row, column, and 3×3 subgrid).
- **FR-4.2**: Selecting a cell containing a number shall highlight all identical numbers across the entire board.
- **FR-4.3**: Keyboard input `1` through `9` shall enter digits into editable cells.
- **FR-4.4**: `Backspace`, `Delete`, or key `0` shall clear editable cells.
- **FR-4.5**: Arrow keys (`Up`, `Down`, `Left`, `Right`) shall move the cell cursor with circular wrapping.
- **FR-4.6**: An on-screen virtual keypad (digits 1–9 and Erase) shall allow touch and mouse-only operation.

### 3.5 Java Source Code Inspector
- **FR-5.1**: The interface shall include a "Code" button in the header.
- **FR-5.2**: Clicking the button shall toggle a modal overlay displaying the exact Java source implementation with syntax highlighting.
- **FR-5.3**: Pressing the `Escape` key or clicking outside the modal shall dismiss the inspector.

### 3.6 Theme Customization & UI Responsiveness
- **FR-6.1**: The system shall support both **Dark Mode** and **Light Mode**.
- **FR-6.2**: Theme selection shall persist in `localStorage`.
- **FR-6.3**: The layout shall automatically adapt to mobile (single column) and desktop (two-column) viewports.

---

## 4. External Interface Requirements

### 4.1 User Interfaces
- **Header**: Brand title, subtitle, preset selector, code inspector toggle, and theme switch.
- **Board Canvas**: Centered 9×9 grid with distinct 2px borders demarcating 3×3 blocks.
- **Status Banner**: Real-time colored feedback badge indicating system state.
- **Control Bar**: Primary buttons ("Solve Instantly", "Visualize Algorithm", "Reset", "Clear").
- **Side Panel**: 5×2 touch keypad and algorithmic logic overview.

### 4.2 Hardware Interfaces
- Standard pointing devices (mouse, trackpad).
- Physical keyboards with numeric row / keypad.
- Capacitive touchscreens on mobile/tablet devices.

### 4.3 Software Interfaces
- Browser Document Object Model (DOM) API.
- Web Storage API (`localStorage`).
- High Resolution Time API (`performance.now()`).

### 4.4 Communications Interfaces
- None required. All operations are local and offline-capable.

---

## 5. Non-Functional Requirements

### 5.1 Performance Requirements
- **NFR-1.1**: Instant solve execution time for standard 9×9 puzzles shall not exceed 25 milliseconds on modern computing hardware.
- **NFR-1.2**: Initial page asset loading and DOM render shall complete in under 200 milliseconds.
- **NFR-1.3**: Memory footprint of the web application in browser runtime shall remain under 20 MB.

### 5.2 Usability & Accessibility Requirements
- **NFR-2.1**: WCAG 2.1 AA compliant color contrast ratios for text and cell elements in both themes.
- **NFR-2.2**: Full keyboard navigation support across all cells and actions.
- **NFR-2.3**: ARIA grid role attributes (`role="grid"`, `role="gridcell"`) applied to the board.

### 5.3 Reliability & Fault Tolerance
- **NFR-3.1**: The application shall gracefully handle unsolvable board configurations without freezing or crashing.
- **NFR-3.2**: Aborting visualizer simulations shall restore the board cleanly to a stable state without orphan timeouts.

### 5.4 Portability & Cross-Platform Support
- **NFR-4.1**: Compatible across Windows, macOS, Linux, Android, and iOS.
- **NFR-4.2**: Zero external package installation or build compilation required to run the frontend.

---

## 6. System Architecture & Flow Diagrams

### 6.1 Data Flow Diagram (DFD Level 1)

```
[ User Input / Key Event ]
           |
           v
  +------------------+       Valid Input?
  | Input Controller | ------------------------> [ Update currentGrid ]
  +------------------+                                    |
           |                                              v
           | Click "Solve"                       +-----------------+
           +------------------------------------>| Constraint Check|
                                                 |   isSafe()      |
                                                 +-----------------+
                                                          |
                                                          v
                                                 +-----------------+
                                                 | DFS Backtracker |
                                                 +-----------------+
                                                          |
                                       +------------------+------------------+
                                       | Solved                              | Unsolvable
                                       v                                     v
                             [ Render Solved Grid ]                [ Display Error Banner ]
```

### 6.2 Backtracking Solver State Machine

```
      +--------------+
      |     IDLE     |<---------------------------------------+
      +--------------+                                        |
             |                                                |
             | Click "Visualize"                              |
             v                                                |
      +--------------+      Valid Candidate      +------------+---+
      | Cell Scan    | ------------------------->| Place Candidate|
      | (Row, Col)   |                           +----------------+
      +--------------+                                    |
             ^                                            v
             | Backtrack (Reset to 0)            +----------------+
             +-----------------------------------| Recurse Next   |
                                                 +----------------+
                                                          |
                                                          | All Cells Valid
                                                          v
                                                 +----------------+
                                                 |    COMPLETE    |
                                                 +----------------+
```

---

## 7. Mathematical & Algorithmic Formulation

A Sudoku board is formalized as an assignment problem over a grid:
$$G = [g_{i,j}]_{9 \times 9} \quad \text{where} \quad g_{i,j} \in \{0, 1, 2, \dots, 9\}$$

where $0$ denotes an unassigned cell. A complete and valid assignment requires:

1. **Row Uniqueness**:
   $$\forall i \in \{0, \dots, 8\}, \quad \{g_{i,j} \mid j \in \{0, \dots, 8\}\} = \{1, 2, \dots, 9\}$$

2. **Column Uniqueness**:
   $$\forall j \in \{0, \dots, 8\}, \quad \{g_{i,j} \mid i \in \{0, \dots, 8\}\} = \{1, 2, \dots, 9\}$$

3. **Subgrid (Block) Uniqueness**:
   $$\forall k, m \in \{0, 1, 2\}, \quad \{g_{3k+a, 3m+b} \mid a, b \in \{0, 1, 2\}\} = \{1, 2, \dots, 9\}$$

### Complexity Analysis
- **Worst-Case Time Complexity**: $\mathcal{O}(9^M)$, where $M$ is the number of unassigned cells ($M \le 81$).
- **Optimized Practical Complexity**: Because candidate testing via `isSafe()` prunes the decision tree at early depths, standard puzzles evaluate in $\mathcal{O}(V)$ where $V \ll 9^M$ (typically $< 10^5$ operations).
- **Space Complexity**: $\mathcal{O}(M)$ auxiliary space on the call stack due to recursion depth capped at 81.
