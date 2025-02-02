package org.example;

import java.io.*;
import java.util.*;

public class Main {
    private int rows;
    private int cols;
    private boolean[][] grid;

    public Main() {
    }

    public void loadBoardFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: File does not exist -> " + filePath);
            return;
        }

        List<boolean[]> boardList = new ArrayList<>();
        int maxCols = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean[] row = new boolean[line.length()];
                for (int i = 0; i < line.length(); i++) {
                    row[i] = line.charAt(i) == '1';
                }
                boardList.add(row);
                maxCols = Math.max(maxCols, line.length()); // Determine the widest row
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        rows = boardList.size();
        cols = maxCols;
        grid = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            boolean[] row = boardList.get(i);
            System.arraycopy(row, 0, grid[i], 0, row.length);
        }

        System.out.println("Board loaded successfully! Size: " + rows + "x" + cols);
    }

    public void printGrid() {
        clearConsole(); // Clear console before printing new grid
        for (boolean[] row : grid) {
            for (boolean cell : row) {
                System.out.print(cell ? "█ " : ". ");
            }
            System.out.println();
        }
    }

    private int countNeighbors(int x, int y) {
        int count = 0;
        int[] directions = {-1, 0, 1};

        for (int dx : directions) {
            for (int dy : directions) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && grid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void updateGrid() {
        boolean[][] newGrid = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int neighbors = countNeighbors(i, j);
                newGrid[i][j] = (grid[i][j] && (neighbors == 2 || neighbors == 3)) || (!grid[i][j] && neighbors == 3);
            }
        }
        grid = newGrid;
    }

    public void run(int generations) throws InterruptedException {
        for (int i = 0; i < generations; i++) {
            printGrid();
            updateGrid();
            Thread.sleep(300); // Shorter delay for smoother animation
        }
    }

    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else if (System.console() != null) {
                System.out.print("\033[H\033[2J"); // ANSI escape code for Linux/Mac
                System.out.flush();
            } else {
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to clear console.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Hello and welcome!");

        Main game = new Main();

        System.out.print("Enter absolute path to map file: ");
        String filePath = scanner.nextLine().trim();
        game.loadBoardFromFile(filePath);

        System.out.print("Enter number of generations: ");
        int generations = scanner.nextInt();

        game.run(generations);

        System.out.println("Game over!");
    }
}
