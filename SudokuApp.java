import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * SudokuApp — Pure Java Modern Graphical Interface (GUI)
 * Desktop application built entirely in Java using Swing.
 * Interacts directly with Sudoku.java backtracking solver.
 */
public class SudokuApp extends JFrame {

    private final JTextField[][] cells = new JTextField[9][9];
    private final int[][] initialGrid = new int[9][9];
    private final JLabel statusLabel = new JLabel("Ready — Select a cell or choose an action");

    // Default benchmark puzzle from Sudoku.java
    private static final int[][] DEFAULT_PUZZLE = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    // Minimal Modern Color Palette
    private static final Color BG_DARK = new Color(15, 23, 42);          // Slate 900
    private static final Color CARD_BG = new Color(30, 41, 59);          // Slate 800
    private static final Color CELL_BG = new Color(30, 41, 59);
    private static final Color CELL_TEXT_CLUE = new Color(255, 255, 255);
    private static final Color CELL_TEXT_SOLVED = new Color(52, 211, 153); // Emerald
    private static final Color CELL_SELECTED = new Color(51, 65, 85);
    private static final Color GRID_LINE_THIN = new Color(71, 85, 105);
    private static final Color GRID_LINE_THICK = new Color(148, 163, 184);
    private static final Color BTN_PRIMARY = new Color(99, 102, 241);     // Indigo
    private static final Color BTN_ACCENT = new Color(14, 165, 233);      // Sky

    public SudokuApp() {
        super("Sudoku — Pure Java Solver & GUI");
        initUI();
        loadPreset(DEFAULT_PUZZLE);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 740);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 25, 5, 25));

        JLabel titleLabel = new JLabel("Sudoku Solver (Java)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("100% Pure Java Implementation | Backtracking Algorithm");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 9x9 Sudoku Board Panel
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(BG_DARK);

        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        boardPanel.setPreferredSize(new Dimension(480, 480));
        boardPanel.setBackground(CARD_BG);
        boardPanel.setBorder(new MatteBorder(3, 3, 3, 3, GRID_LINE_THICK));

        Font cellFont = new Font("Segoe UI", Font.BOLD, 22);

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(cellFont);
                cell.setBackground(CELL_BG);
                cell.setForeground(CELL_TEXT_CLUE);
                cell.setCaretColor(Color.WHITE);

                // Distinct 3x3 block borders
                int top = (r % 3 == 0 && r != 0) ? 2 : 1;
                int left = (c % 3 == 0 && c != 0) ? 2 : 1;
                int bottom = (r == 8) ? 0 : ((r % 3 == 2) ? 2 : 1);
                int right = (c == 8) ? 0 : ((c % 3 == 2) ? 2 : 1);

                Color topColor = (r % 3 == 0 && r != 0) ? GRID_LINE_THICK : GRID_LINE_THIN;
                Color leftColor = (c % 3 == 0 && c != 0) ? GRID_LINE_THICK : GRID_LINE_THIN;
                Color bottomColor = (r % 3 == 2) ? GRID_LINE_THICK : GRID_LINE_THIN;
                Color rightColor = (c % 3 == 2) ? GRID_LINE_THICK : GRID_LINE_THIN;

                Border matte = BorderFactory.createMatteBorder(top, left, bottom, right, GRID_LINE_THIN);
                cell.setBorder(new CompoundBorder(matte, new EmptyBorder(2, 2, 2, 2)));

                // Keyboard validation: allow only 1-9
                final int row = r;
                final int col = c;
                cell.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char ch = e.getKeyChar();
                        if (ch < '1' || ch > '9') {
                            e.consume(); // reject non-digits
                        } else {
                            cell.setText(""); // replace existing
                        }
                    }
                });

                cells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        boardContainer.add(boardPanel);
        add(boardContainer, BorderLayout.CENTER);

        // Bottom Controls Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BG_DARK);
        bottomPanel.setBorder(new EmptyBorder(10, 25, 20, 25));

        // Status Banner
        statusLabel.setForeground(new Color(203, 213, 225));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        // Action Buttons Bar
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.setBackground(BG_DARK);

        JButton solveBtn = createStyledButton("⚡ Solve with Java", BTN_PRIMARY);
        JButton resetBtn = createStyledButton("Reset", new Color(51, 65, 85));
        JButton clearBtn = createStyledButton("Clear", new Color(51, 65, 85));
        JButton loadPresetBtn = createStyledButton("Load Preset", BTN_ACCENT);

        solveBtn.addActionListener(e -> solvePuzzle());
        resetBtn.addActionListener(e -> resetToInitial());
        clearBtn.addActionListener(e -> clearBoard());
        loadPresetBtn.addActionListener(e -> loadPreset(DEFAULT_PUZZLE));

        buttonsPanel.add(solveBtn);
        buttonsPanel.add(loadPresetBtn);
        buttonsPanel.add(resetBtn);
        buttonsPanel.add(clearBtn);

        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadPreset(int[][] puzzle) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                initialGrid[r][c] = puzzle[r][c];
                if (puzzle[r][c] != 0) {
                    cells[r][c].setText(String.valueOf(puzzle[r][c]));
                    cells[r][c].setEditable(false);
                    cells[r][c].setForeground(CELL_TEXT_CLUE);
                    cells[r][c].setFont(new Font("Segoe UI", Font.BOLD, 22));
                } else {
                    cells[r][c].setText("");
                    cells[r][c].setEditable(true);
                    cells[r][c].setForeground(CELL_TEXT_SOLVED);
                    cells[r][c].setFont(new Font("Segoe UI", Font.PLAIN, 22));
                }
            }
        }
        statusLabel.setText("Loaded Sudoku.java reference puzzle");
    }

    private void solvePuzzle() {
        int[][] grid = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                String txt = cells[r][c].getText().trim();
                grid[r][c] = txt.isEmpty() ? 0 : Integer.parseInt(txt);
            }
        }

        // Validate board before solving
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = grid[r][c];
                if (val != 0) {
                    grid[r][c] = 0;
                    boolean safe = Sudoku.isSafe(grid, r, c, val);
                    grid[r][c] = val;
                    if (!safe) {
                        statusLabel.setText("Rule violation detected at Row " + (r + 1) + ", Col " + (c + 1));
                        return;
                    }
                }
            }
        }

        long start = System.nanoTime();
        boolean solved = Sudoku.SudokuSolver(grid, 0, 0);
        double durationMs = (System.nanoTime() - start) / 1_000_000.0;

        if (solved) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (initialGrid[r][c] == 0) {
                        cells[r][c].setText(String.valueOf(grid[r][c]));
                        cells[r][c].setForeground(CELL_TEXT_SOLVED);
                    }
                }
            }
            statusLabel.setText(String.format("Solved in %.2f ms using Sudoku.java Backtracking Engine", durationMs));
        } else {
            statusLabel.setText("Solution does not exist for this puzzle.");
        }
    }

    private void resetToInitial() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (initialGrid[r][c] != 0) {
                    cells[r][c].setText(String.valueOf(initialGrid[r][c]));
                } else {
                    cells[r][c].setText("");
                }
            }
        }
        statusLabel.setText("Reset to initial board");
    }

    private void clearBoard() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                initialGrid[r][c] = 0;
                cells[r][c].setText("");
                cells[r][c].setEditable(true);
                cells[r][c].setForeground(CELL_TEXT_SOLVED);
            }
        }
        statusLabel.setText("Board cleared — You can enter any custom puzzle");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SudokuApp().setVisible(true);
        });
    }
}
