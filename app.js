/**
 * Sudoku Web App — Logic & Solver Engine
 * Directly implements and extends the algorithm from Sudoku.java
 */

// =============================================================================
// Puzzle Presets
// =============================================================================
const PRESETS = {
  'java-default': [
    [5, 3, 0, 0, 7, 0, 0, 0, 0],
    [6, 0, 0, 1, 9, 5, 0, 0, 0],
    [0, 9, 8, 0, 0, 0, 0, 6, 0],
    [8, 0, 0, 0, 6, 0, 0, 0, 3],
    [4, 0, 0, 8, 0, 3, 0, 0, 1],
    [7, 0, 0, 0, 2, 0, 0, 0, 6],
    [0, 6, 0, 0, 0, 0, 2, 8, 0],
    [0, 0, 0, 4, 1, 9, 0, 0, 5],
    [0, 0, 0, 0, 8, 0, 0, 7, 9]
  ],
  'easy': [
    [0, 0, 0, 2, 6, 0, 7, 0, 1],
    [6, 8, 0, 0, 7, 0, 0, 9, 0],
    [1, 9, 0, 0, 0, 4, 5, 0, 0],
    [8, 2, 0, 1, 0, 0, 0, 4, 0],
    [0, 0, 4, 6, 0, 2, 9, 0, 0],
    [0, 5, 0, 0, 0, 3, 0, 2, 8],
    [0, 0, 9, 3, 0, 0, 0, 7, 4],
    [0, 4, 0, 0, 5, 0, 0, 3, 6],
    [7, 0, 3, 0, 1, 8, 0, 0, 0]
  ],
  'medium': [
    [0, 2, 0, 6, 0, 8, 0, 0, 0],
    [5, 8, 0, 0, 0, 9, 7, 0, 0],
    [0, 0, 0, 0, 4, 0, 0, 0, 0],
    [3, 7, 0, 0, 0, 0, 5, 0, 0],
    [6, 0, 0, 0, 0, 0, 0, 0, 4],
    [0, 0, 8, 0, 0, 0, 0, 1, 3],
    [0, 0, 0, 0, 2, 0, 0, 0, 0],
    [0, 0, 9, 8, 0, 0, 0, 3, 6],
    [0, 0, 0, 3, 0, 6, 0, 9, 0]
  ],
  'hard': [
    [0, 0, 0, 6, 0, 0, 4, 0, 0],
    [7, 0, 0, 0, 0, 3, 6, 0, 0],
    [0, 0, 0, 0, 9, 1, 0, 8, 0],
    [0, 0, 0, 0, 0, 0, 0, 0, 0],
    [0, 5, 0, 1, 8, 0, 0, 0, 3],
    [0, 0, 0, 3, 0, 6, 0, 4, 5],
    [0, 4, 0, 2, 0, 0, 0, 6, 0],
    [9, 0, 3, 0, 0, 0, 0, 0, 0],
    [0, 2, 0, 0, 0, 0, 1, 0, 0]
  ],
  'blank': Array.from({ length: 9 }, () => Array(9).fill(0))
};

// =============================================================================
// State Management
// =============================================================================
let initialGrid = [];
let currentGrid = [];
let selectedCell = { row: null, col: null };
let isVisualizing = false;
let isPaused = false;
let abortVisualization = false;
let stepCount = 0;
let backtrackCount = 0;

// DOM Elements
const gridContainer = document.getElementById('sudokuGrid');
const presetSelect = document.getElementById('presetSelect');
const solveInstantBtn = document.getElementById('solveInstantBtn');
const visualizeBtn = document.getElementById('visualizeBtn');
const resetBoardBtn = document.getElementById('resetBoardBtn');
const clearBoardBtn = document.getElementById('clearBoardBtn');
const statusBanner = document.getElementById('statusBanner');
const statusText = document.getElementById('statusText');
const themeToggleBtn = document.getElementById('themeToggleBtn');
const toggleCodeBtn = document.getElementById('toggleCodeBtn');
const codeModal = document.getElementById('codeModal');
const closeCodeBtn = document.getElementById('closeCodeBtn');
const visualizerControls = document.getElementById('visualizerControls');
const stepCounter = document.getElementById('stepCounter');
const backtrackCounter = document.getElementById('backtrackCounter');
const speedRange = document.getElementById('speedRange');
const pauseVisBtn = document.getElementById('pauseVisBtn');
const stopVisBtn = document.getElementById('stopVisBtn');
const toastEl = document.getElementById('toast');

