# Sudoku Solver & Algorithm Visualizer (Pure Java)

<p align="center">
  <img src="https://img.shields.io/badge/Language-100%25%20Java-orange?style=for-the-badge&logo=openjdk" alt="Language" />
  <img src="https://img.shields.io/badge/Java%20Version-8%20%2F%2011%20%2F%2017%20%2F%2021%20%2F%2025-blue?style=for-the-badge&logo=java" alt="Java Version" />
  <img src="https://img.shields.io/badge/Architecture-DFS%20Backtracking-6366f1?style=for-the-badge" alt="Algorithm" />
  <img src="https://img.shields.io/badge/Zero%20Dependencies-Standard%20JDK%20Only-success?style=for-the-badge" alt="Zero Dependencies" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License" />
</p>

A pure **Java**-powered **Sudoku Solver & Algorithm Visualizer** featuring:
1. **Core Java Solver (`Sudoku.java`)**: Depth-first recursive backtracking algorithm.
2. **Built-in Java Web Server (`SudokuServer.java`)**: Serves the modern, minimal web UI and handles solving/validation API requests directly on the JVM.
3. **Pure Java Desktop Application (`SudokuApp.java`)**: Clean, minimalist Swing desktop GUI.
4. **Minimalist Web Client**: An interactive visualizer with dark/light modes and visual step-by-step backtracking.

---

## Architecture Overview

```text
+-------------------------------------------------------------------------+
|                        Java 25 Runtime (HotSpot JVM)                    |
|                                                                         |
|   +-----------------------------------------------------------------+   |
|   |                        Sudoku.java                              |   |
|   |   - isSafe(grid, row, col, digit): Row, Col & 3x3 Box Checks   |   |
|   |   - SudokuSolver(grid, row, col): DFS Recursive Backtracking   |   |
|   +-----------------------------------------------------------------+   |
|                 ^                                     ^                 |
|                 |                                     |                 |
|   +---------------------------+         +---------------------------+   |
|   |     SudokuServer.java     |         |      SudokuApp.java       |   |
|   |   - Built-in HTTP Server  |         |   - Modern Java Swing UI  |   |
|   |   - REST API: /api/solve  |         |   - 9x9 Interactive Grid  |   |
|   |   - REST API: /api/valid  |         |   - Native Desktop Window |   |
|   |   - Serves Web Frontend   |         +---------------------------+   |
|   +---------------------------+                                         |
+-----------------|-------------------------------------------------------+
                  | HTTP / JSON (Port 8080)
                  v
+-------------------------------------------------------------------------+
|                      Minimal Web Client Interface                       |
|   - index.html, style.css, app.js (Visualizer & Interactive Player)     |
+-------------------------------------------------------------------------+
```

---

## How to Run

Zero external libraries or build tools required. Works directly with `javac` and `java`.

### Option 1: Run Java Web Server (Recommended)
Compiles and starts the built-in Java web server, automatically launching the web interface in your browser:

```bash
# Compile all Java files
javac *.java

# Start Java Web Server
java SudokuServer
```
*Your browser will automatically open `http://localhost:8080` with the solver powered directly by Java!*

---

### Option 2: Run Pure Java Desktop GUI
Launches the native Java Swing desktop application:

```bash
java SudokuApp
```

---

### Option 3: Run Java Console / Terminal Solver
Runs the classic command-line benchmark:

```bash
java Sudoku
```

---

## Features

- **100% Java Engine**: Backtracking solver and constraint propagation implemented entirely in Java.
- **Dual Presentation Layers**:
  - **Modern Web Application**: Responsive, minimal UI with dark/light themes, crosshairs, and visual step-by-step backtracking.
  - **Native Desktop GUI**: Standalone Java Swing application with instant solving and custom board input.
- **RESTful Java API**:
  - `POST /api/solve` — Computes solutions on the JVM and returns execution latency in milliseconds.
  - `POST /api/validate` — Validates board constraints using `Sudoku.isSafe()`.
  - `GET /api/health` — Checks server and JVM status.
- **Algorithmic Transparency**: View the exact Java methods in the web code inspector or inspect the documented source files.

---

## Repository Structure

```text
Sudoku-Solver-Visualizer/
├── Sudoku.java        # Core Java recursive backtracking solver
├── SudokuServer.java  # Pure Java built-in HTTP server & REST API
├── SudokuApp.java     # Pure Java modern desktop GUI (Swing)
├── index.html         # Minimal web interface
├── style.css          # Minimal design system, dark/light themes
├── app.js             # Visualizer engine & client API connector
├── SRS.md             # IEEE 830 Software Requirements Specification
├── README.md          # Project documentation
├── LICENSE            # MIT License
├── .gitignore         # Build and IDE artifact exclusions
└── .gitattributes     # Configures GitHub Linguist to 100% Java
```

---

## Algorithmic Formulation

The solver formalizes Sudoku as a **Constraint Satisfaction Problem (CSP)**:

1. **Validation (`isSafe`)**:
   - Ensures candidate digit $d \in \{1, \dots, 9\}$ does not violate row, column, or $3 \times 3$ subgrid uniqueness.
2. **Recursive Search (`SudokuSolver`)**:
   - Depth-First Search with backtracking.
   - Places a valid candidate and recurses to the next cell.
   - If a dead end is encountered, resets the cell to $0$ and backtracks to explore alternative branches.

### Complexity
- **Worst-Case Time Complexity**: $\mathcal{O}(9^M)$, where $M \le 81$ is the number of unassigned cells.
- **Optimized Practical Complexity**: Early constraint pruning evaluates standard puzzles in $< 10^5$ operations (sub-15ms).
- **Space Complexity**: $\mathcal{O}(M)$ recursion depth on the JVM call stack.

---

## Documentation

- [Software Requirements Specification (SRS - IEEE 830)](SRS.md)

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
