# Software Requirements Specification (SRS)
## For Pure Java Sudoku Solver, Web Server & GUI Application

**Document Version:** 2.0.0  
**Author:** Yash Bhutada  
**Date:** September 2026  
**Status:** Approved  
**Language:** Java 100%  
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
   - 2.1 Product Perspective & Pure Java Architecture
   - 2.2 Product Functions
   - 2.3 User Classes and Characteristics
   - 2.4 Operating Environment
   - 2.5 Design and Implementation Constraints
   - 2.6 Assumptions and Dependencies
3. [System Features & Functional Requirements](#3-system-features--functional-requirements)
   - 3.1 Core Backtracking Algorithm (`Sudoku.java`)
   - 3.2 Java HTTP Web Server & REST API (`SudokuServer.java`)
   - 3.3 Native Java Swing Desktop GUI (`SudokuApp.java`)
   - 3.4 Web Client & Algorithm Visualizer
   - 3.5 Board Constraint Validation (`isSafe`)
4. [External Interface Requirements](#4-external-interface-requirements)
   - 4.1 User Interfaces (Web & Desktop GUI)
   - 4.2 Hardware Interfaces
   - 4.3 Software Interfaces (Standard JDK)
   - 4.4 Communications Interfaces (HTTP/REST)
5. [Non-Functional Requirements](#5-non-functional-requirements)
   - 5.1 Performance & Latency Requirements
   - 5.2 Usability & Accessibility
   - 5.3 Reliability & Fault Tolerance
   - 5.4 Portability & Zero-Dependency Requirement
6. [System Architecture & Flow Diagrams](#6-system-architecture--flow-diagrams)
   - 6.1 Unified Java Architecture
   - 6.2 Data Flow Diagram (DFD Level 1)
   - 6.3 Backtracking Finite State Machine
7. [Mathematical & Algorithmic Formulation](#7-mathematical--algorithmic-formulation)

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) details the functional and non-functional requirements for the **Pure Java Sudoku Solver, Web Server & GUI Application**. The system implements a robust, dependency-free Java architecture encompassing the core recursive backtracking algorithm, a built-in Java HTTP server delivering a RESTful solving API and web UI, and a native Java Swing desktop application.

### 1.2 Document Conventions
- **Shall**: Mandatory requirement.
- **Should**: Recommended requirement.
- Requirement codes:
  - `FR-x`: Functional Requirement
  - `NFR-x`: Non-Functional Requirement

### 1.3 Intended Audience
Computer science students, software engineers, academic evaluators, and open-source contributors examining clean-architecture Java implementations of Constraint Satisfaction Problems (CSP).

### 1.4 Project Scope
The project provides:
1. **`Sudoku.java`**: Depth-first recursive backtracking algorithm with row, column, and subgrid validation.
2. **`SudokuServer.java`**: Zero-dependency built-in HTTP server (`com.sun.net.httpserver`) providing REST endpoints (`/api/solve`, `/api/validate`) and serving the web interface.
3. **`SudokuApp.java`**: Standalone native Java Swing desktop GUI with dark theme styling and instant solving.
4. **Web Frontend**: Client interface with interactive step-by-step visualizer connected to the Java backend.

---

## 2. Overall Description

### 2.1 Product Perspective & Pure Java Architecture
All core computation, web serving, and desktop GUI capabilities run on the standard Java Virtual Machine (JVM):

```
+--------------------------------------------------------------------------+
|                          Java Virtual Machine (JVM)                      |
|                                                                          |
|   +------------------------------------------------------------------+   |
|   |                         Sudoku.java                              |   |
|   |   - isSafe(grid, row, col, digit)                                |   |
|   |   - SudokuSolver(grid, row, col)                                 |   |
|   +------------------------------------------------------------------+   |
|                 ^                                      ^                 |
|                 |                                      |                 |
|   +----------------------------+         +---------------------------+   |
|   |     SudokuServer.java      |         |       SudokuApp.java      |   |
|   |   - com.sun.net.httpserver |         |   - Java Swing GUI        |   |
|   |   - POST /api/solve        |         |   - 9x9 Native Board      |   |
|   |   - POST /api/validate     |         |   - Direct JVM Execution  |   |
|   |   - Serves Web Client      |         +---------------------------+   |
|   +----------------------------+                                         |
+-----------------|--------------------------------------------------------+
                  | HTTP/JSON (Port 8080)
                  v
+--------------------------------------------------------------------------+
|                       Minimalist Web Client                              |
|   - index.html, style.css, app.js                                        |
+--------------------------------------------------------------------------+
```

### 2.2 Design and Implementation Constraints
- **Zero External Dependencies**: The entire project must compile and execute using solely the standard Java Development Kit (`javac *.java`). No Maven, Gradle, or external libraries (e.g. Jackson, Spring, FlatLaf) are required.
- **Language Purity**: All server, solver, and application logic must reside in Java files.

---

## 3. System Features & Functional Requirements

### 3.1 Core Backtracking Algorithm (`Sudoku.java`)
- **FR-1.1**: The class shall provide `public static boolean isSafe(int Sudoku[][], int row, int col, int digit)` ensuring row, column, and 3×3 box uniqueness.
- **FR-1.2**: The class shall provide `public static boolean SudokuSolver(int Sudoku[][], int row, int col)` implementing depth-first recursive backtracking.
- **FR-1.3**: The class shall terminate and return `true` when $\text{row} = 9 \land \text{col} = 0$.

### 3.2 Java HTTP Web Server & REST API (`SudokuServer.java`)
- **FR-2.1**: The server shall bind to `http://localhost:8080` using `com.sun.net.httpserver.HttpServer`.
- **FR-2.2**: The server shall expose `POST /api/solve` accepting a 9×9 matrix, executing `Sudoku.SudokuSolver()`, and returning JSON containing execution time and solved matrix.
- **FR-2.3**: The server shall expose `POST /api/validate` returning whether the current board violates any Sudoku constraints.
- **FR-2.4**: The server shall automatically launch the system's default browser to `http://localhost:8080` upon startup.

### 3.3 Native Java Swing Desktop GUI (`SudokuApp.java`)
- **FR-3.1**: The application shall present a native window with a 9×9 grid of custom styled `JTextField` elements.
- **FR-3.2**: Subgrid boundaries (3×3 blocks) shall be visually separated with thicker borders.
- **FR-3.3**: Key input shall be constrained to digits 1–9.
- **FR-3.4**: Action buttons shall provide "Solve with Java", "Reset", "Clear", and "Load Preset".

### 3.4 Web Client & Algorithm Visualizer
- **FR-4.1**: The web UI shall provide both Dark Mode and Light Mode.
- **FR-4.2**: The client shall communicate with `/api/solve` when hosted by `SudokuServer.java`, displaying execution latency.
- **FR-4.3**: An asynchronous visualizer shall animate candidate testing and backtracking with adjustable speed.

---

## 4. External Interface Requirements

### 4.1 User Interfaces
- **Web Interface**: Browser-based responsive single page application.
- **Java Desktop Interface**: Native OS window via Java Swing (`JFrame`).
- **Command Line**: Terminal standard output (`System.out.println`).

### 4.2 Communications Interfaces
- Local HTTP socket communication on port 8080.
- Standard JSON payloads over HTTP POST.

---

## 5. Non-Functional Requirements

### 5.1 Performance Requirements
- **NFR-1.1**: Solution computation for valid 9×9 puzzles shall complete in under 15 milliseconds on the HotSpot JVM.
- **NFR-1.2**: Java Web Server memory overhead shall remain below 40 MB.

### 5.2 Reliability & Fault Tolerance
- **NFR-2.1**: The system shall detect invalid inputs and return structured error messages without crashing the server or GUI.

### 5.3 Portability & Compatibility
- **NFR-3.1**: Fully functional across JDK 8, 11, 17, 21, and 25 on Windows, macOS, and Linux.

---

## 6. System Architecture & Flow Diagrams

### 6.1 Data Flow Diagram (DFD Level 1)

```
[ Web / GUI / CLI Client ]
           |
           | Board Grid State (9x9)
           v
  +--------------------+
  | SudokuServer.java  |
  | (or SudokuApp.java)|
  +--------------------+
           |
           | Validate Matrix
           v
  +--------------------+       Contradiction?
  |   Sudoku.isSafe    | ------------------------> [ Return Error ]
  +--------------------+
           | Valid
           v
  +--------------------+
  | Sudoku.SudokuSolver| (DFS Recursive Backtracking)
  +--------------------+
           |
           +--------------------------+
           | Solved                   | Unsolvable
           v                          v
  [ Return Solved Grid ]    [ Return Unsolvable Message ]
```

---

## 7. Mathematical & Algorithmic Formulation

The puzzle is formulated as an assignment $G = [g_{i,j}]_{9 \times 9}$ where $g_{i,j} \in \{0, \dots, 9\}$ ($0$ denotes empty).

1. **Row Uniqueness**: $\forall i \in \{0, \dots, 8\}, \{g_{i,j} \mid j \in \{0, \dots, 8\}\} = \{1, \dots, 9\}$
2. **Column Uniqueness**: $\forall j \in \{0, \dots, 8\}, \{g_{i,j} \mid i \in \{0, \dots, 8\}\} = \{1, \dots, 9\}$
3. **Subgrid Uniqueness**: $\forall k, m \in \{0, 1, 2\}, \{g_{3k+a, 3m+b} \mid a, b \in \{0, 1, 2\}\} = \{1, \dots, 9\}$

### Complexity
- **Time Complexity**: $\mathcal{O}(9^M)$ worst-case, pruned to $\mathcal{O}(V)$ where $V \ll 10^5$ operations.
- **Space Complexity**: $\mathcal{O}(M)$ auxiliary space on the call stack ($M \le 81$).