// =============================================================================
// Java Sudoku Solver Core Logic (Faithful Port)
// =============================================================================

/**
 * Checks whether it is safe to place digit in Sudoku[row][col].
 * Replicates public static boolean isSafe(int Sudoku[][], int row, int col, int digit)
 */
function isSafe(sudoku, row, col, digit) {
  // column check: checks if digit exists in col or not
  for (let i = 0; i <= 8; i++) {
    if (sudoku[i][col] === digit) {
      return false;
    }
  }

  // row check: checks if digit exists in row or not
  for (let j = 0; j <= 8; j++) {
    if (sudoku[row][j] === digit) {
      return false;
    }
  }

  // 3x3 grid check: checks if digit exists in 3x3 box
  const sr = Math.floor(row / 3) * 3;
  const sc = Math.floor(col / 3) * 3;
  for (let i = sr; i < sr + 3; i++) {
    for (let j = sc; j < sc + 3; j++) {
      if (sudoku[i][j] === digit) {
        return false;
      }
    }
  }

  return true;
}

/**
 * Recursive backtracking solver
 * Replicates public static boolean SudokuSolver(int Sudoku[][], int row, int col)
 */
function sudokuSolver(sudoku, row = 0, col = 0) {
  // base case: row 9 and col 0 means all 9 rows are complete
  if (row === 9 && col === 0) {
    return true;
  }

  // determine next cell coordinates
  let nextRow = row;
  let nextCol = col + 1;
  if (col + 1 === 9) {
    nextRow = row + 1;
    nextCol = 0;
  }

  // if current cell is already filled, move to next cell
  if (sudoku[row][col] !== 0) {
    return sudokuSolver(sudoku, nextRow, nextCol);
  }

  // try digits 1 through 9
  for (let digit = 1; digit <= 9; digit++) {
    if (isSafe(sudoku, row, col, digit)) {
      sudoku[row][col] = digit;
      if (sudokuSolver(sudoku, nextRow, nextCol)) {
        return true;
      }
      sudoku[row][col] = 0; // backtrack
    }
  }

  return false;
}

// Deep copy helper
function cloneGrid(grid) {
  return grid.map(row => [...row]);
}

// Check initial validity before solving
function isBoardValid(grid) {
  for (let r = 0; r < 9; r++) {
    for (let c = 0; c < 9; c++) {
      const val = grid[r][c];
      if (val !== 0) {
        grid[r][c] = 0; // Temporarily unset to test safety
        const safe = isSafe(grid, r, c, val);
        grid[r][c] = val;
        if (!safe) return false;
      }
    }
  }
  return true;
}

// =============================================================================
// UI & Board Rendering
// =============================================================================

function initializeGrid() {
  gridContainer.innerHTML = '';
  for (let r = 0; r < 9; r++) {
    for (let c = 0; c < 9; c++) {
      const cell = document.createElement('div');
      cell.classList.add('cell');
      cell.dataset.row = r;
      cell.dataset.col = c;
      cell.setAttribute('role', 'gridcell');
      cell.setAttribute('tabindex', '-1');

      cell.addEventListener('click', () => handleCellClick(r, c));
      gridContainer.appendChild(cell);
    }
  }
}

function loadPreset(key) {
  const preset = PRESETS[key] || PRESETS['java-default'];
  initialGrid = cloneGrid(preset);
  currentGrid = cloneGrid(preset);
  selectedCell = { row: null, col: null };
  renderBoard();
  setStatus('Ready — Loaded ' + (key === 'java-default' ? 'Sudoku.java Preset' : key + ' puzzle'));
}

