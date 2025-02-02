package org.example;

import java.io.*;
import java.util.*;

public class Main {
    private int size;
    private boolean[][] grid;

    public Main(int size) {
        this.size = size;
        this.grid = new boolean[size][size];
    }

    public void loadBoardFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: File does not exist -> " + filePath);
            return;
        }

        List<boolean[]> boardList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean[] row = new boolean[line.length()];
                for (int i = 0; i < line.length(); i++) {
                    row[i] = line.charAt(i) == '1';
                }
                boardList.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        grid = boardList.toArray(new boolean[0][]);
        this.size = grid.length;
        System.out.println("Board loaded successfully!");
    }

    public void printGrid() {
        clearConsole(); // Czyszczenie konsoli przed wyświetleniem nowej planszy
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
                if (nx >= 0 && ny >= 0 && nx < size && ny < size && grid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void updateGrid() {
        boolean[][] newGrid = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
            Thread.sleep(300); // Krótsze opóźnienie dla płynniejszej animacji
        }
    }

    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else if (System.console() != null) { // Sprawdza, czy działa w prawdziwym terminalu
                System.out.print("\033[H\033[2J"); // ANSI escape code dla Linux/Mac
                System.out.flush();
            } else {
                // W IntelliJ IDEA i innych IDE po prostu wypisuje 50 pustych linii
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

        System.out.print("Enter size: ");
        int size = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline

        Main game = new Main(size);

        System.out.print("Enter absolute path to map file: ");
        String filePath = scanner.nextLine().trim();
        game.loadBoardFromFile(filePath);

        System.out.print("Enter number of generations: ");
        int generations = scanner.nextInt();

        game.run(generations);

        System.out.println("Game over!");
    }
}
