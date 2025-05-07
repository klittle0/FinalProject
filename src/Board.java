import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Board {
    // Creates instance of board class
    static int dimension;
    static int numPegs;
    static int[] directions;
    static int[][] adjLists;
    static final int INVALIDMOVE = 0;
    static final int OFFBOARD = -1;
    static final char SPACE = '0';
    static final int LEFT = 0;
    static final int UPLEFT = 1;
    static final int UPRIGHT = 2;
    static final int RIGHT = 3;
    static final int DOWNRIGHT = 4;
    static final int DOWNLEFT = 5;

    public Board(int n) {
        dimension = n;

        int pegs = 0;
        for (int i = 1; i <= n; i++) {
            pegs += i;
        }
        numPegs = pegs;
        adjLists = makeAdjacencyLists(numPegs);
        // CHECK: is there a better way I can do this??
        directions = new int[6];
        directions[0] = LEFT;
        directions[1] = UPLEFT;
        directions[2] = UPRIGHT;
        directions[3] = RIGHT;
        directions[4] = DOWNRIGHT;
        directions[5] = DOWNLEFT;
    }

    // For every possible starting move, write its succeeding board states to their own file
    // This way, the board states for removing peg 1 will be separate from when the user removes peg 7.
    public void writeBoardStates(String currentState){
        // Update 3 to numPegs after debugging
        for (int i = 1; i <= numPegs; i++) {
            StringBuilder state = new StringBuilder(currentState);
            // Removes peg i
            state.setCharAt(i, '0');
            String fileName = "peg" + i + ".txt";
            writeBoardStatesHelper(state.toString(), fileName);
        }
    }

    // Finds every possible board state & corresponding move. Writes these to a file
    public void writeBoardStatesHelper(String currentState, String file){
        Queue<String> queue = new LinkedList<>();
        // Why a hashset here?
        Set<String> visited = new HashSet<>();

        // Referenced ChatGPT for this line to write to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            queue.add(currentState);
            visited.add(currentState);

            writer.write("Current state     / next state     / start peg / jumped peg / final peg");
            writer.newLine();

            while (!queue.isEmpty()) {
                String current = queue.remove();

                // For every possible move given the current board state, find future moves
                for (int[] move : findAllMoves(current)) {
                    String nextState = updateState(current, move);
                    if (!visited.contains(nextState)) {
                        queue.add(nextState);
                        visited.add(nextState);
                    }

                    // Writes to my file in this form: currentState nextState moveStart moveJumped moveEnd
                    writer.write(current + " " + nextState + " " + move[0] + " " + move[1] + " " + move[2]);
                    writer.newLine();
                }
            }
            System.out.println("All moves saved to " + file);

            // Referenced Geeks for Geeks for this exception
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

        // Updates & returns the currentState based on any given peg movement
        public String updateState (String currentState,int[] move){
            int start = move[0];
            int jumpedPeg = move[1];
            int end = move[2];


            StringBuilder nextState = new StringBuilder(currentState);
            // Remove peg from start location
            nextState.setCharAt(start, '0');
            // Remove the jumped peg
            nextState.setCharAt(jumpedPeg, '0');
            // Place the peg at new location
            nextState.setCharAt(end, '1');
            String next = nextState.toString();

            // Do I need to update the adjacency lists here?
            return next;
        }

        // If a move is valid, this returns the index of the jumped peg + current peg's destination
        // Parameters: any peg index & intended direction of movement
        public int[] isValidMove (int spotIndex, int direction, String boardState){
            // If there's a neighbor peg in the proper direction
            int neighborPeg = Board.adjLists[spotIndex][direction];
            if (neighborPeg != OFFBOARD) {
                if (boardState.charAt(neighborPeg) != SPACE){
                    int neighborNeighbor = Board.adjLists[neighborPeg][direction];
                    // If the neighborNeighbor is valid AND there's an open space one peg past the neighbor
                    if (neighborNeighbor > INVALIDMOVE && boardState.charAt(neighborNeighbor) == '0') {
                        int[] bundle = {neighborPeg, neighborNeighbor};
                        return bundle;
                    }
                    // I do this exact line 2x...can I remove one?
                    int[] bundle = {INVALIDMOVE};
                    return bundle;
                }
            }
            int[] bundle = {INVALIDMOVE};
            return bundle;
        }

        // Returns list of all possible moves given a current board state (parameter)
        // Each move is formatted w/ index 0 = start value, index 1 = peg jumped over, index 2 = destination
        public ArrayList<int[]> findAllMoves (String currentState){

            ArrayList<int[]> allMoves = new ArrayList<>();
            // For every peg on the board (NO EMPTY SPACES)
            for (int i = 1; i <= numPegs; i++) {
                if (currentState.charAt(i) == '1') {
                    // Check all directions in which it could possibly move & see if they're valid
                    for (int j = LEFT; j <= DOWNLEFT; j++) {
                        int[] jumpAndDest = isValidMove(i, directions[j], currentState);
                        if (jumpAndDest[0] != INVALIDMOVE) {
                            int[] move = {i, jumpAndDest[0], jumpAndDest[1]};
                            allMoves.add(move);
                        }
                    }
                }
            }
            return allMoves;
        }

        // Creates adjacency lists for every peg on the board
        public int[][] makeAdjacencyLists ( int numPegs){
            int[][] adjLists = new int[numPegs + 1][6];
            // For every peg, find adjacency lists for all neighboring pegs
            // This is currently indexing at peg 1 = 1. Try to switch to 0-indexed later!
            for (int i = 1; i <= numPegs; i++) {
                adjLists[i] = getNeighbors(i);
            }
            return adjLists;
        }

        // Returns neighbor list for any peg on the board
        // Doesn't account for spaces! Just lists the peg # where there could possibly be a neighbor at any point
        public int[] getNeighbors (int pegNum){
            // Max neighbors = 6
            int[] neighborPegs = new int[6];

            // Calculate n's row & offset within the row
            int row = 0;
            int offset = 0;
            int rowMax = 0;
            for (int i = 1; i <= Board.dimension; i++) {
                rowMax += i;
                if (pegNum <= rowMax) {
                    // Row goes from 1 to board.dimension
                    row = i;
                    // Maybe come up with better formula...Goes from 1 to row width
                    offset = row - (rowMax - pegNum);
                    break;
                }
            }
            // Left neighbor
            // Up + left diagonal neighbor
            if (offset != 1) {
                neighborPegs[LEFT] = pegNum - 1;
                neighborPegs[UPLEFT] = pegNum - row;
            }
            // All "else" cases represent invalid neighbors/spots that don't exist on the board
            else {
                neighborPegs[LEFT] = -1;
                neighborPegs[UPLEFT] = -1;
            }
            // Up + right diagonal neighbor
            // Right neighbor
            if (offset != row) {
                neighborPegs[UPRIGHT] = pegNum - row + 1;
                neighborPegs[RIGHT] = pegNum + 1;
            } else {
                neighborPegs[UPRIGHT] = -1;
                neighborPegs[RIGHT] = -1;
            }
            // Bottom right + left neighbors
            // All pegs except for bottom row have 2 bottom neighbors
            if (row != Board.dimension) {
                neighborPegs[DOWNRIGHT] = pegNum + row + 1;
                neighborPegs[DOWNLEFT] = pegNum + row;
            } else {
                neighborPegs[DOWNRIGHT] = -1;
                neighborPegs[DOWNLEFT] = -1;
            }
            return neighborPegs;
        }

        // Referenced ChatGPT to create this method
        // Imports only the file necessary for the user's start peg
        public void importFile(int startPeg){
            String filename = "peg" + startPeg + ".txt";
            List<String> fileContents = new ArrayList<>();

            try (Scanner fileScanner = new Scanner(new java.io.File(filename))) {
                while (fileScanner.hasNextLine()) {
                    fileContents.add(fileScanner.nextLine());
                }
                System.out.println("Imported file " + filename + " with " + fileContents.size() + " lines.");
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filename);
                e.printStackTrace();
                return;
            }
        }


        public static void main (String[] args){
            Scanner s = new Scanner(System.in);
            System.out.println("Enter the dimension of your triangle: ");
            int n = s.nextInt();
            Board triangle = new Board(n);

            // Start state should actually be all 1s. I should first prompt the user to click which peg they want to remove first
            // This is what triggers the program to really start analyzing board states

            //Later, change this to be 1-indexed
            // ALS0, THIS MUST BE THE LENGTH OF NUM PEGS FOR THIS BOARD SIZE!!
            StringBuilder startState = new StringBuilder("x");
            for (int i = 0; i < numPegs; i++){
                startState.append('1');
            }

            String current = String.valueOf(startState);
            triangle.writeBoardStates(current);
            System.out.println("What is your first move? Enter a peg #, 1-" + numPegs);
            int startPeg = s.nextInt();

            // Update the board to reflect new start peg
            startState.setCharAt(startPeg, '0');
            current = String.valueOf(startState);

            // Import necessary file into memory


            ArrayList<int[]> possibleStates = triangle.findAllMoves(current);
            for (int[] each : possibleStates){
                System.out.println(each[0] + " over " + each[1] + " to " + each[2]);
            }
        }
    }