function renderBoard() {
  const cells = gridContainer.children;
  for (let i = 0; i < 81; i++) {
    const r = Math.floor(i / 9);
    const c = i % 9;
    const cell = cells[i];
    const val = currentGrid[r][c];
    const isClue = initialGrid[r][c] !== 0;

    cell.textContent = val !== 0 ? val : '';
    
    // Reset basic classes
    cell.className = 'cell';
    if (isClue) {
      cell.classList.add('clue');
    } else if (val !== 0) {
      cell.classList.add('user-filled');
    }

    // Check for conflict
    if (val !== 0) {
      currentGrid[r][c] = 0;
      const safe = isSafe(currentGrid, r, c, val);
      currentGrid[r][c] = val;
      if (!safe) {
        cell.classList.add('conflict');
      }
    }
  }

  updateHighlights();
}

function updateHighlights() {
  const cells = gridContainer.children;
  const { row: selR, col: selC } = selectedCell;
  const selectedValue = (selR !== null && selC !== null) ? currentGrid[selR][selC] : 0;

  for (let i = 0; i < 81; i++) {
    const r = Math.floor(i / 9);
    const c = i % 9;
    const cell = cells[i];

    cell.classList.remove('selected', 'peer', 'same-value');

    if (selR === null || selC === null) continue;

    // Selected cell
    if (r === selR && c === selC) {
      cell.classList.add('selected');
      continue;
    }

    // Peers (same row, col, or 3x3 subgrid)
    const inSameRow = (r === selR);
    const inSameCol = (c === selC);
    const inSameBox = (Math.floor(r / 3) === Math.floor(selR / 3)) && (Math.floor(c / 3) === Math.floor(selC / 3));

    if (inSameRow || inSameCol || inSameBox) {
      cell.classList.add('peer');
    }

    // Matching numbers
    if (selectedValue !== 0 && currentGrid[r][c] === selectedValue) {
      cell.classList.add('same-value');
    }
  }
}

function handleCellClick(r, c) {
  if (isVisualizing) return;
  selectedCell = { row: r, col: c };
  updateHighlights();
}

function inputNumber(digit) {
  if (isVisualizing) return;
  const { row, col } = selectedCell;
  if (row === null || col === null) {
    showToast('Select a cell first');
    return;
  }

  // Prevent modifying preset clues
  if (initialGrid[row][col] !== 0) {
    showToast('Cannot modify initial clue cell');
    return;
  }

  currentGrid[row][col] = digit;
  renderBoard();

  // Check if board complete
  checkBoardCompletion();
}

function checkBoardCompletion() {
  let isComplete = true;
  for (let r = 0; r < 9; r++) {
    for (let c = 0; c < 9; c++) {
      if (currentGrid[r][c] === 0) {
        isComplete = false;
        break;
      }
    }
  }

  if (isComplete && isBoardValid(currentGrid)) {
    setStatus('Congratulations! Puzzle solved successfully!', 'success');
    showToast('🎉 Congratulations! Board is complete & valid!');
  }
}

// =============================================================================
// Solve Actions
// =============================================================================

function handleSolveInstant() {
  if (isVisualizing) stopVisualization();

  if (!isBoardValid(currentGrid)) {
    setStatus('Cannot solve: Current board has rule conflicts', 'error');
    showToast('Rule conflict detected on board!');
    return;
  }

  const solverGrid = cloneGrid(currentGrid);
  const startTime = performance.now();
  const solved = sudokuSolver(solverGrid, 0, 0);
  const duration = (performance.now() - startTime).toFixed(1);

  if (solved) {
    // Animate filled cells
    const cells = gridContainer.children;
    for (let r = 0; r < 9; r++) {
      for (let c = 0; c < 9; c++) {
        const idx = r * 9 + c;
        if (currentGrid[r][c] === 0) {
          cells[idx].classList.add('solved-filled');
        }
      }
    }
    currentGrid = solverGrid;
    renderBoard();
    setStatus(`Solved successfully in ${duration}ms using Java Backtracking`, 'success');
    showToast(`Solved in ${duration}ms!`);
  } else {
    setStatus('Solution does not exist for this configuration', 'error');
    showToast('No valid solution found');
  }
}

