# Sudoku Solver & Algorithm Visualizer

<p align="center">
  <img src="https://img.shields.io/badge/Language-JavaScript%20%7C%20Java-f59e0b?style=for-the-badge&logo=javascript" alt="Language" />
  <img src="https://img.shields.io/badge/Architecture-DFS%20Backtracking-6366f1?style=for-the-badge" alt="Algorithm" />
  <img src="https://img.shields.io/badge/UI-Minimalist%20%26%20Responsive-10b981?style=for-the-badge" alt="UI" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License" />
</p>

A minimal, attractive web-based **Sudoku Solver & Interactive Algorithm Visualizer** alongside its native **Java** reference implementation. Built using depth-first search (DFS) with recursive backtracking, this project allows you to solve standard 9×9 puzzles in milliseconds, step through the recursion tree in real time, or play puzzles with interactive constraint checking.

---

## Key Features

- **Blazing Fast Solver**: Solves valid 9×9 puzzles in under 15ms using recursive backtracking.
- **Interactive Visualizer**: Step-by-step visual animation demonstrating state exploration, candidate placement, and rollback/backtracking in real time.
- **Dual Solver Engines**:
  - **Web Client**: Zero-dependency, pure vanilla HTML5, CSS3, and ES6 JavaScript.
  - **Java CLI**: Reference command-line solver (`Sudoku.java`).
- **Minimalist Aesthetic**:
  - Dark & Light themes with persistent preferences.
  - Crosshair highlighting (active row, column, and 3×3 subgrid).
  - Matching number highlights to spot duplicate patterns instantly.
  - Smooth micro-animations and conflict indicators.
- **Interactive Gameplay**:
  - Play using physical keyboard (1–9, arrows, Backspace/Delete) or on-screen virtual keypad.
  - Instant conflict detection warning when violating Sudoku rules (`isSafe`).
- **Puzzle Presets**:
  - Built-in library: *Sudoku.java Benchmark*, *Easy*, *Medium*, *Hard*, and *Blank Board* for custom inputs.
- **Embedded Code Viewer**:
  - Review the exact Java implementation (`isSafe` & `SudokuSolver`) directly in the web UI.

---

## Repository Structure

```text
Sudoku-Solver-Visualizer/
├── index.html         # Main web application entry point
├── style.css          # Minimal design system, themes, and animations
├── app.js             # Algorithm engine, visualizer scheduler & UI events
├── Sudoku.java        # Core Java reference backtracking solver
├── SRS.md             # IEEE 830 Software Requirements Specification
├── README.md          # Project documentation & overview
├── LICENSE            # MIT License
└── .gitignore         # Build and IDE artifact exclusions
```

---

## Algorithmic Formulation

The solver models Sudoku as a **Constraint Satisfaction Problem (CSP)** solved via recursive backtracking:

1. **Validation (`isSafe`)**:
   - Ensures candidate digit $d \in \{1, \dots, 9\}$ does not appear in the current row, column, or enclosing $3 \times 3$ subgrid.
2. **Recursive Search (`SudokuSolver`)**:
   - Traverses cells left-to-right, row-by-row.
   - Upon encountering an empty cell ($0$), iterates through candidates $1 \dots 9$.
   - If candidate is safe, sets cell value and recurses to the next cell.
   - If the recursive branch returns `false` (dead end), resets cell to $0$ (**backtracks**) and tries the next candidate.

### Complexity
- **Worst-Case Time Complexity**: $\mathcal{O}(9^M)$, where $M \le 81$ is the number of empty cells.
- **Optimized Practical Complexity**: Early constraint pruning keeps actual operations typically under $10^5$.
- **Space Complexity**: $\mathcal{O}(M)$ auxiliary space on the call stack.

---

## Quick Start

### 1. Web Application (Zero Installation)
Simply double-click `index.html` or open it in any web browser (Chrome, Edge, Firefox, Safari):

```bash
# On Windows (PowerShell)
start index.html
```

### 2. Java Terminal Application
Compile and execute the standalone Java solver:

```bash
# Compile
javac Sudoku.java

# Run
java Sudoku
```

---

## Keyboard Shortcuts

| Key | Action |
| :--- | :--- |
| `1` – `9` | Place digit in selected cell |
| `Backspace` / `Delete` / `0` | Clear digit from selected cell |
| `↑` `↓` `←` `→` | Navigate grid cells with wrap-around |
| `Escape` | Deselect current cell / Close modal |

---

## Documentation

For full engineering specifications, architecture diagrams, data flow diagrams, and IEEE 830 compliance details, please refer to:
- [Software Requirements Specification (SRS)](SRS.md)

---

## Author

**Yash Bhutada**  
*Final-Year B.Tech Computer Science Student*  
*GH Raisoni College of Engineering & Management, Pune*  
- GitHub: [@Yashbhutada2005](https://github.com/Yashbhutada2005)  
- Email: yashbhutada05@gmail.com

---

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for details.
