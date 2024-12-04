
//tymiller
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RobotSimulator {
    // Generic Queue implementation
    private static class GenericQueue<T> {
        private class Node {
            T data;
            Node next;
            
            Node(T data) {
                this.data = data;
                this.next = null;
            }
        }
        
        private Node front;
        private Node rear;
        private int size;
        
        public GenericQueue() {
            front = null;
            rear = null;
            size = 0;
        }
        
        public void enqueue(T item) {
            Node newNode = new Node(item);
            if (isEmpty()) {
                front = newNode;
            } else {
                rear.next = newNode;
            }
            rear = newNode;
            size++;
        }
        
        public T dequeue() {
            if (isEmpty()) {
                return null;
            }
            T item = front.data;
            front = front.next;
            size--;
            if (isEmpty()) {
                rear = null;
            }
            return item;
        }
        
        public boolean isEmpty() {
            return size == 0;
        }
        
        public int size() {
            return size;
        }
    }
    
    // Board class to handle the game board
    private static class Board {
        private char[][] grid;
        private int rows;
        private int cols;
        
        public Board(String filename) throws FileNotFoundException {
            loadBoard(filename);
        }
        
        private void loadBoard(String filename) throws FileNotFoundException {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            
            // First pass to determine dimensions
            String firstLine = scanner.nextLine();
            cols = firstLine.length();
            rows = 1;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                rows++;
            }
            
            // Reset scanner and initialize grid
            scanner = new Scanner(file);
            grid = new char[rows][cols];
            
            // Second pass to fill grid
            for (int i = 0; i < rows; i++) {
                String line = scanner.nextLine();
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = line.charAt(j);
                }
            }
            scanner.close();
        }
        
        public void display(Robot robot) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (i == robot.getRow() && j == robot.getCol()) {
                        System.out.print('O');
                    } else {
                        System.out.print(grid[i][j]);
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
        
        public boolean isValidMove(int row, int col) {
            return row >= 0 && row < rows && col >= 0 && col < cols && grid[row][col] != 'X';
        }
    }
    
    // Robot class to handle robot position
    private static class Robot {
        private int row;
        private int col;
        
        public Robot() {
            this.row = 0;
            this.col = 0;
        }
        
        public int getRow() { return row; }
        public int getCol() { return col; }
        
        public void moveUp() { row--; }
        public void moveDown() { row++; }
        public void moveLeft() { col--; }
        public void moveRight() { col++; }
    }
    
    // Main simulator fields and methods
    private Board board;
    private Robot robot;
    private GenericQueue<String> commands;
    
    public RobotSimulator() {
        commands = new GenericQueue<>();
    }
    
    public void loadCommands(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (isValidCommand(command)) {
                commands.enqueue(command);
            }
        }
        scanner.close();
    }
    
    private boolean isValidCommand(String command) {
        return command.equals("Move Up") || 
               command.equals("Move Down") || 
               command.equals("Move Left") || 
               command.equals("Move Right");
    }
    
    public void simulate() {
        robot = new Robot();
        int commandNum = 0;
        
        System.out.println("\nSimulation begin");
        board.display(robot);
        
        while (!commands.isEmpty()) {
            String command = commands.dequeue();
            System.out.println("Command " + commandNum++);
            
            int newRow = robot.getRow();
            int newCol = robot.getCol();
            
            switch (command) {
                case "Move Up":
                    newRow--;
                    break;
                case "Move Down":
                    newRow++;
                    break;
                case "Move Left":
                    newCol--;
                    break;
                case "Move Right":
                    newCol++;
                    break;
            }
            
            if (board.isValidMove(newRow, newCol)) {
                switch (command) {
                    case "Move Up": robot.moveUp(); break;
                    case "Move Down": robot.moveDown(); break;
                    case "Move Left": robot.moveLeft(); break;
                    case "Move Right": robot.moveRight(); break;
                }
                board.display(robot);
            } else {
                System.out.println("CRASH");
                return;
            }
        }
    }
    
    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            try {
                System.out.println("Welcome to the Robot Simulator");
                System.out.print("Enter file for the Board: ");
                String boardFile = scanner.nextLine();
                
                System.out.print("Enter file for the Robot Commands: ");
                String commandsFile = scanner.nextLine();
                
                board = new Board(boardFile);
                loadCommands(commandsFile);
                simulate();
                
                System.out.print("Would you like to run another simulation? (yes/no): ");
                running = scanner.nextLine().trim().toLowerCase().equals("yes");
                commands = new GenericQueue<>();
                
            } catch (FileNotFoundException e) {
                System.out.println("Error: File not found. Please try again.");
            }
        }
        scanner.close();
    }
    
    // Main method to run the simulator
    public static void main(String[] args) {
        RobotSimulator simulator = new RobotSimulator();
        simulator.start();
    }
}