// =============================================================================
// Visual Backtracking Solver
// =============================================================================

async function handleVisualize() {
  if (isVisualizing) return;

  if (!isBoardValid(currentGrid)) {
    setStatus('Cannot visualize: Rule conflicts present', 'error');
    showToast('Rule conflicts detected on board!');
    return;
  }

  isVisualizing = true;
  isPaused = false;
  abortVisualization = false;
  stepCount = 0;
  backtrackCount = 0;

  visualizerControls.classList.remove('hidden');
  pauseVisBtn.textContent = 'Pause';
  stepCounter.textContent = '0';
  backtrackCounter.textContent = '0';
  selectedCell = { row: null, col: null };
  updateHighlights();
  setStatus('Visualizing Sudoku.java backtracking algorithm...');

  // Clone grid to work on
  const gridCopy = cloneGrid(currentGrid);

  const success = await visualSudokuSolver(gridCopy, 0, 0);

  isVisualizing = false;
  visualizerControls.classList.add('hidden');

  if (abortVisualization) {
    setStatus('Visualization cancelled', 'default');
    renderBoard();
    return;
  }

  if (success) {
    currentGrid = gridCopy;
    renderBoard();
    setStatus(`Visual solve complete! (${stepCount} steps, ${backtrackCount} backtracks)`, 'success');
    showToast('Visual solve finished!');
  } else {
    setStatus('No solution exists', 'error');
  }
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function waitStep() {
  while (isPaused && !abortVisualization) {
    await sleep(60);
  }
  if (abortVisualization) return;

  // Speed slider calculation: 1 to 60 (inverted delay)
  const speed = parseInt(speedRange.value, 10);
  const delay = Math.max(2, Math.floor(250 / speed));
  await sleep(delay);
}

async function visualSudokuSolver(grid, row, col) {
  if (abortVisualization) return false;

  // Base case
  if (row === 9 && col === 0) {
    return true;
  }

  let nextRow = row;
  let nextCol = col + 1;
  if (col + 1 === 9) {
    nextRow = row + 1;
    nextCol = 0;
  }

  if (grid[row][col] !== 0) {
    return visualSudokuSolver(grid, nextRow, nextCol);
  }

  const cellEl = gridContainer.children[row * 9 + col];

  for (let digit = 1; digit <= 9; digit++) {
    if (abortVisualization) return false;

    stepCount++;
    stepCounter.textContent = stepCount;

    // Highlight cell trying digit
    cellEl.textContent = digit;
    cellEl.classList.add('vis-trying');
    await waitStep();

    if (isSafe(grid, row, col, digit)) {
      grid[row][col] = digit;
      cellEl.classList.remove('vis-trying');
      cellEl.classList.add('user-filled');

      const nextSolved = await visualSudokuSolver(grid, nextRow, nextCol);
      if (nextSolved) return true;

      // Backtracking
      if (abortVisualization) return false;
      backtrackCount++;
      backtrackCounter.textContent = backtrackCount;

      cellEl.classList.remove('user-filled');
      cellEl.classList.add('vis-backtrack');
      await waitStep();

      grid[row][col] = 0;
      cellEl.textContent = '';
      cellEl.classList.remove('vis-backtrack');
    } else {
      cellEl.classList.remove('vis-trying');
      cellEl.textContent = '';
    }
  }

  return false;
}

function stopVisualization() {
  abortVisualization = true;
  isPaused = false;
  isVisualizing = false;
  visualizerControls.classList.add('hidden');
  renderBoard();
}

// =============================================================================
// Status, Toast & Helper Functions
// =============================================================================

function setStatus(message, type = 'default') {
  statusText.textContent = message;
  statusBanner.className = 'status-banner';
  if (type === 'success') statusBanner.classList.add('status-success');
  if (type === 'error') statusBanner.classList.add('status-error');
}

let toastTimer = null;
function showToast(msg) {
  clearTimeout(toastTimer);
  toastEl.textContent = msg;
  toastEl.classList.remove('hidden');
  toastTimer = setTimeout(() => {
    toastEl.classList.add('hidden');
  }, 2200);
}

// =============================================================================
// Event Listeners
// =============================================================================

// Preset Selection
presetSelect.addEventListener('change', (e) => {
  if (isVisualizing) stopVisualization();
  loadPreset(e.target.value);
});

// Primary Buttons
solveInstantBtn.addEventListener('click', handleSolveInstant);
visualizeBtn.addEventListener('click', handleVisualize);

resetBoardBtn.addEventListener('click', () => {
  if (isVisualizing) stopVisualization();
  currentGrid = cloneGrid(initialGrid);
  selectedCell = { row: null, col: null };
  renderBoard();
  setStatus('Board reset to initial puzzle');
  showToast('Reset to original state');
});

clearBoardBtn.addEventListener('click', () => {
  if (isVisualizing) stopVisualization();
  initialGrid = Array.from({ length: 9 }, () => Array(9).fill(0));
  currentGrid = Array.from({ length: 9 }, () => Array(9).fill(0));
  selectedCell = { row: null, col: null };
  presetSelect.value = 'blank';
  renderBoard();
  setStatus('Board cleared — You can input a custom puzzle');
  showToast('Board cleared');
});

// Visualizer Controls
pauseVisBtn.addEventListener('click', () => {
  isPaused = !isPaused;
  pauseVisBtn.textContent = isPaused ? 'Resume' : 'Pause';
  setStatus(isPaused ? 'Visualizer paused' : 'Visualizing Sudoku.java backtracking...');
});

stopVisBtn.addEventListener('click', stopVisualization);

// On-screen Keypad clicks
document.querySelectorAll('.key-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    const key = parseInt(btn.dataset.key, 10);
    inputNumber(key);
  });
});

