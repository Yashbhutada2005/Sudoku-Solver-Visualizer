import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Desktop;

/**
 * SudokuServer — Pure Java Built-in HTTP Server
 * Serves the web interface and handles Sudoku solving API requests in Java.
 * Zero external dependencies — runs directly on standard JDK.
 */
public class SudokuServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Static Web UI endpoints
        server.createContext("/", new StaticFileHandler());

        // REST API endpoints powered by Java Sudoku algorithm
        server.createContext("/api/solve", new SolveApiHandler());
        server.createContext("/api/validate", new ValidateApiHandler());
        server.createContext("/api/health", new HealthApiHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("=================================================");
        System.out.println("   SUDOKU JAVA WEB SERVER (v1.0.0)");
        System.out.println("   All solving logic is executed 100% in Java!");
        System.out.println("   Server URL: http://localhost:" + PORT);
        System.out.println("=================================================");

        // Automatically open the website in the default browser
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + PORT));
            }
        } catch (Exception e) {
            System.out.println("Open your browser at: http://localhost:" + PORT);
        }
    }

    /**
     * Handler for /api/solve:
     * Receives 9x9 board in JSON, executes Java recursive backtracking, returns solved board.
     */
    static class SolveApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed. Use POST.\"}");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                int[][] grid = parseGridFromJson(body);

                long startTime = System.nanoTime();
                boolean solvable = Sudoku.SudokuSolver(grid, 0, 0);
                long durationNs = System.nanoTime() - startTime;
                double durationMs = durationNs / 1_000_000.0;

                String jsonResponse;
                if (solvable) {
                    jsonResponse = String.format(
                        "{\"success\":true,\"engine\":\"Java 25 HotSpot VM\",\"timeMs\":%.2f,\"solution\":%s}",
                        durationMs, gridToJson(grid)
                    );
                } else {
                    jsonResponse = "{\"success\":false,\"error\":\"Solution does not exist for this board configuration.\"}";
                }

                sendJsonResponse(exchange, 200, jsonResponse);
            } catch (Exception ex) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handler for /api/validate:
     * Checks if current board has any constraint violations using Sudoku.isSafe
     */
    static class ValidateApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String body = readRequestBody(exchange);
                int[][] grid = parseGridFromJson(body);

                boolean isValid = true;
                outer:
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        int val = grid[r][c];
                        if (val != 0) {
                            grid[r][c] = 0;
                            boolean safe = Sudoku.isSafe(grid, r, c, val);
                            grid[r][c] = val;
                            if (!safe) {
                                isValid = false;
                                break outer;
                            }
                        }
                    }
                }

                String json = String.format("{\"valid\":%b,\"engine\":\"Java Sudoku.isSafe\"}", isValid);
                sendJsonResponse(exchange, 200, json);
            } catch (Exception ex) {
                sendJsonResponse(exchange, 400, "{\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Handler for /api/health
     */
    static class HealthApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String json = "{\"status\":\"UP\",\"language\":\"Java\",\"version\":\"" + System.getProperty("java.version") + "\"}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    /**
     * Serves index.html, style.css, app.js directly from disk
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File("." + path);
            if (!file.exists() || file.isDirectory()) {
                file = new File("D:/yash/Sudoku-Solver-Visualizer" + path);
            }

            if (!file.exists()) {
                String notFound = "404 Not Found: " + path;
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
            else if (path.endsWith(".java")) contentType = "text/plain; charset=UTF-8";

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // =========================================================================
    // Utilities: JSON Parsing & Response Helpers (Zero-dependency)
    // =========================================================================

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * Parses a 9x9 matrix from JSON string: [[...],[...],...] or {"grid":[[...]]}
     */
    public static int[][] parseGridFromJson(String json) {
        int[][] grid = new int[9][9];
        // Extract all digits or numbers from the JSON structure
        int startIndex = json.indexOf("[[");
        if (startIndex == -1) {
            startIndex = json.indexOf('[');
        }
        if (startIndex == -1) {
            throw new IllegalArgumentException("Invalid grid format in JSON request.");
        }

        String content = json.substring(startIndex).replaceAll("[^0-9,]", " ");
        String[] tokens = content.trim().split("[,\\s]+");

        int count = 0;
        for (String t : tokens) {
            if (!t.isEmpty()) {
                int r = count / 9;
                int c = count % 9;
                if (r < 9 && c < 9) {
                    grid[r][c] = Integer.parseInt(t);
                    count++;
                }
            }
            if (count >= 81) break;
        }

        if (count < 81) {
            throw new IllegalArgumentException("Grid must contain exactly 81 values. Found: " + count);
        }
        return grid;
    }

    public static String gridToJson(int[][] grid) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 9; i++) {
            sb.append("[");
            for (int j = 0; j < 9; j++) {
                sb.append(grid[i][j]);
                if (j < 8) sb.append(",");
            }
            sb.append("]");
            if (i < 8) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