// Keyboard Navigation & Shortcuts
window.addEventListener('keydown', (e) => {
  // If code modal is open, let Escape close it
  if (!codeModal.classList.contains('hidden')) {
    if (e.key === 'Escape') codeModal.classList.add('hidden');
    return;
  }

  // Digits 1-9
  if (e.key >= '1' && e.key <= '9') {
    inputNumber(parseInt(e.key, 10));
    return;
  }

  // Clear / Backspace / Delete / 0
  if (e.key === 'Backspace' || e.key === 'Delete' || e.key === '0') {
    inputNumber(0);
    return;
  }

  // Navigation with arrows
  if (selectedCell.row !== null && selectedCell.col !== null) {
    let { row, col } = selectedCell;
    if (e.key === 'ArrowUp') {
      row = (row - 1 + 9) % 9;
      e.preventDefault();
    } else if (e.key === 'ArrowDown') {
      row = (row + 1) % 9;
      e.preventDefault();
    } else if (e.key === 'ArrowLeft') {
      col = (col - 1 + 9) % 9;
      e.preventDefault();
    } else if (e.key === 'ArrowRight') {
      col = (col + 1) % 9;
      e.preventDefault();
    } else if (e.key === 'Escape') {
      selectedCell = { row: null, col: null };
      updateHighlights();
      return;
    }
    selectedCell = { row, col };
    updateHighlights();
  }
});

// Theme Toggle
function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('sudoku-theme', theme);
}

themeToggleBtn.addEventListener('click', () => {
  const currentTheme = document.documentElement.getAttribute('data-theme');
  const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
  applyTheme(newTheme);
});

// Restore Theme
const savedTheme = localStorage.getItem('sudoku-theme') || 'dark';
applyTheme(savedTheme);

// Code Modal Toggle
toggleCodeBtn.addEventListener('click', () => {
  codeModal.classList.remove('hidden');
});

closeCodeBtn.addEventListener('click', () => {
  codeModal.classList.add('hidden');
});

codeModal.addEventListener('click', (e) => {
  if (e.target === codeModal) {
    codeModal.classList.add('hidden');
  }
});

// =============================================================================
// Initialization
// =============================================================================
initializeGrid();
loadPreset('java-default');
